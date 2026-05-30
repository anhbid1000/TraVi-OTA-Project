package com.ota.travi.dto.response;

import java.util.List;
import java.util.Set;

import com.ota.travi.enums.TienIchPhong;
import com.ota.travi.enums.TrangThaiPhong;

public record PhongResponse(
        String id,
        String khachSanId,
        String soPhong,
        String loaiPhong,
        Integer sucChuaToiDa,
        Float dienTich,
        TrangThaiPhong trangThai,
        Set<TienIchPhong> tienIch,
        Float phanTramGiamGia,
        List<AnhResponse> danhSachAnh
) {
}
