package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiBan;

public record BanResponse(
        String id,
        String nhaHangId,
        String viTriSanh,
        Integer soChoNgoi,
        TrangThaiBan trangThai
) {
}
