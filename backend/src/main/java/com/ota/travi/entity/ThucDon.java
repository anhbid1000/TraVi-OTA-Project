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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "thuc_don")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ThucDon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nha_hang_id", nullable = false)
    private NhaHang nhaHang;

    private String phanLoai;

    @OneToMany(mappedBy = "thucDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MonAn> monAn = new ArrayList<>();

    @OneToMany(mappedBy = "thucDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Combo> combo = new ArrayList<>();
}
