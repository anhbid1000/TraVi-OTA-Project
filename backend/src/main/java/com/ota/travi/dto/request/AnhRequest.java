package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AnhRequest(
        @NotBlank(message = "Duong dan anh khong duoc de trong")
        String duongDanUrl,

        String moTaAnh,

        Boolean laAnhDaiDien,

        LocalDate ngayTaiLen
) {
}
