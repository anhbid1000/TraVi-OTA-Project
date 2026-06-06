package com.ota.travi.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record HotelSearchRequest(
        @NotBlank(message = "Thanh pho khong duoc de trong")
        String city,

        String keyword,

        @NotNull(message = "Ngay nhan phong khong duoc de trong")
        LocalDate checkIn,

        @NotNull(message = "Ngay tra phong khong duoc de trong")
        LocalDate checkOut,

        @NotNull(message = "So khach khong duoc de trong")
        @Min(value = 1, message = "So khach phai lon hon hoac bang 1")
        Integer guests,

        @DecimalMin(value = "0.0", message = "Gia toi thieu khong hop le")
        BigDecimal minPrice,

        @DecimalMin(value = "0.0", message = "Gia toi da khong hop le")
        BigDecimal maxPrice,

        List<Integer> stars,

        List<String> amenities,

        String sort,

        @Min(value = 0, message = "Page phai lon hon hoac bang 0")
        Integer page,

        @Min(value = 1, message = "Size phai lon hon hoac bang 1")
        @Max(value = 50, message = "Size toi da la 50")
        Integer size
) {
    @AssertTrue(message = "Ngay tra phong phai sau ngay nhan phong")
    public boolean isDateRangeValid() {
        if (checkIn == null || checkOut == null) {
            return true;
        }
        return checkOut.isAfter(checkIn);
    }

    @AssertTrue(message = "Gia toi thieu khong duoc lon hon gia toi da")
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) {
            return true;
        }
        return minPrice.compareTo(maxPrice) <= 0;
    }
}

