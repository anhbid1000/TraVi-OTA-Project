import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { userService, type HotelBookingResponse, type RestaurantBookingResponse } from '../services/userService'
import { formatVnd } from '../utils/display'

type PaymentState = {
  bookingType?: 'hotel' | 'restaurant'
  booking?: HotelBookingResponse | RestaurantBookingResponse
}

function formatRemainingTime(totalSeconds: number) {
  const minutes = Math.floor(totalSeconds / 60)
  const seconds = totalSeconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

export function PaymentPage() {
  const location = useLocation()
  const navigate = useNavigate()
  const state = (location.state ?? {}) as PaymentState

  const [booking, setBooking] = useState<HotelBookingResponse | RestaurantBookingResponse | null>(
    (state.booking as HotelBookingResponse | RestaurantBookingResponse | undefined) ?? null,
  )
  const bookingType: 'hotel' | 'restaurant' =
    state.bookingType ?? ((booking && 'rooms' in booking) ? 'hotel' : 'restaurant')

  const [remainingSeconds, setRemainingSeconds] = useState<number>(state.booking?.paymentExpiresInSeconds ?? 0)
  const [isPaying, setIsPaying] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  const [successMessage, setSuccessMessage] = useState('')

  useEffect(() => {
    if (!booking?.paymentExpiredAt) {
      setRemainingSeconds(0)
      return
    }

    if (booking.paymentExpiresInSeconds && booking.paymentExpiresInSeconds > 0) {
      setRemainingSeconds(booking.paymentExpiresInSeconds)
      return
    }

    const expireAt = new Date(booking.paymentExpiredAt).getTime()
    const remain = Math.max(0, Math.floor((expireAt - Date.now()) / 1000))
    setRemainingSeconds(remain)
  }, [booking?.paymentExpiredAt, booking?.paymentExpiresInSeconds])

  useEffect(() => {
    if (remainingSeconds <= 0) return
    const timer = window.setInterval(() => {
      setRemainingSeconds((prev) => Math.max(0, prev - 1))
    }, 1000)
    return () => window.clearInterval(timer)
  }, [remainingSeconds])

  const isPaid = booking?.trangThaiDon === 'DA_THANH_TOAN'
  const isExpired = Boolean(booking) && !isPaid && remainingSeconds <= 0

  const handlePayment = async () => {
    if (!booking?.id) return

    try {
      setIsPaying(true)
      setErrorMessage('')
      const response = bookingType === 'hotel'
        ? await userService.mockPayHotelBooking(booking.id)
        : await userService.mockPayRestaurantBooking(booking.id)
      setBooking(response)
      setSuccessMessage('Thanh toán thành công. Đơn đặt chỗ của quý khách đã được ghi nhận.')
    } catch (error) {
      console.error('Failed to process payment:', error)
      setErrorMessage('Không thể xử lý thanh toán. Vui lòng kiểm tra trạng thái đơn hoặc thử lại sau.')
    } finally {
      setIsPaying(false)
    }
  }

  if (!booking) {
    return (
      <main className="mx-auto flex min-h-screen max-w-3xl flex-col items-center justify-center bg-surface px-5 py-12 text-center text-on-surface">
        <div className="rounded-2xl border border-outline-variant/40 bg-white p-8 shadow-sm">
          <h1 className="font-display text-3xl font-bold text-primary">Không tìm thấy thông tin thanh toán</h1>
          <p className="mt-2 text-on-surface-variant">Vui lòng tạo đơn đặt chỗ trước khi tiến hành thanh toán.</p>
          <Link to="/" className="mt-5 inline-flex rounded-xl bg-primary px-5 py-2.5 text-sm font-semibold text-on-primary">
            Về trang chủ
          </Link>
        </div>
      </main>
    )
  }

  return (
    <main className="min-h-screen bg-surface px-5 py-10 text-on-surface md:px-12">
      <div className="mx-auto grid w-full max-w-5xl gap-8 lg:grid-cols-12">
        <section className="lg:col-span-7">
          <div className="rounded-3xl border border-outline-variant/40 bg-white p-6 shadow-sm md:p-8">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="mb-5 text-sm font-semibold text-primary hover:underline"
            >
              ← Quay lại
            </button>

            <p className="text-sm font-semibold uppercase tracking-wide text-secondary">Thanh toán đơn đặt chỗ</p>
            <h1 className="mt-2 font-display text-3xl font-bold text-primary">Xác nhận thanh toán</h1>
            <p className="mt-2 text-sm text-on-surface-variant">
              Vui lòng hoàn tất thanh toán trước khi thời gian giữ chỗ kết thúc.
              {bookingType === 'hotel'
                ? ' Đây là bước thanh toán giả lập trong môi trường phát triển.'
                : ' Đơn nhà hàng hiện mới dừng ở bước tạo đơn và điều phối bàn tự động.'}
            </p>

            {errorMessage && (
              <div className="mt-6 rounded-2xl border border-red-300 bg-red-50 p-4 text-sm text-red-700">{errorMessage}</div>
            )}

            {successMessage && (
              <div className="mt-6 rounded-2xl border border-green-300 bg-green-50 p-4 text-sm text-green-700">{successMessage}</div>
            )}

            <div className={`mt-8 rounded-2xl border p-6 ${isExpired ? 'border-red-300 bg-red-50' : 'border-amber-300 bg-amber-50'}`}>
              <p className="text-sm font-semibold uppercase tracking-wide text-on-surface">Thời gian thanh toán còn lại</p>
              <p className={`mt-2 font-display text-5xl font-bold ${isExpired ? 'text-red-600' : 'text-primary'}`}>
                {isPaid ? '00:00' : formatRemainingTime(remainingSeconds)}
              </p>
              <p className="mt-2 text-sm text-on-surface-variant">
                {isPaid
                  ? 'Đơn đặt chỗ đã được thanh toán thành công.'
                  : isExpired
                    ? 'Đơn đã hết thời gian thanh toán. Vui lòng tạo đơn đặt chỗ mới nếu quý khách vẫn có nhu cầu.'
                    : 'Hệ thống đang giữ chỗ trong thời gian chờ thanh toán.'}
              </p>
            </div>

            <button
              type="button"
              onClick={handlePayment}
              disabled={isPaying || isExpired || isPaid}
              className="mt-6 w-full rounded-2xl bg-primary px-5 py-3 text-sm font-bold text-on-primary shadow-sm transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isPaid
                ? 'Đã thanh toán thành công'
                : isPaying
                  ? 'Đang xử lý thanh toán...'
                  : isExpired
                    ? 'Đơn đã hết hạn thanh toán'
                    : 'Thanh toán ngay'}
            </button>

            {isPaid && (
              <Link
                to="/"
                className="mt-4 inline-flex w-full items-center justify-center rounded-2xl border border-primary px-5 py-3 text-sm font-bold text-primary transition hover:bg-primary/5"
              >
                Trở về trang chủ
              </Link>
            )}
          </div>
        </section>

        <aside className="lg:col-span-5">
          <div className="sticky top-8 rounded-3xl border border-outline-variant/40 bg-white p-6 shadow-sm">
            <h2 className="font-display text-2xl font-bold text-primary">Thông tin đơn đặt chỗ</h2>
            <div className="mt-5 space-y-3 text-sm text-on-surface-variant">
              <p><span className="font-semibold text-on-surface">Mã đơn:</span> {booking.maDon}</p>
              {bookingType === 'hotel' && 'rooms' in booking && (
                <>
                  <p><span className="font-semibold text-on-surface">Khách sạn:</span> {booking.tenKhachSan}</p>
                  <p><span className="font-semibold text-on-surface">Ngày lưu trú:</span> {booking.ngayCheckIn} → {booking.ngayCheckOut}</p>
                  <p><span className="font-semibold text-on-surface">Số đêm:</span> {booking.soDem}</p>
                  <p><span className="font-semibold text-on-surface">Số khách:</span> {booking.soKhach}</p>
                </>
              )}
              {bookingType === 'restaurant' && 'tables' in booking && (
                <>
                  <p><span className="font-semibold text-on-surface">Nhà hàng:</span> {booking.tenNhaHang}</p>
                  <p><span className="font-semibold text-on-surface">Thời gian:</span> {new Date(booking.ngayGioBatDau).toLocaleString('vi-VN')}</p>
                  <p><span className="font-semibold text-on-surface">Số khách:</span> {booking.soNguoi}</p>
                </>
              )}
              <p><span className="font-semibold text-on-surface">Trạng thái:</span> {booking.trangThaiDon}</p>
              <p><span className="font-semibold text-on-surface">Hết hạn lúc:</span> {new Date(booking.paymentExpiredAt).toLocaleString('vi-VN')}</p>
            </div>

            {bookingType === 'hotel' && 'rooms' in booking && (
              <div className="mt-6 rounded-2xl border border-outline-variant/30 bg-surface-container-lowest p-4">
                <h3 className="font-display text-lg font-bold text-primary">Chi tiết phòng</h3>
                <div className="mt-4 space-y-3">
                  {booking.rooms.map((room) => (
                    <div key={room.roomId} className="rounded-xl bg-white p-4 shadow-sm">
                      <div className="flex items-start justify-between gap-3 text-sm">
                        <div>
                          <p className="font-semibold text-on-surface">{room.tenPhong}</p>
                          <p className="text-xs text-on-surface-variant">Số lượng: {room.soLuong} phòng • {room.soDem} đêm</p>
                        </div>
                        <p className="shrink-0 font-semibold text-primary">{formatVnd(room.thanhTien)}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {bookingType === 'restaurant' && 'tables' in booking && (
              <div className="mt-6 rounded-2xl border border-outline-variant/30 bg-surface-container-lowest p-4">
                <h3 className="font-display text-lg font-bold text-primary">Bàn được điều phối</h3>
                <div className="mt-4 space-y-3">
                  {booking.tables.map((table) => (
                    <div key={table.tableId} className="rounded-xl bg-white p-4 shadow-sm">
                      <p className="font-semibold text-on-surface">{table.tenBan}</p>
                      <p className="text-xs text-on-surface-variant">{table.soChoNgoi} chỗ{table.viTriSanh ? ` • ${table.viTriSanh}` : ''}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            <div className="mt-6 border-t border-outline-variant/30 pt-5">
              <p className="text-sm text-on-surface-variant">Tổng thanh toán</p>
              <p className="mt-1 font-display text-3xl font-bold text-primary">{formatVnd(booking.tongTienThanhToan ?? 0)}</p>
            </div>
          </div>
        </aside>
      </div>
    </main>
  )
}
