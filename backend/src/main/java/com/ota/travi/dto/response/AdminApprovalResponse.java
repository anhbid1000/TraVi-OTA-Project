package com.ota.travi.dto.response;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiKiemDuyet;

import java.time.LocalDateTime;
import java.util.List;

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
        TrangThaiKiemDuyet trangThaiKiemDuyet,
        LocalDateTime thoiGianDangKy,
        LocalDateTime thoiGianDuyet,
        ChinhSachResponse chinhSach,
        List<TaiSanResponse> danhSachTaiSan
) {
}
