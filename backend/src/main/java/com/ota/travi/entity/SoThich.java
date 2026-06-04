package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "so_thich")
@Data
public class SoThich {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String tenSoThich;

    @ManyToOne
    @JoinColumn(name = "danh_muc_id")
    private DanhMucSoThich danhMuc;

    @ManyToMany(mappedBy = "danhSachSoThich", fetch = FetchType.LAZY)
    private Set<KhachHang> danhSachKhachHang;
}