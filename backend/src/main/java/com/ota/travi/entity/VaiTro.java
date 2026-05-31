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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @CreationTimestamp
    private Timestamp ngayTao;
}
