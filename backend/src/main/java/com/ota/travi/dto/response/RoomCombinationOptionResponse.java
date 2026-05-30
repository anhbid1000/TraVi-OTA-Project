package com.ota.travi.dto.response;

import java.util.List;

public record RoomCombinationOptionResponse(
        Integer totalCapacity,
        Integer totalRooms,
        Integer totalPricePerNight,
        List<RoomComboItemResponse> items
) {
}
