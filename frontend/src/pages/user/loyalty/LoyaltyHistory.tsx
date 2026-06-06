import type { PointHistory } from '../../../services/loyaltyService'
import { pointTypeLabel, formatVietnamDateTime } from './utils'

interface LoyaltyHistoryProps {
  history: PointHistory[]
}

export function LoyaltyHistory({ history }: LoyaltyHistoryProps) {
  return (
    <article className="rounded-xl border border-outline-variant/40 bg-white p-6 shadow-sm">
      <h3 className="font-display text-lg font-bold text-primary">Lịch sử điểm gần đây</h3>
      <div className="mt-4 space-y-3">
        {history.length === 0 && <p className="text-sm text-on-surface-variant">Chưa có giao dịch điểm.</p>}
        {history.map((item) => (
          <div key={item.id} className="rounded-lg border border-outline-variant/30 p-3">
            <div className="flex items-center justify-between gap-3">
              <p className="text-sm font-semibold text-on-surface">{pointTypeLabel(item.loaiGiaoDichDiem)}</p>
              <p className={`text-sm font-bold ${item.soDiemThayDoi >= 0 ? 'text-secondary' : 'text-error'}`}>
                {item.soDiemThayDoi > 0 ? '+' : ''}{item.soDiemThayDoi}
              </p>
            </div>
            <p className="mt-1 text-xs text-on-surface-variant">{formatVietnamDateTime(item.createdAt)}</p>
            {item.ghiChu && <p className="mt-1 text-sm text-on-surface-variant">{item.ghiChu}</p>}
          </div>
        ))}
      </div>
    </article>
  )
}
