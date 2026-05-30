package com.ota.travi.dto.response;

import java.util.List;

public record ThucDonResponse(
        String id,
        String nhaHangId,
        String phanLoai,
        List<MonAnResponse> monAn,
        List<ComboResponse> combo
) {
}
