package com.ota.travi.booking.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "BookingGiaoDichThanhToan")
@Table(name = "booking_giao_dich_thanh_toan")
@Getter
@Setter
public class GiaoDichThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_dat_cho_id", nullable = false)
    private DonDatCho donDatCho;

    private String maGiaoDichNganHang;
    private String phuongThuc;
    private BigDecimal soTien;
}
