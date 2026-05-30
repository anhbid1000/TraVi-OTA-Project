package com.ota.travi.dto.response;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiHoatDong;
import com.ota.travi.enums.TrangThaiKiemDuyet;

import java.time.LocalDateTime;

public record BusinessProfileResponse(
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
        TrangThaiKiemDuyet trangThaiKiemDuyet,
        TrangThaiHoatDong trangThaiHoatDong,
        String lyDoTuChoiGanNhat,
        LocalDateTime thoiGianDangKy,
        LocalDateTime thoiGianCapNhat,
        LocalDateTime thoiGianDuyet,
        ChinhSachResponse chinhSach,
        TaiSanResponse taiSan
) {
}

