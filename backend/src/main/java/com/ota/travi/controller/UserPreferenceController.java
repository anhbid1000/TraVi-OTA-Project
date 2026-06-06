package com.ota.travi.controller;

import com.ota.travi.dto.request.UpdatePreferencesRequest;
import com.ota.travi.dto.response.DanhMucSoThichResponse;
import com.ota.travi.dto.response.SoThichResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.UserPreferenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ota.travi.constant.ApiEndpoints.USER_PREFERENCES;
import static com.ota.travi.constant.ApiEndpoints.USER_PREFERENCES_CATEGORIES;

@RestController
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    public UserPreferenceController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    @GetMapping(USER_PREFERENCES_CATEGORIES)
    public ResponseEntity<List<DanhMucSoThichResponse>> getCategories() {
        return ResponseEntity.ok(userPreferenceService.getAllCategories());
    }

    @GetMapping(USER_PREFERENCES)
    public ResponseEntity<List<SoThichResponse>> getUserPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(userPreferenceService.getUserPreferences(userDetails.getUsername()));
    }

    @PutMapping(USER_PREFERENCES)
    public ResponseEntity<?> updateUserPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdatePreferencesRequest request
    ) {
        try {
            List<SoThichResponse> updated = userPreferenceService.updateUserPreferences(
                    userDetails.getUsername(), request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
