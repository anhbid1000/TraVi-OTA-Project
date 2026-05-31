package com.ota.travi.booking.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name = "BookingDonKhachSan")
@Table(name = "booking_don_khach_san")
@Getter
@Setter
public class DonKhachSan extends DonDatCho {
    private Long idPhong;
    private LocalDate ngayCheckIn;
    private LocalDate ngayCheckOut;
}
