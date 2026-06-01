package com.ota.travi.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ho_so_ai")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HoSoAI {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ho_so_ai")
    private String idHoSoAI;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id", nullable = false, unique = true)
    private KhachHang khachHang;

    @ElementCollection
    @CollectionTable(name = "ho_so_ai_lich_su_click", joinColumns = @JoinColumn(name = "ho_so_ai_id"))
    @Column(name = "id_tai_san")
    private List<String> lichSuClick = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ho_so_ai_tu_khoa_tim_kiem", joinColumns = @JoinColumn(name = "ho_so_ai_id"))
    @Column(name = "tu_khoa")
    private List<String> tuKhoaTimKiem = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String soThichTongHop;

    @Column(nullable = false, length = 50)
    private String trangThai = "KHOI_DONG_NGUOI_DUNG";

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
