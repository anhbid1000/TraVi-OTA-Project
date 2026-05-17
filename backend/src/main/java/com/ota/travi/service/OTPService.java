package com.ota.travi.service;

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

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

    // Tạo mã OTP ngẫu nhiên và thêm vào redis
    public String createOtp(String email) {
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        redisTemplate.opsForValue().set(email, otp, Duration.ofMinutes(4));
        return otp;
    }

    public void checkOtp(String email, String confirmOTP) {
        String redisOtp = redisTemplate.opsForValue().get(email);

        if (redisOtp == null || redisOtp.trim().isEmpty()) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        if (!redisOtp.equals(confirmOTP)) {
            throw new RuntimeException("OTP không hợp lệ");
        }

        redisTemplate.delete(email);
    }

    @Async
    public void sendVerificationRegister(User user) {
        if (user == null) {
            System.err.println("User is null");
            return;
        }

        String email = user.getEmail();
        String otp = this.createOtp(email);

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

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception ex) {
            redisTemplate.delete(email);
            throw new RuntimeException("Không thể gửi email OTP", ex);
        }
    }

}