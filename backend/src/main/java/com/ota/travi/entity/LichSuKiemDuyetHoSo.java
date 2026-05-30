package com.ota.travi.entity;

import com.ota.travi.enums.TrangThaiKiemDuyet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_kiem_duyet_ho_so")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LichSuKiemDuyetHoSo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ho_so_kinh_doanh_id", nullable = false)
    private HoSoKinhDoanh hoSoKinhDoanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private QuanTriVien admin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiKiemDuyet trangThaiCu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiKiemDuyet trangThaiMoi;

    @Column(length = 1000)
    private String lyDo;

    @Column(length = 1000)
    private String ghiChuNoiBo;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

