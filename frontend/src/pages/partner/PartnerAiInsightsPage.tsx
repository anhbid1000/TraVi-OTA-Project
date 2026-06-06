import { useEffect, useMemo, useState } from 'react'
import { Activity, AlertTriangle, BrainCircuit, Lightbulb, RefreshCw, Star, TrendingUp, Wallet } from 'lucide-react'
import { partnerAssetService } from '../../services/partnerAssetService'
import type { PartnerAiInsightResponse } from '../../types/asset'
import { getApiErrorMessage } from '../../utils/apiError'
import '../dashboard.css'

function formatCurrency(value?: number | null) {
  return `${Math.round(value ?? 0).toLocaleString('vi-VN')} d`
}

function formatPercent(value?: number | null) {
  const normalized = value ?? 0
  return `${normalized > 0 ? '+' : ''}${normalized.toFixed(1)}%`
}

function severityMeta(severity: string) {
  switch (severity) {
    case 'HIGH':
      return { label: 'Ưu tiên cao', className: 'negative', icon: <AlertTriangle size={18} /> }
    case 'MEDIUM':
      return { label: 'Cần theo dõi', className: 'warning', icon: <Activity size={18} /> }
    default:
      return { label: 'Tín hiệu tốt', className: 'success', icon: <Lightbulb size={18} /> }
  }
}

export function PartnerAiInsightsPage() {
  const [data, setData] = useState<PartnerAiInsightResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const loadInsights = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await partnerAssetService.getPartnerAiInsights(30)
      setData(response)
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tải AI insights đối tác.'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadInsights()
  }, [])

  const kpiCards = useMemo(() => {
    if (!data) return []
    return [
      {
        key: 'health',
        label: 'Điểm sức khỏe AI',
        value: `${data.healthScore}/100`,
        icon: <BrainCircuit size={20} />,
        growth: data.healthScore >= 70 ? 'positive' : data.healthScore < 50 ? 'negative' : 'neutral',
      },
      {
        key: 'revenue',
        label: 'Doanh thu kỳ này',
        value: formatCurrency(data.revenue),
        hint: formatPercent(data.revenueGrowthPercent),
        icon: <Wallet size={20} />,
        growth: data.revenueGrowthPercent >= 0 ? 'positive' : 'negative',
      },
      {
        key: 'bookings',
        label: 'Lượt đặt chỗ',
        value: data.bookings.toLocaleString('vi-VN'),
        hint: formatPercent(data.bookingGrowthPercent),
        icon: <TrendingUp size={20} />,
        growth: data.bookingGrowthPercent >= 0 ? 'positive' : 'negative',
      },
      {
        key: 'rating',
        label: 'Đánh giá trung bình',
        value: `${data.averageRating.toFixed(1)}/5`,
        hint: `${data.reviewCount} đánh giá`,
        icon: <Star size={20} />,
        growth: data.averageRating >= 4 ? 'positive' : data.averageRating > 0 ? 'negative' : 'neutral',
      },
    ]
  }, [data])

  if (loading) {
    return (
      <div className="partner-dashboard-loading panel">
        <strong>Đang phân tích AI đối tác...</strong>
        <span>Hệ thống đang đọc booking, review và cập nhật insights.</span>
      </div>
    )
  }

  if (error) {
    return (
      <div className="partner-dashboard-empty panel">
        <h2>Không thể tải AI insights</h2>
        <p>{error}</p>
        <button className="primary-btn" onClick={() => void loadInsights()}>
          <RefreshCw size={18} />
          Thử lại
        </button>
      </div>
    )
  }

  if (!data) return null

  return (
    <div className="partner-dashboard-overview partner-ai-insights-page">
      <div className="partner-page-header split partner-dashboard-header">
        <div>
          <span className="eyebrow">Partner AI</span>
          <h1>{data.businessName}</h1>
          {/* <p>{data.profileSummary}</p> */}
        </div>
        <div className="partner-dashboard-header-actions">
          <div className="partner-dashboard-range">
            {data.fromDate} - {data.toDate}
          </div>
          <button className="secondary-btn" onClick={() => void loadInsights()}>
            <RefreshCw size={18} />
            Cập nhật
          </button>
        </div>
      </div>

      <section className="partner-dashboard-kpi-grid">
        {kpiCards.map((card) => (
          <article key={card.key} className="partner-dashboard-kpi-card">
            <div className="partner-dashboard-kpi-top">
              <span className="partner-dashboard-kpi-icon">{card.icon}</span>
              <span className={`partner-dashboard-kpi-growth ${card.growth}`}>{card.hint}</span>
            </div>
            <p>{card.label}</p>
            <strong>{card.value}</strong>
          </article>
        ))}
      </section>

      <section className="partner-dashboard-panel partner-ai-insight-panel">
        <div className="partner-dashboard-panel-head">
          <div>
            <h2>Gợi ý hành động</h2>
            <p>AI tạo insights từ doanh thu, xu hướng booking và đánh giá gần đây.</p>
          </div>
        </div>

        <div className="partner-ai-insight-list">
          {data.insights.map((insight) => {
            const meta = severityMeta(insight.severity)
            return (
              <article key={insight.id} className="partner-ai-insight-item">
                <div className={`partner-dashboard-status ${meta.className}`}>
                  {meta.icon}
                  {meta.label}
                </div>
                <div>
                  <strong>{insight.title}</strong>
                  <p>{insight.message}</p>
                  {insight.recommendedAction && <span>{insight.recommendedAction}</span>}
                </div>
              </article>
            )
          })}
        </div>
      </section>
    </div>
  )
}
