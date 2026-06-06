package com.ota.travi.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "khach_san")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KhachSan extends TaiSan {
    @Column(nullable = false)
    private String ten;

    private Integer hangSao;

    private String loaiKhachSan;

    private LocalTime gioNhanPhong;

    private LocalTime gioTraPhong;

    private LocalTime gioNhanPhongMacDinh;

    private LocalTime gioTraPhongMacDinh;

    private Integer soTang;

    private Integer tongSoPhong;

    @OneToMany(mappedBy = "khachSan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phong> danhSachPhong = new ArrayList<>();

    @OneToMany(mappedBy = "khachSan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnhKhachSan> danhSachAnh = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "khach_san_tien_ich",
            joinColumns = @JoinColumn(name = "khach_san_id"),
            inverseJoinColumns = @JoinColumn(name = "tien_ich_id")
    )
    private Set<TienIchKhachSan> tienIch = new HashSet<>();
}
