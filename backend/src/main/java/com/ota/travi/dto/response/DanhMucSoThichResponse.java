package com.ota.travi.dto.response;

import java.util.List;

public record DanhMucSoThichResponse(
    String id,
    String tenDanhMuc,
    String moTa,
    List<SoThichResponse> danhSachSoThich
) {}
