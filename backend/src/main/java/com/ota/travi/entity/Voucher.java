package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.ota.travi.enums.PhamViApDung;

@Entity
@Table(name = "voucher")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher extends UuDai {
    @Column(unique = true, nullable = false)
    private String maVoucher;

    @Column(nullable = false)
    private Integer soLuongPhatHanh;

    @Column(nullable = false)
    private Integer soLuongDaDung = 0;

    @Column(name = "don_hang_toi_thieu")
    private Double donHangToiThieu = 0.0;

    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser;

    @Column(name = "diem_can_doi")
    private Integer diemCanDoi;

    @Column(name = "cho_phep_doi_bang_diem")
    private Boolean choPhepDoiBangDiem = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "pham_vi_ap_dung")
    private PhamViApDung phamViApDung;
}
