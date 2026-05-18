package com.ota.travi.enums;

public enum TrangThaiMonAn {
    CO_SAN("Có sẵn"),
    TAM_HET("Tạm hết");

    private final String displayName;

    TrangThaiMonAn(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
