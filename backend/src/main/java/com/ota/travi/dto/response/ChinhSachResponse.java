package com.ota.travi.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ChinhSachResponse(
        String id,
        String hoSoKinhDoanhId,
        String loaiChinhSach,
        String noiDung,
        LocalDate ngayApDung,
        LocalTime gioNhanPhong,
        LocalTime gioTraPhong,
        LocalTime gioMoCua,
        LocalTime gioDongCua,
        String chinhSachHuy,
        String chinhSachHoanTien,
        String quyDinhTreEm,
        String quyDinhVatNuoi,
        String ghiChuKhac,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
