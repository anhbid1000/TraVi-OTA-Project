package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nha_hang")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NhaHang extends TaiSan {
    // Ten nha hang
    @Column(nullable = false)
    private String ten;

    private String loaiAmThuc;

    private LocalTime gioMoCua;

    private LocalTime gioDongCua;

    private Integer sucChua;

    @Column(nullable = false)
    private Boolean coDatBanTruoc = true;

    @Column(nullable = false)
    private Boolean coDatMonTruoc = true;

    @OneToMany(mappedBy = "nhaHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ban> danhSachBan = new ArrayList<>();

    @OneToMany(mappedBy = "nhaHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnhNhaHang> danhSachAnh = new ArrayList<>();

    @OneToMany(mappedBy = "nhaHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TienIchNhaHang> tienIch = new ArrayList<>();

    @OneToMany(mappedBy = "nhaHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThucDon> thucDon = new ArrayList<>();
}
