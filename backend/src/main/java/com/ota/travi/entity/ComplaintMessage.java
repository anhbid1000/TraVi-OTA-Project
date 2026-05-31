package com.ota.travi.entity;

import com.ota.travi.enums.VaiTroTinNhan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "complaint_tin_nhan")
public class ComplaintMessage {

    // Getters and Setters
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @Column(name = "nguoi_gui_id", nullable = false)
    private String nguoiGuiId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro_nguoi_gui", nullable = false)
    private VaiTroTinNhan vaiTroNguoiGui;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }

    public Complaint getComplaint() { return complaint; }
}
