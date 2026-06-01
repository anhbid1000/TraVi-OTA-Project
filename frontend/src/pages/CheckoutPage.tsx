import { useEffect, useMemo, useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { userService } from '../services/userService'
import { formatVnd } from '../utils/display'

type CheckoutState = {
  type?: 'hotel' | 'restaurant'
  hotelId?: string
  restaurantId?: string
  roomId?: string
  checkIn?: string
  checkOut?: string
  date?: string
  time?: string
  guests?: number
  nights?: number
  quantity?: number
  roomSelections?: Array<{ roomId?: string; roomName?: string; quantity?: number; pricePerNight?: number }>
  roomCombination?: Array<{ roomId?: string; roomName?: string; roomType?: string; quantity?: number; pricePerRoom?: number }>
  roomCombinationTotalRooms?: number
  roomCombinationTotalCapacity?: number
  roomCombinationPricePerNight?: number
  selectedItems?: Record<string, number>
  selectedItemNames?: Record<string, string>
  totalPrice?: number
}

type BookingForm = {
  fullName: string
  email: string
  phone: string
  note: string
}

function getUserString(user: Record<string, unknown> | null, keys: string[]) {
  if (!user) return ''
  for (const key of keys) {
    const value = user[key]
    if (typeof value === 'string' && value.trim()) return value
  }
  return ''
}

export function CheckoutPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { isAuthenticated, user } = useAuth()
  const booking = (location.state ?? {}) as CheckoutState

  const initialForm = useMemo<BookingForm>(() => ({
    fullName: isAuthenticated ? getUserString(user, ['hoTen', 'fullName', 'name', 'username']) : '',
    email: isAuthenticated ? getUserString(user, ['email']) : '',
    phone: isAuthenticated ? getUserString(user, ['soDienThoai', 'phone', 'phoneNumber']) : '',
    note: '',
  }), [isAuthenticated, user])

  const [form, setForm] = useState<BookingForm>(initialForm)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const isValidPhoneNumber = (value: string) => /^(0|\+84)\d{9,10}$/.test(value.trim())

  useEffect(() => {
    setForm(initialForm)
  }, [initialForm])

  useEffect(() => {
    if (!isAuthenticated || user?.email) return
    const fetchProfile = async () => {
      try {
        const profile = await userService.getProfile()
        setForm((current) => ({
          ...current,
          fullName: profile.hoTen || current.fullName,
          email: profile.email || current.email,
          phone: profile.soDienThoai || current.phone,
        }))
      } catch (error) {
        console.error('Failed to fetch user profile:', error)
      }
    }
    void fetchProfile()
  }, [isAuthenticated, user?.email])

  const modeLabel = isAuthenticated ? 'Đặt chỗ bằng tài khoản' : 'Đặt chỗ nhanh'
  const isHotel = booking.type === 'hotel'
  const isRestaurant = booking.type === 'restaurant'
  const hasBookingState = isHotel || isRestaurant

  const roomSelectionLines = booking.roomSelections ?? []
  const roomCombinationLines = booking.roomCombination ?? []
  const hasRoomBreakdown = roomSelectionLines.length > 0 || roomCombinationLines.length > 0

  const menuPreorderLines = Object.entries(booking.selectedItems ?? {})
    .filter(([, qty]) => Number(qty) > 0)
    .map(([itemId, qty]) => ({ itemId, quantity: Number(qty) }))

  const canSubmitHotelBooking = isHotel && Boolean(booking.hotelId) && hasRoomBreakdown

  const buildHotelRoomsPayload = () => {
    if (roomCombinationLines.length > 0) {
      return roomCombinationLines
        .filter((item) => item.roomId && Number(item.quantity) > 0)
        .map((item) => ({ roomId: String(item.roomId), soLuong: Number(item.quantity ?? 1) }))
    }
    return roomSelectionLines
      .filter((room) => room.roomId && Number(room.quantity) > 0)
      .map((room) => ({ roomId: String(room.roomId), soLuong: Number(room.quantity ?? 1) }))
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setErrorMessage('')

    if (!isAuthenticated) {
      setErrorMessage('Vui lòng đăng nhập để tiếp tục thanh toán.')
      return
    }

    if (isHotel && !canSubmitHotelBooking) {
      setErrorMessage('Không đủ dữ liệu để tạo đơn đặt phòng. Vui lòng chọn lại phòng.')
      return
    }

    if (!isValidPhoneNumber(form.phone)) {
      setErrorMessage('Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam hợp lệ.')
      return
    }

    try {
      setIsSubmitting(true)
      if (isHotel) {
        const response = await userService.createHotelBooking({
          hotelId: String(booking.hotelId),
          tenNguoiDat: form.fullName.trim(),
          sdtNguoiDat: form.phone.trim(),
          emailNguoiDat: form.email.trim(),
          ghiChu: form.note.trim(),
          ngayCheckIn: String(booking.checkIn),
          ngayCheckOut: String(booking.checkOut),
          soKhach: Number(booking.guests ?? 1),
          expectedTotalAmount: Number(booking.totalPrice ?? 0),
          rooms: buildHotelRoomsPayload(),
        })
        navigate('/payment', { state: { booking: response, bookingType: 'hotel' } })
      } else if (isRestaurant) {
        const response = await userService.createRestaurantBooking({
          restaurantId: String(booking.restaurantId),
          tenNguoiDat: form.fullName.trim(),
          sdtNguoiDat: form.phone.trim(),
          emailNguoiDat: form.email.trim(),
          ghiChu: form.note.trim(),
          date: String(booking.date),
          time: String(booking.time),
          soNguoi: Number(booking.guests ?? 1),
        })
        navigate('/payment', { state: { booking: response, bookingType: 'restaurant' } })
      }
    } catch (error) {
      console.error('Failed to create booking:', error)
      const err = error as Error
      setErrorMessage(`Tạo đơn đặt chỗ thất bại: ${err.message || 'Vui lòng kiểm tra lại thông tin và thử lại.'}`)
    } finally {
      setIsSubmitting(false)
    }
  }

  if (!hasBookingState) {
    return (
      <main className="mx-auto flex min-h-screen max-w-3xl flex-col items-center justify-center bg-surface px-5 py-12 text-center text-on-surface">
        <div className="rounded-2xl border border-outline-variant/40 bg-white p-8 shadow-sm">
          <h1 className="font-display text-3xl font-bold text-primary">Chưa có thông tin đặt chỗ</h1>
          <p className="mt-2 text-on-surface-variant">Vui lòng chọn khách sạn hoặc nhà hàng trước khi tiếp tục.</p>
          <Link to="/" className="mt-5 inline-flex rounded-xl bg-primary px-5 py-2.5 text-sm font-semibold text-on-primary">Về trang chủ</Link>
        </div>
      </main>
    )
  }

  return (
    <main className="min-h-screen bg-background px-5 py-8 text-on-background md:px-12 md:py-10">
      <div className="mx-auto w-full max-w-6xl">
        <div className="mb-8">
          <button type="button" onClick={() => navigate(-1)} className="mb-4 text-sm font-semibold text-primary hover:underline">← Quay lại</button>
          <h1 className="font-display text-3xl font-bold text-primary md:text-4xl">Thanh toán an toàn</h1>
          <p className="mt-2 text-sm text-on-surface-variant">{modeLabel}</p>
          <div className="mt-6 flex w-full max-w-2xl items-center text-sm">
            <div className="flex items-center text-primary"><div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary-container text-sm font-bold text-on-primary-container">✓</div><span className="ml-2 font-semibold">Xem lại</span></div>
            <div className="mx-3 h-px flex-1 bg-primary-container" />
            <div className="flex items-center text-primary"><div className="flex h-8 w-8 items-center justify-center rounded-full border-2 border-primary bg-white text-sm font-bold">2</div><span className="ml-2 font-semibold">Thanh toán</span></div>
            <div className="mx-3 h-px flex-1 bg-outline-variant/50" />
            <div className="flex items-center text-on-surface-variant"><div className="flex h-8 w-8 items-center justify-center rounded-full bg-surface-container text-sm font-bold">3</div><span className="ml-2 font-semibold">Xác nhận</span></div>
          </div>
        </div>

        <div className="grid gap-6 lg:grid-cols-12">
          <section className="space-y-6 lg:col-span-8">
            <form onSubmit={handleSubmit} className="space-y-6">
              <section className="rounded-xl border border-outline-variant/30 bg-surface-container-lowest p-6 shadow-sm">
                <h2 className="font-display text-xl font-bold text-on-surface">Thông tin khách đặt</h2>
                <div className="mt-5 grid grid-cols-1 gap-5 md:grid-cols-2">
                  <label className="text-sm font-semibold text-on-surface">Họ và tên
                    <input value={form.fullName} onChange={(e) => setForm((c) => ({ ...c, fullName: e.target.value }))} required className="mt-2 w-full rounded-xl border border-outline-variant/50 bg-surface px-4 py-3 text-sm outline-none transition focus:border-primary" placeholder="Nguyễn Văn A" />
                  </label>
                  <label className="text-sm font-semibold text-on-surface">Số điện thoại
                    <input value={form.phone} onChange={(e) => setForm((c) => ({ ...c, phone: e.target.value }))} required className="mt-2 w-full rounded-xl border border-outline-variant/50 bg-surface px-4 py-3 text-sm outline-none transition focus:border-primary" placeholder="09xxxxxxxx" />
                  </label>
                  <label className="text-sm font-semibold text-on-surface md:col-span-2">Email
                    <input type="email" value={form.email} onChange={(e) => setForm((c) => ({ ...c, email: e.target.value }))} required className="mt-2 w-full rounded-xl border border-outline-variant/50 bg-surface px-4 py-3 text-sm outline-none transition focus:border-primary" placeholder="email@example.com" />
                  </label>
                </div>
              </section>

              <section className="rounded-xl border border-outline-variant/30 bg-surface-container-lowest p-6 shadow-sm">
                <h2 className="font-display text-xl font-bold text-on-surface">Mã ưu đãi</h2>
                <div className="mt-4 flex gap-3">
                  <input className="w-full rounded-xl border border-outline-variant/50 bg-surface px-4 py-3 text-sm outline-none transition focus:border-primary" placeholder="Nhập mã giảm giá" type="text" />
                  <button type="button" className="rounded-xl bg-secondary px-5 py-3 text-sm font-bold text-on-secondary">Áp dụng</button>
                </div>
              </section>

              <section className="rounded-xl border border-outline-variant/30 bg-surface-container-lowest p-6 shadow-sm">
                <h2 className="font-display text-xl font-bold text-on-surface">Yêu cầu đặc biệt</h2>
                <p className="mt-2 text-sm text-on-surface-variant">Cơ sở sẽ cố gắng đáp ứng, nhưng không đảm bảo 100%.</p>
                <textarea value={form.note} onChange={(e) => setForm((c) => ({ ...c, note: e.target.value }))} rows={4} className="mt-4 w-full rounded-xl border border-outline-variant/50 bg-surface px-4 py-3 text-sm outline-none transition focus:border-primary" placeholder="Ví dụ: check-in sớm, bàn gần cửa sổ..." />
              </section>

              {errorMessage && <div className="rounded-xl border border-red-300 bg-red-50 p-4 text-sm text-red-700">{errorMessage}</div>}

              <button type="submit" disabled={isSubmitting} className="w-full rounded-xl bg-primary px-5 py-3 text-sm font-bold text-on-primary shadow-sm transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60">
                {isSubmitting ? 'Đang tạo đơn đặt chỗ...' : 'Tiếp tục đến trang thanh toán'}
              </button>
            </form>
          </section>

          <aside className="lg:col-span-4">
            <div className="sticky top-8 overflow-hidden rounded-xl border border-outline-variant/40 bg-white shadow-sm">
              <div className="h-44 bg-gradient-to-br from-primary/20 to-secondary/35" />
              <div className="p-6">
                <h2 className="font-display text-2xl font-bold text-primary">Tóm tắt đơn</h2>
                <div className="mt-5 space-y-3 text-sm text-on-surface-variant">
                  <p><span className="font-semibold text-on-surface">Loại:</span> {isHotel ? 'Khách sạn' : 'Nhà hàng'}</p>
                  {isHotel && <p><span className="font-semibold text-on-surface">Ngày:</span> {booking.checkIn} → {booking.checkOut}</p>}
                  {isRestaurant && <p><span className="font-semibold text-on-surface">Thời gian:</span> {booking.date} {booking.time}</p>}
                  <p><span className="font-semibold text-on-surface">Số khách:</span> {booking.guests ?? 1}</p>
                  {isHotel && <p><span className="font-semibold text-on-surface">Số đêm:</span> {booking.nights ?? 1}</p>}
                </div>

                {isHotel && (
                  <div className="mt-6 rounded-2xl border border-outline-variant/30 bg-surface-container-lowest p-4">
                    <h3 className="font-display text-lg font-bold text-primary">Chi tiết phòng</h3>
                    {!hasRoomBreakdown ? <p className="mt-2 text-sm text-on-surface-variant">Chưa có chi tiết phòng được truyền sang checkout.</p> : (
                      <div className="mt-4 space-y-4">
                        {roomCombinationLines.length > 0 && (
                          <div className="rounded-xl bg-white p-4 shadow-sm">
                            <div className="mb-3 flex items-start justify-between gap-3">
                              <div>
                                <p className="font-semibold text-on-surface">Tổ hợp phòng đã chọn</p>
                                <p className="text-xs text-on-surface-variant">{booking.roomCombinationTotalRooms ?? booking.quantity ?? 1} phòng • Tối đa {booking.roomCombinationTotalCapacity ?? booking.guests ?? 1} khách</p>
                              </div>
                              <p className="text-right text-sm font-bold text-primary">{formatVnd(booking.roomCombinationPricePerNight ?? 0)} / đêm</p>
                            </div>
                            <div className="space-y-2">
                              {roomCombinationLines.map((item, index) => (
                                <div key={`${item.roomId ?? index}-${index}`} className="flex justify-between gap-3 text-sm">
                                  <div>
                                    <p className="font-medium text-on-surface">{item.quantity ?? 1}x {item.roomType || item.roomName || 'Phòng'}</p>
                                    {item.roomName && item.roomName !== item.roomType && <p className="text-xs text-on-surface-variant">{item.roomName}</p>}
                                  </div>
                                  <p className="shrink-0 text-on-surface-variant">{formatVnd(item.pricePerRoom ?? 0)} / phòng</p>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}

                        {roomSelectionLines.length > 0 && (
                          <div className="space-y-3">
                            {roomSelectionLines.map((room, index) => {
                              const quantity = room.quantity ?? 1
                              const pricePerNight = room.pricePerNight ?? 0
                              return (
                                <div key={`${room.roomId ?? index}-${index}`} className="rounded-xl bg-white p-4 shadow-sm">
                                  <div className="flex items-start justify-between gap-3">
                                    <div>
                                      <p className="font-semibold text-on-surface">{room.roomName || 'Phòng'}</p>
                                      <p className="text-xs text-on-surface-variant">Số lượng: {quantity} phòng</p>
                                    </div>
                                    <div className="text-right">
                                      <p className="text-sm font-bold text-primary">{formatVnd(pricePerNight)} / đêm</p>
                                      <p className="text-xs text-on-surface-variant">Tạm tính: {formatVnd(pricePerNight * quantity * (booking.nights ?? 1))}</p>
                                    </div>
                                  </div>
                                </div>
                              )
                            })}
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                )}

                {isRestaurant && (
                  <div className="mt-6 rounded-2xl border border-outline-variant/30 bg-surface-container-lowest p-4">
                    <h3 className="font-display text-lg font-bold text-primary">Chi tiết đặt bàn</h3>
                    <div className="mt-3 space-y-2 text-sm text-on-surface-variant">
                      <p><span className="font-semibold text-on-surface">Ngày:</span> {booking.date || 'Chưa chọn'}</p>
                      <p><span className="font-semibold text-on-surface">Giờ:</span> {booking.time || 'Chưa chọn'}</p>
                      <p><span className="font-semibold text-on-surface">Số khách:</span> {booking.guests ?? 1}</p>
                    </div>
                    <div className="mt-4 rounded-xl bg-white p-4 shadow-sm">
                      <p className="font-semibold text-on-surface">Món đặt trước</p>
                  {menuPreorderLines.length === 0 ? <p className="mt-2 text-sm text-on-surface-variant">Chưa chọn món đặt trước.</p> : (
                        <div className="mt-3 space-y-2">
                          {menuPreorderLines.map((item) => (
                            <div key={item.itemId} className="flex justify-between gap-3 text-sm">
                              <span className="text-on-surface">{booking.selectedItemNames?.[item.itemId] || `Món #${item.itemId}`}</span>
                              <span className="font-semibold text-primary">x{item.quantity}</span>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                )}

                <div className="mt-6 border-t border-outline-variant/30 pt-5">
                  <p className="text-sm text-on-surface-variant">Tạm tính</p>
                  <p className="mt-1 font-display text-3xl font-bold text-primary">{formatVnd(booking.totalPrice ?? 0)}</p>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </main>
  )
}
