package com.ota.travi.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "khach_hang")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KhachHang extends User{

    private Integer diemThanhVien = 0;

    private String hangThanhVien;

    private Double tongChiTieu = 0.0;

    @ElementCollection // Dành cho list các String đơn giản
    @CollectionTable(name = "khach_hang_tu_khoa", joinColumns = @JoinColumn(name = "khach_hang_id"))
    @Column(name = "tu_khoa")
    private List<String> tuKhoaGanDay;
}
