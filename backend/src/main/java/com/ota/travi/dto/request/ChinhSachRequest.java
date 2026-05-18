package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ChinhSachRequest(
        @NotBlank(message = "Loại chính sách không được để trống")
        @Size(max = 100, message = "Loại chính sách không được vượt quá 100 ký tự")
        String loaiChinhSach,

        @NotBlank(message = "Nội dung chính sách không được để trống")
        @Size(max = 5000, message = "Nội dung chính sách không được vượt quá 5000 ký tự")
        String noiDung,

        @NotNull(message = "Ngày áp dụng không được để trống")
        LocalDate ngayApDung
) {
}
