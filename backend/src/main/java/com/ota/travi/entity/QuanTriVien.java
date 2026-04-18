package com.ota.travi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "quan_tri_vien")
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class QuanTriVien extends User{
}
