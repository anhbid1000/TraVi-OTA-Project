package com.ota.travi.dto.response;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiHoatDong;

import java.time.LocalDateTime;

public record AdminApprovalResponse(
        String idHoSo,
        String doiTacId,
        String doiTacEmail,
        String doiTacHoTen,
        String tenCoSo,
        String sdtLienHe,
        LoaiDichVu loaiDichVu,
        String maSoThue,
        String giayPhepKinhDoanh,
        String toaDoGPS,
        TrangThaiHoatDong trangThaiHoatDong,
        LocalDateTime thoiGianDangKy,
        LocalDateTime thoiGianDuyet,
        ChinhSachResponse chinhSach,
        TaiSanResponse taiSan,
        ApprovalComparisonResponse comparison
) {
}
