package com.ota.travi.entity;

import com.ota.travi.enums.TrangThaiDonDatCho;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "don_dat_cho")
@Getter
@Setter
@NoArgsConstructor
public class DonDatCho {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String maDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ho_so_kinh_doanh_id", nullable = false)
    private HoSoKinhDoanh hoSoKinhDoanh;

    @Column(nullable = false)
    private LocalDateTime ngayTao;

    private Double tongTienGoc;

    private Double tienKhuyenMai;

    private Double tongTienThanhToan;

    private String tenNguoiDat;

    private String sdtNguoiDat;

    private String emailNguoiDat;

    @Column(length = 1000)
    private String ghiChu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiDon trangThaiDon = TrangThaiDon.CHO_THANH_TOAN;

    private LocalDateTime holdExpiredAt;

    private LocalDateTime paymentExpiredAt;

    private LocalDateTime cancelledAt;

    @Column(length = 500)
    private String cancelReason;

    @Column(nullable = false)
    private Boolean deleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

