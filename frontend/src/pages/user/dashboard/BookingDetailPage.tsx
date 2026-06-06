import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { CalendarDays, ChevronLeft, MapPin, Phone, Mail, FileText } from 'lucide-react'
import { CustomerFeedbackLayout } from '../../../features/feedback-v2/components/CustomerFeedbackLayout'
import { userService, type BookingSearchResponse } from '../../../services/userService'
import { getApiErrorMessage } from '../../../utils/apiError'

function formatVND(amount?: number) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount || 0)
}

function formatDate(value?: string) {
  if (!value) return 'Đang cập nhật'
  return new Date(value).toLocaleDateString('vi-VN', {
    weekday: 'short',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

function formatDateTime(value?: string) {
  if (!value) return 'Đang cập nhật'
  return new Date(value).toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function statusLabel(status: string) {
  const labels: Record<string, string> = {
    CHO_THANH_TOAN: 'Chờ thanh toán',
    DA_THANH_TOAN: 'Đã thanh toán',
    DA_XAC_NHAN: 'Đã xác nhận',
    DANG_PHUC_VU: 'Đang phục vụ',
    DA_HOAN_THANH: 'Đã hoàn thành',
    DA_HUY: 'Đã hủy',
    YEU_CAU_HOAN_TIEN: 'Yêu cầu hoàn tiền',
    DA_HOAN_TIEN: 'Đã hoàn tiền',
    THANH_TOAN_THAT_BAI: 'Thanh toán thất bại',
    KHACH_KHONG_DEN: 'Khách không đến',
  }
  return labels[status] ?? status
}

function statusClass(status: string) {
  if (status === 'DA_THANH_TOAN' || status === 'DA_XAC_NHAN' || status === 'DA_HOAN_THANH') {
    return 'bg-emerald-100 text-emerald-700'
  }
  if (status === 'CHO_THANH_TOAN') {
    return 'bg-amber-100 text-amber-700'
  }
  if (status === 'DA_HUY' || status === 'THANH_TOAN_THAT_BAI') {
    return 'bg-red-100 text-red-700'
  }
  return 'bg-slate-100 text-slate-700'
}

function isCancellableStatus(status: string) {
  return ['CHO_THANH_TOAN', 'DA_THANH_TOAN', 'DA_XAC_NHAN'].includes(status)
}

export default function BookingDetailPage() {
  const { bookingId = '' } = useParams()
  const navigate = useNavigate()
  const [booking, setBooking] = useState<BookingSearchResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [cancelLoading, setCancelLoading] = useState(false)
  const [cancelMessage, setCancelMessage] = useState<string | null>(null)
  const [cancelModalOpen, setCancelModalOpen] = useState(false)
  const [cancelReason, setCancelReason] = useState('')

  useEffect(() => {
    const loadDetail = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await userService.getMyBookingDetail(bookingId)
        setBooking(data)
      } catch (err) {
        setError(getApiErrorMessage(err, 'Không thể tải chi tiết đơn đặt chỗ.'))
      } finally {
        setLoading(false)
      }
    }

    if (bookingId) {
      void loadDetail()
    } else {
      setError('Thiếu mã đơn đặt chỗ.')
      setLoading(false)
    }
  }, [bookingId])

  const openCancelModal = () => {
    if (!booking) return

    if (!isCancellableStatus(booking.trangThai)) {
      setCancelMessage('Đơn hiện không thể hủy ở trạng thái này.')
      return
    }

    setCancelReason('')
    setCancelModalOpen(true)
  }

  const handleCancelBooking = async () => {
    if (!booking || cancelLoading) return

    try {
      setCancelLoading(true)
      setCancelMessage(null)
      const response = await userService.cancelMyBooking(booking.id, {
        reason: cancelReason.trim() || undefined,
      })

      const freshBooking = await userService.getMyBookingDetail(booking.id)
      setBooking(freshBooking)
      setCancelModalOpen(false)
      setCancelReason('')
      setCancelMessage(response.message)
    } catch (err) {
      setCancelMessage(getApiErrorMessage(err, 'Không thể hủy đơn đặt chỗ. Vui lòng thử lại.'))
    } finally {
      setCancelLoading(false)
    }
  }

  useEffect(() => {
    const handleEscKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && cancelModalOpen && !cancelLoading) {
        setCancelModalOpen(false)
      }
    }
    document.addEventListener('keydown', handleEscKey)
    return () => document.removeEventListener('keydown', handleEscKey)
  }, [cancelModalOpen, cancelLoading])

  const summary = useMemo(() => {
    if (!booking) return null

    const roomTotal = booking.rooms?.reduce((sum, room) => sum + room.thanhTien, 0) ?? 0
    const hasRoomBreakdown = roomTotal > 0

    return {
      imageUrl:
        booking.anhTaiSan ||
        (booking.loaiTaiSan === 'HOTEL'
          ? 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80'
          : 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1200&q=80'),
      primaryAmount: hasRoomBreakdown ? roomTotal : booking.tongTienThanhToan,
    }
  }, [booking])

  return (
    <CustomerFeedbackLayout active="bookings" title="Chi tiết đặt chỗ" subtitle="Xem thông tin chi tiết đơn đặt chỗ của bạn.">
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="inline-flex items-center gap-2 rounded-lg border border-outline-variant/50 bg-white px-4 py-2 text-sm font-semibold text-on-surface hover:bg-surface-container-low"
          >
            <ChevronLeft size={16} /> Quay lại
          </button>
          {booking && (
            <span className={`rounded-full px-3 py-1 text-sm font-semibold ${statusClass(booking.trangThai)}`}>
              {statusLabel(booking.trangThai)}
            </span>
          )}
        </div>

        {loading && <div className="rounded-xl bg-white p-8 text-center font-semibold">Đang tải chi tiết đơn đặt chỗ...</div>}
        {error && <div className="rounded-xl bg-red-50 p-4 text-red-700">{error}</div>}
        {cancelMessage && <div className="rounded-xl bg-amber-50 p-4 text-amber-800">{cancelMessage}</div>}

        {!loading && booking && summary && (
          <>
          <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
            <div className="space-y-6 xl:col-span-8">
              <section className="overflow-hidden rounded-2xl border border-outline-variant/40 bg-white shadow-sm">
                <div className="relative h-56 md:h-72">
                  <img src={summary.imageUrl} alt={booking.tenTaiSan} className="h-full w-full object-cover" />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
                  <div className="absolute bottom-5 left-6 right-6 text-white">
                    <p className="mb-2 text-sm font-semibold uppercase tracking-wider opacity-90">
                      {booking.loaiTaiSan === 'HOTEL' ? 'Khách sạn' : 'Nhà hàng'}
                    </p>
                    <h2 className="text-2xl font-bold md:text-3xl">{booking.tenTaiSan}</h2>
                    <p className="mt-1 text-sm opacity-90">Mã đơn: {booking.maDon}</p>
                  </div>
                </div>

                <div className="space-y-8 p-6 md:p-8">
                  <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                    <div>
                      <p className="mb-1 text-xs font-semibold uppercase tracking-wider text-on-surface-variant">
                        {booking.loaiTaiSan === 'HOTEL' ? 'Ngày nhận phòng' : 'Thời gian bắt đầu'}
                      </p>
                      <p className="text-lg font-semibold text-on-surface">
                        {booking.loaiTaiSan === 'HOTEL'
                          ? formatDate(booking.ngayNhanPhong || booking.ngayBatDau)
                          : formatDateTime(booking.ngayBatDau)}
                      </p>
                      {booking.loaiTaiSan === 'HOTEL' && booking.chinhSach?.gioNhanPhong && (
                        <p className="text-sm text-on-surface-variant">Từ {booking.chinhSach.gioNhanPhong}</p>
                      )}
                    </div>
                    <div>
                      <p className="mb-1 text-xs font-semibold uppercase tracking-wider text-on-surface-variant">
                        {booking.loaiTaiSan === 'HOTEL' ? 'Ngày trả phòng' : 'Thời gian kết thúc'}
                      </p>
                      <p className="text-lg font-semibold text-on-surface">
                        {booking.loaiTaiSan === 'HOTEL'
                          ? formatDate(booking.ngayTraPhong || booking.ngayKetThuc)
                          : formatDateTime(booking.ngayKetThuc)}
                      </p>
                      {booking.loaiTaiSan === 'HOTEL' && booking.chinhSach?.gioTraPhong && (
                        <p className="text-sm text-on-surface-variant">Đến {booking.chinhSach.gioTraPhong}</p>
                      )}
                    </div>
                  </div>

                  {booking.loaiTaiSan === 'RESTAURANT' && (
                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                      <div className="flex items-start gap-3 rounded-xl border border-outline-variant/40 bg-surface-container-low p-4">
                        <div className="rounded-lg bg-primary/10 p-2 text-primary">
                          <CalendarDays size={18} />
                        </div>
                        <div>
                          <p className="text-xs font-semibold uppercase tracking-wider text-on-surface-variant">Ngày đặt bàn</p>
                          <p className="mt-1 font-semibold text-on-surface">{formatDate(booking.ngayBatDau)}</p>
                        </div>
                      </div>
                      <div className="flex items-start gap-3 rounded-xl border border-outline-variant/40 bg-surface-container-low p-4">
                        <div className="rounded-lg bg-primary/10 p-2 text-primary">
                          <CalendarDays size={18} />
                        </div>
                        <div>
                          <p className="text-xs font-semibold uppercase tracking-wider text-on-surface-variant">Giờ đặt bàn</p>
                          <p className="mt-1 font-semibold text-on-surface">
                            {booking.ngayBatDau ? new Date(booking.ngayBatDau).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) : 'Đang cập nhật'}
                            {booking.ngayKetThuc ? ` - ${new Date(booking.ngayKetThuc).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}` : ''}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-start gap-3 rounded-xl border border-outline-variant/40 bg-surface-container-low p-4">
                        <div className="rounded-lg bg-primary/10 p-2 text-primary">
                          <FileText size={18} />
                        </div>
                        <div>
                          <p className="text-xs font-semibold uppercase tracking-wider text-on-surface-variant">Số khách</p>
                          <p className="mt-1 font-semibold text-on-surface">{booking.soKhach} người</p>
                        </div>
                      </div>
                      <div className="flex items-start gap-3 rounded-xl border border-outline-variant/40 bg-surface-container-low p-4">
                        <div className="rounded-lg bg-primary/10 p-2 text-primary">
                          <MapPin size={18} />
                        </div>
                        <div>
                          <p className="text-xs font-semibold uppercase tracking-wider text-on-surface-variant">Bàn đã đặt</p>
                          <p className="mt-1 font-semibold text-on-surface">
                            {booking.tables && booking.tables.length > 0
                              ? booking.tables.map(t => `${t.tenBan} (${t.soChoNgoi} chỗ)`).join(', ')
                              : 'Đang cập nhật'}
                          </p>
                        </div>
                      </div>
                    </div>
                  )}

                  {booking.loaiTaiSan === 'HOTEL' && booking.diaChiTaiSan && (
                    <div className="flex items-start gap-3 rounded-xl border border-outline-variant/40 bg-surface-container-low p-4">
                      <MapPin size={20} className="mt-0.5 shrink-0 text-primary" />
                      <div>
                        <p className="font-semibold text-on-surface">Địa chỉ</p>
                        <p className="mt-1 text-sm text-on-surface-variant">{booking.diaChiTaiSan}</p>
                      </div>
                    </div>
                  )}

                  {booking.loaiTaiSan === 'RESTAURANT' && booking.diaChiTaiSan && (
                    <div className="flex items-start gap-3 rounded-xl border border-outline-variant/40 bg-surface-container-low p-4">
                      <MapPin size={20} className="mt-0.5 shrink-0 text-primary" />
                      <div>
                        <p className="font-semibold text-on-surface">Địa chỉ nhà hàng</p>
                        <p className="mt-1 text-sm text-on-surface-variant">{booking.diaChiTaiSan}</p>
                      </div>
                    </div>
                  )}

                  <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                    <div>
                      <h3 className="mb-4 text-lg font-bold text-on-surface">Thông tin đặt chỗ</h3>
                      <div className="space-y-3 text-sm text-on-surface">
                        <p className="flex items-center gap-2"><CalendarDays size={16} /> Ngày tạo đơn: {formatDateTime(booking.ngayTao)}</p>
                        <p className="flex items-center gap-2"><FileText size={16} /> Số khách: {booking.soKhach}</p>
                        <p className="flex items-center gap-2"><Phone size={16} /> {booking.sdtNguoiDat}</p>
                        <p className="flex items-center gap-2"><Mail size={16} /> {booking.emailNguoiDat}</p>
                        <p className="flex items-start gap-2"><MapPin size={16} className="mt-0.5" /> <span>Người đặt: {booking.tenNguoiDat}</span></p>
                      </div>
                    </div>
                    <div>
                      <h3 className="mb-4 text-lg font-bold text-on-surface">Chi tiết dịch vụ</h3>
                      {booking.loaiTaiSan === 'HOTEL' && booking.rooms && booking.rooms.length > 0 ? (
                        <div className="space-y-3">
                          {booking.rooms.map((room, index) => (
                            <div key={`${room.tenPhong}-${index}`} className="rounded-xl border border-outline-variant/40 bg-surface-container-low p-4">
                              <p className="font-semibold text-on-surface">{room.tenPhong}</p>
                              <p className="mt-1 text-sm text-on-surface-variant">Số lượng: {room.soLuong}</p>
                              <p className="mt-1 text-sm text-on-surface-variant">Đơn giá: {formatVND(room.donGia)}</p>
                              <p className="mt-1 text-sm font-semibold text-primary">Thành tiền: {formatVND(room.thanhTien)}</p>
                            </div>
                          ))}
                        </div>
                      ) : booking.loaiTaiSan === 'RESTAURANT' && booking.tables && booking.tables.length > 0 ? (
                        <div className="space-y-3">
                          {booking.tables.map((table, index) => (
                            <div key={`${table.tenBan}-${index}`} className="rounded-xl border border-outline-variant/40 bg-surface-container-low p-4">
                              <p className="font-semibold text-on-surface">{table.tenBan}</p>
                              <p className="mt-1 text-sm text-on-surface-variant">Số chỗ ngồi: {table.soChoNgoi}</p>
                              <p className="mt-1 text-sm text-on-surface-variant">Vị trí: {table.viTri || 'Đang cập nhật'}</p>
                            </div>
                          ))}
                        </div>
                      ) : (
                        <p className="text-sm text-on-surface-variant">Thông tin chi tiết dịch vụ đang được cập nhật.</p>
                      )}
                    </div>
                  </div>

                  {booking.loaiTaiSan === 'RESTAURANT' && booking.ghiChu && (
                    <div className="rounded-xl border border-outline-variant/40 bg-surface-container-low p-4">
                      <h4 className="mb-2 font-semibold text-on-surface">Ghi chú đặt chỗ</h4>
                      <p className="text-sm text-on-surface-variant">{booking.ghiChu}</p>
                    </div>
                  )}
                </div>
              </section>

              <section className="rounded-2xl border border-outline-variant/40 bg-white p-6 shadow-sm md:p-8">
                <h3 className="mb-4 text-lg font-bold text-on-surface">Chính sách và lưu ý</h3>
                {booking.chinhSach ? (
                  <div className="space-y-4 text-sm text-on-surface-variant">
                    {booking.chinhSach.chinhSachHuy && (
                      <div>
                        <p className="font-semibold text-on-surface">Chính sách hủy</p>
                        <p className="mt-1">{booking.chinhSach.chinhSachHuy}</p>
                      </div>
                    )}
                    {booking.chinhSach.chinhSachHoanTien && (
                      <div>
                        <p className="font-semibold text-on-surface">Chính sách hoàn tiền</p>
                        <p className="mt-1">{booking.chinhSach.chinhSachHoanTien}</p>
                      </div>
                    )}
                    {booking.chinhSach.quyDinhTreEm && (
                      <div>
                        <p className="font-semibold text-on-surface">Quy định trẻ em</p>
                        <p className="mt-1">{booking.chinhSach.quyDinhTreEm}</p>
                      </div>
                    )}
                    {booking.chinhSach.quyDinhVatNuoi && (
                      <div>
                        <p className="font-semibold text-on-surface">Quy định vật nuôi</p>
                        <p className="mt-1">{booking.chinhSach.quyDinhVatNuoi}</p>
                      </div>
                    )}
                    {booking.chinhSach.ghiChuKhac && (
                      <div>
                        <p className="font-semibold text-on-surface">Ghi chú khác</p>
                        <p className="mt-1">{booking.chinhSach.ghiChuKhac}</p>
                      </div>
                    )}
                  </div>
                ) : (
                  <p className="text-sm text-on-surface-variant">Hiện chưa có dữ liệu chính sách cho đơn đặt chỗ này.</p>
                )}
              </section>
            </div>

            <aside className="space-y-6 xl:col-span-4">
              <section className="rounded-2xl border border-outline-variant/40 bg-white p-6 shadow-sm">
                <h3 className="mb-5 text-lg font-bold text-on-surface">Tóm tắt thanh toán</h3>
                <div className="space-y-3 text-sm text-on-surface">
                  <div className="flex items-center justify-between">
                    <span>Tổng tiền thanh toán</span>
                    <span className="font-semibold">{formatVND(booking.tongTienThanhToan)}</span>
                  </div>

                  {booking.loaiTaiSan === 'RESTAURANT' && typeof booking.tienCoc === 'number' && (
                    <div className="flex items-center justify-between">
                      <span>Tiền cọc</span>
                      <span className="font-semibold">{formatVND(booking.tienCoc)}</span>
                    </div>
                  )}

                  <div className="flex items-center justify-between">
                    <span>Trạng thái đơn</span>
                    <span className="font-semibold">{statusLabel(booking.trangThai)}</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span>Loại dịch vụ</span>
                    <span className="font-semibold">{booking.loaiTaiSan === 'HOTEL' ? 'Khách sạn' : 'Nhà hàng'}</span>
                  </div>
                </div>
                <div className="mt-5 border-t border-outline-variant/40 pt-5">
                  <p className="text-xs text-on-surface-variant">
                    {booking.loaiTaiSan === 'RESTAURANT'
                      ? 'Đây là khoản tiền cọc đặt bàn. Phần còn lại sẽ thanh toán trực tiếp tại nhà hàng.'
                      : 'Hệ thống hiện chưa trả về breakdown thuế/phí và phương thức thanh toán ở endpoint chi tiết booking.'}
                  </p>
                </div>
              </section>

              <div className="flex flex-col gap-3">
                <button type="button" className="rounded-xl bg-primary px-4 py-3 text-sm font-semibold text-white hover:bg-primary-container">
                  Điều chỉnh đặt chỗ
                </button>
                <button
                  type="button"
                  onClick={openCancelModal}
                  disabled={cancelLoading || !isCancellableStatus(booking.trangThai)}
                  className="rounded-xl border border-outline px-4 py-3 text-sm font-semibold text-on-surface hover:bg-surface-container-low disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {cancelLoading ? 'Đang hủy...' : 'Hủy đặt chỗ'}
                </button>
                <Link to="/user/bookings-v2" className="rounded-xl border border-outline px-4 py-3 text-center text-sm font-semibold text-on-surface hover:bg-surface-container-low">
                  Quay lại danh sách đơn
                </Link>
              </div>
            </aside>
          </div>

          {/* Cancel Modal */}
          {cancelModalOpen && (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
              <div className="w-full max-w-md rounded-2xl bg-white p-6 shadow-lg">
                <h2 className="mb-4 text-lg font-bold text-on-surface">Xác nhận hủy đặt chỗ</h2>
                
                <div className="mb-6 rounded-lg bg-amber-50 p-4 text-sm text-amber-800">
                  {booking.loaiTaiSan === 'HOTEL'
                    ? 'Lưu ý: Hủy trong vòng 24 giờ trước ngày nhận phòng sẽ không được hoàn tiền.'
                    : 'Lưu ý: Hủy trong vòng 24 giờ trước giờ đặt bàn sẽ mất tiền cọc.'}
                </div>

                <div className="mb-6">
                  <label className="mb-2 block text-sm font-semibold text-on-surface">
                    Lý do hủy (không bắt buộc)
                  </label>
                  <textarea
                    value={cancelReason}
                    onChange={(e) => setCancelReason(e.target.value)}
                    placeholder="Nhập lý do hủy..."
                    maxLength={500}
                    className="w-full rounded-lg border border-outline-variant/40 bg-surface-container-low p-3 text-sm text-on-surface placeholder-on-surface-variant focus:border-primary focus:outline-none"
                    rows={3}
                  />
                  <p className="mt-1 text-xs text-on-surface-variant">{cancelReason.length}/500</p>
                </div>

                <div className="flex gap-3">
                  <button
                    type="button"
                    onClick={() => setCancelModalOpen(false)}
                    disabled={cancelLoading}
                    className="flex-1 rounded-lg border border-outline-variant/40 px-4 py-2 text-sm font-semibold text-on-surface hover:bg-surface-container-low disabled:opacity-60"
                  >
                    Hủy bỏ
                  </button>
                  <button
                    type="button"
                    onClick={handleCancelBooking}
                    disabled={cancelLoading}
                    className="flex-1 rounded-lg bg-error px-4 py-2 text-sm font-semibold text-white hover:bg-red-700 disabled:opacity-60"
                  >
                    {cancelLoading ? 'Đang hủy...' : 'Xác nhận hủy'}
                  </button>
                </div>
              </div>
            </div>
          )}
          </>
        )}
      </div>
    </CustomerFeedbackLayout>
  )
}
