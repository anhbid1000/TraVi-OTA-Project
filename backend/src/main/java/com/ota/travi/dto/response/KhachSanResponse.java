package com.ota.travi.dto.response;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import com.ota.travi.enums.TrangThaiTaiSan;

public record KhachSanResponse(
        String idTaiSan,
        String idHoSo,
        String ten,
        String moTa,
        TrangThaiTaiSan trangThai,
        Double giaCoBan,
        Boolean isDynamicPricing,
        Integer hangSao,
        LocalTime gioNhanPhong,
        LocalTime gioTraPhong,
        List<PhongResponse> danhSachPhong,
        List<AnhResponse> danhSachAnh,
        Set<TienIchKhachSanResponse> tienIch
) {
}
