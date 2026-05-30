package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "vai_tro")
@NoArgsConstructor
@AllArgsConstructor
public class VaiTro {
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    public String getTen() { return this.ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getMoTa() { return this.moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @CreationTimestamp
    private Timestamp ngayTao;



}
