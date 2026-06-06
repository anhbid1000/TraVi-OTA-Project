package com.ota.travi.dto.response;

public record RestaurantAssignedTableResponse(
        String tableId,
        String tenBan,
        Integer soChoNgoi,
        String viTriSanh
) {
}
