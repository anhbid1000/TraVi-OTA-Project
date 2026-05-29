package com.ota.travi.enums;

/**
 * Trạng thái của một ticket khiếu nại (Complaint).
 * - CHO_PHAN_HOI: Khách vừa tạo, chờ đối tác phản hồi.
 * - DANG_XU_LY: Đối tác đã phản hồi, hai bên đang trao đổi.
 * - DA_GIAI_QUYET: Đối tác đánh dấu đã xử lý xong (khách có thể mở lại nếu không đồng ý).
 * - DA_DONG: Ticket chính thức kết thúc, không được nhắn thêm.
 */
public enum TrangThaiKhieuNai {
    CHO_PHAN_HOI,
    DANG_XU_LY,
    DA_GIAI_QUYET,
    DA_DONG
}
