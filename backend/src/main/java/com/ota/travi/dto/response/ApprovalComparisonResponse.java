package com.ota.travi.dto.response;

public record ApprovalComparisonResponse(
        String oldTenCoSo,
        String oldSdtLienHe,
        String oldLoaiDichVu,
        String oldMaSoThue,
        String oldGiayPhepKinhDoanh,
        String oldToaDoGPS,
        String newTenCoSo,
        String newSdtLienHe,
        String newLoaiDichVu,
        String newMaSoThue,
        String newGiayPhepKinhDoanh,
        String newToaDoGPS
) {
}
