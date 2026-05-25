package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MenuCreateRequest(
        @NotBlank @Size(max = 150) String tenThucDon,
        @Size(max = 100) String phanLoai
) {
}

