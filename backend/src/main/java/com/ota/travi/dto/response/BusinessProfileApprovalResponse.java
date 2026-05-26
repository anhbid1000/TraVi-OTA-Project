package com.ota.travi.dto.response;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiHoatDong;
import com.ota.travi.enums.TrangThaiKiemDuyet;

import java.time.LocalDateTime;
import java.util.List;

public record BusinessProfileApprovalResponse(
        String idHoSo,
        String doiTacId,
        String doiTacEmail,
        String doiTacHoTen,
        String tenCoSo,
        String sdtLienHe,
        String maSoThue,
        LoaiDichVu loaiDichVu,
        TrangThaiKiemDuyet trangThaiKiemDuyet,
        TrangThaiHoatDong trangThaiHoatDong,
        String lyDoTuChoiGanNhat,
        LocalDateTime thoiGianDangKy,
        LocalDateTime thoiGianDuyet,
        ChinhSachResponse chinhSach,
        List<TaiSanResponse> danhSachTaiSan
) {
}

