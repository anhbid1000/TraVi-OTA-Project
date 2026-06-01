import { Link } from 'react-router-dom'
import { Bot, MapPin, Sparkles, Star } from 'lucide-react'
import { useAiRecommendations } from '../hooks/useAiRecommendations'
import { sendRecommendationFeedback } from '../services/aiService'
import { formatVnd } from '../../../utils/display'

type UserAiRecommendationsProps = {
  enabled: boolean
  city?: string
}

export function UserAiRecommendations({ enabled, city }: UserAiRecommendationsProps) {
  const { data, loading, error, refetch } = useAiRecommendations(
    { type: 'ALL', city, limit: 6 },
    { enabled },
  )

  if (!enabled) {
    return null
  }

  const handleOpen = async (idTaiSan: string) => {
    try {
      await sendRecommendationFeedback({ idTaiSan, viewed: true, clicked: true })
    } catch {
      // Feedback is best-effort; navigation should not be blocked.
    }
  }

  return (
    <section className="rounded-2xl border border-outline-variant/40 bg-white p-6 shadow-sm md:p-8">
      <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <div className="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-secondary">
            <Bot size={16} />
            AI cá nhân hóa
          </div>
          <h2 className="mt-2 font-display text-3xl font-bold text-on-surface">Gợi ý hợp với bạn</h2>
          {data?.profileSummary && (
            <p className="mt-2 max-w-2xl text-sm leading-relaxed text-on-surface-variant">
              {data.profileSummary}
            </p>
          )}
        </div>
        <button
          type="button"
          onClick={() => void refetch()}
          className="w-fit rounded-lg border border-outline-variant px-4 py-2 text-sm font-semibold text-primary transition hover:bg-surface-container-low"
        >
          Cập nhật gợi ý
        </button>
      </div>

      {loading && <p className="rounded-lg bg-surface-container-low p-4 text-sm">Đang tạo gợi ý AI...</p>}
      {error && (
        <p className="rounded-lg border border-error-container bg-error-container/30 p-4 text-sm font-medium text-error">
          {error}
        </p>
      )}

      {!loading && !error && (
        <div className="grid gap-4 md:grid-cols-3">
          {(data?.recommendations ?? []).map((item) => {
            const target = item.type === 'HOTEL' ? `/hotels/${item.id}` : `/restaurants/${item.id}`
            return (
              <Link
                key={`${item.type}-${item.id}`}
                to={target}
                onClick={() => void handleOpen(item.id)}
                className="group rounded-xl border border-outline-variant/40 bg-surface-container-lowest p-4 transition hover:-translate-y-0.5 hover:shadow-md"
              >
                <div className="flex items-start justify-between gap-3">
                  <span className="rounded-lg bg-mint-green px-2 py-1 text-xs font-bold text-primary">
                    {item.type === 'HOTEL' ? 'Khách sạn' : 'Nhà hàng'}
                  </span>
                  <span className="flex items-center gap-1 text-xs font-bold text-tertiary-container">
                    <Star size={13} />
                    {item.score}
                  </span>
                </div>
                <h3 className="mt-4 line-clamp-2 font-display text-lg font-bold text-on-surface group-hover:text-primary">
                  {item.name}
                </h3>
                <p className="mt-2 flex items-center gap-1 text-xs text-on-surface-variant">
                  <MapPin size={12} />
                  {[item.district, item.city].filter(Boolean).join(', ') || 'Việt Nam'}
                </p>
                <p className="mt-3 text-sm font-semibold text-primary">
                  {item.basePrice ? formatVnd(item.basePrice) : 'Xem giá chi tiết'}
                </p>
                <div className="mt-4 space-y-2">
                  {item.reasons.slice(0, 2).map((reason) => (
                    <p key={reason} className="flex gap-2 text-xs leading-relaxed text-on-surface-variant">
                      <Sparkles size={13} className="mt-0.5 shrink-0 text-secondary" />
                      {reason}
                    </p>
                  ))}
                </div>
              </Link>
            )
          })}
        </div>
      )}
    </section>
  )
}
