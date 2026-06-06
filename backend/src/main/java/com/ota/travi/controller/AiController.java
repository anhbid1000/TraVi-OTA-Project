package com.ota.travi.controller;

import com.ota.travi.dto.request.AiRecommendationFeedbackRequest;
import com.ota.travi.dto.request.AiUserEventRequest;
import com.ota.travi.dto.response.UserAiRecommendationResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.AiRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ota.travi.constant.ApiEndpoints.USER_RECOMMENDATION_FEEDBACK;
import static com.ota.travi.constant.ApiEndpoints.USER_RECOMMENDATIONS;
import static com.ota.travi.constant.ApiEndpoints.USER_AI_EVENTS;

@RestController
public class AiController {

    private final AiRecommendationService aiRecommendationService;

    public AiController(AiRecommendationService aiRecommendationService) {
        this.aiRecommendationService = aiRecommendationService;
    }

    @GetMapping(USER_RECOMMENDATIONS)
    public ResponseEntity<?> recommendForUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer limit
    ) {
        try {
            UserAiRecommendationResponse response = aiRecommendationService.recommendForUser(
                    userDetails.getUsername(),
                    type,
                    city,
                    limit
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Loi khi tao goi y AI: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(USER_RECOMMENDATION_FEEDBACK)
    public ResponseEntity<?> captureRecommendationFeedback(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AiRecommendationFeedbackRequest request
    ) {
        try {
            aiRecommendationService.captureFeedback(userDetails.getUsername(), request);
            return new ResponseEntity<>("Da ghi nhan phan hoi goi y AI", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Loi khi ghi nhan phan hoi AI: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(USER_AI_EVENTS)
    public ResponseEntity<?> captureUserEvent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AiUserEventRequest request
    ) {
        try {
            aiRecommendationService.captureUserEvent(userDetails.getUsername(), request);
            return new ResponseEntity<>("Da cap nhat tin hieu AI", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Loi khi cap nhat tin hieu AI: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
