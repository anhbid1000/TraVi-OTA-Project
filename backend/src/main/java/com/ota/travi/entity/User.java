package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ota.travi.enums.GioiTinh;
import com.ota.travi.enums.TrangThaiUser;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // Chiến lược kế thừa JOIN
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;

    @Column(unique = true, nullable = false)
    protected String email;

    @Column(unique = true, nullable = false)
    protected String username;

    @Column(nullable = false)
    protected String matKhau;

    @Column(nullable = false)
    protected String hoTen;

    protected LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    protected GioiTinh gioiTinh;

    protected String soDienThoai;

    @Enumerated(EnumType.STRING)
    protected TrangThaiUser trangThai = TrangThaiUser.CHUA_XAC_THUC;

    protected Integer soLanDangNhapSai = 0;
    protected LocalDateTime lanCuoiDangNhap;

    @CreationTimestamp
    protected LocalDateTime ngayTao;

    @UpdateTimestamp
    protected LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vai_tro_id")
    protected VaiTro vaiTro;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public TrangThaiUser getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiUser trangThai) { this.trangThai = trangThai; }
    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }


}
