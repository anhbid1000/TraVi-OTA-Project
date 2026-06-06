package com.ota.travi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RestaurantAmenitiesUpdateRequest(
        @Valid
        @NotNull(message = "Danh sach tien ich khong duoc de trong")
        List<TienIchNhaHangRequest> tienIchNhaHang
) {
}
