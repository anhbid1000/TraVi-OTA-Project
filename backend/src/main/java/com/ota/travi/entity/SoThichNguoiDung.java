package com.ota.travi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "so_thich_nguoi_dung",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "danh_muc"},
                name = "uk_so_thich_user_danh_muc"
        )
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SoThichNguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String danhMuc;

    @Column(nullable = false)
    private Integer diemSo = 0;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime capNhatCuoi;
}
