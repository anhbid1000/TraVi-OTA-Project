package com.ota.travi.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "don_nha_hang")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonNhaHang extends DonDatCho {
    private LocalDateTime ngayGioBatDau;

    private LocalDateTime ngayGioKetThuc;

    private Integer soNguoi;

    private Double tienCoc;

    private Boolean coDatMonTruoc;

    @OneToMany(mappedBy = "donNhaHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DonNhaHangBan> banDaGan = new ArrayList<>();
}

