package com.ota.travi.booking.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "BookingDonNhaHang")
@Table(name = "booking_don_nha_hang")
@Getter
@Setter
public class DonNhaHang extends DonDatCho {
    private Long idBan;
    private LocalDateTime ngayGioDatCho;
    private Integer soNguoi;
}
