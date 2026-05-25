package com.ota.travi.service;

import com.ota.travi.enums.TrangThaiUser;
import com.ota.travi.dto.request.GoogleAuthRequest;
import com.ota.travi.dto.request.ResetPasswordRequest;
import com.ota.travi.dto.response.AuthResponse;
import com.ota.travi.entity.DoiTac;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.User;
import com.ota.travi.entity.VaiTro;
import com.ota.travi.repository.UserRepository;
import com.ota.travi.repository.VaiTroRepository;
import com.ota.travi.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.ota.travi.enums.OtpPurpose.PASSWORD_RESET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VaiTroRepository vaiTroRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private OTPService otpService;

    @Mock
    private GoogleTokenVerifierService googleTokenVerifierService;

    @InjectMocks
    private AuthService authService;

    private VaiTro customerRole;
    private VaiTro partnerRole;
    private VaiTro adminRole;

    @BeforeEach
    void setUp() {
        customerRole = new VaiTro();
        customerRole.setTen("KHACH_HANG");

        partnerRole = new VaiTro();
        partnerRole.setTen("DOI_TAC");

        adminRole = new VaiTro();
        adminRole.setTen("QUAN_TRI_VIEN");
    }

    @Test
    void loginWithGoogleShouldCreateCustomerWhenEmailDoesNotExist() {
        when(googleTokenVerifierService.verifyIdToken("valid-google-token"))
                .thenReturn(new GoogleTokenVerifierService.GoogleUserInfo("customer@gmail.com", "Nguyễn Văn A"));
        when(userRepository.findByEmail("customer@gmail.com")).thenReturn(Optional.empty());
        when(vaiTroRepository.findByTen("KHACH_HANG")).thenReturn(Optional.of(customerRole));
        when(userRepository.existsByUsername("nguyen.van.a")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-google-secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken("nguyen.van.a", "ROLE_KHACH_HANG")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("nguyen.van.a")).thenReturn("refresh-token");

        AuthResponse response = authService.loginWithGoogle(new GoogleAuthRequest("valid-google-token", "KHACH_HANG"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertInstanceOf(KhachHang.class, savedUser);
        assertEquals("customer@gmail.com", savedUser.getEmail());
        assertEquals("nguyen.van.a", savedUser.getUsername());
        assertEquals(TrangThaiUser.HOAT_DONG, savedUser.getTrangThai());
        assertEquals("access-token", response.token());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    void loginWithGoogleShouldActivatePendingPartnerAccount() {
        DoiTac partner = new DoiTac();
        partner.setEmail("partner@gmail.com");
        partner.setUsername("partner.demo");
        partner.setHoTen("Partner Demo");
        partner.setVaiTro(partnerRole);
        partner.setTrangThai(TrangThaiUser.CHUA_XAC_THUC);
        partner.setMatKhau("hashed-password");

        when(googleTokenVerifierService.verifyIdToken("partner-token"))
                .thenReturn(new GoogleTokenVerifierService.GoogleUserInfo("partner@gmail.com", "Partner Demo"));
        when(userRepository.findByEmail("partner@gmail.com")).thenReturn(Optional.of(partner));
        when(userRepository.save(partner)).thenReturn(partner);
        when(jwtUtil.generateToken("partner.demo", "ROLE_DOI_TAC")).thenReturn("partner-access");
        when(jwtUtil.generateRefreshToken("partner.demo")).thenReturn("partner-refresh");

        AuthResponse response = authService.loginWithGoogle(new GoogleAuthRequest("partner-token", "DOI_TAC"));

        assertEquals(TrangThaiUser.HOAT_DONG, partner.getTrangThai());
        assertEquals("partner-access", response.token());
        assertEquals("partner-refresh", response.refreshToken());
    }

    @Test
    void requestPasswordResetOtpShouldRejectAdminAccount() {
        User admin = new DoiTac();
        admin.setEmail("admin@gmail.com");
        admin.setVaiTro(adminRole);
        admin.setTrangThai(TrangThaiUser.HOAT_DONG);

        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.requestPasswordResetOtp("admin@gmail.com")
        );

        assertEquals("Tính năng này chỉ hỗ trợ khách hàng và đối tác", exception.getMessage());
        verify(otpService, never()).sendPasswordResetOtp(any(User.class));
    }

    @Test
    void verifyPasswordResetOtpShouldValidateOtpAndMarkSession() {
        KhachHang customer = new KhachHang();
        customer.setEmail("customer@gmail.com");
        customer.setVaiTro(customerRole);
        customer.setTrangThai(TrangThaiUser.HOAT_DONG);

        when(userRepository.findByEmail("customer@gmail.com")).thenReturn(Optional.of(customer));

        String message = authService.verifyPasswordResetOtp("customer@gmail.com", "123456");

        assertEquals("Xác thực OTP thành công. Bạn có thể đặt lại mật khẩu mới.", message);
        verify(otpService).checkOtp("customer@gmail.com", "123456", PASSWORD_RESET);
        verify(otpService).markPasswordResetVerified("customer@gmail.com");
    }

    @Test
    void resetPasswordShouldUpdatePasswordAndClearVerifiedSession() {
        KhachHang customer = new KhachHang();
        customer.setEmail("customer@gmail.com");
        customer.setVaiTro(customerRole);
        customer.setTrangThai(TrangThaiUser.HOAT_DONG);
        customer.setMatKhau("old-password");

        when(userRepository.findByEmail("customer@gmail.com")).thenReturn(Optional.of(customer));
        when(otpService.isPasswordResetVerified("customer@gmail.com")).thenReturn(true);
        when(passwordEncoder.encode("Password@123")).thenReturn("new-hashed-password");
        when(userRepository.save(customer)).thenReturn(customer);

        String message = authService.resetPassword(
                new ResetPasswordRequest("customer@gmail.com", "Password@123", "Password@123")
        );

        assertEquals("Đặt lại mật khẩu thành công. Bạn có thể đăng nhập bằng mật khẩu mới.", message);
        assertEquals("new-hashed-password", customer.getMatKhau());
        verify(otpService).clearPasswordResetVerification("customer@gmail.com");
    }
}

