package com.ota.travi.dto.response;

import java.time.LocalTime;
import java.util.List;

public record RestaurantDetailResponse(
        String id,
        String businessProfileId,
        String tenNhaHang,
        String moTa,
        String diaChi,
        String thanhPho,
        String quanHuyen,
        String phuongXa,
        Double kinhDo,
        Double viDo,
        String loaiAmThuc,
        LocalTime gioMoCua,
        LocalTime gioDongCua,
        Boolean coDatBanTruoc,
        Boolean coDatMonTruoc,
        Double diemDanhGiaTrungBinh,
        Integer soLuongDanhGia,
        List<AnhResponse> images,
        List<TienIchNhaHangResponse> amenities,
        List<TableAvailabilityResponse> availableTables,
        List<MenuResponse> menus
) {
}


