package com.ota.travi.service;

import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.NhaHang;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.UUID;

@Service
public class AiModelOpsService {
    private static final String MODEL_NAME = "two_tower_feature_hashing";
    private static final String MODEL_VERSION = "v1-feature-hash";

    private final JdbcTemplate jdbcTemplate;
    private final TwoTowerRecommendationModel twoTowerRecommendationModel;

    public AiModelOpsService(
            JdbcTemplate jdbcTemplate,
            TwoTowerRecommendationModel twoTowerRecommendationModel
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.twoTowerRecommendationModel = twoTowerRecommendationModel;
    }

    public void recordTrainingInteraction(
            String userId,
            String hoSoAIId,
            String assetId,
            String assetType,
            String eventType,
            String contextCity,
            String source,
            String metadata
    ) {
        if (eventType == null || eventType.isBlank()) {
            return;
        }

        jdbcTemplate.update(
                """
                INSERT INTO ai_training_interaction (
                    id, user_id, ho_so_ai_id, asset_id, asset_type, event_type,
                    label_weight, context_city, context_weather, source, metadata, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                UUID.randomUUID().toString(),
                userId,
                hoSoAIId,
                emptyToNull(assetId),
                normalizeAssetType(assetType),
                eventType.trim().toUpperCase(Locale.ROOT),
                eventWeight(eventType),
                emptyToNull(contextCity),
                inferWeatherContext(contextCity),
                emptyToNull(source),
                emptyToNull(metadata)
        );
    }

    public void upsertHotelEmbedding(KhachSan hotel) {
        if (hotel == null || hotel.getIdTaiSan() == null) {
            return;
        }
        upsertItemEmbedding(
                hotel.getIdTaiSan(),
                "HOTEL",
                toJsonArray(twoTowerRecommendationModel.buildItemEmbeddingForHotel(hotel)),
                twoTowerRecommendationModel.summarizeHotelFeatures(hotel)
        );
    }

    public void upsertRestaurantEmbedding(NhaHang restaurant) {
        if (restaurant == null || restaurant.getIdTaiSan() == null) {
            return;
        }
        upsertItemEmbedding(
                restaurant.getIdTaiSan(),
                "RESTAURANT",
                toJsonArray(twoTowerRecommendationModel.buildItemEmbeddingForRestaurant(restaurant)),
                twoTowerRecommendationModel.summarizeRestaurantFeatures(restaurant)
        );
    }

    private void upsertItemEmbedding(String assetId, String assetType, String embedding, String featureSummary) {
        jdbcTemplate.update(
                """
                INSERT INTO ai_item_embedding (
                    id, asset_id, asset_type, embedding, feature_summary,
                    model_name, model_version, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (asset_id, asset_type, model_name, model_version)
                DO UPDATE SET
                    embedding = EXCLUDED.embedding,
                    feature_summary = EXCLUDED.feature_summary,
                    updated_at = CURRENT_TIMESTAMP
                """,
                UUID.randomUUID().toString(),
                assetId,
                assetType,
                embedding,
                emptyToNull(featureSummary),
                MODEL_NAME,
                MODEL_VERSION
        );
    }

    private BigDecimal eventWeight(String eventType) {
        String normalized = eventType.trim().toUpperCase(Locale.ROOT);
        double weight = switch (normalized) {
            case "SEARCH" -> 0.10;
            case "VIEW", "PROMOTION_VIEW" -> 0.20;
            case "CLICK" -> 0.50;
            case "REVIEW" -> 0.80;
            case "BOOK" -> 1.00;
            default -> 0.05;
        };
        return BigDecimal.valueOf(weight).setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeAssetType(String assetType) {
        if (assetType == null || assetType.isBlank()) {
            return null;
        }
        return assetType.trim().toUpperCase(Locale.ROOT);
    }

    private String inferWeatherContext(String city) {
        String normalizedCity = normalizeText(city);
        if (normalizedCity.contains("da lat") || normalizedCity.contains("sapa") || normalizedCity.contains("tam dao")) {
            return "COOL";
        }
        if (normalizedCity.contains("phu quoc") || normalizedCity.contains("da nang") || normalizedCity.contains("nha trang")) {
            return "SUNNY";
        }
        int month = LocalDate.now().getMonthValue();
        if (month >= 5 && month <= 10) {
            return "RAINY";
        }
        return "MILD";
    }

    private String toJsonArray(double[] embedding) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (double value : embedding) {
            joiner.add(BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP).toPlainString());
        }
        return joiner.toString();
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
