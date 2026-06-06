package com.ota.travi.entity;

import jakarta.persistence.Entity;
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

@Entity
@Table(name = "don_nha_hang_ban")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonNhaHangBan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_nha_hang_id", nullable = false)
    private DonNhaHang donNhaHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ban_id", nullable = false)
    private Ban ban;
}

