package com.ota.travi.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ota.travi.enums.TienIchPhong;
import com.ota.travi.enums.TrangThaiPhong;

@Entity
@Table(name = "phong")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Phong {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_san_id", nullable = false)
    private KhachSan khachSan;

    @Column(nullable = false)
    private String soPhong;

    @Column(nullable = false)
    private String tenPhong;

    @Column(nullable = false)
    private String loaiPhong;

    @Column(length = 1000)
    private String moTa;

    private Integer sucChuaToiDa;

    private Integer soGiuong;

    private Float dienTich;

    private Double giaCoBan;

    private Integer soLuongPhong;

    @Enumerated(EnumType.STRING)
    private TrangThaiPhong trangThai = TrangThaiPhong.SAN_SANG;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "phong_tien_ich", joinColumns = @JoinColumn(name = "phong_id"))
    @Column(name = "tien_ich")
    private Set<TienIchPhong> tienIch = new HashSet<>();

    private Float phanTramGiamGia = 0.0f;

    @Column(nullable = false)
    private Boolean deleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "phong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnhPhong> danhSachAnh = new ArrayList<>();
}
