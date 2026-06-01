package com.ota.travi.service;

import com.ota.travi.dto.response.PartnerAiInsightItemResponse;
import com.ota.travi.dto.response.PartnerAiInsightResponse;
import com.ota.travi.exception.ResourceNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PartnerAiInsightService {
    private static final int DEFAULT_RANGE_DAYS = 30;
    private static final int MIN_RANGE_DAYS = 7;
    private static final int MAX_RANGE_DAYS = 90;
    private static final String MODEL_VERSION = "partner-insight-v1";

    private final JdbcTemplate jdbcTemplate;

    public PartnerAiInsightService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public PartnerAiInsightResponse getInsights(String partnerId, Integer days) {
        int rangeDays = normalizeRangeDays(days);
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(rangeDays - 1L);
        LocalDate previousToDate = fromDate.minusDays(1);
        LocalDate previousFromDate = previousToDate.minusDays(rangeDays - 1L);

        BusinessProfileRow profile = findBusinessProfile(partnerId);
        PeriodMetrics current = loadMetrics(profile.businessProfileId(), fromDate, toDate);
        PeriodMetrics previous = loadMetrics(profile.businessProfileId(), previousFromDate, previousToDate);
        ReviewMetrics reviews = loadReviewMetrics(profile.businessProfileId(), fromDate, toDate);

        double revenueGrowth = growthPercentage(current.revenue(), previous.revenue());
        double bookingGrowth = growthPercentage(current.bookings(), previous.bookings());
        int healthScore = calculateHealthScore(current, previous, reviews);
        String summary = buildProfileSummary(profile, current, revenueGrowth, reviews, healthScore);
        List<InsightDraft> drafts = buildInsightDrafts(profile, current, previous, reviews, revenueGrowth, bookingGrowth, healthScore);

        upsertPartnerProfile(partnerId, profile.businessProfileId(), summary, healthScore, revenueGrowth, bookingGrowth, reviews);
        List<PartnerAiInsightItemResponse> insights = persistAndLoadInsights(partnerId, profile.businessProfileId(), drafts);

        return new PartnerAiInsightResponse(
                partnerId,
                profile.businessProfileId(),
                profile.businessName(),
                profile.serviceType(),
                fromDate,
                toDate,
                healthScore,
                summary,
                roundTwo(current.revenue()),
                roundTwo(revenueGrowth),
                current.bookings(),
                roundTwo(bookingGrowth),
                roundTwo(reviews.averageRating()),
                reviews.reviewCount(),
                MODEL_VERSION,
                insights
        );
    }

    private BusinessProfileRow findBusinessProfile(String partnerId) {
        List<BusinessProfileRow> profiles = jdbcTemplate.query(
                """
                SELECT id_ho_so, ten_co_so, loai_dich_vu
                FROM ho_so_kinh_doanh
                WHERE doi_tac_id = ? AND deleted = false
                ORDER BY thoi_gian_cap_nhat DESC NULLS LAST, thoi_gian_dang_ky DESC NULLS LAST
                LIMIT 1
                """,
                (rs, rowNum) -> new BusinessProfileRow(
                        rs.getString("id_ho_so"),
                        rs.getString("ten_co_so"),
                        rs.getString("loai_dich_vu")
                ),
                partnerId
        );

        if (profiles.isEmpty()) {
            throw new ResourceNotFoundException("Ban chua co ho so kinh doanh de AI phan tich");
        }
        return profiles.get(0);
    }

    private PeriodMetrics loadMetrics(String businessProfileId, LocalDate fromDate, LocalDate toDate) {
        return jdbcTemplate.queryForObject(
                """
                SELECT
                    COALESCE(SUM(tong_tien_thanh_toan), 0) AS revenue,
                    COUNT(*) AS bookings
                FROM don_dat_cho
                WHERE ho_so_kinh_doanh_id = ?
                  AND deleted = false
                  AND ngay_tao >= ?
                  AND ngay_tao < ?
                  AND trang_thai IN ('CHO_THANH_TOAN','DA_THANH_TOAN','DA_XAC_NHAN','DANG_PHUC_VU','DA_HOAN_THANH')
                """,
                (rs, rowNum) -> new PeriodMetrics(
                        rs.getDouble("revenue"),
                        rs.getInt("bookings")
                ),
                businessProfileId,
                fromDate.atStartOfDay(),
                toDate.plusDays(1).atStartOfDay()
        );
    }

    private ReviewMetrics loadReviewMetrics(String businessProfileId, LocalDate fromDate, LocalDate toDate) {
        return jdbcTemplate.queryForObject(
                """
                SELECT
                    COALESCE(AVG(so_sao), 0) AS avg_rating,
                    COUNT(*) AS review_count,
                    COALESCE(SUM(CASE WHEN so_sao <= 3 THEN 1 ELSE 0 END), 0) AS low_reviews
                FROM review_danh_gia
                WHERE ho_so_kinh_doanh_id = ?
                  AND created_at >= ?
                  AND created_at < ?
                  AND trang_thai = 'DA_HIEN_THI'
                """,
                (rs, rowNum) -> new ReviewMetrics(
                        rs.getDouble("avg_rating"),
                        rs.getInt("review_count"),
                        rs.getInt("low_reviews")
                ),
                businessProfileId,
                fromDate.atStartOfDay(),
                toDate.plusDays(1).atStartOfDay()
        );
    }

    private int calculateHealthScore(PeriodMetrics current, PeriodMetrics previous, ReviewMetrics reviews) {
        int score = 55;
        double revenueGrowth = growthPercentage(current.revenue(), previous.revenue());
        double bookingGrowth = growthPercentage(current.bookings(), previous.bookings());

        if (current.bookings() > 0) {
            score += 12;
        }
        if (revenueGrowth > 10) {
            score += 12;
        } else if (revenueGrowth < -10) {
            score -= 12;
        }
        if (bookingGrowth > 10) {
            score += 8;
        } else if (bookingGrowth < -10) {
            score -= 8;
        }
        if (reviews.reviewCount() > 0 && reviews.averageRating() >= 4.2) {
            score += 10;
        } else if (reviews.reviewCount() > 0 && reviews.averageRating() < 3.5) {
            score -= 12;
        }
        if (reviews.lowReviews() >= 2) {
            score -= 8;
        }

        return Math.max(0, Math.min(100, score));
    }

    private String buildProfileSummary(
            BusinessProfileRow profile,
            PeriodMetrics current,
            double revenueGrowth,
            ReviewMetrics reviews,
            int healthScore
    ) {
        return "AI partner profile for " + profile.businessName()
                + ": service=" + profile.serviceType()
                + ", bookings=" + current.bookings()
                + ", revenue=" + roundTwo(current.revenue())
                + ", revenueGrowth=" + roundTwo(revenueGrowth) + "%"
                + ", avgRating=" + roundTwo(reviews.averageRating())
                + ", healthScore=" + healthScore + ".";
    }

    private List<InsightDraft> buildInsightDrafts(
            BusinessProfileRow profile,
            PeriodMetrics current,
            PeriodMetrics previous,
            ReviewMetrics reviews,
            double revenueGrowth,
            double bookingGrowth,
            int healthScore
    ) {
        List<InsightDraft> insights = new ArrayList<>();

        if (current.bookings() == 0) {
            insights.add(new InsightDraft(
                    "DEMAND",
                    "HIGH",
                    "Nhu cầu đang thấp",
                    "Trong kỳ này chưa ghi nhận đơn đặt chỗ nào cho " + profile.businessName() + ".",
                    "Kích hoạt ưu đãi ngắn hạn, làm mới hình ảnh dịch vụ và đẩy sản phẩm vào nhóm gợi ý AI.",
                    "bookings",
                    0.0
            ));
        } else if (bookingGrowth < -15) {
            insights.add(new InsightDraft(
                    "DEMAND",
                    "MEDIUM",
                    "Lượng đặt chỗ đang giảm",
                    "Số đơn giảm " + roundTwo(Math.abs(bookingGrowth)) + "% so với kỳ trước.",
                    "Kiểm tra giá, tình trạng phòng/bàn và tạo gói ưu đãi cho khung giờ ngày thấp điểm.",
                    "booking_growth_percent",
                    bookingGrowth
            ));
        }

        if (revenueGrowth < -15) {
            insights.add(new InsightDraft(
                    "REVENUE",
                    "HIGH",
                    "Doanh thu có xu hướng giảm",
                    "Doanh thu giảm " + roundTwo(Math.abs(revenueGrowth)) + "% so với kỳ trước.",
                    "Ưu tiên gói combo, điều chỉnh giá theo ngày thấp điểm và tăng hiển thị dịch vụ có rating tốt.",
                    "revenue_growth_percent",
                    revenueGrowth
            ));
        } else if (revenueGrowth > 20) {
            insights.add(new InsightDraft(
                    "REVENUE",
                    "LOW",
                    "Doanh thu tăng tốt",
                    "Doanh thu tăng " + roundTwo(revenueGrowth) + "% so với kỳ trước.",
                    "Duy trì giá hiện tại, mở rộng gói upsell và theo dõi công suất để tránh quá tải.",
                    "revenue_growth_percent",
                    revenueGrowth
            ));
        }

        if (reviews.reviewCount() > 0 && reviews.averageRating() < 3.8) {
            insights.add(new InsightDraft(
                    "REVIEW",
                    "HIGH",
                    "Điểm đánh giá cần cải thiện",
                    "Điểm trung bình hiện tại là " + roundTwo(reviews.averageRating()) + "/5.",
                    "Đọc các review điểm thấp, phản hồi công khai và cập nhật quy trình phục vụ theo nhóm vấn đề lặp lại.",
                    "average_rating",
                    reviews.averageRating()
            ));
        } else if (reviews.reviewCount() > 0) {
            insights.add(new InsightDraft(
                    "REVIEW",
                    "LOW",
                    "Tín hiệu review ổn định",
                    "Có " + reviews.reviewCount() + " đánh giá trong kỳ với điểm trung bình " + roundTwo(reviews.averageRating()) + "/5.",
                    "Dùng review tốt làm bằng chứng trên trang chi tiết và trong chiến dịch khuyến mại.",
                    "average_rating",
                    reviews.averageRating()
            ));
        }

        if (healthScore >= 75) {
            insights.add(new InsightDraft(
                    "MARKETING",
                    "LOW",
                    "Sẵn sàng đẩy hiển thị",
                    "Health score đạt " + healthScore + "/100, phù hợp để tăng hiển thị trên khu vực gợi ý.",
                    "Tạo gói ưu đãi nhẹ hoặc nổi bật điểm mạnh trong mô tả dịch vụ.",
                    "health_score",
                    (double) healthScore
            ));
        } else if (healthScore < 50) {
            insights.add(new InsightDraft(
                    "OPERATIONS",
                    "HIGH",
                    "Cần ưu tiên tối ưu vận hành",
                    "Health score đang ở mức " + healthScore + "/100.",
                    "Tập trung xử lý review thấp, cập nhật tồn kho phòng/bàn và thử nghiệm ưu đãi ngắn hạn.",
                    "health_score",
                    (double) healthScore
            ));
        }

        if (insights.isEmpty()) {
            insights.add(new InsightDraft(
                    "SUMMARY",
                    "LOW",
                    "Hoạt động đang ổn định",
                    "AI chưa phát hiện bất thường lớn trong kỳ hiện tại.",
                    "Tiếp tục theo dõi booking, review và cập nhật hình ảnh/nội dung dịch vụ định kỳ.",
                    "bookings",
                    (double) current.bookings()
            ));
        }

        return insights;
    }

    private void upsertPartnerProfile(
            String partnerId,
            String businessProfileId,
            String summary,
            int healthScore,
            double revenueGrowth,
            double bookingGrowth,
            ReviewMetrics reviews
    ) {
        jdbcTemplate.update(
                """
                INSERT INTO ai_partner_profile (
                    id, partner_id, business_profile_id, profile_summary, health_score,
                    revenue_trend, occupancy_trend, review_trend, model_version, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT (partner_id)
                DO UPDATE SET
                    business_profile_id = EXCLUDED.business_profile_id,
                    profile_summary = EXCLUDED.profile_summary,
                    health_score = EXCLUDED.health_score,
                    revenue_trend = EXCLUDED.revenue_trend,
                    occupancy_trend = EXCLUDED.occupancy_trend,
                    review_trend = EXCLUDED.review_trend,
                    model_version = EXCLUDED.model_version,
                    updated_at = CURRENT_TIMESTAMP
                """,
                UUID.randomUUID().toString(),
                partnerId,
                businessProfileId,
                summary,
                healthScore,
                trendLabel(revenueGrowth),
                trendLabel(bookingGrowth),
                reviews.reviewCount() == 0 ? "NO_DATA" : trendLabel((reviews.averageRating() - 3.8) * 25),
                MODEL_VERSION
        );
    }

    private List<PartnerAiInsightItemResponse> persistAndLoadInsights(
            String partnerId,
            String businessProfileId,
            List<InsightDraft> drafts
    ) {
        jdbcTemplate.update(
                "UPDATE ai_partner_insight SET status = 'ARCHIVED' WHERE partner_id = ? AND status = 'ACTIVE'",
                partnerId
        );

        for (InsightDraft draft : drafts) {
            jdbcTemplate.update(
                    """
                    INSERT INTO ai_partner_insight (
                        id, doi_tac_id, ho_so_kinh_doanh_id, partner_id, business_profile_id,
                        insight_type, severity, priority, title, description, message,
                        action, recommended_action, metric_name, metric_value,
                        model_version, status, generated_at, created_at, updated_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    """,
                    UUID.randomUUID().toString(),
                    partnerId,
                    businessProfileId,
                    partnerId,
                    businessProfileId,
                    draft.insightType(),
                    draft.severity(),
                    draft.severity(),
                    draft.title(),
                    draft.message(),
                    draft.message(),
                    draft.recommendedAction(),
                    draft.recommendedAction(),
                    draft.metricName(),
                    draft.metricValue(),
                    MODEL_VERSION
            );
        }

        return jdbcTemplate.query(
                """
                SELECT id, insight_type, severity, title, message, recommended_action,
                       metric_name, metric_value, created_at
                FROM ai_partner_insight
                WHERE partner_id = ? AND status = 'ACTIVE'
                ORDER BY
                    CASE severity WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END,
                    created_at DESC
                """,
                this::mapInsight,
                partnerId
        );
    }

    private PartnerAiInsightItemResponse mapInsight(ResultSet rs, int rowNum) throws SQLException {
        return new PartnerAiInsightItemResponse(
                rs.getString("id"),
                rs.getString("insight_type"),
                rs.getString("severity"),
                rs.getString("title"),
                rs.getString("message"),
                rs.getString("recommended_action"),
                rs.getString("metric_name"),
                rs.getObject("metric_value") == null ? null : rs.getDouble("metric_value"),
                rs.getObject("created_at", LocalDateTime.class)
        );
    }

    private String trendLabel(double growth) {
        if (growth >= 10) {
            return "UP";
        }
        if (growth <= -10) {
            return "DOWN";
        }
        return "STABLE";
    }

    private int normalizeRangeDays(Integer days) {
        if (days == null) {
            return DEFAULT_RANGE_DAYS;
        }
        if (days < MIN_RANGE_DAYS) {
            return MIN_RANGE_DAYS;
        }
        return Math.min(days, MAX_RANGE_DAYS);
    }

    private double growthPercentage(double current, double previous) {
        if (previous <= 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((current - previous) / previous) * 100.0;
    }

    private double roundTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private record BusinessProfileRow(String businessProfileId, String businessName, String serviceType) {
    }

    private record PeriodMetrics(double revenue, int bookings) {
    }

    private record ReviewMetrics(double averageRating, int reviewCount, int lowReviews) {
    }

    private record InsightDraft(
            String insightType,
            String severity,
            String title,
            String message,
            String recommendedAction,
            String metricName,
            Double metricValue
    ) {
    }
}
