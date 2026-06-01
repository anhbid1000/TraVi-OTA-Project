import { Bell, CloudRain, Info } from 'lucide-react'
import { useSmartNotifications } from '../hooks/useAiFeatures'
import type { ThongBaoNguCanhResponse } from '../types'

function getIcon(type: string, content: string) {
  if (type === 'WEATHER' || content.toLowerCase().includes('mưa') || content.toLowerCase().includes('bão')) {
    return <CloudRain size={16} className="text-blue-500" />
  }
  if (type === 'BOOKING') {
    return <Bell size={16} className="text-amber-500" />
  }
  return <Info size={16} className="text-secondary" />
}

export function SmartNotificationWidget({ enabled = true }: { enabled?: boolean }) {
  const { notifications, loading } = useSmartNotifications(enabled)

  if (!enabled) return null

  if (loading && notifications.length === 0) {
    return (
      <div className="rounded-lg border border-outline-variant/40 p-4 shadow-sm">
        <p className="text-sm text-on-surface-variant">Đang tải thông báo ngữ cảnh...</p>
      </div>
    )
  }

  if (notifications.length === 0) {
    return null
  }

  return (
    <div className="space-y-3">
      <h3 className="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-secondary">
        <Bell size={16} /> Thông báo thông minh
      </h3>
      <div className="flex flex-col gap-2">
        {notifications.map((notif: ThongBaoNguCanhResponse) => (
          <div key={notif.idThongBao} className="flex items-start gap-3 rounded-lg border border-outline-variant/40 bg-white p-3 shadow-sm transition hover:bg-surface-container-low">
            <div className="mt-0.5 rounded-full bg-surface-container p-1.5">
              {getIcon(notif.type, notif.noiDung)}
            </div>
            <div>
              <p className="text-sm font-semibold">{notif.loaiNguCanh === 'WEATHER_WARNING' ? 'Cảnh báo thời tiết' : notif.loaiNguCanh === 'BOOKING_REMINDER' ? 'Nhắc nhở lịch trình' : 'Gợi ý'}</p>
              <p className="text-xs text-on-surface-variant">{notif.noiDung}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
