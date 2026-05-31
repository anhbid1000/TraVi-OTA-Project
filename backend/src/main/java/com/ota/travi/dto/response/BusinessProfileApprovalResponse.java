package com.ota.travi.dto.response;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiHoatDong;

import java.time.LocalDateTime;

public record BusinessProfileApprovalResponse(
        String idHoSo,
        String doiTacId,
        String doiTacEmail,
        String doiTacHoTen,
        String tenCoSo,
        String sdtLienHe,
        String maSoThue,
        LoaiDichVu loaiDichVu,
        TrangThaiHoatDong trangThaiHoatDong,
        String lyDoTuChoiGanNhat,
        LocalDateTime thoiGianDangKy,
        LocalDateTime thoiGianDuyet,
        ChinhSachResponse chinhSach,
        TaiSanResponse taiSan
) {
}

