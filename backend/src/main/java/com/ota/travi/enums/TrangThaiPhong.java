package com.ota.travi.enums;

public enum TrangThaiPhong {
    DANG_BAN("Đang bán"),
    TAM_DUNG("Tạm dừng"),
    NGUNG_KINH_DOANH("Ngừng kinh doanh"),
    SAN_SANG("Sẵn sàng"),
    DA_DAT_TRUOC("Đã đặt trước"),
    DANG_SU_DUNG("Đang sử dụng"),
    DANG_BAO_TRI("Đang bảo trì"),
    DA_AN("Đã ẩn");

    private final String displayName;

    TrangThaiPhong(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
