package com.ota.travi.dto.response;

import java.util.List;

public record MenuResponse(
        String id,
        String tenThucDon,
        String moTa,
        List<MenuItemResponse> items
) {
}


