package com.ota.travi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HotelBookingRoomRequest(
        @NotBlank(message = "roomId không được để trống")
        String roomId,

        @NotNull(message = "soLuong không được để trống")
        @Min(value = 1, message = "soLuong phải lớn hơn 0")
        Integer soLuong
) {
}
