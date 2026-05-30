package com.ota.travi.entity;

import com.ota.travi.enums.ComplaintCategory;
import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.MucDoKhieuNai;
import com.ota.travi.enums.TrangThaiKhieuNai;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "complaint_khieu_nai")
public class Complaint {

    // Getters and Setters
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ho_so_kinh_doanh_id", nullable = false)
    private HoSoKinhDoanh hoSoKinhDoanh;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_dich_vu", nullable = false)
    private LoaiDichVu loaiDichVu;

    @Column(name = "booking_id")
    private String bookingId;

    @Column(name = "reservation_id")
    private String reservationId;

    @Column(name = "tieu_de", nullable = false, length = 150)
    private String tieuDe;

    @Column(name = "noi_dung_tom_tat", nullable = false, columnDefinition = "TEXT")
    private String noiDungTomTat;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "muc_do", nullable = false)
    private MucDoKhieuNai mucDo;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiKhieuNai trangThai = TrangThaiKhieuNai.CHO_PHAN_HOI;

    @Column(name = "last_customer_message_at")
    private LocalDateTime lastCustomerMessageAt;

    @Column(name = "last_partner_response_at")
    private LocalDateTime lastPartnerResponseAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplaintMessage> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
