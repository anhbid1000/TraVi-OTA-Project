package com.ota.travi.controller;

import com.ota.travi.dto.request.LoginRequest;
import com.ota.travi.dto.request.RefreshTokenRequest;
import com.ota.travi.dto.request.RegisterRequest;
import com.ota.travi.dto.request.VerifyOtpRequest;
import com.ota.travi.dto.response.AuthResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.security.CustomUserDetailsService;
import com.ota.travi.security.JwtUtil;
import com.ota.travi.service.AuthService;
import com.ota.travi.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import static com.ota.travi.constant.ApiEndpoints.AUTH_LOGIN;
import static com.ota.travi.constant.ApiEndpoints.AUTH_LOGOUT;
import static com.ota.travi.constant.ApiEndpoints.AUTH_REFRESH;
import static com.ota.travi.constant.ApiEndpoints.AUTH_REGISTER;
import static com.ota.travi.constant.ApiEndpoints.AUTH_VERIFY_EMAIL;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;


    // --- 1. API ĐĂNG KÝ (REGISTER) ---
    @PostMapping(AUTH_REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try{
            String message = authService.register(request);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }catch (RuntimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(AUTH_VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyOtpRequest request) {
        try{
            String message = authService.verifyRegisterOtp(request.email(), request.confirmOTP());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch (RuntimeException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 2. API ĐĂNG NHẬP (LOGIN) ---
    @PostMapping(AUTH_LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try{
            //1. Xử lý login, Tạo phản hồi và trả về cho client
            AuthResponse authResponse = authService.login(authenticationManager, jwtUtil, request);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (DisabledException ex) {
            return new ResponseEntity<>("Tài khoản chưa kích hoạt hoặc đã bị vô hiệu hóa", HttpStatus.FORBIDDEN);
        } catch (BadCredentialsException ex) {
            return new ResponseEntity<>("Sai tài khoản hoặc mật khẩu", HttpStatus.UNAUTHORIZED);
        } catch(Exception ex){
            return new ResponseEntity<>("Đăng nhập thất bại", HttpStatus.UNAUTHORIZED);
        }
    }

    // --- 3. API ĐĂNG XUẤT (LOGOUT) ---
    @PostMapping(AUTH_LOGOUT)
    public ResponseEntity<Object> logout(
            HttpServletRequest request,
            @Nullable @RequestBody(required = false) RefreshTokenRequest refreshTokenRequest
    ) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Tính thời gian còn lại của Token để set giới hạn lưu trong Redis
            long remainingTime = jwtUtil.getRemainingExpirationTime(token);

            // Ném token vào danh sách đen
            blacklistService.addToBlacklist(token, remainingTime);
        }

        if (refreshTokenRequest != null && refreshTokenRequest.refreshToken() != null && !refreshTokenRequest.refreshToken().isBlank()) {
            String refreshToken = refreshTokenRequest.refreshToken();
            long remainingTime = jwtUtil.getRemainingExpirationTime(refreshToken);
            blacklistService.addToBlacklist(refreshToken, remainingTime);
        }

        return new ResponseEntity<>("Đăng xuất thành công!", HttpStatus.OK);
    }

    // --- 4. API LÀM MỚI TOKEN (REFRESH TOKEN) ---
    @PostMapping(AUTH_REFRESH)
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            return new ResponseEntity<>("Refresh Token không được để trống", HttpStatus.BAD_REQUEST);
        }

        // 1. Thẻ này có bị đăng xuất (nằm trong blacklist) không?
        if (blacklistService.isBlacklisted(refreshToken)) {
            return new ResponseEntity<>("Thẻ đã bị vô hiệu hóa (Đăng xuất).", HttpStatus.UNAUTHORIZED);
        }

        try {
            // 2. Cố gắng đọc tên User từ Refresh Token
            // (Nếu thẻ Refresh đã quá 7 ngày, nó sẽ tự động văng lỗi và nhảy xuống khối catch)
            String username = jwtUtil.extractUsername(refreshToken);

            // 3. Lấy lại hồ sơ mới nhất từ DB để đảm bảo User chưa bị khóa/đổi quyền
            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsernameValue(username);
            if (!customUserDetails.isEnabled()) {
                return new ResponseEntity<>("Tài khoản chưa kích hoạt hoặc đã bị vô hiệu hóa", HttpStatus.FORBIDDEN);
            }
            String role = customUserDetails.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền của người dùng"))
                    .getAuthority();

            // 4. Mọi thứ hợp lệ -> In ra Access Token mới tinh
            String newAccessToken = jwtUtil.generateToken(customUserDetails.getUsername(), role);

            // Trả về thẻ Access mới, thẻ Refresh thì giữ nguyên thẻ cũ (hoặc in thẻ refresh mới cũng được)
            AuthResponse response = new AuthResponse(newAccessToken, refreshToken, "Bearer", "Cấp lại thẻ thành công!");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Refresh token hết hạn, bị giả mạo...
            return new ResponseEntity<>("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", HttpStatus.UNAUTHORIZED);
        }
    }
}
