package com.ota.travi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RestaurantCreateRequest(
        @Valid @NotNull NhaHangRequest nhaHang
) {
}

