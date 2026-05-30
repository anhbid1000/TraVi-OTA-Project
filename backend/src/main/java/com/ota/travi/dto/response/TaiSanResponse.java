package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiTaiSan;

public record TaiSanResponse(
        String idTaiSan,
        String idHoSo,
        String moTa,
        TrangThaiTaiSan trangThai,
        Double giaCoBan,
        Boolean isDynamicPricing
) {
}
