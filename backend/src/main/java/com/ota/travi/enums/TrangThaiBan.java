package com.ota.travi.enums;

/**
 * Trạng thái của bàn trong nhà hàng.
 * 
 * - SAN_SANG: Bàn sẵn sàng phục vụ
 * - TAM_DUNG: Bàn tạm dừng (bảo trì, vệ sinh, v.v.)
 * - NGUNG_SU_DUNG: Bàn ngừng sử dụng (đã xóa mềm)
 */
public enum TrangThaiBan {
    SAN_SANG("Sẵn sàng"),
    TAM_DUNG("Tạm dừng"),
    NGUNG_SU_DUNG("Ngừng sử dụng");

    private final String displayName;

    TrangThaiBan(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
