package com.ota.travi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record ComboRequest(
        @NotBlank(message = "Tên combo không được để trống")
        @Size(max = 150, message = "Tên combo không được vượt quá 150 ký tự")
        String tenCombo,

        @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
        String moTa,

        @NotNull(message = "Giá combo không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá combo phải lớn hơn 0")
        Float giaCombo,

        @NotNull(message = "Trạng thái combo không được để trống")
        Integer trangThai,

        LocalDate ngayBatDau,

        LocalDate ngayKetThuc,

        Set<String> monAnIds
) {
}
