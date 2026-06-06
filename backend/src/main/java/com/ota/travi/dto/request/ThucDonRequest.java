package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ThucDonRequest(
        @NotBlank(message = "Tên thực đơn không được để trống")
        @Size(max = 200, message = "Tên thực đơn không vượt quá 200 ký tự")
        String tenThucDon,

        @Size(max = 100, message = "Phân loại không vượt quá 100 ký tự")
        String phanLoai
) {}
