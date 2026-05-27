package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_rule")
@Getter
@Setter
public class LoyaltyRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "money_per_point", nullable = false)
    private BigDecimal moneyPerPoint;

    @Column(name = "silver_threshold", nullable = false)
    private BigDecimal silverThreshold;

    @Column(name = "gold_threshold", nullable = false)
    private BigDecimal goldThreshold;

    @Column(name = "diamond_threshold", nullable = false)
    private BigDecimal diamondThreshold;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}