package com.ota.travi.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

import com.ota.travi.enums.HangThanhVien;

@Entity
@Table(name = "khach_hang")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KhachHang extends User{

    private Integer diemThanhVien = 0;

    @Enumerated(EnumType.STRING)
    private HangThanhVien hangThanhVien = HangThanhVien.DONG;

    private Double tongChiTieu = 0.0;

    @Column(name = "da_hoan_thanh_onboarding")
    private Boolean daHoanThanhOnboarding = false;

    @ElementCollection
    @CollectionTable(name = "khach_hang_tu_khoa", joinColumns = @JoinColumn(name = "khach_hang_id"))
    @Column(name = "tu_khoa")
    private List<String> tuKhoaGanDay;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "khach_hang_so_thich",
            joinColumns = @JoinColumn(name = "khach_hang_id"),
            inverseJoinColumns = @JoinColumn(name = "so_thich_id")
    )
    private Set<SoThich> danhSachSoThich;

    public Integer getDiemThanhVien() { return diemThanhVien; }
    public void setDiemThanhVien(Integer diemThanhVien) { this.diemThanhVien = diemThanhVien; }
    public HangThanhVien getHangThanhVien() { return hangThanhVien; }
    public void setHangThanhVien(HangThanhVien hangThanhVien) { this.hangThanhVien = hangThanhVien; }
    public Double getTongChiTieu() { return tongChiTieu; }
    public void setTongChiTieu(Double tongChiTieu) { this.tongChiTieu = tongChiTieu; }
}
