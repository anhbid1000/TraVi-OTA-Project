package com.ota.travi.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ota.travi.dto.response.WeatherForecastRangeResponse;
import com.ota.travi.dto.response.WeatherForecastResponse;
import com.ota.travi.dto.response.WeatherForecastSummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {
    private static final int MAX_FORECAST_DAYS = 14;

    private final RestClient restClient;

    public WeatherService(
            RestClient.Builder restClientBuilder,
            @Value("${weather.open-meteo.base-url:https://api.open-meteo.com}") String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public WeatherForecastResponse getForecast(Double lat, Double lng, LocalDate date) {
        WeatherForecastRangeResponse range = getForecastRange(lat, lng, date, date);
        return range.forecasts().getFirst();
    }

    public WeatherForecastRangeResponse getForecastRange(Double lat, Double lng, LocalDate startDate, LocalDate endDate) {
        validateCoordinates(lat, lng);
        validateDateRange(startDate, endDate);

        OpenMeteoForecastResponse response = fetchOpenMeteoForecast(lat, lng, startDate, endDate);
        OpenMeteoDailyForecast daily = response == null ? null : response.daily();
        if (daily == null || daily.time() == null || daily.time().isEmpty()) {
            throw new RuntimeException("Không nhận được dữ liệu dự báo thời tiết từ Open-Meteo.");
        }

        List<WeatherForecastResponse> forecasts = new ArrayList<>();
        for (int index = 0; index < daily.time().size(); index++) {
            forecasts.add(mapDailyForecast(daily, index));
        }

        return new WeatherForecastRangeResponse(forecasts, buildSummary(forecasts));
    }

    private OpenMeteoForecastResponse fetchOpenMeteoForecast(Double lat, Double lng, LocalDate startDate, LocalDate endDate) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lng)
                        .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                        .queryParam("timezone", "auto")
                        .queryParam("start_date", startDate)
                        .queryParam("end_date", endDate)
                        .build())
                .retrieve()
                .body(OpenMeteoForecastResponse.class);
    }

    private WeatherForecastResponse mapDailyForecast(OpenMeteoDailyForecast daily, int index) {
        double maxTemperature = getRequiredDouble(daily.temperatureMax(), index, "temperature_2m_max");
        double minTemperature = getRequiredDouble(daily.temperatureMin(), index, "temperature_2m_min");
        int weatherCode = getRequiredInteger(daily.weatherCode(), index, "weather_code");
        Integer precipitationProbability = getOptionalInteger(daily.precipitationProbabilityMax(), index);

        WeatherCondition condition = mapCondition(weatherCode, precipitationProbability);
        double averageTemperature = roundOneDecimal((maxTemperature + minTemperature) / 2);

        return new WeatherForecastResponse(
                daily.time().get(index),
                averageTemperature,
                condition.condition(),
                condition.icon(),
                condition.warningMessage()
        );
    }

    private WeatherForecastSummaryResponse buildSummary(List<WeatherForecastResponse> forecasts) {
        double averageTemperature = roundOneDecimal(forecasts.stream()
                .mapToDouble(WeatherForecastResponse::temperature)
                .average()
                .orElse(0));

        long stormDays = forecasts.stream().filter(forecast -> "cloud-lightning".equals(forecast.icon())).count();
        long rainyDays = forecasts.stream()
                .filter(forecast -> "cloud-rain".equals(forecast.icon()))
                .count();
        long sunnyDays = forecasts.stream().filter(forecast -> "sun".equals(forecast.icon())).count();

        if (stormDays > 0) {
            return new WeatherForecastSummaryResponse(
                    averageTemperature,
                    "Có giông bão",
                    "cloud-lightning",
                    "Lịch trình có " + stormDays + " ngày có khả năng mưa giông, bạn nên chuẩn bị phương án trong nhà."
            );
        }

        if (rainyDays > 0) {
            return new WeatherForecastSummaryResponse(
                    averageTemperature,
                    "Có mưa",
                    "cloud-rain",
                    "Lịch trình có " + rainyDays + " ngày có khả năng mưa, bạn nên mang theo áo mưa hoặc ô."
            );
        }

        if (sunnyDays == forecasts.size()) {
            return new WeatherForecastSummaryResponse(
                    averageTemperature,
                    "Nắng đẹp",
                    "sun",
                    "Thời tiết tổng thể đẹp, phù hợp cho các hoạt động ngoài trời."
            );
        }

        return new WeatherForecastSummaryResponse(
                averageTemperature,
                "Thời tiết ổn định",
                "cloud",
                "Thời tiết nhìn chung ổn định, phù hợp cho lịch trình du lịch."
        );
    }

    private void validateCoordinates(Double lat, Double lng) {
        if (lat == null || lng == null || lat.isNaN() || lng.isNaN()) {
            throw new RuntimeException("Tọa độ dự báo thời tiết không hợp lệ.");
        }
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            throw new RuntimeException("Tọa độ dự báo thời tiết nằm ngoài phạm vi hợp lệ.");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new RuntimeException("Vui lòng chọn khoảng ngày dự báo thời tiết.");
        }
        if (endDate.isBefore(startDate)) {
            throw new RuntimeException("Ngày kết thúc dự báo không được trước ngày bắt đầu.");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days > MAX_FORECAST_DAYS) {
            throw new RuntimeException("Khoảng dự báo thời tiết tối đa " + MAX_FORECAST_DAYS + " ngày.");
        }
    }

    private WeatherCondition mapCondition(int weatherCode, Integer precipitationProbability) {
        if (precipitationProbability != null && precipitationProbability >= 70) {
            return new WeatherCondition(
                    "Có mưa rào",
                    "cloud-rain",
                    "Dự báo khả năng mưa cao, bạn nên ưu tiên các hoạt động trong nhà."
            );
        }

        return switch (weatherCode) {
            case 0 -> new WeatherCondition(
                    "Nắng đẹp",
                    "sun",
                    "Thời tiết đẹp, phù hợp cho các hoạt động ngoài trời."
            );
            case 1, 2, 3 -> new WeatherCondition(
                    "Nhiều mây",
                    "cloud",
                    "Thời tiết ổn định, có mây nhưng vẫn phù hợp để di chuyển."
            );
            case 45, 48 -> new WeatherCondition(
                    "Sương mù",
                    "cloud",
                    "Có thể có sương mù, bạn nên chú ý khi di chuyển."
            );
            case 51, 53, 55, 56, 57 -> new WeatherCondition(
                    "Mưa phùn",
                    "cloud-rain",
                    "Có khả năng mưa nhẹ, bạn nên mang theo áo mưa hoặc ô."
            );
            case 61, 63, 65, 66, 67, 80, 81, 82 -> new WeatherCondition(
                    "Có mưa rào",
                    "cloud-rain",
                    "Dự báo có mưa, bạn nên ưu tiên các hoạt động trong nhà."
            );
            case 95, 96, 99 -> new WeatherCondition(
                    "Có giông bão",
                    "cloud-lightning",
                    "Có khả năng mưa giông, bạn nên hạn chế hoạt động ngoài trời."
            );
            default -> new WeatherCondition(
                    "Thời tiết ổn định",
                    "sun",
                    "Thời tiết ổn định, phù hợp cho lịch trình du lịch."
            );
        };
    }

    private double getRequiredDouble(List<Double> values, int index, String fieldName) {
        if (values == null || values.size() <= index || values.get(index) == null) {
            throw new RuntimeException("Open-Meteo thiếu dữ liệu " + fieldName + ".");
        }
        return values.get(index);
    }

    private int getRequiredInteger(List<Integer> values, int index, String fieldName) {
        if (values == null || values.size() <= index || values.get(index) == null) {
            throw new RuntimeException("Open-Meteo thiếu dữ liệu " + fieldName + ".");
        }
        return values.get(index);
    }

    private Integer getOptionalInteger(List<Integer> values, int index) {
        if (values == null || values.size() <= index) {
            return null;
        }
        return values.get(index);
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private record WeatherCondition(
            String condition,
            String icon,
            String warningMessage
    ) {
    }

    private record OpenMeteoForecastResponse(OpenMeteoDailyForecast daily) {
    }

    private record OpenMeteoDailyForecast(
            List<LocalDate> time,
            @JsonProperty("weather_code") List<Integer> weatherCode,
            @JsonProperty("temperature_2m_max") List<Double> temperatureMax,
            @JsonProperty("temperature_2m_min") List<Double> temperatureMin,
            @JsonProperty("precipitation_probability_max") List<Integer> precipitationProbabilityMax
    ) {
    }
}
