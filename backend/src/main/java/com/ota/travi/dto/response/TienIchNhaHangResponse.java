package com.ota.travi.dto.response;

public record TienIchNhaHangResponse(
        String id,
        String nhaHangId,
        String tenTienIch,
        String loaiTienIch,
        String moTa,
        Boolean coThuPhi,
        Float phiSuDung
) {
}
