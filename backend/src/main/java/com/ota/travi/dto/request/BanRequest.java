package com.ota.travi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BanRequest(
        @Size(max = 100, message = "Vị trí sảnh không được vượt quá 100 ký tự")
        String viTriSanh,

        @NotNull(message = "Số chỗ ngồi không được để trống")
        @Min(value = 1, message = "Số chỗ ngồi phải lớn hơn hoặc bằng 1")
        Integer soChoNgoi,

        @Min(value = 0, message = "Trạng thái không hợp lệ")
        Integer trangThai
) {
}
