package com.ota.travi.dto.request;

import com.ota.travi.enums.LoaiDichVu;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HoSoKinhDoanhRequest(
        @NotBlank(message = "Tên cơ sở không được để trống")
        @Size(max = 200, message = "Tên cơ sở không được vượt quá 200 ký tự")
        String tenCoSo,

        @NotBlank(message = "Số điện thoại liên hệ không được để trống")
        @Pattern(regexp = "^(\\+84|0)\\d{9}$", message = "Số điện thoại liên hệ không hợp lệ")
        String sdtLienHe,

        @NotNull(message = "Loại dịch vụ không được để trống")
        LoaiDichVu loaiDichVu,

        @NotBlank(message = "Mã số thuế không được để trống")
        @Size(max = 50, message = "Mã số thuế không được vượt quá 50 ký tự")
        String maSoThue,

        @NotBlank(message = "Giấy phép kinh doanh không được để trống")
        @Size(max = 500, message = "Giấy phép kinh doanh không được vượt quá 500 ký tự")
        String giayPhepKinhDoanh,

        @NotBlank(message = "Tọa độ GPS không được để trống")
        @Size(max = 100, message = "Tọa độ GPS không được vượt quá 100 ký tự")
        String toaDoGPS,

        @Valid
        @NotNull(message = "Chính sách không được để trống")
        ChinhSachRequest chinhSach
) {
}
