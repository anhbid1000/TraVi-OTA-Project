package com.ota.travi.entity;

import com.ota.travi.enums.TrangThaiBan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ban")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Ban {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nha_hang_id", nullable = false)
    private NhaHang nhaHang;

    private String tenBan;

    private String viTriSanh;

    @Column(length = 1000)
    private String moTa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiBan trangThai = TrangThaiBan.SAN_SANG;

    private Integer soChoNgoi;

    @Column(nullable = false)
    private Boolean deleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
