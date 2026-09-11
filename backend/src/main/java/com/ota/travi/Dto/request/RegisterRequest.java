package com.ota.travi.Dto.request;

import com.ota.travi.Enum.GioiTinh;
import jakarta.validation.constraints.*;

import java.util.Date;

public record RegisterRequest (
        @NotBlank(message = "Username không được để trống")
        String username,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 9 , max = 20 , message = "Password musth be between 8 and 20 characters")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
        String matKhau,

        @NotBlank(message = "Họ tên không được để trống")
        String hoTen,
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(\\+84|0)\\d{9}$", message = "Invalid phone number format")
        String soDienThoai,

        @NotNull(message = "Vui lòng chọn ngay sinh")
        Date ngaySinh,

        @NotNull(message = "Vui lòng chọn giới tính")
        GioiTinh gioiTinh
) { }
