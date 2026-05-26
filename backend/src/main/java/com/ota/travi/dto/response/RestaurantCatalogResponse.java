package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiHoatDong;

import java.util.List;

public record RestaurantCatalogResponse(
        String id,
        String tenNhaHang,
        String diaChi,
        String thanhPho,
        String loaiAmThuc,
        String thumbnailUrl,
        Double diemDanhGiaTrungBinh,
        Integer soLuongDanhGia,
        String khoangGia,
        Integer soGheConTrong,
        List<TienIchNhaHangResponse> tienIchNoiBat,
        TrangThaiHoatDong trangThaiHoatDong
) {
}


