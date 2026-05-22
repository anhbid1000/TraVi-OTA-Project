package com.ota.travi.entity;

import com.ota.travi.Enum.TrangThaiPhanHoi;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "to_cao")
public class ToCao extends PhanHoiUser {
    @Column(name = "id_nguoi_giai_quyet")
    private String idNguoiGiaiQuyet;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "to_cao_id")
    private List<LichSuGiaiTrinh> lichSuGiaiTrinhs = new ArrayList<>();;
    public String getIdNguoiGiaiQuyet() { return idNguoiGiaiQuyet; }
    public void setIdNguoiGiaiQuyet(String idNguoiGiaiQuyet) { this.idNguoiGiaiQuyet = idNguoiGiaiQuyet; }
    public List<LichSuGiaiTrinh> getLichSuGiaiTrinhs() { return lichSuGiaiTrinhs; }
    public void setLichSuGiaiTrinhs(List<LichSuGiaiTrinh> lichSuGiaiTrinhs) { this.lichSuGiaiTrinhs = lichSuGiaiTrinhs; }
}
