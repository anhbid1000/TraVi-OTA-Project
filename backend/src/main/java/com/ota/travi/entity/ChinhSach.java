package com.ota.travi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "chinh_sach")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChinhSach {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String loaiChinhSach;

    @Column(columnDefinition = "TEXT")
    private String noiDung;

    private LocalDate ngayApDung;

    private LocalTime gioNhanPhong;

    private LocalTime gioTraPhong;

    private LocalTime gioMoCua;

    private LocalTime gioDongCua;

    @Column(columnDefinition = "TEXT")
    private String chinhSachHuy;

    @Column(columnDefinition = "TEXT")
    private String chinhSachHoanTien;

    @Column(columnDefinition = "TEXT")
    private String quyDinhTreEm;

    @Column(columnDefinition = "TEXT")
    private String quyDinhVatNuoi;

    @Column(columnDefinition = "TEXT")
    private String ghiChuKhac;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ho_so_kinh_doanh_id", nullable = false, unique = true)
    private HoSoKinhDoanh hoSoKinhDoanh;
}
