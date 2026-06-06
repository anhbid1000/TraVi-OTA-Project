package com.ota.travi.dto.response;

import java.time.LocalDate;
import java.util.Set;

public record ComboResponse(
        String id,
        String thucDonId,
        String tenCombo,
        String moTa,
        Float giaCombo,
        Integer trangThai,
        LocalDate ngayBatDau,
        LocalDate ngayKetThuc,
        Set<String> monAnIds
) {
}
