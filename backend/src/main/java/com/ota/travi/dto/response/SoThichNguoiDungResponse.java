package com.ota.travi.dto.response;

import java.time.LocalDateTime;

public record SoThichNguoiDungResponse(
        Long id,
        String userId,
        String danhMuc,
        Integer diemSo,
        LocalDateTime capNhatCuoi
) {
}
