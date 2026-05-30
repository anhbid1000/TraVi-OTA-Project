package com.ota.travi.enums;

public enum GioiTinh {
    NAM("Nam"),
    NU("Nữ"),
    KHAC("Khác");

    private final String displayName;

    GioiTinh(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
