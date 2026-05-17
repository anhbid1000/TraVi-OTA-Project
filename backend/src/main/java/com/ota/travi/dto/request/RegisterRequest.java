package com.ota.travi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username không được để trống")
        String username,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Định dạng email không hợp lệ")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 9 , max = 20 , message = "Mật khẩu phải gồm 8-20 kí tự")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Mật khẩu phải chứa ít nhất 1 kí tự in hoa, 1 kí tự thường, 1 chữ số, và 1 kí tự đặc biệt")
        String matKhau,

        @NotBlank(message = "Họ tên không được để trống")
        @Size(min = 2, max = 100, message = "Họ tên phải gồm 2-100 ký tự")
        String hoTen,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(\\+84|0)\\d{9}$", message = "Định dạng không hợp lệ")
        String soDienThoai,

        // Gửi lên: KHACH_HANG, DOI_TAC
        @NotBlank(message = "Vui lòng chọn loại tài khoản đăng ký")
        String loaiTaiKhoan,

        // Chỉ bắt buộc nếu là Đối Tác
        String maSoThue,
        String giayPhepKinhDoanh
) {}