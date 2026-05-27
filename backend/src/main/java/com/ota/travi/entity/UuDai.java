package com.ota.travi.entity;

import com.ota.travi.enums.CreatedByRole;
import com.ota.travi.enums.LoaiGiamGia;
import com.ota.travi.enums.TrangThaiUuDai;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name= "uu_dai")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class UuDai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_uu_dai", nullable = false)
    private String tenUuDai;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "created_by_user_id", nullable = false)
    private String createdByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by_role", nullable = false)
    private CreatedByRole createdByRole;

    @Column(name = "business_profile_id")
    private String businessProfileId;

    @Column(name = "muc_giam", nullable = false)
    private BigDecimal mucGiam;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam_gia", nullable = false)
    private LoaiGiamGia loaiGiamGia;

    @Column(name = "gia_tri_giam_toi_da")
    private BigDecimal giaTriGiamToiDa;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_uu_dai")
    private TrangThaiUuDai trangThaiUuDai = TrangThaiUuDai.DA_LEN_LICH;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    private boolean deleted = false;

    @Version
    private Long version;

}
