package com.ota.travi.dto.request;

import com.ota.travi.enums.ComplaintCategory;
import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.MucDoKhieuNai;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request tạo ticket khiếu nại mới.
 */
public record ComplaintCreateRequest(
        @NotNull(message = "Loại dịch vụ không được để trống")
        LoaiDichVu loaiDichVu,

        String bookingId,
        String reservationId,

        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 150, message = "Tiêu đề tối đa 150 ký tự")
        String tieuDe,

        @NotBlank(message = "Nội dung tóm tắt không được để trống")
        @Size(max = 2000, message = "Nội dung tối đa 2000 ký tự")
        String noiDungTomTat,

        @NotNull(message = "Danh mục khiếu nại không được để trống")
        ComplaintCategory category,

        @NotNull(message = "Mức độ khiếu nại không được để trống")
        MucDoKhieuNai mucDo
) {
}
