package com.ota.travi.dto.response;

import java.time.LocalTime;
import java.util.List;

import com.ota.travi.enums.TrangThaiTaiSan;

public record NhaHangResponse(
        String idTaiSan,
        String idHoSo,
        String ten,
        String moTa,
        TrangThaiTaiSan trangThai,
        Double giaCoBan,
        Boolean isDynamicPricing,
        Integer sucChua,
        String loaiAmThuc,
        LocalTime gioMoCua,
        LocalTime gioDongCua,
        Boolean coDatBanTruoc,
        Boolean coDatMonTruoc,
        List<BanResponse> danhSachBan,
        List<AnhResponse> danhSachAnh,
        List<TienIchNhaHangResponse> tienIch,
        List<ThucDonResponse> thucDon
) {
}
