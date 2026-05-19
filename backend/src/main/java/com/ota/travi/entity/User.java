package com.ota.travi.entity;

import com.ota.travi.Enum.GioiTinh;
import com.ota.travi.Enum.TrangThaiUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@EqualsAndHashCode
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


}
