package com.ota.travi.enums;

public enum TrangThaiHoatDong {
    CHUA_HOAT_DONG("Chưa hoạt động"),
    DANG_HOAT_DONG("Đang hoạt động"),
    TAM_DUNG("Tạm dừng"),
    BI_KHOA("Bị khóa");

    private final String displayName;

    TrangThaiHoatDong(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

