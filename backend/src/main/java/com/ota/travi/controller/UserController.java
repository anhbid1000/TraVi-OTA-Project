package com.ota.travi.controller;

import com.ota.travi.dto.request.ConfirmHotelBookingPaymentRequest;
import com.ota.travi.dto.request.CreateHotelBookingRequest;
import com.ota.travi.dto.request.CreateRestaurantBookingRequest;
import com.ota.travi.dto.response.HotelBookingResponse;
import com.ota.travi.dto.response.RestaurantBookingResponse;
import com.ota.travi.dto.response.UserProfileResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.ota.travi.constant.ApiEndpoints.USER_BOOKINGS;
import static com.ota.travi.constant.ApiEndpoints.USER_ME;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(USER_ME)
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            UserProfileResponse profile = userService.getUserProfile(username);
            return new ResponseEntity<>(profile, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi lấy thông tin người dùng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(USER_BOOKINGS + "/hotel")
    public ResponseEntity<?> createHotelBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateHotelBookingRequest request
    ) {
        try {
            String username = userDetails.getUsername();
            HotelBookingResponse booking = userService.createHotelBooking(username, request);
            return new ResponseEntity<>(booking, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi tạo đơn đặt khách sạn: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(USER_BOOKINGS + "/restaurant")
    public ResponseEntity<?> createRestaurantBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateRestaurantBookingRequest request
    ) {
        try {
            String username = userDetails.getUsername();
            RestaurantBookingResponse booking = userService.createRestaurantBooking(username, request);
            return new ResponseEntity<>(booking, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi tạo đơn đặt nhà hàng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(USER_BOOKINGS + "/restaurant/mock-pay")
    public ResponseEntity<?> mockPayRestaurantBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ConfirmHotelBookingPaymentRequest request
    ) {
        try {
            String username = userDetails.getUsername();
            RestaurantBookingResponse booking = userService.mockPayRestaurantBooking(username, request.bookingId());
            return new ResponseEntity<>(booking, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi thanh toán giả lập nhà hàng: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(USER_BOOKINGS + "/hotel/mock-pay")
    public ResponseEntity<?> mockPayHotelBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ConfirmHotelBookingPaymentRequest request
    ) {
        try {
            String username = userDetails.getUsername();
            HotelBookingResponse booking = userService.mockPayHotelBooking(username, request.bookingId());
            return new ResponseEntity<>(booking, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi thanh toán giả lập: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(USER_BOOKINGS + "/hotel/confirm-payment")
    public ResponseEntity<?> confirmHotelBookingPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ConfirmHotelBookingPaymentRequest request
    ) {
        try {
            String username = userDetails.getUsername();
            HotelBookingResponse booking = userService.confirmHotelBookingPayment(username, request.bookingId());
            return new ResponseEntity<>(booking, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi xác nhận thanh toán: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
