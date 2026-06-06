package com.ota.travi.dto.response;

import java.time.LocalDate;

public record AnhResponse(
        String id,
        String doiTuongId,
        String duongDanUrl,
        String moTaAnh,
        Boolean laAnhDaiDien,
        LocalDate ngayTaiLen
) {
}
