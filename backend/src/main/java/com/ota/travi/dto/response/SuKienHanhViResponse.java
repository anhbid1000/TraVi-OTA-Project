package com.ota.travi.dto.response;

import com.ota.travi.enums.HanhDongSuKien;
import com.ota.travi.enums.LoaiDoiTuongHanhVi;

import java.time.LocalDateTime;

public record SuKienHanhViResponse(
        Long id,
        String userId,
        HanhDongSuKien hanhDong,
        Long doiTuanId,
        LoaiDoiTuongHanhVi loaiDoiTuong,
        Integer thoiLuongXemMs,
        String metadata,
        LocalDateTime thoiGian
) {
}
