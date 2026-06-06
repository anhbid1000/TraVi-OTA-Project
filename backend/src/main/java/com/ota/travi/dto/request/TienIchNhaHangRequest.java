package com.ota.travi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TienIchNhaHangRequest(
        @NotBlank(message = "Tên tiện ích không được để trống")
        @Size(max = 150, message = "Tên tiện ích không được vượt quá 150 ký tự")
        String tenTienIch,

        @Size(max = 100, message = "Loại tiện ích không được vượt quá 100 ký tự")
        String loaiTienIch,

        @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
        String moTa,

        Boolean coThuPhi,

        @DecimalMin(value = "0.0", message = "Phí sử dụng không được âm")
        Float phiSuDung
) {
}
