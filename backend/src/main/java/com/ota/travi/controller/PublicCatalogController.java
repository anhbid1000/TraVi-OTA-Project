package com.ota.travi.controller;

import com.ota.travi.dto.request.HotelSearchRequest;
import com.ota.travi.dto.request.RestaurantSearchRequest;
import com.ota.travi.dto.response.HotelCatalogResponse;
import com.ota.travi.dto.response.HotelDetailResponse;
import com.ota.travi.dto.response.RestaurantCatalogResponse;
import com.ota.travi.dto.response.RestaurantDetailResponse;
import com.ota.travi.dto.response.WeatherForecastResponse;
import com.ota.travi.service.PublicCatalogService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.ota.travi.constant.ApiEndpoints.PUBLIC_HOTEL_DETAIL;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_HOTELS_SEARCH;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_RESTAURANT_DETAIL;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_RESTAURANTS_SEARCH;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_WEATHER_FORECAST;

@RestController
public class PublicCatalogController {
    private final PublicCatalogService publicCatalogService;
    private final Validator validator;

    public PublicCatalogController(PublicCatalogService publicCatalogService, Validator validator) {
        this.publicCatalogService = publicCatalogService;
        this.validator = validator;
    }

    // --- 1. API TÌM KIẾM KHÁCH SẠN (SEARCH HOTELS) ---
    @GetMapping(PUBLIC_HOTELS_SEARCH)
    public ResponseEntity<?> searchHotels(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam Integer guests,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String stars,
            @RequestParam(required = false) String amenities,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        try {
            // 1. Xây dựng đối tượng HotelSearchRequest từ các tham số query
            HotelSearchRequest request = new HotelSearchRequest(
                    city,
                    keyword,
                    checkIn,
                    checkOut,
                    guests,
                    minPrice,
                    maxPrice,
                    parseIntegerList(stars),
                    parseStringList(amenities),
                    sort,
                    page,
                    size
            );

            // 2. Xác thực dữ liệu yêu cầu
            String error = validateRequest(request);
            if (error != null) {
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }

            // 3. Gọi service để tìm kiếm khách sạn và trả về danh sách kết quả
            Page<HotelCatalogResponse> response = publicCatalogService.searchHotels(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 2. API LẤY CHI TIẾT KHÁCH SẠN (HOTEL DETAIL) ---
    @GetMapping(PUBLIC_HOTEL_DETAIL)
    public ResponseEntity<?> getHotelDetail(
            @PathVariable("id") String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam Integer guests
    ) {
        try {
            // Gọi service để lấy chi tiết khách sạn với các thông tin phòng, giá, tiện ích
            HotelDetailResponse response = publicCatalogService.getHotelDetail(id, checkIn, checkOut, guests);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 3. API TÌM KIẾM NHÀ HÀNG (SEARCH RESTAURANTS) ---
    @GetMapping(PUBLIC_RESTAURANTS_SEARCH)
    public ResponseEntity<?> searchRestaurants(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam Integer guests,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) String amenities,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        try {
            // 1. Xây dựng đối tượng RestaurantSearchRequest từ các tham số query
            RestaurantSearchRequest request = new RestaurantSearchRequest(
                    city,
                    keyword,
                    date,
                    time,
                    guests,
                    cuisineType,
                    parseStringList(amenities),
                    minPrice,
                    maxPrice,
                    sort,
                    page,
                    size
            );

            // 2. Xác thực dữ liệu yêu cầu
            String error = validateRequest(request);
            if (error != null) {
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }

            // 3. Gọi service để tìm kiếm nhà hàng và trả về danh sách kết quả
            Page<RestaurantCatalogResponse> response = publicCatalogService.searchRestaurants(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 4. API LẤY CHI TIẾT NHÀ HÀNG (RESTAURANT DETAIL) ---
    @GetMapping(PUBLIC_RESTAURANT_DETAIL)
    public ResponseEntity<?> getRestaurantDetail(
            @PathVariable("id") String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam(required = false) Integer guests
    ) {
        try {
            // Gọi service để lấy chi tiết nhà hàng với thông tin bàn, menu, tiện ích
            RestaurantDetailResponse response = publicCatalogService.getRestaurantDetail(id, date, time, guests);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 5. API DỰ BÁO THỜI TIẾT (WEATHER FORECAST) ---
    @GetMapping(PUBLIC_WEATHER_FORECAST)
    public ResponseEntity<?> getWeatherForecast(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // Mock response to keep API contract stable until weather provider integration is added.
        boolean likelyRain = Math.abs(lat) > 10 || Math.abs(lng) > 100;
        WeatherForecastResponse response = new WeatherForecastResponse(
                date,
                likelyRain ? 27.0 : 30.0,
                likelyRain ? "MAY_CO_MUA_RAO" : "NANG_NHE",
                likelyRain ? "cloud-rain" : "sun",
                likelyRain
                        ? "Du bao co mua rao, ban nen uu tien cac hoat dong trong nha."
                        : "Thoi tiet kha dep, phu hop cho cac hoat dong ngoai troi."
        );
        return ResponseEntity.ok(response);
    }

    // --- UTILITY METHODS ---
    // Chuyển đổi chuỗi được phân tách bằng dấu phẩy thành danh sách số nguyên
    private List<Integer> parseIntegerList(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(rawValue.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Integer::valueOf)
                .toList();
    }

    // Chuyển đổi chuỗi được phân tách bằng dấu phẩy thành danh sách chuỗi
    private List<String> parseStringList(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(rawValue.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    // Xác thực đối tượng yêu cầu bằng Jakarta Validation Annotations
    private <T> String validateRequest(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (violations.isEmpty()) {
            return null;
        }
        return violations.iterator().next().getMessage();
    }
}



