package com.ota.travi.entity;

import com.ota.travi.Enum.TrangThaiPhanHoi;
import jakarta.persistence.*;

@Entity
@Table(name = "danh_gia")
public class DanhGia extends PhanHoiUser {
    @Column(name = "so_sao")
    private Integer soSao;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "phan_hoi_partner_id")

    private PhanHoiPartner phanHoiPartner;
    public Integer getSoSao() { return soSao; }
    public void setSoSao(Integer soSao) { this.soSao = soSao; }
    public PhanHoiPartner getPhanHoiPartner() { return phanHoiPartner; }
    public void setPhanHoiPartner(PhanHoiPartner phanHoiPartner) { this.phanHoiPartner = phanHoiPartner; }
}
