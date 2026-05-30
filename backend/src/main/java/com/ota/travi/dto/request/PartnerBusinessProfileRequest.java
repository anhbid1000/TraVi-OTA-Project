package com.ota.travi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PartnerBusinessProfileRequest(
        @Valid
        @NotNull(message = "Ho so kinh doanh khong duoc de trong")
        HoSoKinhDoanhRequest hoSo,

        @Valid
        KhachSanRequest khachSan,

        @Valid
        NhaHangRequest nhaHang,

        @Valid
        List<AnhRequest> danhSachAnh,

        @Valid
        List<TienIchKhachSanRequest> tienIchKhachSan,

        @Valid
        List<TienIchNhaHangRequest> tienIchNhaHang
) {
}
