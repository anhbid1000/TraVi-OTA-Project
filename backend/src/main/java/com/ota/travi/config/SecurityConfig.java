package com.ota.travi.config;
import com.ota.travi.constant.ApiEndpoints;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không dùng Session
                .authorizeHttpRequests(auth -> auth

                        // 1. NHÓM TỰ DO (Guest)
                        .requestMatchers(ApiEndpoints.PUBLIC_PREFIX + "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, ApiEndpoints.SEARCH_ROOMS + "/**").permitAll()

                        // 2. NHÓM ADMIN (Quyền cao nhất)
                        .requestMatchers(HttpMethod.DELETE, ApiEndpoints.ADMIN_USERS + "/**").hasRole("ADMIN")
                        .requestMatchers(ApiEndpoints.ADMIN_PREFIX + "/**").hasRole("ADMIN")

                        // 3. NHÓM PARTNER (Đối tác)
                        .requestMatchers(ApiEndpoints.PARTNER_PREFIX + "/**").hasRole("PARTNER")

                        // 4. NHÓM USER (Thành viên)
                        .requestMatchers(ApiEndpoints.USER_BOOKING + "/**").hasRole("USER")
                        .requestMatchers(ApiEndpoints.USER_REVIEW + "/**").hasRole("USER")

                        // 5. NHÓM HỆ THỐNG (External Systems)
                        .requestMatchers(ApiEndpoints.SYSTEM_PREFIX + "/**").hasRole("SYSTEM")

                        // Tất cả các yêu cầu còn lại đều phải Authenticated
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
