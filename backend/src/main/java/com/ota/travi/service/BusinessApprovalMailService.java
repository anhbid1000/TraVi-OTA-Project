package com.ota.travi.service;

import com.ota.travi.exception.EmailDeliveryException;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BusinessApprovalMailService {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendApprovalEmail(String email, String hoTen, String tenCoSo) {
        String subject = "TraVi OTA - Ho so kinh doanh da duoc phe duyet";
        String content = "<p>Xin chao " + hoTen + ",</p>"
                + "<p>Ho so kinh doanh <b>" + tenCoSo + "</b> cua ban da duoc phe duyet.</p>"
                + "<p>Ban co the tiep tuc cau hinh dich vu va san sang hoat dong tren TraVi OTA.</p>"
                + "<br><p>Tran trong!</p>";
        sendEmail(email, subject, content);
    }

    @Async
    public void sendRejectionEmail(String email, String hoTen, String tenCoSo, String reason) {
        String subject = "TraVi OTA - Ho so kinh doanh bi tu choi";
        String content = "<p>Xin chao " + hoTen + ",</p>"
                + "<p>Ho so kinh doanh <b>" + tenCoSo + "</b> cua ban chua duoc phe duyet.</p>"
                + "<p>Ly do: <b>" + reason + "</b></p>"
                + "<p>Vui long cap nhat lai thong tin va gui duyet lai.</p>"
                + "<br><p>Tran trong!</p>";
        sendEmail(email, subject, content);
    }

    private void sendEmail(String email, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception ex) {
            throw new EmailDeliveryException("Khong the gui email kiem duyet ho so", ex);
        }
    }
}
