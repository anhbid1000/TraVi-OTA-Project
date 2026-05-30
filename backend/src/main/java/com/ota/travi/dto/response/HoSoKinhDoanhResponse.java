package com.ota.travi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiKiemDuyet;

public record HoSoKinhDoanhResponse(
        String idHoSo,
        String doiTacId,
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
