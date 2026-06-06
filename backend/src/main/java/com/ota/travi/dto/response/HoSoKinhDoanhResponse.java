package com.ota.travi.dto.response;

import java.time.LocalDateTime;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiHoatDong;

public record HoSoKinhDoanhResponse(
        String idHoSo,
        String doiTacId,
        String tenCoSo,
        String sdtLienHe,
        String emailLienHe,
        String diaChi,
        String thanhPho,
        String quanHuyen,
        String phuongXa,
        Double kinhDo,
        Double viDo,
        LoaiDichVu loaiDichVu,
        String maSoThue,
        String giayPhepKinhDoanh,
        String toaDoGPS,
        TrangThaiHoatDong trangThaiHoatDong,
        LocalDateTime thoiGianDangKy,
        LocalDateTime thoiGianCapNhat,
        ChinhSachResponse chinhSach,
        TaiSanResponse taiSan
) {
}
