package com.ota.travi.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        String email,

        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(min = 9, max = 20, message = "Mật khẩu phải gồm 9-20 kí tự")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{9,20}$",
                message = "Mật khẩu phải chứa ít nhất 1 kí tự in hoa, 1 kí tự thường, 1 chữ số, và 1 kí tự đặc biệt"
        )
        String matKhauMoi,

        @NotBlank(message = "Xác nhận mật khẩu không được để trống")
        String xacNhanMatKhau
) {
    @AssertTrue(message = "Xác nhận mật khẩu không khớp")
    public boolean isPasswordConfirmed() {
        return matKhauMoi != null && matKhauMoi.equals(xacNhanMatKhau);
    }
}

