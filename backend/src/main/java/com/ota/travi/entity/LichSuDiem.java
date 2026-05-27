package com.ota.travi.entity;

import com.ota.travi.enums.LoaiGiaoDichDiem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_diem")
@Getter
@Setter
public class LichSuDiem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private KhachHang khachHang;

    @Column(name = "so_diem_thay_doi", nullable = false)
    private Integer soDiemThayDoi;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giao_dich_diem", nullable = false)
    private LoaiGiaoDichDiem loaiGiaoDichDiem;

    @Column(name = "diem_truoc_giao_dich", nullable = false)
    private Integer diemTruocGiaoDich;

    @Column(name = "diem_sau_giao_dich", nullable = false)
    private Integer diemSauGiaoDich;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}