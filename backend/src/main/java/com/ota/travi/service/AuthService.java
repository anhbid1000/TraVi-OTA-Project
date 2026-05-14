package com.ota.travi.service;

import com.ota.travi.Enum.HangThanhVien;
import com.ota.travi.Enum.TrangThaiUser;
import com.ota.travi.dto.response.AuthResponse;
import com.ota.travi.dto.request.LoginRequest;
import com.ota.travi.dto.request.RegisterRequest;
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

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OTPService otpService;

    //Method đăng ký tài khoản mới
    @Transactional
    public String register(RegisterRequest request) {

        // 1. Kiểm tra trùng lặp cả Username và Email
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Lỗi: Username đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }

        User newUser;
        VaiTro vaiTro;

        // 2. Rẽ nhánh khởi tạo Entity dựa theo Loại Tài Khoản
        switch (request.loaiTaiKhoan().toUpperCase()) {
            case "KHACH_HANG":
                KhachHang kh = new KhachHang();
                kh.setDiemThanhVien(0);
                kh.setHangThanhVien(HangThanhVien.DONG); //
                kh.setTongChiTieu(0.0);
                newUser = kh;
                vaiTro = vaiTroRepository.findByTen("KHACH_HANG")
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Role KHACH_HANG"));
                break;

            case "DOI_TAC":
                DoiTac dt = new DoiTac();
                dt.setTiLeChietKhau(0.0f);
                // Mọi thông tin pháp lý (Mã số thuế, Giấy phép) đã được đẩy về Sprint 2 (HoSoKinhDoanh)
                newUser = dt;
                vaiTro = vaiTroRepository.findByTen("DOI_TAC")
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Role DOI_TAC"));
                break;

            default:
                throw new RuntimeException("Lỗi: Loại tài khoản không hợp lệ (Chỉ hỗ trợ KHACH_HANG hoặc DOI_TAC)!");
        }

        // 3. Gán các trường thông tin dùng chung của lớp cha (User)
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này"));

        otpService.checkOtp(email, confirmOTP);

        user.setTrangThai(TrangThaiUser.HOAT_DONG);
        userRepository.save(user);

        return "Xác minh OTP thành công. Tài khoản đã được kích hoạt.";
    }

    public AuthResponse login(AuthenticationManager authenticationManager, JwtUtil jwtUtil, LoginRequest request) {
        // 1. Giao việc kiểm tra Username/Password cho Spring Security (AuthenticationManager)
        // Quá trình này sẽ tự động gọi hàm loadUserByUsername ở CustomUserDetailsService (Task 4)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
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
}
