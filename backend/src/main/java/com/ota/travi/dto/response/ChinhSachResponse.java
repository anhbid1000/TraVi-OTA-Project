package com.ota.travi.dto.response;

import java.time.LocalDate;

public record ChinhSachResponse(
        String id,
        String hoSoKinhDoanhId,
        String loaiChinhSach,
        String noiDung,
        LocalDate ngayApDung
) {
}
