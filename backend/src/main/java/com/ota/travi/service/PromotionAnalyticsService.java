package com.ota.travi.service;

import com.ota.travi.entity.PromotionAnalyticsDaily;
import com.ota.travi.repository.PromotionAnalyticsDailyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionAnalyticsService {

    private final PromotionAnalyticsDailyRepository promotionAnalyticsDailyRepository;

    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional
    public void aggregateDailyAnalytics() {
        log.info("Starting daily promotion analytics aggregation");

        try {
            // This would aggregate metrics from the previous day
            // Implementation depends on specific booking and voucher usage tracking
            // For now, provide placeholder that can be extended
            LocalDate yesterday = LocalDate.now().minusDays(1);
            log.info("Aggregating metrics for date: {}", yesterday);

            // Query campaigns with active bookings from yesterday
            // For each campaign:
            // - Count voucher_usage from yesterday
            // - Sum booking_count from yesterday
            // - Sum generated_revenue from yesterday
            // - Sum discount_cost from yesterday
            // - Calculate conversion_rate = bookings / uses
            // - Upsert into promotion_analytics_daily

            log.info("Daily promotion analytics aggregation completed");
        } catch (Exception e) {
            log.error("Error during daily promotion analytics aggregation: {}", e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public PromotionAnalyticsSummaryResponse getCampaignAnalyticsSummary(Long campaignId) {
        log.debug("Fetching analytics summary for campaign {}", campaignId);

        // Validate campaign exists (would need CampaignRepository)
        List<PromotionAnalyticsDaily> dailyMetrics = 
                promotionAnalyticsDailyRepository.findByCampaignIdAndNgayBetween(
                        campaignId, 
                        LocalDate.of(1970, 1, 1), 
                        LocalDate.now()
                );

        long totalUsages = dailyMetrics.stream()
                .mapToLong(d -> d.getUsageCount() != null ? d.getUsageCount() : 0)
                .sum();

        long totalBookings = dailyMetrics.stream()
                .mapToLong(d -> d.getBookingCount() != null ? d.getBookingCount() : 0)
                .sum();

        Double totalRevenue = dailyMetrics.stream()
                .mapToDouble(d -> d.getGeneratedRevenue() != null ? d.getGeneratedRevenue() : 0.0)
                .sum();

        Double totalDiscount = dailyMetrics.stream()
                .mapToDouble(d -> d.getDiscountCost() != null ? d.getDiscountCost() : 0.0)
                .sum();

        Double conversionRate = totalUsages > 0 ? 
                (double) totalBookings / totalUsages * 100 : 0.0;

        return PromotionAnalyticsSummaryResponse.builder()
                .campaignId(campaignId)
                .totalVoucherUsages(totalUsages)
                .totalBookings(totalBookings)
                .totalGeneratedRevenue(new BigDecimal(totalRevenue))
                .totalDiscountCost(new BigDecimal(totalDiscount))
                .conversionRate(BigDecimal.valueOf(conversionRate))
                .build();
    }

    @Transactional(readOnly = true)
    public List<PromotionAnalyticsDailyResponse> getCampaignAnalyticsDaily(
            Long campaignId, LocalDate fromDate, LocalDate toDate) {
        log.debug("Fetching daily analytics for campaign {} from {} to {}", 
                campaignId, fromDate, toDate);

        List<PromotionAnalyticsDaily> dailyMetrics = 
                promotionAnalyticsDailyRepository.findByCampaignIdAndNgayBetween(
                        campaignId, fromDate, toDate
                );

        return dailyMetrics.stream()
                .map(metric -> PromotionAnalyticsDailyResponse.builder()
                        .date(metric.getNgay())
                        .voucherUsageCount(metric.getUsageCount())
                        .bookingCount(metric.getBookingCount())
                        .generatedRevenue(new BigDecimal(metric.getGeneratedRevenue() != null ? 
                                metric.getGeneratedRevenue() : 0.0))
                        .discountCost(new BigDecimal(metric.getDiscountCost() != null ? 
                                metric.getDiscountCost() : 0.0))
                        .conversionRate(BigDecimal.valueOf(metric.getConversionRate() != null ? 
                                metric.getConversionRate() : 0.0))
                        .build())
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .toList();
    }

    @Transactional
    public void trackVoucherUsage(Long campaignId, String customerId) {
        log.debug("Tracking voucher usage for campaign {} by customer {}", campaignId, customerId);

        // Get or create today's analytics record
        LocalDate today = LocalDate.now();
        PromotionAnalyticsDaily daily = getOrCreateDailyMetric(campaignId, today);

        daily.setUsageCount((daily.getUsageCount() != null ? daily.getUsageCount() : 0) + 1);
        promotionAnalyticsDailyRepository.save(daily);

        log.debug("Voucher usage tracked for campaign {}", campaignId);
    }

    @Transactional
    public void trackBookingCompletion(Long campaignId, Double bookingAmount, Double discountAmount) {
        log.debug("Tracking booking completion for campaign {} - Amount: {}, Discount: {}", 
                campaignId, bookingAmount, discountAmount);

        LocalDate today = LocalDate.now();
        PromotionAnalyticsDaily daily = getOrCreateDailyMetric(campaignId, today);

        daily.setBookingCount((daily.getBookingCount() != null ? daily.getBookingCount() : 0) + 1);
        daily.setGeneratedRevenue((daily.getGeneratedRevenue() != null ? daily.getGeneratedRevenue() : 0.0) 
                + (bookingAmount != null ? bookingAmount : 0.0));
        daily.setDiscountCost((daily.getDiscountCost() != null ? daily.getDiscountCost() : 0.0) 
                + (discountAmount != null ? discountAmount : 0.0));

        if (daily.getUsageCount() != null && daily.getUsageCount() > 0) {
            daily.setConversionRate((double) daily.getBookingCount() / daily.getUsageCount() * 100);
        }

        promotionAnalyticsDailyRepository.save(daily);
        log.debug("Booking completion tracked for campaign {}", campaignId);
    }

    private PromotionAnalyticsDaily getOrCreateDailyMetric(Long campaignId, LocalDate date) {
        return promotionAnalyticsDailyRepository.findByCampaignIdAndNgay(campaignId, date)
                .orElseGet(() -> {
                    PromotionAnalyticsDaily daily = new PromotionAnalyticsDaily();
                    daily.setCampaignId(campaignId);
                    daily.setNgay(date);
                    daily.setUsageCount(0);
                    daily.setBookingCount(0);
                    daily.setGeneratedRevenue(0.0);
                    daily.setDiscountCost(0.0);
                    daily.setConversionRate(0.0);
                    return daily;
                });
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class PromotionAnalyticsSummaryResponse {
        private Long campaignId;
        private Long totalVoucherUsages;
        private Long totalBookings;
        private BigDecimal totalGeneratedRevenue;
        private BigDecimal totalDiscountCost;
        private BigDecimal conversionRate;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class PromotionAnalyticsDailyResponse {
        private LocalDate date;
        private Integer voucherUsageCount;
        private Integer bookingCount;
        private BigDecimal generatedRevenue;
        private BigDecimal discountCost;
        private BigDecimal conversionRate;
    }
}
