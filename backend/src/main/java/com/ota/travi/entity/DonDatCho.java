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

    @Column(unique = true)
    private String maDon;

    @CreationTimestamp
    private LocalDateTime ngayLap;

    private Double tongTien = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiDonDatCho trangThai = TrangThaiDonDatCho.DANG_CHO;

    private Double soTienDaThanhToan = 0.0;

    private String emailNguoiDat;

    private String sdtNguoiDat;

    @OneToMany(mappedBy = "donDatCho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DatPhong> datPhong = new ArrayList<>();

    @OneToMany(mappedBy = "donDatCho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DonDatMon> donDatMon = new ArrayList<>();
}
