package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import com.ota.travi.enums.LoaiGiaoDichDiem;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_diem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichSuDiem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "so_diem_thay_doi", nullable = false)
    private Integer soDiemThayDoi;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giao_dich_diem", nullable = false)
    private LoaiGiaoDichDiem loaiGiaoDichDiem;

    @Column(name = "diem_truoc_giao_dich")
    private Integer diemTruocGiaoDich;

    @Column(name = "diem_sau_giao_dich")
    private Integer diemSauGiaoDich;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
