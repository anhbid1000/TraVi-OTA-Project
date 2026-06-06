package com.ota.travi.dto.response;

import java.time.LocalDate;

public record WeatherForecastResponse(
        LocalDate date,
        Double temperature,
        String condition,
        String icon,
        String warningMessage
) {
}


