package com.ota.travi.service;

import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.TienIchKhachSan;
import com.ota.travi.entity.TienIchNhaHang;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class TwoTowerRecommendationModel {
    private static final int EMBEDDING_SIZE = 32;
    private static final double SCORE_SCALE = 3.2;

    public TwoTowerScore scoreHotel(
            KhachHang khachHang,
            KhachSan hotel,
            List<String> profileTokens,
            String city,
            Set<String> clickedAssetIds,
            Set<String> bookedAssetIds,
            Map<String, Integer> collaborativeScores
    ) {
        UserContext userContext = UserContext.from(
                khachHang,
                profileTokens,
                city,
                "HOTEL",
                clickedAssetIds,
                bookedAssetIds,
                collaborativeScores
        );
        ItemContext itemContext = ItemContext.fromHotel(hotel);
        return score(userContext, itemContext);
    }

    public TwoTowerScore scoreRestaurant(
            KhachHang khachHang,
            NhaHang restaurant,
            List<String> profileTokens,
            String city,
            Set<String> clickedAssetIds,
            Set<String> bookedAssetIds,
            Map<String, Integer> collaborativeScores
    ) {
        UserContext userContext = UserContext.from(
                khachHang,
                profileTokens,
                city,
                "RESTAURANT",
                clickedAssetIds,
                bookedAssetIds,
                collaborativeScores
        );
        ItemContext itemContext = ItemContext.fromRestaurant(restaurant);
        return score(userContext, itemContext);
    }

    public TwoTowerScore score(UserContext userContext, ItemContext itemContext) {
        double[] userEmbedding = buildUserEmbedding(userContext);
        double[] itemEmbedding = buildItemEmbedding(itemContext);
        double dotProduct = dotProduct(userEmbedding, itemEmbedding);
        double probability = sigmoid(dotProduct / SCORE_SCALE);
        int score = clampScore((int) Math.round(probability * 100));

        List<String> reasons = explain(userContext, itemContext, probability);
        return new TwoTowerScore(score, round(probability), round(dotProduct), reasons);
    }

    double[] buildUserEmbedding(UserContext context) {
        double[] embedding = new double[EMBEDDING_SIZE];

        addFeature(embedding, "service:" + context.serviceType(), 1.25);
        addFeature(embedding, "weather:" + inferWeatherContext(context.city()), 0.45);
        addFeature(embedding, "member:" + safeText(context.memberRank()), 0.30);
        addFeature(embedding, "budget:" + inferBudgetBucket(context.totalSpend()), 0.35);

        addTextFeatures(embedding, context.city(), 0.95);
        for (String token : context.profileTokens()) {
            addTextFeatures(embedding, token, 0.80);
        }

        for (String assetId : context.bookedAssetIds()) {
            addFeature(embedding, "asset:" + assetId, 1.10);
        }
        for (String assetId : context.clickedAssetIds()) {
            addFeature(embedding, "asset:" + assetId, 0.75);
        }
        context.collaborativeScores().forEach((assetId, rawScore) -> {
            if (rawScore != null && rawScore > 0) {
                addFeature(embedding, "asset:" + assetId, Math.min(rawScore, 4) * 0.30);
            }
        });

        return normalize(embedding);
    }

    double[] buildItemEmbedding(ItemContext context) {
        double[] embedding = new double[EMBEDDING_SIZE];

        addFeature(embedding, "service:" + context.assetType(), 1.25);
        addFeature(embedding, "asset:" + context.assetId(), 0.90);
        addFeature(embedding, "price:" + inferPriceBucket(context.basePrice()), 0.35);
        addFeature(embedding, "quality:" + inferQualityBucket(context.qualitySignal()), 0.35);
        addFeature(embedding, "weather:" + inferWeatherContext(context.city()), 0.35);

        addTextFeatures(embedding, context.name(), 0.80);
        addTextFeatures(embedding, context.category(), 0.85);
        addTextFeatures(embedding, context.description(), 0.55);
        addTextFeatures(embedding, context.city(), 0.95);
        addTextFeatures(embedding, context.district(), 0.60);

        for (String feature : context.features()) {
            addTextFeatures(embedding, feature, 0.65);
        }

        return normalize(embedding);
    }

    double dotProduct(double[] left, double[] right) {
        double result = 0;
        for (int i = 0; i < Math.min(left.length, right.length); i++) {
            result += left[i] * right[i];
        }
        return result;
    }

    private List<String> explain(UserContext userContext, ItemContext itemContext, double probability) {
        List<String> reasons = new ArrayList<>();
        if (sameNormalized(userContext.city(), itemContext.city())) {
            reasons.add("Phù hợp với địa điểm bạn đang quan tâm");
        }
        if (hasSharedToken(userContext.profileTokens(), itemContext.textCorpus())) {
            reasons.add("Nội dung dịch vụ gần với sở thích của bạn");
        }
        if (userContext.bookedAssetIds().contains(itemContext.assetId())) {
            reasons.add("Tương đồng với dịch vụ bạn từng đặt");
        } else if (userContext.clickedAssetIds().contains(itemContext.assetId())) {
            reasons.add("Gần với dịch vụ bạn từng quan tâm");
        }
        if (userContext.collaborativeScores().getOrDefault(itemContext.assetId(), 0) > 0) {
            reasons.add("Được nhiều người có hành vi tương tự quan tâm");
        }
        if (probability >= 0.65) {
            reasons.add("Mức độ phù hợp được hệ thống dự đoán cao");
        }
        if (reasons.isEmpty()) {
            reasons.add("Phù hợp với ngữ cảnh tìm kiếm hiện tại");
        }
        return reasons;
    }

    private boolean hasSharedToken(Collection<String> profileTokens, String corpus) {
        String normalizedCorpus = normalizeText(corpus);
        return profileTokens.stream()
                .map(this::normalizeText)
                .filter(token -> token.length() >= 3)
                .anyMatch(normalizedCorpus::contains);
    }

    private void addTextFeatures(double[] embedding, String value, double weight) {
        for (String token : tokenize(value)) {
            addFeature(embedding, "text:" + token, weight);
        }
    }

    private void addFeature(double[] embedding, String feature, double weight) {
        String normalizedFeature = normalizeText(feature);
        if (normalizedFeature.isBlank()) {
            return;
        }

        int hash = normalizedFeature.hashCode();
        int index = Math.floorMod(hash, EMBEDDING_SIZE);
        double sign = (hash & 1) == 0 ? 1.0 : -1.0;
        embedding[index] += sign * weight;
    }

    private double[] normalize(double[] embedding) {
        double magnitude = 0;
        for (double value : embedding) {
            magnitude += value * value;
        }

        if (magnitude == 0) {
            return embedding;
        }

        double norm = Math.sqrt(magnitude);
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = embedding[i] / norm;
        }
        return embedding;
    }

    private double sigmoid(double value) {
        return 1.0 / (1.0 + Math.exp(-value));
    }

    private int clampScore(int score) {
        return Math.min(Math.max(score, 0), 100);
    }

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private List<String> tokenize(String value) {
        String normalized = normalizeText(value);
        if (normalized.isBlank()) {
            return List.of();
        }

        Set<String> tokens = new LinkedHashSet<>();
        for (String token : normalized.split("[^a-z0-9]+")) {
            if (token.length() >= 2) {
                tokens.add(token);
            }
        }
        return List.copyOf(tokens);
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

    private boolean sameNormalized(String left, String right) {
        String normalizedLeft = normalizeText(left);
        String normalizedRight = normalizeText(right);
        return !normalizedLeft.isBlank() && normalizedLeft.equals(normalizedRight);
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

    private String inferBudgetBucket(Double totalSpend) {
        if (totalSpend == null || totalSpend <= 0) {
            return "NEW";
        }
        if (totalSpend >= 10_000_000) {
            return "PREMIUM";
        }
        if (totalSpend >= 3_000_000) {
            return "MID";
        }
        return "VALUE";
    }

    private String inferPriceBucket(Double price) {
        if (price == null || price <= 0) {
            return "UNKNOWN";
        }
        if (price >= 1_500_000) {
            return "PREMIUM";
        }
        if (price >= 500_000) {
            return "MID";
        }
        return "VALUE";
    }

    private String inferQualityBucket(Double qualitySignal) {
        if (qualitySignal == null || qualitySignal <= 0) {
            return "UNKNOWN";
        }
        if (qualitySignal >= 4) {
            return "HIGH";
        }
        if (qualitySignal >= 2) {
            return "MID";
        }
        return "BASIC";
    }

    private String safeText(Object value) {
        return value == null ? "" : value.toString();
    }

    public record UserContext(
            String userId,
            String memberRank,
            Double totalSpend,
            List<String> profileTokens,
            String city,
            String serviceType,
            Set<String> clickedAssetIds,
            Set<String> bookedAssetIds,
            Map<String, Integer> collaborativeScores
    ) {
        static UserContext from(
                KhachHang khachHang,
                List<String> profileTokens,
                String city,
                String serviceType,
                Set<String> clickedAssetIds,
                Set<String> bookedAssetIds,
                Map<String, Integer> collaborativeScores
        ) {
            return new UserContext(
                    khachHang.getId(),
                    khachHang.getHangThanhVien() == null ? null : khachHang.getHangThanhVien().name(),
                    khachHang.getTongChiTieu(),
                    profileTokens == null ? List.of() : List.copyOf(profileTokens),
                    city,
                    serviceType,
                    clickedAssetIds == null ? Set.of() : Set.copyOf(clickedAssetIds),
                    bookedAssetIds == null ? Set.of() : Set.copyOf(bookedAssetIds),
                    collaborativeScores == null ? Map.of() : Map.copyOf(collaborativeScores)
            );
        }
    }

    public record ItemContext(
            String assetId,
            String assetType,
            String name,
            String category,
            String description,
            String city,
            String district,
            Double basePrice,
            Double qualitySignal,
            List<String> features
    ) {
        static ItemContext fromHotel(KhachSan hotel) {
            HoSoKinhDoanh profile = hotel.getHoSoKinhDoanh();
            List<String> features = hotel.getTienIch() == null
                    ? List.of()
                    : hotel.getTienIch().stream()
                    .map(TienIchKhachSan::getTenTienIch)
                    .toList();
            return new ItemContext(
                    hotel.getIdTaiSan(),
                    "HOTEL",
                    hotel.getTen(),
                    hotel.getLoaiKhachSan(),
                    hotel.getMoTa(),
                    profile == null ? null : profile.getThanhPho(),
                    profile == null ? null : profile.getQuanHuyen(),
                    hotel.getGiaCoBan(),
                    hotel.getHangSao() == null ? null : hotel.getHangSao().doubleValue(),
                    features
            );
        }

        static ItemContext fromRestaurant(NhaHang restaurant) {
            HoSoKinhDoanh profile = restaurant.getHoSoKinhDoanh();
            List<String> features = restaurant.getTienIch() == null
                    ? List.of()
                    : restaurant.getTienIch().stream()
                    .map(TienIchNhaHang::getTenTienIch)
                    .toList();
            return new ItemContext(
                    restaurant.getIdTaiSan(),
                    "RESTAURANT",
                    restaurant.getTen(),
                    restaurant.getLoaiAmThuc(),
                    restaurant.getMoTa(),
                    profile == null ? null : profile.getThanhPho(),
                    profile == null ? null : profile.getQuanHuyen(),
                    restaurant.getGiaCoBan(),
                    restaurant.getSucChua() == null ? null : Math.min(restaurant.getSucChua() / 10.0, 5.0),
                    features
            );
        }

        String textCorpus() {
            return String.join(" ", safe(name), safe(category), safe(description), safe(city), safe(district), String.join(" ", features));
        }

        private static String safe(String value) {
            return value == null ? "" : value;
        }
    }

    public record TwoTowerScore(
            int score,
            double probability,
            double dotProduct,
            List<String> reasons
    ) {
    }
}
