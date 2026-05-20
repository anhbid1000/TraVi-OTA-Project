package com.ota.travi.dto.request;

import com.ota.travi.enums.TrangThaiMonAn;

import jakarta.validation.constraints.NotNull;

public record MenuItemStatusRequest(
        @NotNull(message = "Trang thai mon an khong duoc de trong")
        TrangThaiMonAn trangThai
) {
}
