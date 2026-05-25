package com.ota.travi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record TableCreateRequest(
        @Valid @NotNull BanRequest ban
) {
}

