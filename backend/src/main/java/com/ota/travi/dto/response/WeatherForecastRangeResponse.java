package com.ota.travi.dto.response;

import java.util.List;

public record WeatherForecastRangeResponse(
        List<WeatherForecastResponse> forecasts,
        WeatherForecastSummaryResponse summary
) {
}
