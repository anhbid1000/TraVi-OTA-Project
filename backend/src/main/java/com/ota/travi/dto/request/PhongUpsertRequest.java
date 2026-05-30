package com.ota.travi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PhongUpsertRequest(
        @Valid
        @NotNull(message = "Thong tin phong khong duoc de trong")
        PhongRequest phong,

        @Valid
        List<AnhRequest> danhSachAnh
) {
}
