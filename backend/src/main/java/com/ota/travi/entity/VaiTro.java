package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "vai_tro")
public class VaiTro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @CreationTimestamp
    private Timestamp ngayTao;

    public VaiTro() {
    }

    public VaiTro(String id, String ten, String moTa, Timestamp ngayTao) {
        this.id = id;
        this.ten = ten;
        this.moTa = moTa;
        this.ngayTao = ngayTao;
    }


}
