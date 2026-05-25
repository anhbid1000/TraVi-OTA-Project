package com.ota.travi.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    @Value("${GOOGLE_CLIENT_ID:}")
    private String googleClientId;

    public GoogleUserInfo verifyIdToken(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new RuntimeException("Google ID token không được để trống");
        }

        if (googleClientId == null || googleClientId.isBlank()) {
            throw new RuntimeException("Đăng nhập Google chưa được cấu hình trên máy chủ");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new RuntimeException("Google ID token không hợp lệ");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            Boolean emailVerified = payload.getEmailVerified();
            String hoTen = (String) payload.get("name");

            if (email == null || email.isBlank()) {
                throw new RuntimeException("Không thể lấy email từ tài khoản Google");
            }

            if (!Boolean.TRUE.equals(emailVerified)) {
                throw new RuntimeException("Tài khoản Google chưa xác thực email");
            }

            return new GoogleUserInfo(email.trim().toLowerCase(), hoTen);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Không thể xác minh tài khoản Google lúc này", ex);
        }
    }

    public record GoogleUserInfo(String email, String hoTen) {
    }
}

