package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record VoucherCreateRequest(
        @NotBlank(message = "Tên ưu đãi không được để trống")
        String tenUuDai,

        String moTa,

        @NotBlank(message = "ID hồ sơ kinh doanh không được để trống")
        String businessProfileId,

        @NotNull(message = "Mức giảm không được để trống")
        Double mucGiam,

        @NotBlank(message = "Loại giảm giá không được để trống")
        String loaiGiamGia,

        Double giaTriGiamToiDa,

        @NotNull(message = "Ngày bắt đầu không được để trống")
        LocalDateTime ngayBatDau,

        @NotNull(message = "Ngày kết thúc không được để trống")
        LocalDateTime ngayKetThuc,

        @NotBlank(message = "Mã voucher không được để trống")
        String maVoucher,

        @NotNull(message = "Số lượng phát hành không được để trống")
        Integer soLuongPhatHanh,

        Double donHangToiThieu,

        Integer usageLimitPerUser,

        Integer diemCanDoi,

        Boolean choPhepDoiBangDiem,

        @NotBlank(message = "Phạm vi áp dụng không được để trống")
        String phamViApDung
) {
}
