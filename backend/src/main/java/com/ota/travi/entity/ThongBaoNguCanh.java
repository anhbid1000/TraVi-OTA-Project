package com.ota.travi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao_ngu_canh")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoNguCanh {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_thong_bao")
    private String idThongBao;

    @Column(nullable = false, length = 50)
    private String loaiNguCanh;

    @Column(nullable = false, length = 1000)
    private String noiDung;

    @Column(nullable = false, length = 50)
    private String mucDo;

    private LocalDateTime thoiGianHieuLuc;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
