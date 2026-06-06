package com.ota.travi.entity;

import jakarta.persistence.Entity;
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

@Entity
@Table(name = "don_khach_san_chi_tiet")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonKhachSanChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_khach_san_id", nullable = false)
    private DonKhachSan donKhachSan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    private Phong phong;

    private String tenPhongTaiThoiDiemDat;

    private Integer soLuong;

    private Double donGiaTaiThoiDiemDat;

    private Integer soDem;

    private Double thanhTien;
}

