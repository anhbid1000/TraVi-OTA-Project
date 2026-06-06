package com.ota.travi.enums;

/**
 * Trạng thái của một đánh giá (Review).
 * - DA_HIEN_THI: Đánh giá hợp lệ, được hiển thị công khai.
 * - BI_AN: Bị ẩn do chứa từ ngữ thô tục/vi phạm, chỉ lưu để audit.
 */
public enum TrangThaiDanhGia {
    DA_HIEN_THI,
    BI_AN
}
