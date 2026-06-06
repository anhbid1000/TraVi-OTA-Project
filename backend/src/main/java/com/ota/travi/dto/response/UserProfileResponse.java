package com.ota.travi.dto.response;

public record UserProfileResponse(
        String id,
        String username,
        String email,
        String hoTen,
        String soDienThoai,
        String vaiTro
) {
}