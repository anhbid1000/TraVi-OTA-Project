package com.ota.travi.dto.response;

import java.util.List;

public record FilterOptionsResponse(
        List<FilterOptionResponse> amenities,
        List<FilterOptionResponse> types,
        List<FilterOptionResponse> cuisines
) {
}

