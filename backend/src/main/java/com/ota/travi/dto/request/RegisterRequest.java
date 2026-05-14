package com.ota.travi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Username không được để trống")
        String username,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Định dạng email không hợp lệ")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        String matKhau,

        @NotBlank(message = "Họ tên không được để trống")
        String hoTen,

        String soDienThoai,

        // Gửi lên: KHACH_HANG, DOI_TAC
        @NotBlank(message = "Vui lòng chọn loại tài khoản đăng ký")
        String loaiTaiKhoan,

        // Chỉ bắt buộc nếu là Đối Tác
        String maSoThue,
        String giayPhepKinhDoanh
) {}