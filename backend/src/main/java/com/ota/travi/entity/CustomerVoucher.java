package com.ota.travi.entity;

import com.ota.travi.enums.TrangThaiCustomerVoucher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_voucher")
@Getter
@Setter
public class CustomerVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private UuDai voucher; // Móc sang ID bảng uu_dai/voucher

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private KhachHang khachHang;

    @Column(name = "ma_voucher_ca_nhan", length = 100)
    private String maVoucherCaNhan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiCustomerVoucher trangThai = TrangThaiCustomerVoucher.CHUA_DUNG;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "booking_id")
    private Long bookingId;
}