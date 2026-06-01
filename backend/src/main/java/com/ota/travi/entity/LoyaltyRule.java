package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "money_per_point", nullable = false)
    private Double moneyPerPoint;

    @Column(name = "silver_threshold", nullable = false)
    private Integer silverThreshold;

    @Column(name = "gold_threshold", nullable = false)
    private Integer goldThreshold;

    @Column(name = "diamond_threshold", nullable = false)
    private Integer diamondThreshold;

    @Column(name = "silver_multiplier", nullable = false)
    private Double silverMultiplier;

    @Column(name = "gold_multiplier", nullable = false)
    private Double goldMultiplier;

    @Column(name = "diamond_multiplier", nullable = false)
    private Double diamondMultiplier;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
