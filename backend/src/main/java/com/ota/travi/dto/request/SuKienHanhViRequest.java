package com.ota.travi.dto.request;

import com.ota.travi.enums.HanhDongSuKien;
import com.ota.travi.enums.LoaiDoiTuongHanhVi;
import jakarta.validation.constraints.NotNull;

public record SuKienHanhViRequest(
        @NotNull(message = "Hành động không được để trống")
        HanhDongSuKien hanhDong,

        @NotNull(message = "ID đối tượng không được để trống")
        Long doiTuanId,

        @NotNull(message = "Loại đối tượng không được để trống")
        LoaiDoiTuongHanhVi loaiDoiTuong,

        Integer thoiLuongXemMs,

        String metadata
) {
}
