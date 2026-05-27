package com.ota.travi.dto.request;

import com.ota.travi.enums.LoaiGiamGia;
import com.ota.travi.enums.TargetType;
import com.ota.travi.enums.TrangThaiUuDai;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionUpdateRequest(
        String tenUuDai,
        String moTa,
        String businessProfileId,

        @DecimalMin(value = "0.0", inclusive = false, message = "Mức giảm phải lớn hơn 0")
        BigDecimal mucGiam,

        LoaiGiamGia loaiGiamGia,

        @Positive(message = "Giá trị giảm tối đa phải lớn hơn 0")
        BigDecimal giaTriGiamToiDa,

        LocalDateTime ngayBatDau,

        @Future(message = "Ngày kết thúc phải là tương lai")
        LocalDateTime ngayKetThuc,

        TrangThaiUuDai trangThaiUuDai,
        TargetType targetType,
        String targetId
) {}
