package com.ota.travi.config;


import com.ota.travi.security.CustomUserDetailsService;
import com.ota.travi.security.JwtAuthenticationFilter;
import com.ota.travi.security.RateLimitFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.ota.travi.constant.ApiEndpoints.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Cho phép dùng annotation @PreAuthorize trên các API
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Đã tạo ở AppConfig

    @Value("${FRONTEND_URL}")
    private String frontendURL;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder); // Khai báo máy băm mật khẩu
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    // CẤU HÌNH CORS NGHIÊM NGẶT
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(frontendURL));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF vì chúng ta dùng Token Stateless
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Bật CORS để cho phép Frontend React gọi API
                .authorizeHttpRequests(auth -> auth
                        // 1. CÁC API MỞ TỰ DO (Không cần đăng nhập)
                        .requestMatchers(AUTH_PREFIX + "/**").permitAll()
                        .requestMatchers(PUBLIC_PREFIX + "/**").permitAll()
                        .requestMatchers(BASE_PREFIX + "/attachments/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 2. CÁC API PHÂN QUYỀN (Roles theo Seed Data DB)
                        .requestMatchers(ADMIN_PREFIX +"/**").hasRole("QUAN_TRI_VIEN")
                        .requestMatchers(PARTNER_PREFIX + "/**").hasRole("DOI_TAC")
                        .requestMatchers(USER_FEEDBACK_PREFIX + "/**").hasRole("USER")

                        // 3. TẤT CẢ CÁC API KHÁC ĐỀU PHẢI QUẸT THẺ (Có JWT hợp lệ mới được vào)
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không lưu Session
                .authenticationProvider(authenticationProvider())
                // Đặt Rate Limit lên ĐẦU TIÊN, sau đó mới tới JWT Filter
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthFilter, RateLimitFilter.class);

        return http.build();
    }
}
