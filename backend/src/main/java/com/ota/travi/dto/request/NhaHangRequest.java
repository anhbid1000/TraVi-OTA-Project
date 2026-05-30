package com.ota.travi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record NhaHangRequest(
        @NotBlank(message = "Tên nhà hàng không được để trống")
        @Size(max = 200, message = "Tên nhà hàng không được vượt quá 200 ký tự")
        String ten,

        @Size(max = 100, message = "Loại ẩm thực không được vượt quá 100 ký tự")
        String loaiAmThuc,

        @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
        String moTa,

        @NotNull(message = "Giá cơ bản không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá cơ bản phải lớn hơn 0")
        Double giaCoBan,

        Boolean isDynamicPricing,

        @NotNull(message = "Sức chứa không được để trống")
        @Min(value = 1, message = "Sức chứa phải lớn hơn hoặc bằng 1")
        Integer sucChua,

        @NotNull(message = "Giờ mở cửa không được để trống")
        LocalTime gioMoCua,

        @NotNull(message = "Giờ đóng cửa không được để trống")
        LocalTime gioDongCua,

        Boolean coDatBanTruoc,

        Boolean coDatMonTruoc
) {
}
