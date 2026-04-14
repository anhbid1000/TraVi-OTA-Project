package com.ota.travi.Entity;

import com.ota.travi.Enum.GioiTinh;
import com.ota.travi.Enum.TrangThaiUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Date;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // Chiến lược kế thừa JOIN
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;

    @Column(nullable = false, unique = true)
    protected String username;

    @Column(nullable = false, unique = true)
    protected String email;

    @Column(nullable = false)
    protected String matKhau;

    protected String hoTen;
    protected Date ngaySinh;

    @Enumerated(EnumType.STRING)
    protected GioiTinh gioiTinh;

    protected String soDienThoai;


    @Enumerated(EnumType.STRING)
    protected TrangThaiUser trangThai = TrangThaiUser.CHUA_XAC_THUC;

    protected Integer soLanDangNhapSai = 0;

    protected Timestamp lanCuoiDangNhap;

    @CreationTimestamp
    protected Timestamp ngayTao;

    @UpdateTimestamp
    protected Timestamp ngayCapNhat;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vai_tro_id")
    protected VaiTro vaiTro;


}
