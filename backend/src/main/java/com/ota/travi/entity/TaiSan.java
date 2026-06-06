package com.ota.travi.entity;

import com.ota.travi.enums.TrangThaiTaiSan;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tai_san")
@Inheritance(strategy = InheritanceType.JOINED)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class TaiSan {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tai_san")
    protected String idTaiSan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ho_so_kinh_doanh_id", nullable = false, unique = true)
    protected HoSoKinhDoanh hoSoKinhDoanh;

    protected String moTa;

    @Enumerated(EnumType.STRING)
    protected TrangThaiTaiSan trangThai = TrangThaiTaiSan.SAN_SANG;

    protected Double giaCoBan;

    @Column(name = "is_dynamic_pricing", nullable = false)
    protected Boolean isDynamicPricing = false;

    // Module 5: Rating fields
    @Column(name = "rating_average")
    protected Double ratingAverage = 0.0;

    @Column(name = "review_count")
    protected Integer reviewCount = 0;
}
