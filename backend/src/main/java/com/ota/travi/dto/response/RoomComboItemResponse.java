package com.ota.travi.dto.response;

public record RoomComboItemResponse(
        String roomId,
        String roomName,
        String roomType,
        Integer quantity,
        Integer capacityPerRoom,
        Double pricePerRoom
) {
}
