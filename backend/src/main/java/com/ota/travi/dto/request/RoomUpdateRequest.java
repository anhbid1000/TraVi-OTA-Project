package com.ota.travi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RoomUpdateRequest(
        @Valid @NotNull PhongRequest phong,
        @Valid List<AnhRequest> danhSachAnh
) {
}

