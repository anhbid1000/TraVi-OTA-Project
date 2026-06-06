package com.ota.travi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username không được để trống")
        @Size(min = 3, max = 50, message = "Username phải gồm 3-50 ký tự")
        String username,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Định dạng email không hợp lệ")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 8, max = 20, message = "Mật khẩu phải gồm 8-20 ký tự")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "Mật khẩu phải chứa ít nhất 1 ký tự in hoa, 1 ký tự thường, 1 chữ số và 1 ký tự đặc biệt"
        )
        String matKhau,

        @NotBlank(message = "Họ tên không được để trống")
        @Size(min = 2, max = 100, message = "Họ tên phải gồm 2-100 ký tự")
        String hoTen,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(\\+84|0)\\d{9}$", message = "Số điện thoại không hợp lệ")
        String soDienThoai,

        @NotBlank(message = "Vui lòng chọn loại tài khoản đăng ký")
        @Pattern(regexp = "^(KHACH_HANG|DOI_TAC)$", message = "Loại tài khoản chỉ được là KHACH_HANG hoặc DOI_TAC")
        String loaiTaiKhoan,

        @Size(max = 50, message = "Mã số thuế không được vượt quá 50 ký tự")
        String maSoThue,

        @Size(max = 500, message = "Giấy phép kinh doanh không được vượt quá 500 ký tự")
        String giayPhepKinhDoanh
) {
}
