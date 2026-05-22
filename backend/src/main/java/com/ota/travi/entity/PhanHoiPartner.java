package com.ota.travi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "phan_hoi_partner")
public class PhanHoiPartner {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(columnDefinition = "TEXT")
    private String noiDung;
    private LocalDateTime ngayTao = LocalDateTime.now();
    // Getters, Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}
