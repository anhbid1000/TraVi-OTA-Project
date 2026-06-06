package com.ota.travi.enums;

public enum TrangThaiUser {
    CHUA_XAC_THUC("Chờ xác thực"),
    HOAT_DONG("Đang hoạt động"),
    TAM_VO_HIEU_HOA("Tạm vô hiệu hóa"),
    BI_KHOA_TAM_THOI("Bị khóa tạm thời"),
    BI_CAM_VINH_VIEN("Bị cấm vĩnh viễn"),
    DA_XOA("Đã xóa");

    private final String displayName;

    TrangThaiUser(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
