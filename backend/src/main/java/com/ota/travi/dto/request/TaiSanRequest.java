package com.ota.travi.dto.request;

import com.ota.travi.enums.TrangThaiTaiSan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaiSanRequest(
        @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
        String moTa,

        TrangThaiTaiSan trangThai,

        @NotNull(message = "Giá cơ bản không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá cơ bản phải lớn hơn 0")
        Double giaCoBan,

        Boolean isDynamicPricing
) {
}
