package com.ota.travi.dto.response;

public record TableAvailabilityResponse(
        String tableId,
        String tenBan,
        Integer soGhe,
        String viTri,
        Integer trangThai,
        Boolean available
) {
}


