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

import static com.ota.travi.constant.ApiEndpoints.AUTH_LOGIN;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Sử dụng ConcurrentHashMap để lưu trữ xô riêng cho từng IP (Thread-safe chống tranh chấp dữ liệu)
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // Hàm tạo cấu hình xô: 5 requests / 1 phút
    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
                .build();
    }

    // Hàm lấy xô dựa theo IP, nếu IP chưa có xô thì tự tạo xô mới
    private Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, k -> createNewBucket());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Kiểm tra chính xác endpoint đăng nhập (Nên dùng equals để an toàn tuyệt đối)
        if (AUTH_LOGIN.equals(request.getRequestURI())) {

            // Lấy IP của Client gửi request lên
            String ip = getClientIP(request);
            Bucket bucket = resolveBucket(ip);

            if (!bucket.tryConsume(1)) { // Rút 1 token của riêng IP đó
                response.setStatus(429); // 429 Too Many Requests
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"status\": 429, \"error\": \"Too Many Requests\", \"message\": \"Bạn đã thử đăng nhập quá nhiều lần. Vui lòng đợi 1 phút.\"}");
                return; // Chặn đứng request của riêng IP phá hoại đó
            }
        }
        filterChain.doFilter(request, response);
    }

    // Hàm bổ trợ lấy IP chuẩn (Kể cả khi deploy qua Proxy/Load Balancer như Nginx)
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim(); // Lấy IP đầu tiên trong chuỗi định tuyến
    }
}