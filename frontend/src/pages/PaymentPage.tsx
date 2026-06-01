import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { userService, type HotelBookingResponse, type RestaurantBookingResponse } from '../services/userService'
import { bookingVoucherService, type VoucherCheckoutResponse, type VoucherPreviewResponse } from '../services/bookingVoucherService'
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
  const [voucherCode, setVoucherCode] = useState('')
  const [voucherPreview, setVoucherPreview] = useState<VoucherPreviewResponse | null>(null)
  const [availableVouchers, setAvailableVouchers] = useState<VoucherPreviewResponse[]>([])
  const [voucherCheckout, setVoucherCheckout] = useState<VoucherCheckoutResponse | null>(null)
  const [voucherLoading, setVoucherLoading] = useState(false)
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
  const effectiveOriginalAmount =
    voucherCheckout?.originalAmount ??
    (booking && 'tongTienGoc' in booking ? booking.tongTienGoc : booking?.tongTienThanhToan ?? 0)
  const effectiveDiscountAmount =
    voucherCheckout?.discountAmount ??
    (booking && 'tienKhuyenMai' in booking ? booking.tienKhuyenMai ?? 0 : 0)
  const effectiveFinalAmount = voucherCheckout?.finalAmount ?? booking?.tongTienThanhToan ?? 0

  useEffect(() => {
    const loadAvailableVouchers = async () => {
      if (!booking?.id || isPaid || isExpired) return
      try {
        const vouchers = await bookingVoucherService.getAvailableVouchers(booking.id)
        setAvailableVouchers(vouchers)
      } catch {
        setAvailableVouchers([])
      }
    }
    void loadAvailableVouchers()
  }, [booking?.id, isPaid, isExpired])

  const handlePreviewVoucher = async () => {
    if (!booking?.id || !voucherCode.trim()) return
    try {
      setVoucherLoading(true)
      setErrorMessage('')
      const preview = await bookingVoucherService.previewVoucher(booking.id, voucherCode.trim())
      setVoucherPreview(preview)
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Không thể xem trước voucher.')
    } finally {
      setVoucherLoading(false)
    }
  }

  const handleApplyVoucher = async (code?: string) => {
    if (!booking?.id) return
    const candidateCode = (code ?? voucherCode).trim()
    if (!candidateCode) return
    try {
      setVoucherLoading(true)
      setErrorMessage('')
      setSuccessMessage('')
      const response = await bookingVoucherService.applyVoucher(booking.id, candidateCode)
      const originalAmount = Number(response.originalAmount ?? 0)
      const discountAmount = Number(response.discountAmount ?? 0)
      const finalAmount = Number(response.finalAmount ?? 0)
      setVoucherCheckout(response)
      setVoucherCode(candidateCode)
      setVoucherPreview(null)
      setBooking((current) => {
        if (!current) return current
        return {
          ...current,
          tongTienThanhToan: finalAmount,
          ...(('tongTienGoc' in current)
            ? { tongTienGoc: originalAmount, tienKhuyenMai: discountAmount }
            : {}),
        }
      })
      setSuccessMessage(response.message || 'Áp dụng voucher thành công.')
      const vouchers = await bookingVoucherService.getAvailableVouchers(booking.id)
      setAvailableVouchers(vouchers)
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Không thể áp dụng voucher.')
    } finally {
      setVoucherLoading(false)
    }
  }

  const handleRemoveVoucher = async () => {
    if (!booking?.id) return
    try {
      setVoucherLoading(true)
      setErrorMessage('')
      setSuccessMessage('')
      await bookingVoucherService.removeVoucher(booking.id)
      setVoucherCheckout(null)
      setVoucherPreview(null)
      setBooking((current) => {
        if (!current) return current
        const original = 'tongTienGoc' in current ? current.tongTienGoc : current.tongTienThanhToan
        return {
          ...current,
          tongTienThanhToan: original ?? 0,
          ...(('tongTienGoc' in current) ? { tienKhuyenMai: 0 } : {}),
        }
      })
      setSuccessMessage('Đã gỡ voucher khỏi đơn đặt chỗ.')
      const vouchers = await bookingVoucherService.getAvailableVouchers(booking.id)
      setAvailableVouchers(vouchers)
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Không thể gỡ voucher.')
    } finally {
      setVoucherLoading(false)
    }
  }

  const handlePayment = async () => {
    if (!booking?.id) return

    try {
      setIsPaying(true)
      setErrorMessage('')
      const response = bookingType === 'hotel'
        ? await userService.mockPayHotelBooking(booking.id)
        : await userService.mockPayRestaurantBooking(booking.id)
      setBooking(response)
      const discountFromBooking = 'tienKhuyenMai' in response ? (response.tienKhuyenMai ?? 0) : 0
      if (discountFromBooking > 0) {
        const originalFromBooking = 'tongTienGoc' in response
          ? response.tongTienGoc
          : response.tongTienThanhToan + discountFromBooking
        setVoucherCheckout({
          bookingId: response.id,
          voucherId: 0,
          maVoucher: voucherCode || 'APPLIED',
          originalAmount: originalFromBooking,
          discountAmount: discountFromBooking,
          finalAmount: response.tongTienThanhToan ?? 0,
          isApplied: true,
          message: 'Voucher đã được áp dụng.',
        })
      } else {
        setVoucherCheckout(null)
      }
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

            {!isPaid && !isExpired && (
              <section className="mt-8 rounded-2xl border border-outline-variant/40 bg-surface-container-lowest p-5">
                <h3 className="font-display text-lg font-bold text-primary">Voucher cho đơn đặt chỗ</h3>
                <div className="mt-3 flex gap-3">
                  <input
                    value={voucherCode}
                    onChange={(event) => setVoucherCode(event.target.value.toUpperCase())}
                    className="w-full rounded-xl border border-outline-variant/50 bg-white px-4 py-3 text-sm outline-none transition focus:border-primary"
                    placeholder="Nhập mã voucher"
                  />
                  <button
                    type="button"
                    onClick={handlePreviewVoucher}
                    disabled={voucherLoading || !voucherCode.trim()}
                    className="rounded-xl border border-primary px-4 py-3 text-sm font-semibold text-primary disabled:opacity-60"
                  >
                    Xem trước
                  </button>
                  <button
                    type="button"
                    onClick={() => handleApplyVoucher()}
                    disabled={voucherLoading || !voucherCode.trim()}
                    className="rounded-xl bg-secondary px-4 py-3 text-sm font-semibold text-on-secondary disabled:opacity-60"
                  >
                    Áp dụng
                  </button>
                </div>

                {voucherPreview && (
                  <div className="mt-3 rounded-xl border border-outline-variant/40 bg-white p-3 text-sm">
                    {voucherPreview.isEligible ? (
                      <p className="text-secondary">
                        Có thể áp dụng, giảm {formatVnd(voucherPreview.discountAmount ?? 0)} còn {formatVnd(voucherPreview.estimatedFinalAmount ?? 0)}.
                      </p>
                    ) : (
                      <p className="text-error">{voucherPreview.ineligibilityReason || 'Voucher không hợp lệ.'}</p>
                    )}
                  </div>
                )}

                {voucherCheckout?.isApplied && (
                  <div className="mt-3 flex items-center justify-between rounded-xl border border-green-300 bg-green-50 p-3 text-sm text-green-700">
                    <span>Đã áp dụng voucher: {voucherCode}</span>
                    <button
                      type="button"
                      onClick={handleRemoveVoucher}
                      disabled={voucherLoading}
                      className="rounded-lg border border-green-600 px-3 py-1 font-semibold"
                    >
                      Gỡ voucher
                    </button>
                  </div>
                )}

                {availableVouchers.length > 0 && (
                  <div className="mt-4">
                    <p className="text-sm font-semibold text-on-surface">Voucher khả dụng</p>
                    <div className="mt-2 space-y-2">
                      {availableVouchers.slice(0, 6).map((item) => (
                        <button
                          key={`${item.voucherId}-${item.maVoucher}`}
                          type="button"
                          onClick={() => {
                            setVoucherCode(item.maVoucher)
                            void handleApplyVoucher(item.maVoucher)
                          }}
                          disabled={!item.isEligible || voucherLoading}
                          className="flex w-full items-center justify-between rounded-lg border border-outline-variant/40 bg-white px-3 py-2 text-left text-sm disabled:opacity-60"
                        >
                          <span>{item.maVoucher}</span>
                          <span className="font-semibold text-primary">
                            {item.isEligible ? `-${formatVnd(item.discountAmount ?? 0)}` : item.ineligibilityReason || 'Không hợp lệ'}
                          </span>
                        </button>
                      ))}
                    </div>
                  </div>
                )}
              </section>
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
              <p className="text-sm text-on-surface-variant">Tổng tiền gốc</p>
              <p className="mt-1 text-lg font-semibold text-on-surface">{formatVnd(effectiveOriginalAmount)}</p>
              <p className="mt-3 text-sm text-on-surface-variant">Giảm giá</p>
              <p className="mt-1 text-lg font-semibold text-secondary">- {formatVnd(effectiveDiscountAmount)}</p>
              <p className="mt-3 text-sm text-on-surface-variant">Tổng thanh toán</p>
              <p className="mt-1 font-display text-3xl font-bold text-primary">{formatVnd(effectiveFinalAmount)}</p>
            </div>
          </div>
        </aside>
      </div>
    </main>
  )
}
