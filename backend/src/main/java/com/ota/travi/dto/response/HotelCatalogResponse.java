package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiHoatDong;

import java.util.List;

public record HotelCatalogResponse(
        String id,
        String tenKhachSan,
        String diaChi,
        String thanhPho,
        Integer hangSao,
        String thumbnailUrl,
        Double diemDanhGiaTrungBinh,
        Integer soLuongDanhGia,
        Double giaThapNhat,
        Integer soPhongConTrong,
        List<TienIchKhachSanResponse> tienIchNoiBat,
        TrangThaiHoatDong trangThaiHoatDong
) {
}


