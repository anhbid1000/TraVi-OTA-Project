package com.ota.travi.enums;

public enum TrangThaiMonAn {
    DANG_BAN("Đang bán"),
    CO_SAN("Có sẵn"),
    TAM_HET("Tạm hết"),
    NGUNG_BAN("Ngừng bán");

    private final String displayName;

    TrangThaiMonAn(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
