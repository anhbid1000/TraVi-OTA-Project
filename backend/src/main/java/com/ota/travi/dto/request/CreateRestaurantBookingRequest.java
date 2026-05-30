package com.ota.travi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateRestaurantBookingRequest(
        @NotBlank(message = "restaurantId không được để trống")
        String restaurantId,

        @NotBlank(message = "tenNguoiDat không được để trống")
        String tenNguoiDat,

        @NotBlank(message = "sdtNguoiDat không được để trống")
        String sdtNguoiDat,

        @Email(message = "emailNguoiDat không hợp lệ")
        String emailNguoiDat,

        String ghiChu,

        @NotNull(message = "date không được để trống")
        @Future(message = "date phải ở tương lai")
        LocalDate date,

        @NotNull(message = "time không được để trống")
        LocalTime time,

        @NotNull(message = "soNguoi không được để trống")
        @Min(value = 1, message = "soNguoi phải lớn hơn 0")
        Integer soNguoi
) {
}
