package com.ota.travi.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_giai_trinh")
public class LichSuGiaiTrinh {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(columnDefinition = "TEXT")
    private String noiDung;
    private LocalDateTime ngayNop = LocalDateTime.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public LocalDateTime getNgayNop() { return ngayNop; }
    public void setNgayNop(LocalDateTime ngayNop) { this.ngayNop = ngayNop; }
}
