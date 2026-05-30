package com.ota.travi.security;

import com.ota.travi.service.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.ota.travi.constant.ApiEndpoints.AUTH_PREFIX;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_PREFIX;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private CustomUserDetailsService userDetailsService; // lấy user từ Database

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri != null && (requestUri.startsWith(AUTH_PREFIX) || requestUri.startsWith(PUBLIC_PREFIX));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Máy quét nhìn vào tiêu đề (Header) của gói tin xem có thẻ từ không
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String username;

        // Nếu không có thẻ, hoặc thẻ không bắt đầu bằng "Bearer ", cho đi qua nhưng không cấp quyền
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Cắt bỏ chữ "Bearer " (7 ký tự) để lấy cái lõi mã token
        jwtToken = authHeader.substring(7);

        // KIỂM TRA BLACKLIST NGAY TẠI ĐÂY
        if (tokenBlacklistService.isBlacklisted(jwtToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Phiên bản đăng nhập hiện tại đã kết thúc.\"}");
            return;
        }

        try {
            username = jwtUtil.extractUsername(jwtToken);

        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Token đã hết hạn. Hãy đăng nhập lại.\"}");
            return; // Dừng lại luôn, không cho đi tiếp

        } catch (SignatureException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Chữ ký token không hợp lệ.\"}");
            return; // Dừng lại luôn

        } catch (MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Định dạng token không hợp lệ.\"}");
            return; // Dừng lại luôn

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Không thể xác thực phiên đăng nhập hiện tại.\"}");
            return; // Dừng lại luôn
        }

        // 4. Nếu đọc được tên khách, và khách chưa được cấp quyền trong SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Tìm khách này trong hệ thống máy chủ (Database)
            UserDetails userDetails = userDetailsService.loadUserByUsernameValue(username);

            // Kiểm tra thẻ từ có hợp lệ/chính chủ không
            if (jwtUtil.isTokenValid(jwtToken, userDetails)) {
                // Tạo một cái "Giấy thông hành" nội bộ cho Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Mở cửa: Cấp quyền thành công vào Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Chuyển cho màng lọc tiếp theo hoặc đi vào Controller
        filterChain.doFilter(request, response);
    }
}
