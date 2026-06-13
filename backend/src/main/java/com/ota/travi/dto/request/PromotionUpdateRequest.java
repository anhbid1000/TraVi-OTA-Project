package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PromotionUpdateRequest(
        @NotBlank(message = "Ten uu dai khong duoc de trong")
        String tenUuDai,

        String moTa,

        @NotBlank(message = "ID ho so kinh doanh khong duoc de trong")
        String businessProfileId,

        @NotNull(message = "Muc giam khong duoc de trong")
        Double mucGiam,

        @NotBlank(message = "Loai giam gia khong duoc de trong")
        String loaiGiamGia,

        Double giaTriGiamToiDa,

        @NotNull(message = "Ngay bat dau khong duoc de trong")
        LocalDateTime ngayBatDau,

        @NotNull(message = "Ngay ket thuc khong duoc de trong")
        LocalDateTime ngayKetThuc,

        String targetType,

        Long targetId,

        Integer soLuongPhatHanh,

        Double donHangToiThieu,

        Integer usageLimitPerUser,

        Integer diemCanDoi,

        Boolean choPhepDoiBangDiem,

        String phamViApDung
) {
}
