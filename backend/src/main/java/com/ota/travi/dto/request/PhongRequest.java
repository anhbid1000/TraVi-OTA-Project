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

        @Size(max = 255, message = "Tên phòng không được vượt quá 255 ký tự")
        String tenPhong,

        @NotBlank(message = "Loại phòng không được để trống")
        @Size(max = 100, message = "Loại phòng không được vượt quá 100 ký tự")
        String loaiPhong,

        @Size(max = 1000, message = "Mô tả phòng không được vượt quá 1000 ký tự")
        String moTa,

        @NotNull(message = "Sức chứa tối đa không được để trống")
        @Min(value = 1, message = "Sức chứa tối đa phải lớn hơn hoặc bằng 1")
        Integer sucChuaToiDa,

        @Min(value = 1, message = "Số giường phải lớn hơn hoặc bằng 1")
        Integer soGiuong,

        @DecimalMin(value = "0.0", inclusive = false, message = "Diện tích phải lớn hơn 0")
        Float dienTich,

        @DecimalMin(value = "0.0", inclusive = false, message = "Giá cơ bản phải lớn hơn 0")
        Double giaCoBan,

        @Min(value = 1, message = "Số lượng phòng phải lớn hơn hoặc bằng 1")
        Integer soLuongPhong,

        TrangThaiPhong trangThai,

        Set<TienIchPhong> tienIch,

        @DecimalMin(value = "0.0", message = "Phần trăm giảm giá không được âm")
        @DecimalMax(value = "100.0", message = "Phần trăm giảm giá không được vượt quá 100")
        Float phanTramGiamGia
) {
}
