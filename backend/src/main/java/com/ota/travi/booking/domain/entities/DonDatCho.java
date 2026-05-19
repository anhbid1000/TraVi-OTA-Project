package com.traviota.booking.domain.entities;

import com.traviota.booking.domain.enums.TrangThaiDon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "don_dat_cho")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class DonDatCho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String maDon;

    @Column(nullable = false, updatable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(nullable = false)
    private BigDecimal tongTien;

    private BigDecimal tienKhuyenMai = BigDecimal.ZERO;

    @Column(nullable = false)
    private String tenNguoiDat;

    @Column(nullable = false)
    private String sdtNguoiDat;

    private String emailNguoiDat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiDon trangThai = TrangThaiDon.CHO_THANH_TOAN;
}