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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.ota.travi.constant.ApiEndpoints.PUBLIC_HOTEL_DETAIL;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_HOTELS_FEATURED;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_HOTELS_FILTER_OPTIONS;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_HOTELS_SEARCH;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_CITIES;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_RESTAURANT_DETAIL;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_RESTAURANTS_FEATURED;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_RESTAURANTS_FILTER_OPTIONS;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_RESTAURANTS_SEARCH;
import static com.ota.travi.constant.ApiEndpoints.PUBLIC_WEATHER_FORECAST;

/**
 * Controller phục vụ dữ liệu catalog công khai (Public Catalog) cho khách vãng lai và thành viên.
 **/
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
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) Integer guests,
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
            if (city == null || city.isBlank()) {
                return new ResponseEntity<>("Vui lòng chọn thành phố.", HttpStatus.BAD_REQUEST);
            }
            if (checkIn == null || checkOut == null) {
                return new ResponseEntity<>("Vui lòng chọn ngày nhận và trả phòng.", HttpStatus.BAD_REQUEST);
            }
            if (guests == null || guests < 1) {
                return new ResponseEntity<>("Số khách phải lớn hơn hoặc bằng 1.", HttpStatus.BAD_REQUEST);
            }

        // 1. Kiểm tra tham số cơ bản
            if (page != null && page < 0) {
                return new ResponseEntity<>("Trang phải lớn hơn hoặc bằng 0.", HttpStatus.BAD_REQUEST);
            }
            if (size != null && (size < 1 || size > 50)) {
                return new ResponseEntity<>("Số lượng kết quả mỗi trang phải từ 1 đến 50.", HttpStatus.BAD_REQUEST);
            }
            if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
                return new ResponseEntity<>("Giá tối thiểu không hợp lệ.", HttpStatus.BAD_REQUEST);
            }
            if (maxPrice != null && minPrice != null && maxPrice.compareTo(minPrice) < 0) {
                return new ResponseEntity<>("Giá tối đa không được nhỏ hơn giá tối thiểu.", HttpStatus.BAD_REQUEST);
            }
            if (stars != null && !stars.isBlank()) {
                List<Integer> starList = parseIntegerList(stars);
                for (Integer star : starList) {
                    if (star < 1 || star > 5) {
                        return new ResponseEntity<>("Hạng sao phải từ 1 đến 5.", HttpStatus.BAD_REQUEST);
                    }
                }
            }
            // Validate sort parameter
            if (sort != null && !sort.isBlank()) {
                List<String> validSorts = List.of("popular_desc", "price_asc", "price_desc", "rating_desc");
                if (!validSorts.contains(sort)) {
                    return new ResponseEntity<>("Tham số sắp xếp không hợp lệ. Các giá trị cho phép: " + String.join(", ", validSorts), HttpStatus.BAD_REQUEST);
                }
            }

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

    @GetMapping(PUBLIC_HOTELS_FEATURED)
    public ResponseEntity<?> getFeaturedHotels(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) Integer size
    ) {
        try {
            LocalDate resolvedCheckIn = checkIn == null ? LocalDate.now().plusDays(1) : checkIn;
            LocalDate resolvedCheckOut = checkOut == null ? resolvedCheckIn.plusDays(1) : checkOut;
            int resolvedGuests = guests == null || guests < 1 ? 2 : guests;

            if (!resolvedCheckOut.isAfter(resolvedCheckIn)) {
                resolvedCheckOut = resolvedCheckIn.plusDays(1);
            }

            return ResponseEntity.ok(publicCatalogService.getFeaturedHotels(resolvedCheckIn, resolvedCheckOut, resolvedGuests, size));
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- 3. API TÌM KIẾM NHÀ HÀNG (SEARCH RESTAURANTS) ---
    @GetMapping(PUBLIC_RESTAURANTS_SEARCH)
    public ResponseEntity<?> searchRestaurants(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) String amenities,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        try {
            if (city == null || city.isBlank()) {
                return new ResponseEntity<>("Vui lòng chọn thành phố.", HttpStatus.BAD_REQUEST);
            }
            if (date == null || time == null) {
                return new ResponseEntity<>("Vui lòng chọn ngày và giờ dùng bữa.", HttpStatus.BAD_REQUEST);
            }

            LocalDateTime reservationDateTime = LocalDateTime.of(date, time);
            if (reservationDateTime.isBefore(LocalDateTime.now())) {
                return new ResponseEntity<>("Ngày giờ đặt chỗ không được ở quá khứ.", HttpStatus.BAD_REQUEST);
            }

            if (guests == null || guests < 1) {
                return new ResponseEntity<>("Số khách phải lớn hơn hoặc bằng 1.", HttpStatus.BAD_REQUEST);
            }

            // Validate paging/price/sort
            if (page != null && page < 0) {
                return new ResponseEntity<>("Trang phải lớn hơn hoặc bằng 0.", HttpStatus.BAD_REQUEST);
            }
            if (size != null && (size < 1 || size > 50)) {
                return new ResponseEntity<>("Số lượng kết quả mỗi trang phải từ 1 đến 50.", HttpStatus.BAD_REQUEST);
            }
            if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
                return new ResponseEntity<>("Giá tối thiểu không hợp lệ.", HttpStatus.BAD_REQUEST);
            }
            if (maxPrice != null && minPrice != null && maxPrice.compareTo(minPrice) < 0) {
                return new ResponseEntity<>("Giá tối đa không được nhỏ hơn giá tối thiểu.", HttpStatus.BAD_REQUEST);
            }
            if (sort != null && !sort.isBlank()) {
                List<String> validSorts = List.of("popular_desc", "price_asc", "price_desc", "rating_desc");
                if (!validSorts.contains(sort)) {
                    return new ResponseEntity<>("Tham số sắp xếp không hợp lệ. Các giá trị cho phép: " + String.join(", ", validSorts), HttpStatus.BAD_REQUEST);
                }
            }

            // Build request
            RestaurantSearchRequest request = new RestaurantSearchRequest(
                    city,
                    keyword,
                    date,
                    time,
                    guests,
                    parseStringList(cuisine != null ? cuisine : cuisineType),
                    parseStringList(amenities),
                    minPrice,
                    maxPrice,
                    sort,
                    page,
                    size
            );

            // Validate using Jakarta Validator on DTO
            String error = validateRequest(request);
            if (error != null) {
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }

            // Call service
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

    @GetMapping(PUBLIC_RESTAURANTS_FEATURED)
    public ResponseEntity<?> getFeaturedRestaurants(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) Integer size
    ) {
        try {
            int resolvedGuests = guests == null || guests < 1 ? 2 : guests;
            LocalDate resolvedDate = date == null ? LocalDate.now().plusDays(1) : date;
            LocalTime resolvedTime = time == null ? LocalTime.of(19, 0) : time;

            LocalDateTime reservationDateTime = LocalDateTime.of(resolvedDate, resolvedTime);
            if (reservationDateTime.isBefore(LocalDateTime.now())) {
                reservationDateTime = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0);
            }

            return ResponseEntity.ok(publicCatalogService.getFeaturedRestaurants(
                    reservationDateTime.toLocalDate(),
                    reservationDateTime.toLocalTime(),
                    resolvedGuests,
                    size
            ));
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(PUBLIC_CITIES)
    public ResponseEntity<List<String>> getPublicCities() {
        return ResponseEntity.ok(publicCatalogService.getPublicCities());
    }

    @GetMapping(PUBLIC_HOTELS_FILTER_OPTIONS)
    public ResponseEntity<?> getHotelFilterOptions() {
        try {
            return ResponseEntity.ok(publicCatalogService.getHotelFilterOptions());
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(PUBLIC_RESTAURANTS_FILTER_OPTIONS)
    public ResponseEntity<?> getRestaurantFilterOptions() {
        try {
            return ResponseEntity.ok(publicCatalogService.getRestaurantFilterOptions());
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



