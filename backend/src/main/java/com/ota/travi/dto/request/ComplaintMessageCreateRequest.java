package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request tạo tin nhắn mới trong ticket khiếu nại.
 */
public record ComplaintMessageCreateRequest(
        @NotBlank(message = "Nội dung tin nhắn không được để trống")
        @Size(max = 2000, message = "Nội dung tối đa 2000 ký tự")
        String noiDung
) {
}
