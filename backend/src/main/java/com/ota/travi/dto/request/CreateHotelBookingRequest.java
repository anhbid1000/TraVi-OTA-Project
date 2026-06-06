package com.ota.travi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateHotelBookingRequest(
        @NotBlank(message = "hotelId không được để trống")
        String hotelId,

        @NotBlank(message = "tenNguoiDat không được để trống")
        String tenNguoiDat,

        @NotBlank(message = "sdtNguoiDat không được để trống")
        String sdtNguoiDat,

        @Email(message = "emailNguoiDat không hợp lệ")
        String emailNguoiDat,

        String ghiChu,

        @NotNull(message = "ngayCheckIn không được để trống")
        @FutureOrPresent(message = "ngayCheckIn không được ở quá khứ")
        LocalDate ngayCheckIn,

        @NotNull(message = "ngayCheckOut không được để trống")
        LocalDate ngayCheckOut,

        @Min(value = 1, message = "soKhach phải lớn hơn 0")
        Integer soKhach,

        LocalTime gioNhanPhongDuKien,

        Double expectedTotalAmount,

        @NotEmpty(message = "Danh sách phòng đặt không được rỗng")
        List<@Valid HotelBookingRoomRequest> rooms
) {
}
