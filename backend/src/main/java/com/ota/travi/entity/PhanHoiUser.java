package com.ota.travi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.ota.travi.Enum.TrangThaiPhanHoi;

@Entity
@Table(name = "phan_hoi_user")
@Inheritance(strategy = InheritanceType.JOINED)
public class PhanHoiUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(name = "id_user", nullable = false)
    private String idUser;
    @Column(name = "id_don", nullable = false)
    private String idDon;
    @Column(name = "noi_dung", columnDefinition = "TEXT", nullable = false)
    private String noiDung;
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiPhanHoi trangThai;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "phan_hoi_id")
    private List<DinhKemPhanHoi> dinhKems = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }
    public String getIdDon() { return idDon; }
    public void setIdDon(String idDon) { this.idDon = idDon; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public TrangThaiPhanHoi getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhanHoi trangThai) { this.trangThai = trangThai; }
    public List<DinhKemPhanHoi> getDinhKems() { return dinhKems; }
    public void setDinhKems(List<DinhKemPhanHoi> dinhKems) { this.dinhKems = dinhKems; }
}
