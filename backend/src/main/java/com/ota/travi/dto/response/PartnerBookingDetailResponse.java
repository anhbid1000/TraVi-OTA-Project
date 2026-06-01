package com.ota.travi.dto.response;

import com.ota.travi.enums.TrangThaiDon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record PartnerBookingDetailResponse(
        String id,
        String maDon,
        String serviceType,
        TrangThaiDon trangThai,
        LocalDateTime ngayTao,
        LocalDateTime paymentExpiredAt,
        LocalDateTime cancelledAt,
        String cancelReason,
        String tenTaiSan,
        String diaChiTaiSan,
        String tenNguoiDat,
        String sdtNguoiDat,
        String emailNguoiDat,
        String ghiChu,
        Integer soKhach,
        Double tongTienGoc,
        Double tienKhuyenMai,
        Double tongTienThanhToan,
        Double tienCoc,
        LocalDate ngayCheckIn,
        LocalDate ngayCheckOut,
        LocalTime gioNhanPhongDuKien,
        LocalDateTime ngayGioBatDau,
        LocalDateTime ngayGioKetThuc,
        List<RoomInfo> rooms,
        List<TableInfo> tables,
        List<String> allowedActions
) {
    public record RoomInfo(
            String roomId,
            String tenPhong,
            Integer soLuong,
            Double donGia,
            Integer soDem,
            Double thanhTien
    ) {
    }

    public record TableInfo(
            String tableId,
            String tenBan,
            Integer soChoNgoi,
            String viTriSanh
    ) {
    }
}
