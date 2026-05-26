package com.ota.travi.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record RestaurantSearchRequest(
        @NotBlank(message = "Thanh pho khong duoc de trong")
        String city,

        String keyword,

        @NotNull(message = "Ngay dung bua khong duoc de trong")
        LocalDate date,

        @NotNull(message = "Gio dung bua khong duoc de trong")
        LocalTime time,

        @NotNull(message = "So khach khong duoc de trong")
        @Min(value = 1, message = "So khach phai lon hon hoac bang 1")
        Integer guests,

        String cuisineType,

        List<String> amenities,

        @DecimalMin(value = "0.0", message = "Gia toi thieu khong hop le")
        BigDecimal minPrice,

        @DecimalMin(value = "0.0", message = "Gia toi da khong hop le")
        BigDecimal maxPrice,

        String sort,

        @Min(value = 0, message = "Page phai lon hon hoac bang 0")
        Integer page,

        @Min(value = 1, message = "Size phai lon hon hoac bang 1")
        @Max(value = 50, message = "Size toi da la 50")
        Integer size
) {
    @AssertTrue(message = "Ngay gio dung bua khong duoc o qua khu")
    public boolean isReservationDateTimeValid() {
        if (date == null || time == null) {
            return true;
        }
        return !LocalDateTime.of(date, time).isBefore(LocalDateTime.now());
    }

    @AssertTrue(message = "Gia toi thieu khong duoc lon hon gia toi da")
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) {
            return true;
        }
        return minPrice.compareTo(maxPrice) <= 0;
    }
}

