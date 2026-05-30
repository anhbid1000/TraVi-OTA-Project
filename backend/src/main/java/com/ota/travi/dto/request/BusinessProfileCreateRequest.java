package com.ota.travi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BusinessProfileCreateRequest(
        @Valid @NotNull HoSoKinhDoanhRequest hoSo,
        @Valid KhachSanRequest khachSan,
        @Valid NhaHangRequest nhaHang,
        @Valid List<AnhRequest> danhSachAnh,
        @Valid List<TienIchKhachSanRequest> tienIchKhachSan,
        @Valid List<TienIchNhaHangRequest> tienIchNhaHang
) {
}

