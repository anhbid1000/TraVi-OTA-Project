package com.ota.travi.dto.response;

public record WeatherForecastSummaryResponse(
        Double averageTemperature,
        String condition,
        String icon,
        String message
) {
}
