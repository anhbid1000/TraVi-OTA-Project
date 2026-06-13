import type { LoyaltyProgress } from '../../../services/loyaltyService'
import { formatVnd } from '../../../utils/display'

type TierProgressPanelProps = {
  progress: LoyaltyProgress | null
}

export function TierProgressPanel({ progress }: TierProgressPanelProps) {
  const progressPercent = Math.max(0, Math.min(100, Number(progress?.progressPercent ?? 0)))

  return (
    <section className="rounded-xl border border-outline-variant/40 bg-white p-6 shadow-sm">
      <h2 className="font-display text-xl font-bold text-primary">Tiến độ hạng thành viên</h2>
      <p className="mt-2 text-sm text-on-surface-variant">
        {progress?.nextTier
          ? `Còn ${formatVnd(progress.remainingSpending ?? 0)} để lên hạng ${progress.nextTier}.`
          : 'Bạn đã đạt hạng cao nhất.'}
      </p>
      <div className="mt-4 h-3 w-full overflow-hidden rounded-full bg-surface-container">
        <div className="h-full rounded-full bg-primary" style={{ width: `${progressPercent}%` }} />
      </div>
      <p className="mt-2 text-sm text-on-surface-variant">{progressPercent.toFixed(1)}%</p>
    </section>
  )
}
