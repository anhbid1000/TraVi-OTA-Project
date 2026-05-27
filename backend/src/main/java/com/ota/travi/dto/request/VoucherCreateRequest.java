package com.ota.travi.dto.request;

import com.ota.travi.enums.LoaiGiamGia;
import com.ota.travi.enums.PhamViApDung;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VoucherCreateRequest(
        @NotBlank(message = "Tên ưu đãi không được để trống")
        String tenUuDai,

        String moTa,

        String businessProfileId,

        @NotNull(message = "Mức giảm không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Mức giảm phải lớn hơn 0")
        BigDecimal mucGiam,

        @NotNull(message = "Loại giảm giá không được để trống")
        LoaiGiamGia loaiGiamGia,

        @Positive(message = "Giá trị giảm tối đa phải lớn hơn 0")
        BigDecimal giaTriGiamToiDa,

        @NotNull(message = "Ngày bắt đầu không được để trống")
        @FutureOrPresent(message = "Ngày bắt đầu phải là hiện tại hoặc tương lai")
        LocalDateTime ngayBatDau,

        @NotNull(message = "Ngày kết thúc không được để trống")
        @Future(message = "Ngày kết thúc phải là tương lai")
        LocalDateTime ngayKetThuc,

        @NotBlank(message = "Mã voucher không được để trống")
        String maVoucher,

        @NotNull(message = "Số lượng phát hành không được để trống")
        @Min(value = 1, message = "Số lượng phát hành phải lớn hơn 0")
        Integer soLuongPhatHanh,

        @DecimalMin(value = "0.0", message = "Đơn hàng tối thiểu không được âm")
        BigDecimal donHangToiThieu,

        @Min(value = 1, message = "Giới hạn sử dụng mỗi khách phải lớn hơn 0")
        Integer usageLimitPerUser,

        @Min(value = 0, message = "Điểm cần đổi không được âm")
        Integer diemCanDoi,

        Boolean choPhepDoiBangDiem,

        @NotNull(message = "Phạm vi áp dụng không được để trống")
        PhamViApDung phamViApDung
) {}
