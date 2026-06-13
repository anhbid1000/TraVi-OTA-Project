import type {
  PromotionAnalyticsDaily,
  PromotionAnalyticsSummary,
} from '../../../services/partnerPromotionService'
import { formatVnd } from '../../../utils/display'

type PromotionAnalyticsPanelProps = {
  selectedPromotionId: number | null
  summary: PromotionAnalyticsSummary | null
  daily: PromotionAnalyticsDaily[]
}

function toDateTimeLocal(value?: string | null) {
  if (!value) return ''
  const date = new Date(value)
  const offset = date.getTimezoneOffset()
  const local = new Date(date.getTime() - offset * 60_000)
  return local.toISOString().slice(0, 16)
}

export function PromotionAnalyticsPanel({ selectedPromotionId, summary, daily }: PromotionAnalyticsPanelProps) {
  return (
    <section className="panel">
      <h2 className="mb-4 text-lg font-semibold text-primary">Campaign analytics</h2>
      {!selectedPromotionId && <p className="text-sm text-on-surface-variant">Chọn một campaign để xem analytics.</p>}
      {selectedPromotionId && summary && (
        <div className="space-y-4">
          <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
            <article className="rounded-lg border border-outline-variant/40 p-4">
              <p className="text-xs text-on-surface-variant">Số lượt dùng voucher</p>
              <p className="mt-1 text-xl font-bold text-primary">{summary.usageCount}</p>
            </article>
            <article className="rounded-lg border border-outline-variant/40 p-4">
              <p className="text-xs text-on-surface-variant">Đơn hàng chuyển đổi</p>
              <p className="mt-1 text-xl font-bold text-primary">{summary.bookingCount}</p>
            </article>
            <article className="rounded-lg border border-outline-variant/40 p-4">
              <p className="text-xs text-on-surface-variant">Conversion rate</p>
              <p className="mt-1 text-xl font-bold text-primary">{summary.conversionRate.toFixed(2)}%</p>
            </article>
            <article className="rounded-lg border border-outline-variant/40 p-4">
              <p className="text-xs text-on-surface-variant">Doanh thu tạo ra</p>
              <p className="mt-1 text-xl font-bold text-primary">{formatVnd(summary.generatedRevenue)}</p>
            </article>
            <article className="rounded-lg border border-outline-variant/40 p-4">
              <p className="text-xs text-on-surface-variant">Chi phí giảm giá</p>
              <p className="mt-1 text-xl font-bold text-primary">{formatVnd(summary.discountCost)}</p>
            </article>
          </div>

          <div className="overflow-x-auto rounded-lg border border-outline-variant/40">
            <table className="min-w-full text-sm">
              <thead className="bg-surface-container-low">
                <tr>
                  <th className="px-3 py-2 text-left">Ngày</th>
                  <th className="px-3 py-2 text-right">Usage</th>
                  <th className="px-3 py-2 text-right">Bookings</th>
                  <th className="px-3 py-2 text-right">Revenue</th>
                  <th className="px-3 py-2 text-right">Discount cost</th>
                  <th className="px-3 py-2 text-right">CR</th>
                </tr>
              </thead>
              <tbody>
                {daily.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-3 py-4 text-center text-on-surface-variant">
                      Chưa có dữ liệu analytics theo ngày.
                    </td>
                  </tr>
                )}
                {daily.map((item) => (
                  <tr key={item.ngay} className="border-t border-outline-variant/30">
                    <td className="px-3 py-2">{toDateTimeLocal(item.ngay).slice(0, 10)}</td>
                    <td className="px-3 py-2 text-right">{item.usageCount}</td>
                    <td className="px-3 py-2 text-right">{item.bookingCount}</td>
                    <td className="px-3 py-2 text-right">{formatVnd(item.generatedRevenue)}</td>
                    <td className="px-3 py-2 text-right">{formatVnd(item.discountCost)}</td>
                    <td className="px-3 py-2 text-right">{item.conversionRate.toFixed(2)}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </section>
  )
}
