package com.ota.travi.entity;

import com.ota.travi.enums.TrangThaiDon;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "don_dat_cho")
@Inheritance(strategy = InheritanceType.JOINED)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class DonDatCho {
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
    private TrangThaiDon trangThai = TrangThaiDon.CHO_THANH_TOAN;

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
