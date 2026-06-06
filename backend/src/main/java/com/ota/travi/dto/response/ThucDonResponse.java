package com.ota.travi.dto.response;

import java.util.List;

public record ThucDonResponse(
        String id,
        String nhaHangId,
        String tenThucDon,
        String phanLoai,
        com.ota.travi.enums.TrangThaiThucDon trangThai,
        List<MonAnResponse> monAn,
        List<ComboResponse> combo
) {
}
