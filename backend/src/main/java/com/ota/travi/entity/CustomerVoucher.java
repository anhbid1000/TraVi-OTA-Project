package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.enums.SourceTypeVoucher;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_voucher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;

    @Column(name = "ma_voucher_ca_nhan", unique = true)
    private String maVoucherCaNhan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiCustomerVoucher trangThai = TrangThaiCustomerVoucher.CHUA_DUNG;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private SourceTypeVoucher sourceType;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;

    @Column(name = "reserve_expires_at")
    private LocalDateTime reserveExpiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "booking_id")
    private Long bookingId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
