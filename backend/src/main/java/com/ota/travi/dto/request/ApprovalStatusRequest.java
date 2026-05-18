package com.ota.travi.dto.request;

import com.ota.travi.enums.TrangThaiKiemDuyet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApprovalStatusRequest(
        @NotNull(message = "Trạng thái kiểm duyệt không được để trống")
        TrangThaiKiemDuyet trangThaiKiemDuyet,

        @Size(max = 500, message = "Lý do không được vượt quá 500 ký tự")
        String lyDo
) {
}
