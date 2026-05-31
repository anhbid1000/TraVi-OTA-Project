package com.ota.travi.booking.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    
    @NotBlank(message = "Tên người đặt không được để trống")
    private String tenNguoiDat;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    private String sdtNguoiDat;
    
    private String emailNguoiDat;
    
    @NotNull(message = "Phải chọn loại đơn: KHACH_SAN hoặc NHA_HANG")
    private LoaiDon loaiDon; 

    // Dành cho Khách sạn
    private Long idPhong;
    private LocalDate ngayCheckIn;
    private LocalDate ngayCheckOut;

    // Dành cho Nhà hàng
    private Long idBan;
    private LocalDateTime ngayGioDatCho;
    private Integer soNguoi;
    
    public enum LoaiDon {
        KHACH_SAN, NHA_HANG
    }
}
