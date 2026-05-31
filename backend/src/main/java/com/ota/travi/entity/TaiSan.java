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
    public String getIdTaiSan() { return this.idTaiSan; }
    public void setIdTaiSan(String idTaiSan) { this.idTaiSan = idTaiSan; }

    public HoSoKinhDoanh getHoSoKinhDoanh() { return this.hoSoKinhDoanh; }
    public void setHoSoKinhDoanh(HoSoKinhDoanh hoSoKinhDoanh) { this.hoSoKinhDoanh = hoSoKinhDoanh; }

    public String getMoTa() { return this.moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public Double getRatingAverage() { return this.ratingAverage; }
    public void setRatingAverage(Double ratingAverage) { this.ratingAverage = ratingAverage; }

    public Integer getReviewCount() { return this.reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

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
