package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ota.travi.enums.TrangThaiUuDai;
import com.ota.travi.enums.LoaiGiamGia;
import com.ota.travi.enums.CreatedByRole;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "uu_dai")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UuDai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenUuDai;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "created_by_user_id")
    private String createdByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by_role")
    private CreatedByRole createdByRole;

    @Column(name = "business_profile_id")
    private Long businessProfileId;

    @Column(nullable = false)
    private Double mucGiam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoaiGiamGia loaiGiamGia;

    @Column(name = "gia_tri_giam_toi_da")
    private Double giaTriGiamToiDa;

    @Column(nullable = false)
    private LocalDate ngayBatDau;

    @Column(nullable = false)
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiUuDai trangThaiUuDai = TrangThaiUuDai.DA_LEN_LICH;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Version
    @Column(name = "version")
    private Long version;
}
