package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record HotelAmenitiesUpdateRequest(
        @NotNull(message = "Danh sach tien ich khong duoc de trong")
        Set<String> amenityIds
) {
}
