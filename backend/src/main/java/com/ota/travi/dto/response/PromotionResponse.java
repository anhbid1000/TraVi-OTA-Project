package com.ota.travi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PromotionResponse(
        Long id,
        String tenUuDai,
        String moTa,
        String createdByUserId,
        String createdByRole,
        String businessProfileId,
        Double mucGiam,
        String loaiGiamGia,
        Double giaTriGiamToiDa,
        LocalDateTime ngayBatDau,
        LocalDateTime ngayKetThuc,
        String trangThaiUuDai,
        Boolean deleted,
        String loaiUuDai,
        String maVoucher,
        Integer soLuongPhatHanh,
        Integer soLuongDaDung,
        Double donHangToiThieu,
        Integer usageLimitPerUser,
        Integer diemCanDoi,
        Boolean choPhepDoiBangDiem,
        String phamViApDung,
        String targetType,
        Long targetId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
