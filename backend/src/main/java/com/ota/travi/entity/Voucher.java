package com.ota.travi.entity;


import com.ota.travi.enums.PhamViApDung;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "voucher")
@PrimaryKeyJoinColumn(name = "uu_dai_id")
@Getter
@Setter
public class Voucher extends UuDai {

    @Column(name = "ma_voucher", unique = true, nullable = false, length = 50)
    private String maVoucher;

    @Column(name = "so_luong_phat_hanh", nullable = false)
    private Integer soLuongPhatHanh;

    @Column(name = "so_luong_da_dung")
    private Integer soLuongDaDung = 0;

    @Column(name = "don_hang_toi_thieu")
    private BigDecimal donHangToiThieu = BigDecimal.ZERO;

    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser = 1;

    @Column(name = "diem_can_doi")
    private Integer diemCanDoi = 0;

    @Column(name = "cho_phep_doi_bang_diem")
    private Boolean choPhepDoiBangDiem = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "pham_vi_ap_dung", nullable = false)
    private PhamViApDung phamViApDung;
}