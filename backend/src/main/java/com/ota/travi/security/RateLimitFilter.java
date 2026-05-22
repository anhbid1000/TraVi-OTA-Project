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

import static com.ota.travi.constant.ApiEndpoints.AUTH_LOGIN;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Giới hạn 5 requests / 1 phút (Bảo vệ brute-force đăng nhập)
    private final Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Chỉ áp dụng giới hạn cho API Đăng nhập (bỏ qua tiền kiểm OPTIONS)
        if (request.getRequestURI().startsWith(AUTH_LOGIN) && !request.getMethod().equalsIgnoreCase("OPTIONS")) {
            if (!bucket.tryConsume(1)) { // Rút 1 token từ xô (bucket)
                response.setStatus(429); // 429 Too Many Requests
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"status\": 429, \"error\": \"Too Many Requests\", \"message\": \"Bạn đã thử đăng nhập quá nhiều lần. Vui lòng đợi 1 phút.\"}");
                return; // Chặn đứng request
            }
        }
        filterChain.doFilter(request, response);
    }
}