package com.ota.travi.enums;

public enum HangThanhVien {
    DONG("Đồng"),
    BAC("Bạc"),
    VANG("Vàng"),
    KIM_CUONG("Kim cương");

    private final String displayName;

    HangThanhVien(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
