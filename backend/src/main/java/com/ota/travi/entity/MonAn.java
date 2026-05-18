package com.ota.travi.entity;

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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ota.travi.enums.TrangThaiMonAn;

@Entity
@Table(name = "mon_an")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonAn {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thuc_don_id", nullable = false)
    private ThucDon thucDon;

    @Column(nullable = false)
    private String tenMon;

    private Double giaBan;

    @Enumerated(EnumType.STRING)
    private TrangThaiMonAn trangThai = TrangThaiMonAn.CO_SAN;

    @Column(name = "duong_dan_url")
    private String duongDanUrl;

    @ElementCollection
    @CollectionTable(name = "mon_an_the_ngu_canh", joinColumns = @JoinColumn(name = "mon_an_id"))
    @Column(name = "the_ngu_canh")
    private List<String> theNguCanh = new ArrayList<>();

    @ManyToMany(mappedBy = "monAn")
    private Set<Combo> combo = new HashSet<>();
}
