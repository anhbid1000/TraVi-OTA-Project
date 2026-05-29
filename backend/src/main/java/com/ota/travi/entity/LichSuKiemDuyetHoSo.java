package com.ota.travi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * DEPRECATED: LichSuKiemDuyetHoSo được giữ lại chỉ để backward compatibility.
 * Module 1 hiện không còn approval flow từ admin.
 * 
 * Lý do: Đã bỏ admin approval flow, chuyển sang partner-only asset management.
 * Nếu cần audit trail trong tương lai, nên tạo entity riêng cho partner actions.
 */
@Deprecated(since = "2024", forRemoval = true)
@Entity
@Table(name = "lich_su_kiem_duyet_ho_so")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LichSuKiemDuyetHoSo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ho_so_kinh_doanh_id", nullable = false)
    private HoSoKinhDoanh hoSoKinhDoanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private QuanTriVien admin;

    @Column(length = 1000)
    private String lyDo;

    @Column(length = 1000)
    private String ghiChuNoiBo;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

