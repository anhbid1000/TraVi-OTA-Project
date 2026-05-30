package com.ota.travi.entity;

import com.ota.travi.enums.ReviewAspectType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(
    name = "review_aspect_score",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_review_aspect", columnNames = {"review_id", "aspect"})
    }
)
public class ReviewAspectScore {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Enumerated(EnumType.STRING)
    @Column(name = "aspect", nullable = false, length = 50)
    private ReviewAspectType aspect;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }
}
