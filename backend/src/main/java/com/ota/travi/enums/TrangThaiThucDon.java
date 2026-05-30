package com.ota.travi.enums;

public enum TrangThaiThucDon {
    DANG_HIEN_THI("Đang hiển thị"),
    TAM_AN("Tạm ẩn");

    private final String displayName;

    TrangThaiThucDon(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

