package com.ota.travi.entity;

import com.ota.travi.enums.TargetType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "khuyen_mai_truc_tiep")
@PrimaryKeyJoinColumn(name = "uu_dai_id")
@Getter
@Setter
public class KhuyenMaiTrucTiep extends UuDai {

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private String targetId;
}
