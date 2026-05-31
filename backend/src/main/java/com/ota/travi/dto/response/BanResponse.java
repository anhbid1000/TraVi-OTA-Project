package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiBan;

public record BanResponse(
        String id,
        String nhaHangId,
        String tenBan,
        String viTriSanh,
        String moTa,
        Integer soChoNgoi,
        TrangThaiBan trangThai
) {
}
