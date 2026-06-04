package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_thao_tac")
@Data
public class LichSuThaoTac {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idLog;

    private String hanhDong;

    @CreationTimestamp
    private LocalDateTime thoiGian;

    @ManyToOne
    @JoinColumn(name = "id_admin")
    private QuanTriVien admin;
}