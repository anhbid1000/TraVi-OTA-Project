package com.ota.travi.dto.request;

import com.ota.travi.enums.LoaiDichVu;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request tạo review mới từ khách hàng.
 */
public record ReviewCreateRequest(
        @NotNull(message = "Loại dịch vụ không được để trống")
        LoaiDichVu loaiDichVu,

        String bookingId,
        String reservationId,

        @NotNull(message = "Số sao không được để trống")
        @Min(value = 1, message = "Số sao tối thiểu là 1")
        @Max(value = 5, message = "Số sao tối đa là 5")
        Integer soSao,

        @NotBlank(message = "Nội dung đánh giá không được để trống")
        @Size(max = 1000, message = "Nội dung tối đa 1000 ký tự")
        String noiDung,

        @Size(max = 20, message = "Tối đa 20 điểm chi tiết")
        java.util.List<ReviewAspectScoreRequest> aspectScores
) {
}

