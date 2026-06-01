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
@Table(name = "milestone_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false, unique = true)
    private String customerId;

    @Column(name = "completed_bookings", nullable = false)
    private Integer completedBookings = 0;

    @Column(name = "milestone_5_granted", nullable = false)
    private Boolean milestone5Granted = false;

    @Column(name = "milestone_5_granted_at")
    private LocalDateTime milestone5GrantedAt;

    @Column(name = "milestone_10_granted", nullable = false)
    private Boolean milestone10Granted = false;

    @Column(name = "milestone_10_granted_at")
    private LocalDateTime milestone10GrantedAt;

    @Column(name = "milestone_20_granted", nullable = false)
    private Boolean milestone20Granted = false;

    @Column(name = "milestone_20_granted_at")
    private LocalDateTime milestone20GrantedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
