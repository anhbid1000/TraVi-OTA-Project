package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiMonAn;

public record MenuItemResponse(
        String id,
        String tenMon,
        String moTa,
        Double gia,
        String danhMucMon,
        String anhMonUrl,
        TrangThaiMonAn trangThai
) {
}


