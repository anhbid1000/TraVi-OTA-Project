package com.ota.travi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.ota.travi.enums.TrangThaiMonAn;

public record MonAnRequest(
        @NotBlank(message = "Tên món không được để trống")
        @Size(max = 150, message = "Tên món không được vượt quá 150 ký tự")
        String tenMon,

        @Size(max = 1000, message = "Mô tả món không được vượt quá 1000 ký tự")
        String moTa,

        @NotNull(message = "Giá bán không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
        Double giaBan,

        @Size(max = 100, message = "Danh mục món không được vượt quá 100 ký tự")
        String danhMucMon,

        @Size(max = 500, message = "Đường dẫn ảnh không được vượt quá 500 ký tự")
        String duongDanUrl,

        TrangThaiMonAn trangThai,

        String thucDonId,

        List<String> theNguCanh
) {
}
