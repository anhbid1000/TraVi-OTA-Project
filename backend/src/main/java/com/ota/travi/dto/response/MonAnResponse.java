package com.ota.travi.dto.response;

import java.util.List;

import com.ota.travi.enums.TrangThaiMonAn;

public record MonAnResponse(
        String id,
        String thucDonId,
        String tenMon,
        Double giaBan,
        TrangThaiMonAn trangThai,
        String duongDanUrl,
        List<String> theNguCanh
) {
}
