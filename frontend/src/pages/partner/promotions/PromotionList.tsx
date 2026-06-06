import { formatVnd } from '../../../utils/display'
import type { PromotionResponse } from '../../../services/partnerPromotionService'

interface PromotionListProps {
  promotions: PromotionResponse[]
  loading: boolean
  actionBusyId: number | null
  onPauseResume: (promotion: PromotionResponse) => Promise<void>
  onViewAnalytics: (promotionId: number) => Promise<void>
}

export function PromotionList({
  promotions,
  loading,
  actionBusyId,
  onPauseResume,
  onViewAnalytics,
}: PromotionListProps) {
  if (loading) return <p className="text-sm text-on-surface-variant">Đang tải campaign...</p>
  if (promotions.length === 0) return <p className="text-sm text-on-surface-variant">Chưa có campaign nào.</p>

  return (
    <div className="space-y-3">
      {promotions.map((promotion) => (
        <article key={promotion.id} className="rounded-lg border border-outline-variant/40 p-4">
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div>
              <h3 className="font-semibold text-on-surface">{promotion.tenUuDai}</h3>
              <p className="text-sm text-on-surface-variant">
                {promotion.loaiUuDai} • {promotion.loaiGiamGia === 'PHAN_TRAM' ? `${promotion.mucGiam}%` : formatVnd(promotion.mucGiam)}
                {promotion.maVoucher ? ` • Mã: ${promotion.maVoucher}` : ''}
              </p>
              <p className="mt-1 text-xs text-on-surface-variant">
                {new Date(promotion.ngayBatDau).toLocaleString('vi-VN')} → {new Date(promotion.ngayKetThuc).toLocaleString('vi-VN')}
              </p>
            </div>
            <div className="flex flex-wrap gap-2">
              <span className="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">
                {promotion.trangThaiUuDai}
              </span>
              <button
                type="button"
                onClick={() => void onViewAnalytics(promotion.id)}
                className="rounded-md border border-primary px-3 py-1.5 text-xs font-semibold text-primary"
              >
                Xem analytics
              </button>
              <button
                type="button"
                disabled={
                  actionBusyId === promotion.id ||
                  (promotion.trangThaiUuDai !== 'DANG_CO_HIEU_LUC' &&
                    promotion.trangThaiUuDai !== 'TAM_DUNG')
                }
                onClick={() => void onPauseResume(promotion)}
                className="rounded-md bg-secondary px-3 py-1.5 text-xs font-semibold text-on-secondary disabled:opacity-60"
              >
                {actionBusyId === promotion.id
                  ? 'Đang xử lý...'
                  : promotion.trangThaiUuDai === 'DANG_CO_HIEU_LUC'
                    ? 'Tạm dừng'
                    : promotion.trangThaiUuDai === 'TAM_DUNG'
                      ? 'Kích hoạt lại'
                      : promotion.trangThaiUuDai === 'DA_HET_HAN'
                        ? 'Đã hết hạn'
                        : 'Đã lên lịch'}
              </button>
            </div>
          </div>
        </article>
      ))}
    </div>
  )
}
