package com.ota.travi.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ota.travi.constant.ApiEndpoints.AUTH_FORGOT_PASSWORD_REQUEST_OTP;
import static com.ota.travi.constant.ApiEndpoints.AUTH_FORGOT_PASSWORD_VERIFY_OTP;
import static com.ota.travi.constant.ApiEndpoints.AUTH_LOGIN;
import static com.ota.travi.constant.ApiEndpoints.AUTH_REGISTER;
import static com.ota.travi.constant.ApiEndpoints.AUTH_RESEND_OTP;


@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Giới hạn chặt cho login/register: 5 request/phút - chống brute-force
    private static final Bandwidth STRICT_AUTH_LIMIT = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
    
    // Giới hạn vừa cho OTP: 10 request/10 phút - chống spam OTP
    private static final Bandwidth OTP_LIMIT = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(10)));
    
    // Giới hạn thoáng cho public: 120 request/phút - cho phép browse thoải mái
    private static final Bandwidth PUBLIC_LIMIT = Bandwidth.classic(120, Refill.greedy(120, Duration.ofMinutes(1)));

    // Map lưu bucket theo IP client cho từng nhóm endpoint
    private final Map<String, Bucket> strictAuthBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> otpBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> publicBuckets = new ConcurrentHashMap<>();

    /**
     * Xử lý mỗi request và áp dụng rate limit theo nhóm endpoint.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String clientKey = resolveClientKey(request);

        // Nhóm 1: Auth nhạy cảm (login/register)
        if (requestUri.startsWith(AUTH_LOGIN) || requestUri.startsWith(AUTH_REGISTER)) {
            Bucket bucket = strictAuthBuckets.computeIfAbsent(clientKey, key -> newBucket(STRICT_AUTH_LIMIT));
            if (!bucket.tryConsume(1)) {
                writeRateLimitResponse(response, "Bạn đã gửi quá nhiều yêu cầu đăng nhập/đăng ký. Vui lòng đợi 1 phút.");
                return;
            }
        } 
        // Nhóm 2: OTP endpoints
        else if (requestUri.startsWith(AUTH_RESEND_OTP)
                || requestUri.startsWith(AUTH_FORGOT_PASSWORD_REQUEST_OTP)
                || requestUri.startsWith(AUTH_FORGOT_PASSWORD_VERIFY_OTP)) {
            Bucket bucket = otpBuckets.computeIfAbsent(clientKey, key -> newBucket(OTP_LIMIT));
            if (!bucket.tryConsume(1)) {
                writeRateLimitResponse(response, "Bạn đã gửi quá nhiều yêu cầu OTP. Vui lòng thử lại sau.");
                return;
            }
        } 
        // Nhóm 3: Public catalog/search/filter
        else if (requestUri.startsWith("/api/v1/public/")) {
            Bucket bucket = publicBuckets.computeIfAbsent(clientKey, key -> newBucket(PUBLIC_LIMIT));
            if (!bucket.tryConsume(1)) {
                writeRateLimitResponse(response, "Bạn đã gửi quá nhiều yêu cầu công khai. Vui lòng thử lại sau ít phút.");
                return;
            }
        }
        // Nhóm 4: Các endpoint khác (swagger, admin, static...) -> không limit

        // Cho request đi tiếp nếu pass rate limit
        filterChain.doFilter(request, response);
    }

    /**
     * Tạo bucket mới với bandwidth đã cho.
     * Mỗi bucket độc lập theo IP client.
     */
    private Bucket newBucket(Bandwidth bandwidth) {
        return Bucket.builder().addLimit(bandwidth).build();
    }

    /**
     * Lấy IP client để làm key cho bucket.
     * 
     * Ưu tiên:
     * 1. X-Forwarded-For header (nếu có proxy/load balancer phía trước)
     * 2. RemoteAddr (IP trực tiếp kết nối đến server)
     *
     */
    private String resolveClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            // X-Forwarded-For có thể chứa nhiều IP (client, proxy1, proxy2...)
            // Lấy IP đầu tiên (client gốc)
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Ghi response 429 Too Many Requests với message tiếng Việt.
     * Format JSON để frontend dễ parse.
     */
    private void writeRateLimitResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"" + message + "\"}");
    }
}
