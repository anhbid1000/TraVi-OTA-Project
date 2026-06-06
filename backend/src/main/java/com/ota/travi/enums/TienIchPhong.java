package com.ota.travi.enums;

public enum TienIchPhong {
    WIFI("Wi-Fi"),
    DIEU_HOA("Điều hòa"),
    TV("TV"),
    TU_LANH("Tủ lạnh"),
    BAN_CONG("Ban công"),
    BON_TAM("Bồn tắm"),
    VIEW_DEP("View đẹp");

    private final String displayName;

    TienIchPhong(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
