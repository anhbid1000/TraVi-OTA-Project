package com.ota.travi.enums;

public enum TrangThaiKiemDuyet {
    BAN_NHAP("Bản nháp"),
    CHO_DUYET("Chờ duyệt"),
    BI_TU_CHOI("Bị từ chối"),
    DANG_HOAT_DONG("Đang hoạt động"),
    BI_KHOA_TAM_THOI("Bị khóa tạm thời");

    private final String displayName;

    TrangThaiKiemDuyet(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
