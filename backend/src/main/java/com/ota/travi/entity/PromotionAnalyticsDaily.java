package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "promotion_analytics_daily")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionAnalyticsDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(nullable = false)
    private LocalDate ngay;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    @Column(name = "booking_count", nullable = false)
    private Integer bookingCount = 0;

    @Column(name = "generated_revenue")
    private Double generatedRevenue = 0.0;

    @Column(name = "discount_cost")
    private Double discountCost = 0.0;

    @Column(name = "conversion_rate")
    private Double conversionRate = 0.0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
