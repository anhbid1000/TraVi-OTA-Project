package com.ota.travi.dto.request;

import com.ota.travi.enums.TrangThaiBan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BanRequest(
        @Size(max = 255, message = "Tên bàn không được vượt quá 255 ký tự")
        String tenBan,

        @Size(max = 100, message = "Vị trí sảnh không được vượt quá 100 ký tự")
        String viTriSanh,

        @Size(max = 1000, message = "Mô tả bàn không được vượt quá 1000 ký tự")
        String moTa,

        @NotNull(message = "Số chỗ ngồi không được để trống")
        @Min(value = 1, message = "Số chỗ ngồi phải lớn hơn hoặc bằng 1")
        Integer soChoNgoi,

        TrangThaiBan trangThai
) {
}
