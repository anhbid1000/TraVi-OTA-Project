package com.ota.travi.dto.response;

public record BanResponse(
        String id,
        String nhaHangId,
        String viTriSanh,
        Integer soChoNgoi,
        Integer trangThai
) {
}
