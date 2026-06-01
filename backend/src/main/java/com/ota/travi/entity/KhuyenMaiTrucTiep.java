package com.ota.travi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.ota.travi.enums.TargetType;

@Entity
@Table(name = "khuyen_mai_truc_tiep")
@PrimaryKeyJoinColumn(name = "uu_dai_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiTrucTiep extends UuDai {
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_id")
    private String targetId;
}
