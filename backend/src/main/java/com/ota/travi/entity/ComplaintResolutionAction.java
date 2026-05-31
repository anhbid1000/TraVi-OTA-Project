package com.ota.travi.entity;

import com.ota.travi.enums.ComplaintResolutionActionStatus;
import com.ota.travi.enums.ComplaintResolutionActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "hanh_dong_xu_ly_khieu_nai")
public class ComplaintResolutionAction {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khieu_nai_id", nullable = false)
    private Complaint complaint;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ComplaintResolutionActionType actionType;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "mo_ta", nullable = false, columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "voucher_code", length = 100)
    private String voucherCode;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ComplaintResolutionActionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposed_by_partner_id", nullable = false)
    private DoiTac proposedByPartner;

    @Column(name = "customer_response_note", columnDefinition = "TEXT")
    private String customerResponseNote;

    @Column(name = "partner_completion_note", columnDefinition = "TEXT")
    private String partnerCompletionNote;

    @Column(name = "proposed_at", nullable = false)
    private LocalDateTime proposedAt;

    @Column(name = "customer_responded_at")
    private LocalDateTime customerRespondedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
