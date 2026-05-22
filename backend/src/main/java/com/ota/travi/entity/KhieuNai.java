package com.ota.travi.entity;

import com.ota.travi.Enum.MucDoUuTien;
import com.ota.travi.Enum.TrangThaiPhanHoi;
import jakarta.persistence.*;

@Entity
@Table(name = "khieu_nai")
public class KhieuNai extends PhanHoiUser {
    @Enumerated(EnumType.STRING)
    @Column(name = "muc_do_uu_tien")
    private MucDoUuTien mucDoUuTien;
    @Column(name = "id_nguoi_giai_quyet")
    private String idNguoiGiaiQuyet;
    public MucDoUuTien getMucDoUuTien() { return mucDoUuTien; }
    public void setMucDoUuTien(MucDoUuTien mucDoUuTien) { this.mucDoUuTien = mucDoUuTien; }
    public String getIdNguoiGiaiQuyet() { return idNguoiGiaiQuyet; }
    public void setIdNguoiGiaiQuyet(String idNguoiGiaiQuyet) { this.idNguoiGiaiQuyet = idNguoiGiaiQuyet; }
}
