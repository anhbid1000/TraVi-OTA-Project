import type { TrangThaiKhieuNai } from '../features/feedback-v2/types/complaint';

/**
 * Kiểm tra xem complaint có đang ở trạng thái mở (chưa giải quyết/đóng) không
 */
export function isOpenComplaintStatus(status: TrangThaiKhieuNai) {
  return status !== 'DA_GIAI_QUYET' && status !== 'DA_DONG';
}

/**
 * Tính toán xem complaint có quá hạn SLA (48h) không
 * Ưu tiên dùng overdue từ backend, fallback tính theo updatedAt nếu backend chưa flag
 */
export function shouldShowOverdueBadge(complaint: {
  overdue: boolean;
  status: TrangThaiKhieuNai;
  updatedAt: string;
}): boolean {
  // Ưu tiên backend flag
  if (complaint.overdue) return true;

  // Nếu backend chưa flag, FE tự tính fallback
  if (!isOpenComplaintStatus(complaint.status)) return false;

  const updatedAtMs = new Date(complaint.updatedAt).getTime();
  if (Number.isNaN(updatedAtMs)) return false;

  const hoursSinceUpdate = (Date.now() - updatedAtMs) / (1000 * 60 * 60);
  return hoursSinceUpdate > 48;
}
