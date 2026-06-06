import { formatVnd } from '../../../utils/display'
import type { LoyaltySummary } from '../../../services/loyaltyService'

interface LoyaltySummaryCardsProps {
  summary: LoyaltySummary
  availableWalletCount: number
}

export function LoyaltySummaryCards({ summary, availableWalletCount }: LoyaltySummaryCardsProps) {
  return (
    <section className="grid grid-cols-1 gap-4 md:grid-cols-4">
      <article className="rounded-xl border border-outline-variant/40 bg-white p-5 shadow-sm">
        <p className="text-sm text-on-surface-variant">Điểm hiện có</p>
        <p className="mt-2 text-3xl font-bold text-primary">{summary.currentPoints ?? 0}</p>
      </article>
      <article className="rounded-xl border border-outline-variant/40 bg-white p-5 shadow-sm">
        <p className="text-sm text-on-surface-variant">Hạng hiện tại</p>
        <p className="mt-2 text-2xl font-bold text-primary">{summary.currentTier}</p>
      </article>
      <article className="rounded-xl border border-outline-variant/40 bg-white p-5 shadow-sm">
        <p className="text-sm text-on-surface-variant">Tổng chi tiêu</p>
        <p className="mt-2 text-2xl font-bold text-primary">{formatVnd(summary.totalSpending ?? 0)}</p>
      </article>
      <article className="rounded-xl border border-outline-variant/40 bg-white p-5 shadow-sm">
        <p className="text-sm text-on-surface-variant">Voucher khả dụng</p>
        <p className="mt-2 text-2xl font-bold text-primary">{availableWalletCount}</p>
      </article>
    </section>
  )
}
