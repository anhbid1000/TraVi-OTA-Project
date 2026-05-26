package com.ota.travi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.Set;

public record KhachSanRequest(
        @NotBlank(message = "Tên khách sạn không được để trống")
        @Size(max = 200, message = "Tên khách sạn không được vượt quá 200 ký tự")
        String ten,

        @Min(value = 1, message = "Hạng sao phải từ 1 đến 5")
        @Max(value = 5, message = "Hạng sao phải từ 1 đến 5")
        Integer hangSao,

        @Size(max = 100, message = "Loại khách sạn không được vượt quá 100 ký tự")
        String loaiKhachSan,

        @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
        String moTa,

        @NotNull(message = "Giá cơ bản không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá cơ bản phải lớn hơn 0")
        Double giaCoBan,

        Boolean isDynamicPricing,

        @NotNull(message = "Giờ nhận phòng không được để trống")
        LocalTime gioNhanPhong,

        @NotNull(message = "Giờ trả phòng không được để trống")
        LocalTime gioTraPhong,

        LocalTime gioNhanPhongMacDinh,

        LocalTime gioTraPhongMacDinh,

        @Min(value = 1, message = "Số tầng phải lớn hơn hoặc bằng 1")
        Integer soTang,

        @Min(value = 1, message = "Tổng số phòng phải lớn hơn hoặc bằng 1")
        Integer tongSoPhong,

        Set<String> tienIchIds
) {
}
