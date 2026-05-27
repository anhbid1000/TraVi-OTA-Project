package com.ota.travi.dto.response;

import com.ota.travi.enums.CreatedByRole;
import com.ota.travi.enums.LoaiGiamGia;
import com.ota.travi.enums.PhamViApDung;
import com.ota.travi.enums.TargetType;
import com.ota.travi.enums.TrangThaiUuDai;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionResponse(
        Long id,
        String tenUuDai,
        String moTa,
        String createdByUserId,
        CreatedByRole createdByRole,
        String businessProfileId,
        BigDecimal mucGiam,
        LoaiGiamGia loaiGiamGia,
        BigDecimal giaTriGiamToiDa,
        LocalDateTime ngayBatDau,
        LocalDateTime ngayKetThuc,
        TrangThaiUuDai trangThaiUuDai,
        Boolean deleted,
        String promotionType,
        String maVoucher,
        Integer soLuongPhatHanh,
        Integer soLuongDaDung,
        BigDecimal donHangToiThieu,
        Integer usageLimitPerUser,
        Integer diemCanDoi,
        Boolean choPhepDoiBangDiem,
        PhamViApDung phamViApDung,
        TargetType targetType,
        String targetId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
