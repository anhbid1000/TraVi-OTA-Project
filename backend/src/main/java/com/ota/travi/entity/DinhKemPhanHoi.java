package com.ota.travi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dinh_kem_phan_hoi")
public class DinhKemPhanHoi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String loaiFile;
    private String duongDanUrl;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLoaiFile() { return loaiFile; }
    public void setLoaiFile(String loaiFile) { this.loaiFile = loaiFile; }
    public String getDuongDanUrl() { return duongDanUrl; }
    public void setDuongDanUrl(String duongDanUrl) { this.duongDanUrl = duongDanUrl; }
}
