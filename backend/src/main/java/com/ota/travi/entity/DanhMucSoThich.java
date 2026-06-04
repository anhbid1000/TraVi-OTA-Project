package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "danh_muc_so_thich")
@Data
public class DanhMucSoThich {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String tenDanhMuc;
    private String moTa;

    @OneToMany(mappedBy = "danhMuc", cascade = CascadeType.ALL)
    private List<SoThich> danhSachSoThich;
}
