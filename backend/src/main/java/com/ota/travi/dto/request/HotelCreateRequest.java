package com.ota.travi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record HotelCreateRequest(
        @Valid @NotNull KhachSanRequest khachSan
) {
}

