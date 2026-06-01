package com.ota.travi.repository;

import com.ota.travi.entity.PromotionAnalyticsDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionAnalyticsDailyRepository extends JpaRepository<PromotionAnalyticsDaily, Long> {
    List<PromotionAnalyticsDaily> findByCampaignIdAndNgayBetween(Long campaignId, LocalDate startDate, LocalDate endDate);

    @Modifying
    @Query(value = """
            INSERT INTO promotion_analytics_daily (campaign_id, ngay, usage_count, booking_count, generated_revenue, discount_cost, conversion_rate)
            VALUES (:campaignId, :ngay, :usageCount, :bookingCount, :generatedRevenue, :discountCost, :conversionRate)
            ON DUPLICATE KEY UPDATE
                usage_count = :usageCount,
                booking_count = :bookingCount,
                generated_revenue = :generatedRevenue,
                discount_cost = :discountCost,
                conversion_rate = :conversionRate
            """, nativeQuery = true)
    void updateOrCreate(
            @Param("campaignId") Long campaignId,
            @Param("ngay") LocalDate ngay,
            @Param("usageCount") Integer usageCount,
            @Param("bookingCount") Integer bookingCount,
            @Param("generatedRevenue") Double generatedRevenue,
            @Param("discountCost") Double discountCost,
            @Param("conversionRate") Double conversionRate
    );
}
