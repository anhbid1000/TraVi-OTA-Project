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

import com.ota.travi.enums.LoaiDichVu;
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

    private String oldTenCoSo;

    private String oldSdtLienHe;

    private String oldLoaiDichVu;

    private String oldMaSoThue;

    private String oldGiayPhepKinhDoanh;

    private String oldToaDoGPS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiKiemDuyet trangThaiKiemDuyet = TrangThaiKiemDuyet.CHO_DUYET;

    @CreationTimestamp
    private LocalDateTime thoiGianDangKy;

    private LocalDateTime thoiGianDuyet;

    @OneToOne(mappedBy = "hoSoKinhDoanh", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChinhSach chinhSach;

    @OneToMany(mappedBy = "hoSoKinhDoanh", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaiSan> danhSachTaiSan = new ArrayList<>();
}
