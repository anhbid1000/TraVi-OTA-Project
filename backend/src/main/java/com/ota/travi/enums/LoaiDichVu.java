package com.ota.travi.enums;

public enum LoaiDichVu {
    KHACH_SAN("Khách sạn"),
    NHA_HANG("Nhà hàng");

    private final String displayName;

    LoaiDichVu(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
