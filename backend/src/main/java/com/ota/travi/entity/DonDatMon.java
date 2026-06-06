package com.ota.travi.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "don_dat_mon")
@Getter
@Setter
@NoArgsConstructor
public class DonDatMon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_dat_cho_id", nullable = false)
    private DonDatCho donDatCho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nha_hang_id", nullable = false)
    private NhaHang nhaHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_id")
    private Ban ban;

    private LocalDateTime thoiGianDat;

    private String ghiChu;

    @OneToMany(mappedBy = "donDatMon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDonDatMon> chiTietDonDatMon = new ArrayList<>();
}
