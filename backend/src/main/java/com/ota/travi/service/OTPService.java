package com.ota.travi.service;

import com.ota.travi.enums.OtpPurpose;
import com.ota.travi.entity.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class OTPService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration REGISTER_OTP_TTL = Duration.ofMinutes(4);
    private static final Duration PASSWORD_RESET_OTP_TTL = Duration.ofMinutes(4);
    private static final Duration PASSWORD_RESET_VERIFIED_TTL = Duration.ofMinutes(10);
    private static final String OTP_KEY_PREFIX = "otp:";
    private static final String PASSWORD_RESET_VERIFIED_PREFIX = "password-reset:verified:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

    // --- 1. TẠO VÀ KIỂM TRA OTP ---
    // Tạo mã OTP ngẫu nhiên và thêm vào redis
    public String createOtp(String email, OtpPurpose purpose) {
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        redisTemplate.opsForValue().set(getOtpKey(email, purpose), otp, getOtpTtl(purpose));

        if (purpose == OtpPurpose.PASSWORD_RESET) {
            clearPasswordResetVerification(email);
        }

        return otp;
    }

    // Xác thực mã OTP được cung cấp so với mã lưu trữ trong Redis
    public void checkOtp(String email, String confirmOTP, OtpPurpose purpose) {
        String otpKey = getOtpKey(email, purpose);
        String redisOtp = redisTemplate.opsForValue().get(otpKey);

        if (redisOtp == null || redisOtp.trim().isEmpty()) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        if (!redisOtp.equals(confirmOTP)) {
            throw new RuntimeException("OTP không hợp lệ");
        }

        redisTemplate.delete(otpKey);
    }

    // --- 2. QUẢN LÝ XÁC THỰC ĐẶT LẠI MẬT KHẨU ---
    // Đánh dấu rằng mật khẩu đã được xác thực và có thể đặt lại
    public void markPasswordResetVerified(String email) {
        redisTemplate.opsForValue().set(getPasswordResetVerifiedKey(email), "true", PASSWORD_RESET_VERIFIED_TTL);
    }

    public boolean isPasswordResetVerified(String email) {
        String value = redisTemplate.opsForValue().get(getPasswordResetVerifiedKey(email));
        return "true".equals(value);
    }

    public void clearPasswordResetVerification(String email) {
        redisTemplate.delete(getPasswordResetVerifiedKey(email));
    }

    // --- 3. GỬI EMAIL OTP ---
    // Gửi email xác minh OTP cho người dùng đăng ký mới
    @Async
    public void sendVerificationRegister(User user) {
        if (user == null) {
            System.err.println("User is null");
            return;
        }

        String email = user.getEmail();
        String otp = this.createOtp(email, OtpPurpose.REGISTER_VERIFICATION);

        String subject = "TraViOTA Resgister - Xác nhận OTP đăng ký tài khoản";

        String content = "<p>Xin chào " + user.getHoTen() + ",</p>"
                + "<p>Mã xác minh tạo tài khoản trên TraVi của bạn là: <p>"
                + "<p><b>" + otp + "</b></p>"
                + "<br>"
                + "<p>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>"
                + "<br>"
                + "<p>Mã OTP có hiệu lực trong vòng 4 phút.</p>"
                + "<br><br>"
                + "Trân trọng!";

        sendEmail(email, subject, content);
    }

    // Gửi email OTP cho người dùng muốn đặt lại mật khẩu
    @Async
    public void sendPasswordResetOtp(User user) {
        if (user == null) {
            System.err.println("User is null");
            return;
        }

        String email = user.getEmail();
        String otp = this.createOtp(email, OtpPurpose.PASSWORD_RESET);

        String subject = "TraViOTA - OTP đặt lại mật khẩu";
        String content = "<p>Xin chào " + user.getHoTen() + ",</p>"
                + "<p>Bạn vừa yêu cầu đặt lại mật khẩu trên TraVi.</p>"
                + "<p>Mã xác thực của bạn là:</p>"
                + "<p><b>" + otp + "</b></p>"
                + "<br>"
                + "<p>Mã OTP có hiệu lực trong vòng 4 phút.</p>"
                + "<p>Nếu bạn không yêu cầu thao tác này, vui lòng bỏ qua email này.</p>"
                + "<br><br>"
                + "Trân trọng!";

        sendEmail(email, subject, content);
    }

    // --- UTILITY METHODS ---
    // Gửi email sử dụng JavaMailSender
    private void sendEmail(String email, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception ex) {
            clearOtpKeys(email);
            throw new RuntimeException("Không thể gửi email OTP", ex);
        }
    }

    private Duration getOtpTtl(OtpPurpose purpose) {
        return switch (purpose) {
            case REGISTER_VERIFICATION -> REGISTER_OTP_TTL;
            case PASSWORD_RESET -> PASSWORD_RESET_OTP_TTL;
        };
    }

    private String getOtpKey(String email, OtpPurpose purpose) {
        return OTP_KEY_PREFIX + purpose.name() + ":" + normalizeEmail(email);
    }

    private String getPasswordResetVerifiedKey(String email) {
        return PASSWORD_RESET_VERIFIED_PREFIX + normalizeEmail(email);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private void clearOtpKeys(String email) {
        redisTemplate.delete(getOtpKey(email, OtpPurpose.REGISTER_VERIFICATION));
        redisTemplate.delete(getOtpKey(email, OtpPurpose.PASSWORD_RESET));
        clearPasswordResetVerification(email);
    }

}