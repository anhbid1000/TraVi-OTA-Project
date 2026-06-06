package com.ota.travi.controller;

import com.ota.travi.dto.response.ThongBaoNguCanhResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.SmartNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ota.travi.constant.ApiEndpoints.USER_NOTIFICATIONS;

@RestController
public class SmartNotificationController {

    private final SmartNotificationService smartNotificationService;

    public SmartNotificationController(SmartNotificationService smartNotificationService) {
        this.smartNotificationService = smartNotificationService;
    }

    @GetMapping(USER_NOTIFICATIONS)
    public ResponseEntity<?> getSmartNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            List<ThongBaoNguCanhResponse> response = smartNotificationService.generateSmartNotifications(userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi tải thông báo ngữ cảnh: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
