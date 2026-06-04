package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Table(name = "quan_tri_vien")
@Data
@EqualsAndHashCode(callSuper = true)
public class QuanTriVien extends User {
    private Integer capDoQuyen = 1;

    @OneToMany(mappedBy = "admin")
    private List<LichSuThaoTac> danhSachThaoTac;
}