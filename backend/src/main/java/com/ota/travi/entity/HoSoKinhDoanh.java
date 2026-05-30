package com.ota.travi.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiHoatDong;
import com.ota.travi.enums.TrangThaiKiemDuyet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ho_so_kinh_doanh")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HoSoKinhDoanh {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ho_so")
    private String idHoSo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doi_tac_id", nullable = false)
    private DoiTac doiTac;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoaiDichVu loaiDichVu;

    @Column(nullable = false)
    private String tenCoSo;

    @Column(nullable = false, unique = true, length = 50)
    private String maSoThue;

    @Column(nullable = false)
    private String giayPhepKinhDoanh;

    @Column(name = "toa_do_gps", nullable = false)
    private String toaDoGPS;

    @Column(nullable = false, length = 20)
    private String sdtLienHe;

    @Column(length = 255)
    private String emailLienHe;

    @Column(length = 255)
    private String diaChi;

    @Column(length = 100)
    private String thanhPho;

    @Column(length = 100)
    private String quanHuyen;

    @Column(length = 100)
    private String phuongXa;

    private Double kinhDo;

    private Double viDo;

    private String oldTenCoSo;

    private String oldSdtLienHe;

    private String oldLoaiDichVu;

    private String oldMaSoThue;

    private String oldGiayPhepKinhDoanh;

    private String oldToaDoGPS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiKiemDuyet trangThaiKiemDuyet = TrangThaiKiemDuyet.CHO_DUYET;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiHoatDong trangThaiHoatDong = TrangThaiHoatDong.CHUA_HOAT_DONG;

    @Column(length = 1000)
    private String lyDoTuChoiGanNhat;

    @CreationTimestamp
    private LocalDateTime thoiGianDangKy;

    @UpdateTimestamp
    private LocalDateTime thoiGianCapNhat;

    private LocalDateTime thoiGianDuyet;

    @Column(nullable = false)
    private Boolean deleted = false;

    @OneToOne(mappedBy = "hoSoKinhDoanh", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChinhSach chinhSach;

    @OneToOne(mappedBy = "hoSoKinhDoanh", cascade = CascadeType.ALL, orphanRemoval = true)
    private TaiSan taiSan;

    @OneToMany(mappedBy = "hoSoKinhDoanh", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LichSuKiemDuyetHoSo> lichSuKiemDuyet = new ArrayList<>();
}
