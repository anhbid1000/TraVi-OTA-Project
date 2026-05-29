package com.ota.travi.enums;

/**
 * DEPRECATED: TrangThaiKiemDuyet được giữ lại chỉ để backward compatibility.
 * Module 1 hiện dùng TrangThaiHoatDong thay thế.
 * 
 * Lý do: Đã bỏ admin approval flow, chuyển sang partner-only asset management.
 */
@Deprecated(since = "2024", forRemoval = true)
public enum TrangThaiKiemDuyet {
    CHO_DUYET("Chờ duyệt"),
    DA_DUYET("Đã duyệt"),
    BI_TU_CHOI("Bị từ chối");

    private final String displayName;

    TrangThaiKiemDuyet(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
