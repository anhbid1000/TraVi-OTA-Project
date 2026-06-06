package com.ota.travi.entity;

import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiDanhGia;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "review_danh_gia")
public class Review {

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

    @Column(name = "so_sao", nullable = false)
    private Integer soSao;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiDanhGia trangThai = TrangThaiDanhGia.DA_HIEN_THI;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ReviewReply reply;

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

    public void setId(String id) { this.id = id; }

    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public void setHoSoKinhDoanh(HoSoKinhDoanh hoSoKinhDoanh) { this.hoSoKinhDoanh = hoSoKinhDoanh; }

    public void setLoaiDichVu(LoaiDichVu loaiDichVu) { this.loaiDichVu = loaiDichVu; }

    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public void setSoSao(Integer soSao) { this.soSao = soSao; }

    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public void setTrangThai(TrangThaiDanhGia trangThai) { this.trangThai = trangThai; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void setReply(ReviewReply reply) { this.reply = reply; }

    public TrangThaiDanhGia getTrangThai() { return trangThai; }
    public KhachHang getKhachHang() { return khachHang; }
    public HoSoKinhDoanh getHoSoKinhDoanh() { return hoSoKinhDoanh; }
}
