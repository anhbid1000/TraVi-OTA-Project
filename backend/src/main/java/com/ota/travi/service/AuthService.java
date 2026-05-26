package com.ota.travi.service;

import com.ota.travi.enums.HangThanhVien;
import com.ota.travi.enums.OtpPurpose;
import com.ota.travi.enums.TrangThaiUser;
import com.ota.travi.dto.request.GoogleAuthRequest;
import com.ota.travi.dto.request.LoginRequest;
import com.ota.travi.dto.request.RegisterRequest;
import com.ota.travi.dto.request.ResetPasswordRequest;
import com.ota.travi.dto.response.AuthResponse;
import com.ota.travi.entity.DoiTac;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.User;
import com.ota.travi.entity.VaiTro;
import com.ota.travi.repository.UserRepository;
import com.ota.travi.repository.VaiTroRepository;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class AuthService {
    private static final String CUSTOMER_ROLE = "KHACH_HANG";
    private static final String PARTNER_ROLE = "DOI_TAC";
    private static final String ADMIN_ROLE = "QUAN_TRI_VIEN";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OTPService otpService;

    @Autowired
    private GoogleTokenVerifierService googleTokenVerifierService;

    // --- 1. Service Đăng ký (Register) ---
    //Method đăng ký tài khoản mới
    @Transactional
    public String register(RegisterRequest request) {
        String normalizedAccountType = normalizeAccountType(request.loaiTaiKhoan());
        String normalizedEmail = normalizeEmail(request.email());
        String normalizedUsername = request.username().trim();

        // 1. Kiểm tra trùng lặp cả Username và Email
        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new RuntimeException("Lỗi: Username đã tồn tại!");
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }

        User newUser;
        VaiTro vaiTro = getRoleByAccountType(normalizedAccountType);
        newUser = createUserByAccountType(normalizedAccountType);

        // 3. Gán các trường thông tin dùng chung của lớp cha (User)
        newUser.setUsername(normalizedUsername);
        newUser.setEmail(normalizedEmail);
        newUser.setHoTen(request.hoTen());
        newUser.setSoDienThoai(request.soDienThoai());
        newUser.setVaiTro(vaiTro);
        newUser.setTrangThai(TrangThaiUser.CHUA_XAC_THUC);

        // 4. Băm mật khẩu an toàn bằng BCrypt
        newUser.setMatKhau(passwordEncoder.encode(request.matKhau()));

        // 5. Lưu xuống Database (Hibernate tự xử lý việc chèn vào bảng cha và bảng con)
        User savedUser = userRepository.save(newUser);

        // 6. Gửi OTP sau khi transaction commit để tránh gửi mail khi DB rollback
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    otpService.sendVerificationRegister(savedUser);
                }
            });
        } else {
            otpService.sendVerificationRegister(savedUser);
        }

        return "Đăng ký thành công tài khoản: " + request.username() + ". Vui lòng kiểm tra email để lấy OTP xác minh.";
    }

    @Transactional
    public String verifyRegisterOtp(String email, String confirmOTP) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này"));

        otpService.checkOtp(normalizedEmail, confirmOTP, OtpPurpose.REGISTER_VERIFICATION);

        user.setTrangThai(TrangThaiUser.HOAT_DONG);
        userRepository.save(user);

        return "Xác minh OTP thành công. Tài khoản đã được kích hoạt.";
    }

    public String resendRegisterOtp(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này"));

        if (user.getTrangThai() == TrangThaiUser.HOAT_DONG) {
            throw new RuntimeException("Tài khoản này đã được xác minh");
        }

        if (user.getTrangThai() != TrangThaiUser.CHUA_XAC_THUC) {
            throw new RuntimeException("Không thể gửi OTP cho tài khoản ở trạng thái hiện tại");
        }

        otpService.sendVerificationRegister(user);
        return "Mã OTP mới đã được gửi tới email của bạn.";
    }


    // --- 2. Service ĐĂNG NHẬP (LOGIN) ---
    public AuthResponse login(LoginRequest request) {
        // 1. Giao việc kiểm tra Username/Password cho Spring Security (AuthenticationManager)
        // Quá trình này sẽ tự động gọi hàm loadUserByUsername ở CustomUserDetailsService (Task 4)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizeEmail(request.email()),
                        request.matKhau()
                )
        );

        // 2. Lưu trạng thái đã xác thực vào Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Lấy thông tin hồ sơ User vừa đăng nhập thành công
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            throw new RuntimeException("Không thể xác thực thông tin người dùng");
        }

        // Lấy Role (Quyền) của User để nhét vào Token
        String role = customUserDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền của người dùng"))
                .getAuthority();

        //4. Tạo token nhờ jwtUtil - In ra 2 thẻ
        String accessToken = jwtUtil.generateToken(customUserDetails.getUsername(), role);
        String refreshToken = jwtUtil.generateRefreshToken(customUserDetails.getUsername());

        return new AuthResponse(accessToken, refreshToken, "Bearer","Đăng nhập thành công");
    }

    // --- 2. Service ĐĂNG NHẬP, Đăng ký Google ---

    @Transactional
    public AuthResponse loginWithGoogle(GoogleAuthRequest request) {
        String normalizedAccountType = normalizeAccountType(request.loaiTaiKhoan());
        if (ADMIN_ROLE.equals(normalizedAccountType)) {
            throw new RuntimeException("Đăng nhập Google chỉ hỗ trợ khách hàng và đối tác");
        }

        GoogleTokenVerifierService.GoogleUserInfo googleUserInfo = googleTokenVerifierService.verifyIdToken(request.idToken());
        String email = googleUserInfo.email();
        String hoTen = resolveDisplayName(googleUserInfo.hoTen(), email);

        User user = userRepository.findByEmail(email)
                .map(existingUser -> prepareExistingGoogleUser(existingUser, hoTen, normalizedAccountType))
                .orElseGet(() -> createGoogleUser(email, hoTen, normalizedAccountType));

        User savedUser = userRepository.save(user);
        return createAuthResponse(savedUser);
    }

    private String normalizeAccountType(String accountType) {
        if (accountType == null || accountType.isBlank()) {
            throw new RuntimeException("Loại tài khoản không được để trống");
        }

        return accountType.trim().toUpperCase(Locale.ROOT);
    }

    private String resolveDisplayName(String hoTen, String email) {
        if (hoTen != null && !hoTen.isBlank()) {
            return hoTen.trim();
        }

        String localPart = email == null ? "user" : email.split("@")[0];
        return localPart == null || localPart.isBlank() ? "TraVi User" : localPart;
    }

    private User prepareExistingGoogleUser(User user, String hoTen, String requestedAccountType) {
        validateSelfServiceRole(user);

        String currentRole = user.getVaiTro() == null ? null : user.getVaiTro().getTen();
        if (!requestedAccountType.equals(currentRole)) {
            throw new RuntimeException("Email này đã đăng ký với vai trò khác. Vui lòng dùng đúng cổng đăng nhập.");
        }

        if (isRestrictedFromSelfService(user.getTrangThai())) {
            throw new RuntimeException("Tài khoản đã bị khóa, không thể đăng nhập bằng Google");
        }

        if (user.getTrangThai() == TrangThaiUser.CHUA_XAC_THUC) {
            user.setTrangThai(TrangThaiUser.HOAT_DONG);
        }

        if (user.getHoTen() == null || user.getHoTen().isBlank()) {
            user.setHoTen(hoTen);
        }

        return user;
    }

    private void validateSelfServiceRole(User user) {
        String roleName = user.getVaiTro() == null ? null : user.getVaiTro().getTen();
        if (!CUSTOMER_ROLE.equals(roleName) && !PARTNER_ROLE.equals(roleName)) {
            throw new RuntimeException("Tính năng này chỉ hỗ trợ khách hàng và đối tác");
        }
    }

    private User createGoogleUser(String email, String hoTen, String accountType) {
        User newUser = createUserByAccountType(accountType);
        newUser.setUsername(generateUniqueUsername(hoTen, email));
        newUser.setEmail(email);
        newUser.setHoTen(hoTen);
        newUser.setVaiTro(getRoleByAccountType(accountType));
        newUser.setTrangThai(TrangThaiUser.HOAT_DONG);
        newUser.setMatKhau(passwordEncoder.encode(generateRandomPasswordSeed(email)));
        return newUser;
    }

    private User createUserByAccountType(String accountType) {
        return switch (accountType) {
            case CUSTOMER_ROLE -> {
                KhachHang kh = new KhachHang();
                kh.setDiemThanhVien(0);
                kh.setHangThanhVien(HangThanhVien.DONG);
                kh.setTongChiTieu(0.0);
                yield kh;
            }
            case PARTNER_ROLE -> {
                DoiTac dt = new DoiTac();
                dt.setTiLeChietKhau(0.0f);
                yield dt;
            }
            default -> throw new RuntimeException("Lỗi: Loại tài khoản không hợp lệ (Chỉ hỗ trợ KHACH_HANG hoặc DOI_TAC)!");
        };
    }

    private String generateUniqueUsername(String hoTen, String email) {
        String candidate = sanitizeUsername(hoTen);
        if (candidate.isBlank()) {
            candidate = sanitizeUsername(email == null ? "user" : email.split("@")[0]);
        }

        if (candidate.isBlank()) {
            candidate = "user";
        }

        if (candidate.length() < 4) {
            candidate = (candidate + "user").substring(0, 4);
        }

        String uniqueCandidate = candidate;
        int suffix = 1;
        while (userRepository.existsByUsername(uniqueCandidate)) {
            uniqueCandidate = candidate + suffix;
            suffix++;
        }
        return uniqueCandidate;
    }

    private String sanitizeUsername(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_.-]+", ".")
                .replaceAll("\\.{2,}", ".")
                .replaceAll("^[.-]+|[.-]+$", "");

        return normalized.length() > 20 ? normalized.substring(0, 20) : normalized;
    }

    private String generateRandomPasswordSeed(String email) {
        return "Google@" + Math.abs(email.hashCode()) + System.nanoTime() + "Aa";
    }

    private AuthResponse createAuthResponse(User user) {
        String role = "ROLE_" + user.getVaiTro().getTen();
        String accessToken = jwtUtil.generateToken(user.getUsername(), role);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return new AuthResponse(accessToken, refreshToken, "Bearer", "Đăng nhập Google thành công");
    }


    // --- 3. API Reset Password ---

    public String requestPasswordResetOtp(String email) {
        User user = findSelfServiceUserByEmail(email);
        validatePasswordResetEligibility(user);
        otpService.sendPasswordResetOtp(user);
        return "Mã OTP đặt lại mật khẩu đã được gửi tới email của bạn.";
    }

    public String verifyPasswordResetOtp(String email, String confirmOTP) {
        User user = findSelfServiceUserByEmail(email);
        validatePasswordResetEligibility(user);

        otpService.checkOtp(email, confirmOTP, OtpPurpose.PASSWORD_RESET);
        otpService.markPasswordResetVerified(email);
        return "Xác thực OTP thành công. Bạn có thể đặt lại mật khẩu mới.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        User user = findSelfServiceUserByEmail(request.email());
        validatePasswordResetEligibility(user);

        if (!otpService.isPasswordResetVerified(request.email())) {
            throw new RuntimeException("Phiên đặt lại mật khẩu không hợp lệ hoặc đã hết hạn");
        }

        user.setMatKhau(passwordEncoder.encode(request.matKhauMoi()));
        userRepository.save(user);
        otpService.clearPasswordResetVerification(request.email());

        return "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập bằng mật khẩu mới.";
    }

    private VaiTro getRoleByAccountType(String accountType) {
        return vaiTroRepository.findByTen(accountType)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role " + accountType));
    }


    private User findSelfServiceUserByEmail(String email) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này"));
        validateSelfServiceRole(user);
        return user;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email không được để trống");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }


    private void validatePasswordResetEligibility(User user) {
        if (user.getTrangThai() == TrangThaiUser.CHUA_XAC_THUC) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt. Vui lòng xác minh email trước khi đặt lại mật khẩu.");
        }

        if (isRestrictedFromSelfService(user.getTrangThai())) {
            throw new RuntimeException("Tài khoản đã bị khóa. Không thể đặt lại mật khẩu.");
        }
    }

    private boolean isRestrictedFromSelfService(TrangThaiUser trangThai) {
        return trangThai == TrangThaiUser.TAM_VO_HIEU_HOA
                || trangThai == TrangThaiUser.BI_KHOA_TAM_THOI
                || trangThai == TrangThaiUser.BI_CAM_VINH_VIEN
                || trangThai == TrangThaiUser.DA_XOA;
    }


}
