package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiDon;

import java.time.LocalDateTime;
import java.util.List;

public record BookingSearchResponse(
        String id,
        String maDon,
        String tenTaiSan,
        String anhTaiSan,
        LocalDateTime ngayTao,
        Double tongTienThanhToan,
        TrangThaiDon trangThai,
        String loaiTaiSan, // "HOTEL" or "RESTAURANT"
        LocalDateTime ngayBatDau,
        LocalDateTime ngayKetThuc,
        String tenNguoiDat,
        String sdtNguoiDat,
        String emailNguoiDat,
        Integer soKhach,
        List<RoomInfo> rooms,
        List<TableInfo> tables
) {
    public record RoomInfo(
            String tenPhong,
            Integer soLuong,
            Double donGia,
            Double thanhTien
    ) {}

    public record TableInfo(
            String tenBan,
            Integer soChoNgoi,
            String viTri
    ) {}
}
