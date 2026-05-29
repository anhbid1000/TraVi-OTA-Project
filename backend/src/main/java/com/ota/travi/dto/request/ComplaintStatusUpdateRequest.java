package com.ota.travi.dto.request;

import com.ota.travi.enums.TrangThaiKhieuNai;
import jakarta.validation.constraints.NotNull;

/**
 * Request cập nhật trạng thái ticket khiếu nại từ phía đối tác.
 */
public record ComplaintStatusUpdateRequest(
        @NotNull(message = "Trạng thái không được để trống")
        TrangThaiKhieuNai status
) {
}

