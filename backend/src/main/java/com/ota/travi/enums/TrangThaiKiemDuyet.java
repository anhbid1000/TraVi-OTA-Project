package com.ota.travi.enums;

public enum TrangThaiKiemDuyet {
    CHO_DUYET("Chờ duyệt"),
    DA_DUYET("Đã duyệt"),
    BI_TU_CHOI("Bị từ chối"),
    BAN_NHAP("Bản nháp"),
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
