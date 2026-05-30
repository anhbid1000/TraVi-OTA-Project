package com.ota.travi.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "don_khach_san")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonKhachSan extends DonDatCho {
    private LocalDate ngayCheckIn;

    private LocalDate ngayCheckOut;

    private Integer soDem;

    private Integer soKhach;

    private LocalTime gioNhanPhongDuKien;

    @OneToMany(mappedBy = "donKhachSan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DonKhachSanChiTiet> chiTietDon = new ArrayList<>();
}

