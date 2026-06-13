import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  ArrowDownRight,
  ArrowUpRight,
  BedDouble,
  BookCheck,
  CalendarDays,
  ChefHat,
  CircleAlert,
  Clock3,
  Hotel,
  LineChart,
  Receipt,
  UtensilsCrossed,
  Wallet,
  Users,
} from 'lucide-react'
import { partnerAssetService } from '../../services/partnerAssetService'
import type {
  PartnerDashboardOverviewResponse,
  PartnerRestaurantDashboardOverviewResponse,
  RestaurantDashboardPeriod,
  ServiceType,
} from '../../types/asset'
import { getApiErrorMessage } from '../../utils/apiError'
import '../dashboard.css'

const DASHBOARD_RANGE_DAYS = 30

function formatCurrency(value: number) {
  return `${Math.round(value).toLocaleString('vi-VN')} đ`
}

function formatPercent(value: number) {
  const signed = value > 0 ? `+${value.toFixed(1)}` : value.toFixed(1)
  return `${signed}%`
}

function formatDate(value?: string | null) {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--'
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

function formatDateTime(value?: string | null) {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--'
  return date.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function formatShortDate(value?: string | null) {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--'
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
  })
}

function formatHourMinute(value?: string | null) {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--'
  return date.toLocaleTimeString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
  })
}

function getStatusMeta(status: string) {
  switch (status) {
    case 'CHO_THANH_TOAN':
      return { label: 'Chờ thanh toán', tone: 'warning' }
    case 'DA_THANH_TOAN':
      return { label: 'Đã thanh toán', tone: 'info' }
    case 'DA_XAC_NHAN':
      return { label: 'Đã xác nhận', tone: 'success' }
    case 'DANG_PHUC_VU':
      return { label: 'Đang phục vụ', tone: 'success' }
    case 'DA_HOAN_THANH':
      return { label: 'Hoàn thành', tone: 'success' }
    case 'DA_HUY':
      return { label: 'Đã hủy', tone: 'negative' }
    default:
      return { label: status, tone: 'neutral' }
  }
}

function canCompleteBooking(status: string) {
  return status === 'DA_THANH_TOAN' || status === 'DA_XAC_NHAN' || status === 'DANG_PHUC_VU'
}

type ChartPoint = {
  x: number
  y: number
  date: string
  value: number
}

function buildChartPoints(data: PartnerDashboardOverviewResponse['doanhThuTheoNgay']): ChartPoint[] {
  if (data.length === 0) return []
  const width = 760
  const height = 260
  const left = 16
  const right = 16
  const top = 14
  const bottom = 30
  const maxValue = Math.max(...data.map((item) => item.doanhThu), 1)
  const stepX = data.length > 1 ? (width - left - right) / (data.length - 1) : 0

  return data.map((item, index) => {
    const ratio = item.doanhThu / maxValue
    const x = left + index * stepX
    const y = top + (1 - ratio) * (height - top - bottom)
    return { x, y, date: item.ngay, value: item.doanhThu }
  })
}

function buildLinePath(points: ChartPoint[]) {
  if (points.length === 0) return ''
  return points.map((point, index) => `${index === 0 ? 'M' : 'L'}${point.x},${point.y}`).join(' ')
}

function buildAreaPath(points: ChartPoint[]) {
  if (points.length === 0) return ''
  const baseline = 230
  const linePath = buildLinePath(points)
  const first = points[0]
  const last = points[points.length - 1]
  return `${linePath} L${last.x},${baseline} L${first.x},${baseline} Z`
}

function getNameInitials(name: string) {
  if (!name.trim()) return 'NA'
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('')
}

function HotelOverview({
  dashboard,
  completingBookingId,
  onCompleteBooking,
}: {
  dashboard: PartnerDashboardOverviewResponse
  completingBookingId: string | null
  onCompleteBooking: (bookingId: string) => void
}) {
  const chartPoints = useMemo(() => buildChartPoints(dashboard.doanhThuTheoNgay ?? []), [dashboard.doanhThuTheoNgay])
  const linePath = useMemo(() => buildLinePath(chartPoints), [chartPoints])
  const areaPath = useMemo(() => buildAreaPath(chartPoints), [chartPoints])

  const chartLabels = useMemo(() => {
    if (dashboard.doanhThuTheoNgay.length === 0) return []
    const raw = dashboard.doanhThuTheoNgay
    const indexSet = new Set<number>([
      0,
      Math.floor(raw.length * 0.25),
      Math.floor(raw.length * 0.5),
      Math.floor(raw.length * 0.75),
      raw.length - 1,
    ])
    return Array.from(indexSet)
      .filter((index) => index >= 0 && index < raw.length)
      .sort((a, b) => a - b)
      .map((index) => raw[index])
  }, [dashboard.doanhThuTheoNgay])

  const kpiCards = [
    {
      key: 'revenue',
      label: 'Tổng doanh thu',
      value: formatCurrency(dashboard.tongQuan.tongDoanhThu),
      growth: dashboard.tongQuan.tyLeTangTruongDoanhThu,
      icon: <Wallet size={18} />,
    },
    {
      key: 'occupancy',
      label: 'Tỷ lệ lấp đầy',
      value: `${dashboard.tongQuan.tyLeLapDay.toFixed(1)}%`,
      growth: dashboard.tongQuan.tyLeTangTruongLapDay,
      icon: <BedDouble size={18} />,
    },
    {
      key: 'adr',
      label: 'Giá trung bình / đêm',
      value: formatCurrency(dashboard.tongQuan.giaTrungBinhMoiDem),
      growth: dashboard.tongQuan.tyLeTangTruongGiaTrungBinh,
      icon: <Hotel size={18} />,
    },
    {
      key: 'bookings',
      label: 'Tổng lượt đặt',
      value: dashboard.tongQuan.tongLuotDat.toLocaleString('vi-VN'),
      growth: dashboard.tongQuan.tyLeTangTruongLuotDat,
      icon: <BookCheck size={18} />,
    },
  ]

  const occupiedRate = Math.max(0, Math.min(100, dashboard.tinhTrangHomNay.tyLeDaDat ?? 0))

  return (
    <div className="partner-dashboard-overview">
      <div className="partner-page-header split partner-dashboard-header">
        <div>
          <h1>Tổng quan vận hành</h1>
          <p>
            Theo dõi hiệu suất kinh doanh theo thời gian thực của cơ sở <strong>{dashboard.tenCoSo}</strong>.
          </p>
        </div>
        <div className="partner-dashboard-range">
          <CalendarDays size={16} />
          <span>{formatDate(dashboard.tuNgay)} - {formatDate(dashboard.denNgay)}</span>
        </div>
      </div>

      <div className="partner-dashboard-grid">
        <section className="partner-dashboard-kpi-grid">
          {kpiCards.map((card) => {
            const positive = card.growth >= 0
            return (
              <article key={card.key} className="partner-dashboard-kpi-card">
                <div className="partner-dashboard-kpi-top">
                  <span className="partner-dashboard-kpi-icon">{card.icon}</span>
                  <span className={`partner-dashboard-kpi-growth ${positive ? 'positive' : 'negative'}`}>
                    {positive ? <ArrowUpRight size={14} /> : <ArrowDownRight size={14} />}
                    {formatPercent(card.growth)}
                  </span>
                </div>
                <p>{card.label}</p>
                <strong>{card.value}</strong>
              </article>
            )
          })}
        </section>

        <section className="partner-dashboard-panel partner-dashboard-chart-panel">
          <div className="partner-dashboard-panel-head">
            <div>
              <h2>Xu hướng doanh thu</h2>
              <p>Hiệu suất doanh thu theo ngày trong {DASHBOARD_RANGE_DAYS} ngày gần nhất</p>
            </div>
            <LineChart size={18} />
          </div>
          <div className="partner-dashboard-chart-wrap">
            {chartPoints.length === 0 ? (
              <div className="partner-dashboard-chart-empty">Chưa có dữ liệu doanh thu</div>
            ) : (
              <>
                <svg viewBox="0 0 760 260" preserveAspectRatio="none" className="partner-dashboard-chart-svg">
                  <defs>
                    <linearGradient id="partnerRevenueGradient" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%" stopColor="#14734a" stopOpacity="0.35" />
                      <stop offset="100%" stopColor="#14734a" stopOpacity="0" />
                    </linearGradient>
                  </defs>
                  <line x1="0" y1="55" x2="760" y2="55" stroke="#e6ece8" strokeDasharray="4" />
                  <line x1="0" y1="120" x2="760" y2="120" stroke="#e6ece8" strokeDasharray="4" />
                  <line x1="0" y1="185" x2="760" y2="185" stroke="#e6ece8" strokeDasharray="4" />
                  <path d={areaPath} fill="url(#partnerRevenueGradient)" />
                  <path d={linePath} fill="none" stroke="#14734a" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
                <div className="partner-dashboard-chart-labels">
                  {chartLabels.map((item) => (
                    <span key={item.ngay}>{formatShortDate(item.ngay)}</span>
                  ))}
                </div>
              </>
            )}
          </div>
        </section>

        <section className="partner-dashboard-panel partner-dashboard-availability-panel">
          <div className="partner-dashboard-panel-head">
            <h2>Công suất hôm nay</h2>
            <span className="partner-dashboard-live-pill">Trực tuyến</span>
          </div>

          <div className="partner-dashboard-occupancy-ring" style={{ background: `conic-gradient(#14734a ${occupiedRate}%, #e7ece9 ${occupiedRate}% 100%)` }}>
            <div className="partner-dashboard-occupancy-ring-inner">
              <strong>{occupiedRate.toFixed(1)}%</strong>
              <span>Đã lấp đầy</span>
            </div>
          </div>

          <div className="partner-dashboard-availability-stats">
            <article>
              <strong>{dashboard.tinhTrangHomNay.soPhongDaDat}</strong>
              <span>Phòng đã đặt</span>
            </article>
            <article>
              <strong>{dashboard.tinhTrangHomNay.soPhongConTrong}</strong>
              <span>Phòng còn trống</span>
            </article>
          </div>
        </section>

        <section className="partner-dashboard-panel partner-dashboard-bookings-panel">
          <div className="partner-dashboard-panel-head">
            <h2>Đặt phòng gần đây</h2>
          </div>
          <div className="partner-dashboard-table-wrap">
            <table className="partner-dashboard-table">
              <thead>
                <tr>
                  <th>Khách</th>
                  <th>Ngày</th>
                  <th>Loại phòng</th>
                  <th>Số tiền</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {dashboard.datChoGanDay.length === 0 && (
                  <tr>
                    <td colSpan={6} className="empty-cell">Chưa có giao dịch đặt phòng gần đây</td>
                  </tr>
                )}
                {dashboard.datChoGanDay.map((item) => {
                  const status = getStatusMeta(item.trangThai)
                  const completable = canCompleteBooking(item.trangThai)
                  return (
                    <tr key={item.id}>
                      <td>
                        <div className="partner-dashboard-guest-cell">
                          <span>{item.tenKhach}</span>
                          <small>#{item.maDon}</small>
                        </div>
                      </td>
                      <td>{formatDate(item.ngayNhanPhong)} - {formatDate(item.ngayTraPhong)}</td>
                      <td>{item.loaiPhong}</td>
                      <td>{formatCurrency(item.tongTien)}</td>
                      <td>
                        <span className={`partner-dashboard-status ${status.tone}`}>{status.label}</span>
                      </td>
                      <td>
                        {completable ? (
                          <button
                            type="button"
                            className="partner-dashboard-complete-btn"
                            disabled={completingBookingId === item.id}
                            onClick={() => onCompleteBooking(item.id)}
                          >
                            {completingBookingId === item.id ? 'Đang xử lý...' : 'Hoàn tất'}
                          </button>
                        ) : (
                          <span className="partner-dashboard-muted-action">--</span>
                        )}
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </section>

        <section className="partner-dashboard-panel partner-dashboard-top-room-panel">
          <div className="partner-dashboard-panel-head">
            <h2>Loại phòng nhu cầu cao</h2>
          </div>
          <div className="partner-dashboard-top-room-list">
            {dashboard.topPhongNhuCau.length === 0 && (
              <div className="partner-dashboard-top-room-empty">Chưa có dữ liệu nhu cầu phòng</div>
            )}
            {dashboard.topPhongNhuCau.map((room) => (
              <article key={room.roomId} className="partner-dashboard-top-room-item">
                <div className="partner-dashboard-top-room-image">
                  {room.anhDaiDienUrl ? (
                    <img src={room.anhDaiDienUrl} alt={room.tenPhong} />
                  ) : (
                    <span>{(room.tenPhong || room.loaiPhong || 'PH').slice(0, 2).toUpperCase()}</span>
                  )}
                </div>
                <div className="partner-dashboard-top-room-content">
                  <strong>{room.tenPhong}</strong>
                  <span>{room.loaiPhong}</span>
                  <small>{room.soLuotDat.toLocaleString('vi-VN')} lượt đặt</small>
                </div>
                <div className="partner-dashboard-top-room-price">
                  {formatCurrency(room.giaTrungBinh)}
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </div>
  )
}

function RestaurantOverview({
  dashboard,
  period,
  onChangePeriod,
  completingBookingId,
  onCompleteBooking,
}: {
  dashboard: PartnerRestaurantDashboardOverviewResponse
  period: RestaurantDashboardPeriod
  onChangePeriod: (value: RestaurantDashboardPeriod) => void
  completingBookingId: string | null
  onCompleteBooking: (bookingId: string) => void
}) {
  const maxRevenue = useMemo(() => {
    return Math.max(...dashboard.doanhThuTheoGio.map((item) => item.doanhThu), 0)
  }, [dashboard.doanhThuTheoGio])

  const periodOptions: Array<{ label: string; value: RestaurantDashboardPeriod }> = [
    { label: 'Hôm nay', value: 'TODAY' },
    { label: 'Tuần', value: 'WEEK' },
    { label: 'Tháng', value: 'MONTH' },
  ]

  const kpiCards = [
    {
      key: 'revenue',
      label: 'Tổng doanh thu',
      value: formatCurrency(dashboard.tongQuan.tongDoanhThu),
      growth: dashboard.tongQuan.tyLeTangTruongDoanhThu,
      icon: <Wallet size={18} />,
      hint: 'So với kỳ trước',
    },
    {
      key: 'occupancy',
      label: 'Lấp đầy bàn',
      value: `${dashboard.tongQuan.tyLeLapDayBan.toFixed(1)}%`,
      icon: <UtensilsCrossed size={18} />,
      hint: `${dashboard.tongQuan.soBanDangPhucVu}/${dashboard.tongQuan.tongSoBanKhaDung} bàn đang phục vụ`,
    },
    {
      key: 'aov',
      label: 'Giá trị đơn trung bình',
      value: formatCurrency(dashboard.tongQuan.giaTriDonTrungBinh),
      growth: dashboard.tongQuan.tyLeTangTruongGiaTriDon,
      icon: <Receipt size={18} />,
      hint: 'So với kỳ trước',
    },
    {
      key: 'covers',
      label: 'Tổng lượt khách',
      value: dashboard.tongQuan.tongLuotKhach.toLocaleString('vi-VN'),
      icon: <Users size={18} />,
      hint: `${dashboard.tongQuan.chenhLechLuotKhach >= 0 ? '+' : ''}${dashboard.tongQuan.chenhLechLuotKhach.toLocaleString('vi-VN')} khách`,
    },
  ]

  return (
    <div className="partner-dashboard-overview">
      <div className="partner-page-header split partner-dashboard-header">
        <div>
          <h1>Tổng quan nhà hàng</h1>
          <p>
            Theo dõi hiệu suất vận hành theo thời gian thực của <strong>{dashboard.tenNhaHang}</strong>.
          </p>
        </div>
        <div className="partner-dashboard-header-actions">
          <div className="partner-dashboard-period-switch" role="tablist" aria-label="Bộ lọc kỳ xem">
            {periodOptions.map((item) => (
              <button
                key={item.value}
                type="button"
                className={period === item.value ? 'active' : ''}
                onClick={() => onChangePeriod(item.value)}
              >
                {item.label}
              </button>
            ))}
          </div>
          <div className="partner-dashboard-range">
            <CalendarDays size={16} />
            <span>{formatDateTime(dashboard.tuThoiDiem)} - {formatDateTime(dashboard.denThoiDiem)}</span>
          </div>
        </div>
      </div>

      <div className="partner-dashboard-grid">
        <section className="partner-dashboard-kpi-grid">
          {kpiCards.map((card) => {
            const hasGrowth = typeof card.growth === 'number'
            const positive = (card.growth ?? 0) >= 0
            return (
              <article key={card.key} className="partner-dashboard-kpi-card">
                <div className="partner-dashboard-kpi-top">
                  <span className="partner-dashboard-kpi-icon">{card.icon}</span>
                  {hasGrowth ? (
                    <span className={`partner-dashboard-kpi-growth ${positive ? 'positive' : 'negative'}`}>
                      {positive ? <ArrowUpRight size={14} /> : <ArrowDownRight size={14} />}
                      {formatPercent(card.growth ?? 0)}
                    </span>
                  ) : null}
                </div>
                <p>{card.label}</p>
                <strong>{card.value}</strong>
                <small className="partner-dashboard-kpi-hint">{card.hint}</small>
              </article>
            )
          })}
        </section>

        <section className="partner-dashboard-panel partner-dashboard-chart-panel">
          <div className="partner-dashboard-panel-head">
            <div>
              <h2>Xu hướng doanh thu</h2>
              <p>Doanh thu theo từng khung giờ vận hành</p>
            </div>
            <LineChart size={18} />
          </div>
          <div className="partner-dashboard-restaurant-chart-wrap">
            {dashboard.doanhThuTheoGio.length === 0 ? (
              <div className="partner-dashboard-chart-empty">Chưa có dữ liệu doanh thu theo giờ</div>
            ) : (
              <>
                <div className="partner-dashboard-restaurant-bars">
                  {dashboard.doanhThuTheoGio.map((item) => {
                    const ratio = maxRevenue <= 0 ? 0 : (item.doanhThu / maxRevenue) * 100
                    const height = maxRevenue <= 0 ? 0 : Math.max(8, Math.round(ratio))
                    return (
                      <div key={item.gio} className="partner-dashboard-restaurant-bar-item" title={`${String(item.gio).padStart(2, '0')}:00 - ${formatCurrency(item.doanhThu)}`}>
                        <div className="partner-dashboard-restaurant-bar-track">
                          <div className="partner-dashboard-restaurant-bar-fill" style={{ height: `${height}%` }} />
                        </div>
                        <span>{`${String(item.gio).padStart(2, '0')}:00`}</span>
                      </div>
                    )
                  })}
                </div>
              </>
            )}
          </div>
        </section>

        <section className="partner-dashboard-panel partner-dashboard-availability-panel">
          <div className="partner-dashboard-panel-head">
            <h2>Khung giờ cao điểm</h2>
            <Clock3 size={18} />
          </div>
          <div className="partner-dashboard-peak-list">
            {dashboard.khungGioCaoDiem.length === 0 && (
              <div className="partner-dashboard-top-room-empty">Chưa có dữ liệu khung giờ cao điểm</div>
            )}
            {dashboard.khungGioCaoDiem.map((item, index) => {
              const percent = Math.max(0, Math.min(100, item.tyLe))
              return (
                <article key={`${item.tenKhungGio}-${item.moTa}`} className="partner-dashboard-peak-item">
                  <div className="partner-dashboard-peak-title-row">
                    <span>{item.tenKhungGio} ({item.moTa})</span>
                    <strong>{percent.toFixed(1)}%</strong>
                  </div>
                  <div className="partner-dashboard-peak-track">
                    <span className={`partner-dashboard-peak-fill tone-${index % 4}`} style={{ width: `${percent}%` }} />
                  </div>
                </article>
              )
            })}
          </div>
        </section>

        <section className="partner-dashboard-panel partner-dashboard-bookings-panel">
          <div className="partner-dashboard-panel-head">
            <h2>Đơn gần đây</h2>
          </div>
          <div className="partner-dashboard-table-wrap">
            <table className="partner-dashboard-table">
              <thead>
                <tr>
                  <th>Khách hàng</th>
                  <th>Thời gian</th>
                  <th>Bàn</th>
                  <th>Thành tiền</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {dashboard.donGanDay.length === 0 && (
                  <tr>
                    <td colSpan={6} className="empty-cell">Chưa có đơn nhà hàng gần đây</td>
                  </tr>
                )}
                {dashboard.donGanDay.map((item) => {
                  const status = getStatusMeta(item.trangThai)
                  const completable = canCompleteBooking(item.trangThai)
                  return (
                    <tr key={item.id}>
                      <td>
                        <div className="partner-dashboard-guest-cell">
                          <span>{item.tenKhachHang}</span>
                          <small>#{item.maDon}</small>
                        </div>
                      </td>
                      <td title={formatDateTime(item.thoiGian)}>{formatHourMinute(item.thoiGian)}</td>
                      <td>{item.banSo}</td>
                      <td>{formatCurrency(item.tongTien)}</td>
                      <td>
                        <span className={`partner-dashboard-status ${status.tone}`}>{status.label}</span>
                      </td>
                      <td>
                        {completable ? (
                          <button
                            type="button"
                            className="partner-dashboard-complete-btn"
                            disabled={completingBookingId === item.id}
                            onClick={() => onCompleteBooking(item.id)}
                          >
                            {completingBookingId === item.id ? 'Đang xử lý...' : 'Hoàn tất'}
                          </button>
                        ) : (
                          <span className="partner-dashboard-muted-action">--</span>
                        )}
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </section>

        <section className="partner-dashboard-panel partner-dashboard-top-room-panel">
          <div className="partner-dashboard-panel-head">
            <h2>Món bán chạy</h2>
            <ChefHat size={18} />
          </div>
          <div className="partner-dashboard-top-room-list">
            {dashboard.monBanChay.length === 0 && (
              <div className="partner-dashboard-top-room-empty">Chưa có dữ liệu món bán chạy</div>
            )}
            {dashboard.monBanChay.map((dish) => (
              <article key={dish.monAnId} className="partner-dashboard-top-room-item">
                <div className="partner-dashboard-top-room-image">
                  {dish.anhMon ? (
                    <img src={dish.anhMon} alt={dish.tenMon} />
                  ) : (
                    <span>{getNameInitials(dish.tenMon)}</span>
                  )}
                </div>
                <div className="partner-dashboard-top-room-content">
                  <strong>{dish.tenMon}</strong>
                  <span>{dish.tongSoLuong.toLocaleString('vi-VN')} lượt gọi</span>
                  <small>Giá TB: {formatCurrency(dish.giaTrungBinh)}</small>
                </div>
                <div className="partner-dashboard-top-room-price">
                  {formatCurrency(dish.giaTrungBinh)}
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </div>
  )
}

export function PartnerDashboardOverviewPage() {
  const navigate = useNavigate()

  const [loading, setLoading] = useState(true)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [needCreateProfile, setNeedCreateProfile] = useState(false)
  const [dashboardType, setDashboardType] = useState<ServiceType | null>(null)

  const [hotelDashboard, setHotelDashboard] = useState<PartnerDashboardOverviewResponse | null>(null)
  const [restaurantDashboard, setRestaurantDashboard] = useState<PartnerRestaurantDashboardOverviewResponse | null>(null)
  const [restaurantPeriod, setRestaurantPeriod] = useState<RestaurantDashboardPeriod>('TODAY')
  const [completingBookingId, setCompletingBookingId] = useState<string | null>(null)

  useEffect(() => {
    const loadDashboard = async () => {
      setLoading(true)
      setErrorMessage(null)

      try {
        const profiles = await partnerAssetService.getBusinessProfiles()
        const currentProfile = profiles[0]

        if (!currentProfile) {
          setNeedCreateProfile(true)
          setDashboardType(null)
          setHotelDashboard(null)
          setRestaurantDashboard(null)
          return
        }

        const loaiDichVu = currentProfile.loaiDichVu as ServiceType
        setDashboardType(loaiDichVu)
        setNeedCreateProfile(false)

        if (loaiDichVu === 'NHA_HANG') {
          const data = await partnerAssetService.getRestaurantDashboardOverview(restaurantPeriod)
          setRestaurantDashboard(data)
          setHotelDashboard(null)
        } else {
          const data = await partnerAssetService.getDashboardOverview(DASHBOARD_RANGE_DAYS)
          setHotelDashboard(data)
          setRestaurantDashboard(null)
        }
      } catch (error) {
        const message = getApiErrorMessage(error, 'Không tải được dữ liệu dashboard. Vui lòng thử lại sau.')
        setErrorMessage(message)
        setHotelDashboard(null)
        setRestaurantDashboard(null)
        setNeedCreateProfile(message.toLowerCase().includes('hồ sơ kinh doanh') || message.toLowerCase().includes('ho so kinh doanh'))
      } finally {
        setLoading(false)
      }
    }

    void loadDashboard()
  }, [restaurantPeriod])

  const handleCompleteBooking = async (bookingId: string) => {
    setCompletingBookingId(bookingId)
    setErrorMessage(null)

    try {
      await partnerAssetService.completeBooking(bookingId)

      if (dashboardType === 'NHA_HANG') {
        const data = await partnerAssetService.getRestaurantDashboardOverview(restaurantPeriod)
        setRestaurantDashboard(data)
      } else {
        const data = await partnerAssetService.getDashboardOverview(DASHBOARD_RANGE_DAYS)
        setHotelDashboard(data)
      }
    } catch (error) {
      setErrorMessage(getApiErrorMessage(error, 'Không thể hoàn tất đơn. Vui lòng thử lại sau.'))
    } finally {
      setCompletingBookingId(null)
    }
  }

  if (loading) {
    return (
      <div className="partner-dashboard-loading panel">
        <strong>Đang tải dữ liệu dashboard...</strong>
        <span>TraVi đang tổng hợp số liệu vận hành theo thời gian thực.</span>
      </div>
    )
  }

  if (needCreateProfile) {
    return (
      <div className="partner-dashboard-empty panel">
        <CircleAlert size={22} />
        <h2>Bạn chưa có hồ sơ kinh doanh</h2>
        <p>Tạo hồ sơ kinh doanh trước để xem dashboard vận hành tài sản.</p>
        <button
          type="button"
          className="primary-btn"
          onClick={() => navigate('/partner/business-profile')}
        >
          Tạo hồ sơ kinh doanh
        </button>
      </div>
    )
  }

  if (!hotelDashboard && !restaurantDashboard) {
    return (
      <div className="partner-dashboard-empty panel">
        <CircleAlert size={22} />
        <h2>Không tải được dashboard</h2>
        <p>{errorMessage ?? 'Đã xảy ra lỗi không xác định.'}</p>
      </div>
    )
  }

  return (
    <>
      {errorMessage && <div className="alert error">{errorMessage}</div>}
      {dashboardType === 'NHA_HANG' && restaurantDashboard ? (
        <RestaurantOverview
          dashboard={restaurantDashboard}
          period={restaurantPeriod}
          onChangePeriod={setRestaurantPeriod}
          completingBookingId={completingBookingId}
          onCompleteBooking={handleCompleteBooking}
        />
      ) : null}
      {dashboardType !== 'NHA_HANG' && hotelDashboard ? (
        <HotelOverview
          dashboard={hotelDashboard}
          completingBookingId={completingBookingId}
          onCompleteBooking={handleCompleteBooking}
        />
      ) : null}
    </>
  )
}
