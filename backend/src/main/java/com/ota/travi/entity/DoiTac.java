package com.ota.travi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "doi_tac")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DoiTac extends User {
    private Float tiLeChietKhau = 0.0f;

    public Float getTiLeChietKhau() { return tiLeChietKhau; }
    public void setTiLeChietKhau(Float tiLeChietKhau) { this.tiLeChietKhau = tiLeChietKhau; }
}
