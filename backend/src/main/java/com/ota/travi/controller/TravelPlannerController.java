package com.ota.travi.controller;

import com.ota.travi.dto.request.ItineraryRequest;
import com.ota.travi.dto.response.ItineraryResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.TravelPlannerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.ota.travi.constant.ApiEndpoints.USER_ITINERARY_PLANNER;

@RestController
public class TravelPlannerController {

    private final TravelPlannerService travelPlannerService;

    public TravelPlannerController(TravelPlannerService travelPlannerService) {
        this.travelPlannerService = travelPlannerService;
    }

    @PostMapping(USER_ITINERARY_PLANNER)
    public ResponseEntity<?> generateItinerary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ItineraryRequest request
    ) {
        try {
            ItineraryResponse response = travelPlannerService.generateItinerary(userDetails.getUsername(), request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi tạo lịch trình: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
