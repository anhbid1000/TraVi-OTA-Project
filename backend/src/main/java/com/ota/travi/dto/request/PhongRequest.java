package com.ota.travi.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

import com.ota.travi.enums.TienIchPhong;
import com.ota.travi.enums.TrangThaiPhong;

public record PhongRequest(
        @NotBlank(message = "Số phòng không được để trống")
        @Size(max = 50, message = "Số phòng không được vượt quá 50 ký tự")
        String soPhong,

        @NotBlank(message = "Loại phòng không được để trống")
        @Size(max = 100, message = "Loại phòng không được vượt quá 100 ký tự")
        String loaiPhong,

        @NotNull(message = "Sức chứa tối đa không được để trống")
        @Min(value = 1, message = "Sức chứa tối đa phải lớn hơn hoặc bằng 1")
        Integer sucChuaToiDa,

        @DecimalMin(value = "0.0", inclusive = false, message = "Diện tích phải lớn hơn 0")
        Float dienTich,

        TrangThaiPhong trangThai,

        Set<TienIchPhong> tienIch,

        @DecimalMin(value = "0.0", message = "Phần trăm giảm giá không được âm")
        @DecimalMax(value = "100.0", message = "Phần trăm giảm giá không được vượt quá 100")
        Float phanTramGiamGia
) {
}
