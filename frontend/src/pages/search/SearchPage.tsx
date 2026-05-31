import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { 
  CalendarDays, 
  ChevronRight as ChevronRightIcon, 
  Ticket, 
  Phone as PhoneIcon, 
  Mail as MailIcon, 
  ShieldCheck, 
  Zap, 
  HeadphonesIcon, 
  X, 
  Loader2, 
  AlertCircle 
} from 'lucide-react'
import { Navbar } from '../../components/layout/Navbar'
import { useAuth } from '../../hooks/useAuth'
import type { AuthUser } from '../../types/auth'
import { userService, type BookingSearchResponse } from '../../services/userService'

function getUserDisplayName(user: AuthUser | null) {
  if (!user) return 'bạn'

  const firstName = typeof user.firstName === 'string' ? user.firstName.trim() : ''
  const lastName = typeof user.lastName === 'string' ? user.lastName.trim() : ''
  const fullName = `${firstName} ${lastName}`.trim()
  if (fullName) return fullName

  const hoTen = typeof user.hoTen === 'string' ? user.hoTen.trim() : ''
  if (hoTen) return hoTen

  const name = typeof user.name === 'string' ? user.name.trim() : ''
  if (name) return name

  const username = typeof user.username === 'string' ? user.username.trim() : ''
  if (username) return username

  const email = typeof user.email === 'string' ? user.email.trim() : ''
  if (email) return email

  return 'bạn'
}

function formatVND(amount: number) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

function getStatusBadge(status: string) {
  switch (status) {
    case 'CHO_THANH_TOAN':
      return <span className="rounded-full bg-amber-50 text-amber-700 border border-amber-200 px-3 py-1 text-xs font-semibold">Chờ thanh toán</span>
    case 'DA_THANH_TOAN':
      return <span className="rounded-full bg-green-50 text-green-700 border border-green-200 px-3 py-1 text-xs font-semibold">Đã thanh toán</span>
    case 'DA_XAC_NHAN':
      return <span className="rounded-full bg-blue-50 text-blue-700 border border-blue-200 px-3 py-1 text-xs font-semibold">Đã xác nhận</span>
    case 'DANG_PHUC_VU':
      return <span className="rounded-full bg-purple-50 text-purple-700 border border-purple-200 px-3 py-1 text-xs font-semibold">Đang phục vụ</span>
    case 'DA_HOAN_THANH':
      return <span className="rounded-full bg-gray-50 text-gray-700 border border-gray-200 px-3 py-1 text-xs font-semibold">Đã hoàn thành</span>
    case 'DA_HUY':
      return <span className="rounded-full bg-rose-50 text-rose-700 border border-rose-200 px-3 py-1 text-xs font-semibold">Đã hủy</span>
    case 'YEU_CAU_HOAN_TIEN':
      return <span className="rounded-full bg-orange-50 text-orange-700 border border-orange-200 px-3 py-1 text-xs font-semibold">Yêu cầu hoàn tiền</span>
    case 'DA_HOAN_TIEN':
      return <span className="rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200 px-3 py-1 text-xs font-semibold">Đã hoàn tiền</span>
    case 'THANH_TOAN_THAT_BAI':
      return <span className="rounded-full bg-red-50 text-red-700 border border-red-200 px-3 py-1 text-xs font-semibold">Thanh toán thất bại</span>
    case 'KHACH_KHONG_DEN':
      return <span className="rounded-full bg-zinc-50 text-zinc-700 border border-zinc-200 px-3 py-1 text-xs font-semibold">Khách không đến</span>
    default:
      return <span className="rounded-full bg-gray-50 text-gray-700 border border-gray-200 px-3 py-1 text-xs font-semibold">{status}</span>
  }
}

export function SearchPage() {
  const { isAuthenticated, user } = useAuth()
  const displayName = getUserDisplayName(user)

  // State for Booking History (Authenticated)
  const [bookings, setBookings] = useState<BookingSearchResponse[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [activeTab, setActiveTab] = useState<'all' | 'hotel' | 'restaurant'>('all')

  // Search Filters in Booking History (Authenticated)
  const [quickMaDon, setQuickMaDon] = useState('')
  const [quickPhone, setQuickPhone] = useState('')
  const [filterMaDon, setFilterMaDon] = useState('')
  const [filterPhone, setFilterPhone] = useState('')

  // State for Unauthenticated Guest Search Form
  const [guestMaDon, setGuestMaDon] = useState('')
  const [guestEmail, setGuestEmail] = useState('')
  const [guestPhone, setGuestPhone] = useState('')
  const [guestLoading, setGuestLoading] = useState(false)
  const [guestError, setGuestError] = useState<string | null>(null)

  // Unified Details Modal State
  const [selectedBooking, setSelectedBooking] = useState<BookingSearchResponse | null>(null)

  // Fetch Booking History when Authenticated
  useEffect(() => {
    if (isAuthenticated) {
      const fetchHistory = async () => {
        setLoading(true)
        setError(null)
        try {
          const res = await userService.getBookingHistory(activeTab)
          setBookings(res)
        } catch (err: any) {
          console.error(err)
          setError(err.response?.data || 'Không thể tải lịch sử đặt chỗ. Vui lòng thử lại sau.')
        } finally {
          setLoading(false)
        }
      }
      fetchHistory()
    }
  }, [isAuthenticated, activeTab])

  // Filter history bookings in memory
  const filteredBookings = bookings.filter(b => {
    const matchCode = filterMaDon ? b.maDon.toLowerCase().includes(filterMaDon.toLowerCase()) : true
    const matchPhone = filterPhone ? b.sdtNguoiDat.includes(filterPhone) : true
    return matchCode && matchPhone
  })

  // Handle Quick Search filter trigger
  const handleQuickSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setFilterMaDon(quickMaDon)
    setFilterPhone(quickPhone)
  }

  // Handle Public Guest Lookup Submit
  const handleGuestSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!guestMaDon.trim()) {
      setGuestError('Vui lòng nhập mã đặt chỗ.')
      return
    }
    if (!guestEmail.trim() && !guestPhone.trim()) {
      setGuestError('Vui lòng nhập ít nhất Email hoặc Số điện thoại xác nhận để tra cứu.')
      return
    }

    setGuestLoading(true)
    setGuestError(null)
    try {
      const result = await userService.lookupBookingPublicly(
        guestMaDon.trim(),
        guestEmail.trim() || undefined,
        guestPhone.trim() || undefined
      )
      setSelectedBooking(result)
    } catch (err: any) {
      console.error(err)
      setGuestError(
        err.response?.data || 
        'Tra cứu thất bại. Vui lòng kiểm tra lại mã đặt chỗ và thông tin xác thực.'
      )
    } finally {
      setGuestLoading(false)
    }
  }

  // Giao diện khi khách ĐÃ đăng nhập
  if (isAuthenticated) {
    return (
      <div className="min-h-screen bg-background text-on-surface font-body-md">
        <Navbar />
        
        <main className="mx-auto w-full max-w-7xl px-5 py-8 md:px-12">
          {/* Personalized Greeting */}
          <section className="mb-10 mt-8">
            <h1 className="mb-2 font-display text-4xl font-bold text-primary">
              Xin chào, {displayName}!
            </h1>
            <p className="text-lg text-on-surface-variant">
              Chào mừng bạn quay trở lại. Hãy quản lý các chuyến đi tuyệt vời của mình tại đây.
            </p>
          </section>

          {/* Quick Search Bar */}
          <section className="mb-12">
            <div className="max-w-3xl rounded-xl border border-surface-variant bg-white/60 p-6 shadow-sm backdrop-blur-md">
              <h2 className="mb-4 text-sm font-semibold text-primary">Tra cứu nhanh đơn đặt chỗ</h2>
              <form onSubmit={handleQuickSearchSubmit} className="flex flex-col gap-4 md:flex-row">
                <div className="relative flex-1">
                  <Ticket className="absolute left-3 top-1/2 -translate-y-1/2 text-outline" size={20} />
                  <input 
                    className="w-full rounded-lg border border-outline-variant bg-white py-3 pl-10 pr-4 text-sm outline-none transition-all focus:border-secondary focus:ring-2 focus:ring-secondary" 
                    placeholder="Mã đặt chỗ (Booking ID)" 
                    type="text" 
                    value={quickMaDon}
                    onChange={(e) => setQuickMaDon(e.target.value)}
                  />
                </div>
                <div className="relative flex-1">
                  <PhoneIcon className="absolute left-3 top-1/2 -translate-y-1/2 text-outline" size={20} />
                  <input 
                    className="w-full rounded-lg border border-outline-variant bg-white py-3 pl-10 pr-4 text-sm outline-none transition-all focus:border-secondary focus:ring-2 focus:ring-secondary" 
                    placeholder="Số điện thoại người đặt" 
                    type="tel" 
                    value={quickPhone}
                    onChange={(e) => setQuickPhone(e.target.value)}
                  />
                </div>
                <div className="flex gap-2">
                  <button 
                    type="submit" 
                    className="rounded-lg bg-primary px-8 py-3 text-sm font-semibold text-white transition-all hover:bg-primary-container active:scale-95 whitespace-nowrap"
                  >
                    Tra cứu
                  </button>
                  {(filterMaDon || filterPhone) && (
                    <button 
                      type="button" 
                      onClick={() => {
                        setQuickMaDon('')
                        setQuickPhone('')
                        setFilterMaDon('')
                        setFilterPhone('')
                      }}
                      className="rounded-lg border border-outline px-4 py-3 text-sm font-semibold text-on-surface hover:bg-surface-container active:scale-95 whitespace-nowrap"
                    >
                      Xóa lọc
                    </button>
                  )}
                </div>
              </form>
            </div>
          </section>

          {/* Booking History Section */}
          <section className="pb-20">
            <div className="mb-8 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
              <h2 className="font-display text-2xl font-bold text-on-surface">Lịch sử đặt chỗ</h2>
              
              {/* Filter Tabs */}
              <div className="flex rounded-lg bg-surface-container p-1 shrink-0 max-w-max">
                <button 
                  onClick={() => setActiveTab('all')}
                  className={`rounded-md px-6 py-2 text-sm font-semibold transition-all ${
                    activeTab === 'all' 
                      ? 'bg-white text-primary shadow-sm' 
                      : 'text-on-surface-variant hover:text-primary'
                  }`}
                >
                  Tất cả
                </button>
                <button 
                  onClick={() => setActiveTab('hotel')}
                  className={`rounded-md px-6 py-2 text-sm font-semibold transition-all ${
                    activeTab === 'hotel' 
                      ? 'bg-white text-primary shadow-sm' 
                      : 'text-on-surface-variant hover:text-primary'
                  }`}
                >
                  Khách sạn
                </button>
                <button 
                  onClick={() => setActiveTab('restaurant')}
                  className={`rounded-md px-6 py-2 text-sm font-semibold transition-all ${
                    activeTab === 'restaurant' 
                      ? 'bg-white text-primary shadow-sm' 
                      : 'text-on-surface-variant hover:text-primary'
                  }`}
                >
                  Nhà hàng
                </button>
              </div>
            </div>

            {/* Booking Cards Grid */}
            {loading ? (
              <div className="flex flex-col items-center justify-center py-20 text-on-surface-variant">
                <Loader2 className="animate-spin text-primary mb-4" size={40} />
                <p className="text-sm">Đang tải danh sách đơn hàng...</p>
              </div>
            ) : error ? (
              <div className="flex items-center gap-3 rounded-xl border border-red-200 bg-red-50 p-6 text-red-800">
                <AlertCircle size={24} className="shrink-0" />
                <p className="text-sm">{error}</p>
              </div>
            ) : filteredBookings.length === 0 ? (
              <div className="rounded-xl border border-dashed border-outline-variant p-12 text-center text-on-surface-variant bg-white/40">
                <Ticket className="mx-auto mb-4 text-outline" size={48} />
                <p className="text-lg font-medium">Không tìm thấy đơn đặt chỗ nào</p>
                <p className="text-sm mt-1 text-outline">Bạn chưa có chuyến đi nào trong mục này hoặc bộ lọc không khớp.</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
                {filteredBookings.map((booking) => (
                  <div 
                    key={booking.id} 
                    className="group flex flex-col overflow-hidden rounded-xl border border-surface-variant bg-white transition-all hover:shadow-lg sm:flex-row"
                  >
                    <div className="h-48 overflow-hidden sm:h-auto sm:w-48 shrink-0">
                      <img 
                        alt={booking.tenTaiSan} 
                        className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105" 
                        src={booking.anhTaiSan || (
                          booking.loaiTaiSan === 'HOTEL'
                            ? 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=500&q=80'
                            : 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=500&q=80'
                        )}
                      />
                    </div>
                    <div className="flex flex-grow flex-col justify-between p-6 min-w-0">
                      <div>
                        <div className="mb-2 flex items-start justify-between gap-2">
                          <h3 className="font-display text-xl font-semibold text-primary line-clamp-1">
                            {booking.tenTaiSan}
                          </h3>
                          <div className="shrink-0">
                            {getStatusBadge(booking.trangThai)}
                          </div>
                        </div>
                        <div className="mb-4 space-y-1">
                          <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                            <CalendarDays size={16} /> 
                            {booking.loaiTaiSan === 'HOTEL' ? (
                              <>
                                {new Date(booking.ngayBatDau!).toLocaleDateString('vi-VN')} - {new Date(booking.ngayKetThuc!).toLocaleDateString('vi-VN')}
                              </>
                            ) : (
                              <>
                                {new Date(booking.ngayBatDau!).toLocaleString('vi-VN', { dateStyle: 'short', timeStyle: 'short' })}
                              </>
                            )}
                          </p>
                          <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                            <Ticket size={16} /> ID: {booking.maDon}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center justify-between border-t border-surface-variant pt-4">
                        <div>
                          <p className="text-xs text-outline">Tổng thanh toán</p>
                          <p className="font-display text-xl font-semibold text-secondary">
                            {formatVND(booking.tongTienThanhToan)}
                          </p>
                        </div>
                        <button 
                          onClick={() => setSelectedBooking(booking)}
                          className="flex items-center gap-1 text-sm font-semibold text-primary hover:underline active:scale-95 transition-all"
                        >
                          Chi tiết <ChevronRightIcon size={16} />
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>
        </main>

        {/* Details Modal */}
        {selectedBooking && renderDetailsModal(selectedBooking, () => setSelectedBooking(null))}
      </div>
    )
  }

  // Giao diện khi khách CHƯA đăng nhập
  return (
    <div className="min-h-screen bg-background text-on-surface font-body-md flex flex-col">
      <Navbar />
      
      <main className="relative flex flex-grow flex-col overflow-hidden">
        {/* Subtle Background Elements */}
        <div 
          className="pointer-events-none absolute inset-0 opacity-15"
          style={{
            backgroundImage: 'radial-gradient(circle at 2px 2px, #c0c9be 1px, transparent 0)',
            backgroundSize: '40px 40px'
          }}
        />
        <div className="pointer-events-none absolute -right-24 -top-24 h-96 w-96 rounded-full bg-primary/5 blur-3xl" />
        <div className="pointer-events-none absolute -bottom-24 -left-24 h-96 w-96 rounded-full bg-secondary/5 blur-3xl" />
        
        {/* Hero Section */}
        <section className="relative z-10 flex flex-grow items-center justify-center px-4 py-12 md:px-12">
          <div className="w-full max-w-2xl text-center">
            
            {/* Branding Accent */}
            <div className="mb-6 flex justify-center">
              <div className="h-1 w-16 rounded-full bg-primary" />
            </div>
            
            <h1 className="mb-4 font-display text-5xl font-bold tracking-tight text-primary">
              Tra cứu đơn đặt chỗ
            </h1>
            <p className="mx-auto mb-12 max-w-lg text-lg text-on-surface-variant">
              Dễ dàng quản lý hành trình của bạn chỉ với mã đặt chỗ và thông tin xác thực.
            </p>

            {/* Search Card */}
            <div className="rounded-2xl border border-white bg-white/60 p-8 text-left shadow-xl backdrop-blur-md transition-all duration-300 hover:shadow-2xl md:p-12">
              {guestError && (
                <div className="mb-6 flex items-center gap-3 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-800">
                  <AlertCircle className="shrink-0" size={20} />
                  <p>{guestError}</p>
                </div>
              )}
              <form 
                className="space-y-6" 
                onSubmit={handleGuestSearch}
              >
                <div>
                  <label className="mb-2 block text-sm font-semibold text-primary" htmlFor="booking-id">Mã đặt chỗ</label>
                  <div className="relative group">
                    <Ticket className="absolute left-4 top-1/2 -translate-y-1/2 text-outline" size={24} />
                    <input 
                      className="w-full rounded-xl border border-outline-variant bg-white/80 py-4 pl-12 pr-4 text-base outline-none transition-all placeholder:text-outline-variant focus:border-primary focus:ring-2 focus:ring-primary" 
                      id="booking-id" 
                      placeholder="VD: DKS123456 hoặc DNH123456" 
                      type="text"
                      required
                      value={guestMaDon}
                      onChange={(e) => setGuestMaDon(e.target.value)}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                  <div>
                    <label className="mb-2 block text-sm font-semibold text-primary" htmlFor="email">Email xác nhận</label>
                    <div className="relative group">
                      <MailIcon className="absolute left-4 top-1/2 -translate-y-1/2 text-outline" size={24} />
                      <input 
                        className="w-full rounded-xl border border-outline-variant bg-white/80 py-4 pl-12 pr-4 text-base outline-none transition-all placeholder:text-outline-variant focus:border-primary focus:ring-2 focus:ring-primary" 
                        id="email" 
                        placeholder="example@travi.com" 
                        type="email"
                        value={guestEmail}
                        onChange={(e) => setGuestEmail(e.target.value)}
                      />
                    </div>
                  </div>
                  <div>
                    <label className="mb-2 block text-sm font-semibold text-primary" htmlFor="phone">Số điện thoại xác nhận</label>
                    <div className="relative group">
                      <PhoneIcon className="absolute left-4 top-1/2 -translate-y-1/2 text-outline" size={24} />
                      <input 
                        className="w-full rounded-xl border border-outline-variant bg-white/80 py-4 pl-12 pr-4 text-base outline-none transition-all placeholder:text-outline-variant focus:border-primary focus:ring-2 focus:ring-primary" 
                        id="phone" 
                        placeholder="09xx xxx xxx" 
                        type="tel"
                        value={guestPhone}
                        onChange={(e) => setGuestPhone(e.target.value)}
                      />
                    </div>
                  </div>
                </div>

                <button 
                  className="mt-8 w-full flex items-center justify-center gap-2 rounded-xl bg-primary py-4 text-xl font-semibold text-white shadow-md transition-all hover:bg-primary-container active:scale-[0.98] disabled:opacity-75 disabled:cursor-not-allowed" 
                  type="submit"
                  disabled={guestLoading}
                >
                  {guestLoading && <Loader2 className="animate-spin" size={20} />}
                  Tra cứu ngay
                </button>
              </form>

              <div className="mt-8 border-t border-outline-variant/30 pt-8 text-center">
                <p className="text-sm text-on-surface-variant">
                  Bạn gặp khó khăn khi tìm mã? <Link to="#" className="font-semibold text-secondary hover:underline">Liên hệ hỗ trợ</Link>
                </p>
              </div>
            </div>

            {/* Featured Experience Hint */}
            <div className="mt-16 grid grid-cols-1 gap-6 opacity-80 md:grid-cols-3">
              <div className="flex items-center space-x-3 rounded-xl bg-surface-container-low p-4">
                <ShieldCheck className="text-secondary" size={24} />
                <span className="text-sm font-semibold text-on-surface">Bảo mật tuyệt đối</span>
              </div>
              <div className="flex items-center space-x-3 rounded-xl bg-surface-container-low p-4">
                <Zap className="text-secondary" size={24} />
                <span className="text-sm font-semibold text-on-surface">Kết quả tức thì</span>
              </div>
              <div className="flex items-center space-x-3 rounded-xl bg-surface-container-low p-4">
                <HeadphonesIcon className="text-secondary" size={24} />
                <span className="text-sm font-semibold text-on-surface">Hỗ trợ 24/7</span>
              </div>
            </div>

          </div>
        </section>

        {/* Decorative Heritage Element (Asymmetric) */}
        <div className="pointer-events-none absolute bottom-0 right-0 hidden h-2/3 w-1/3 opacity-20 lg:block">
          <img 
            className="h-full w-full object-cover object-bottom" 
            src="https://lh3.googleusercontent.com/aida-public/AB6AXuD_42Td8cn6oVWYbzIAOtEe4Bxr4DNm3jiIc47KXwBoEhDXS91lJotbPCk3nF74gFKW6jqmKVWk8hwLQMijqlbh2Wotf6Scd3pAiuF35rfxtNMTyJON2sj9Ot9MYm7Uvidjhx5-ZFar5lsmc1WNS_s5fZ4bnNG4OtNiwkXlBqZCeRdXDsOgCOL8a3bngWoZB1tCDUTSgG0RwIjtF9od8ifhxsAnQ2q4bTvDImeMnsOM1iwHr88SD2_qswBlmTTpxHX0QDecuHsiOcI" 
            style={{ WebkitMaskImage: 'linear-gradient(to top left, black, transparent)', maskImage: 'linear-gradient(to top left, black, transparent)' }}
            alt="Heritage pattern"
          />
        </div>
      </main>

      {/* Details Modal */}
      {selectedBooking && renderDetailsModal(selectedBooking, () => setSelectedBooking(null))}
    </div>
  )
}

function renderDetailsModal(booking: BookingSearchResponse, onClose: () => void) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="relative w-full max-w-2xl overflow-hidden rounded-2xl border border-white/20 bg-white shadow-2xl backdrop-blur-md flex flex-col max-h-[90vh] md:max-h-[85vh] animate-in zoom-in-95 duration-200 text-left">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-surface-variant p-6">
          <div>
            <span className="text-xs font-bold uppercase tracking-wider text-secondary">
              {booking.loaiTaiSan === 'HOTEL' ? 'Khách sạn' : 'Nhà hàng'}
            </span>
            <h3 className="font-display text-2xl font-bold text-primary flex items-center gap-2">
              Mã đơn: {booking.maDon}
            </h3>
          </div>
          <button 
            onClick={onClose}
            className="rounded-full p-2 text-outline hover:bg-surface-container active:scale-95 transition-all"
          >
            <X size={24} />
          </button>
        </div>

        {/* Body */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          {/* Cover & Property info */}
          <div className="flex flex-col gap-4 sm:flex-row">
            <img 
              src={booking.anhTaiSan || (
                booking.loaiTaiSan === 'HOTEL'
                  ? 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=500&q=80'
                  : 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=500&q=80'
              )} 
              alt={booking.tenTaiSan} 
              className="h-32 w-full rounded-lg object-cover sm:w-48 border border-surface-variant shrink-0"
            />
            <div className="flex flex-col justify-between">
              <div>
                <h4 className="font-display text-xl font-bold text-on-surface">{booking.tenTaiSan}</h4>
                <p className="mt-1 flex items-center gap-2 text-sm text-on-surface-variant">
                  <CalendarDays size={16} />
                  {booking.loaiTaiSan === 'HOTEL' ? (
                    <>
                      {new Date(booking.ngayBatDau!).toLocaleDateString('vi-VN')} - {new Date(booking.ngayKetThuc!).toLocaleDateString('vi-VN')}
                    </>
                  ) : (
                    <>
                      {new Date(booking.ngayBatDau!).toLocaleString('vi-VN', { dateStyle: 'short', timeStyle: 'short' })}
                    </>
                  )}
                </p>
              </div>
              <div className="mt-2 flex flex-wrap gap-2">
                {getStatusBadge(booking.trangThai)}
              </div>
            </div>
          </div>

          {/* Guest Information */}
          <div className="rounded-xl bg-surface-container-low p-4 space-y-3">
            <h5 className="font-bold text-primary text-xs uppercase tracking-wider">Thông tin người đặt</h5>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
              <p className="text-on-surface-variant">Họ tên: <strong className="text-on-surface">{booking.tenNguoiDat}</strong></p>
              <p className="text-on-surface-variant">Số điện thoại: <strong className="text-on-surface">{booking.sdtNguoiDat || 'N/A'}</strong></p>
              <p className="text-on-surface-variant col-span-1 md:col-span-2">Email: <strong className="text-on-surface">{booking.emailNguoiDat || 'N/A'}</strong></p>
              <p className="text-on-surface-variant col-span-1 md:col-span-2">Số khách đi cùng: <strong className="text-on-surface">{booking.soKhach} người</strong></p>
            </div>
          </div>

          {/* Detailed Breakdown */}
          {booking.loaiTaiSan === 'HOTEL' && booking.rooms && booking.rooms.length > 0 && (
            <div className="space-y-3">
              <h5 className="font-bold text-primary text-xs uppercase tracking-wider">Chi tiết phòng đặt</h5>
              <div className="overflow-x-auto rounded-xl border border-surface-variant">
                <table className="w-full text-left border-collapse text-sm min-w-[400px]">
                  <thead>
                    <tr className="bg-surface-container text-on-surface-variant font-semibold">
                      <th className="p-3">Tên phòng</th>
                      <th className="p-3 text-center">Số lượng</th>
                      <th className="p-3 text-right">Đơn giá</th>
                      <th className="p-3 text-right">Thành tiền</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-surface-variant">
                    {booking.rooms.map((room, idx) => (
                      <tr key={idx} className="hover:bg-surface-container-lowest">
                        <td className="p-3 font-semibold text-on-surface">{room.tenPhong}</td>
                        <td className="p-3 text-center text-on-surface-variant">{room.soLuong}</td>
                        <td className="p-3 text-right text-on-surface-variant">{formatVND(room.donGia)}</td>
                        <td className="p-3 text-right font-semibold text-secondary">{formatVND(room.thanhTien)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {booking.loaiTaiSan === 'RESTAURANT' && booking.tables && booking.tables.length > 0 && (
            <div className="space-y-3">
              <h5 className="font-bold text-primary text-xs uppercase tracking-wider">Bàn được chỉ định</h5>
              <div className="overflow-hidden rounded-xl border border-surface-variant">
                <table className="w-full text-left border-collapse text-sm">
                  <thead>
                    <tr className="bg-surface-container text-on-surface-variant font-semibold">
                      <th className="p-3">Tên bàn</th>
                      <th className="p-3 text-center">Số chỗ ngồi</th>
                      <th className="p-3">Vị trí / Sảnh</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-surface-variant">
                    {booking.tables.map((table, idx) => (
                      <tr key={idx} className="hover:bg-surface-container-lowest">
                        <td className="p-3 font-semibold text-on-surface">{table.tenBan}</td>
                        <td className="p-3 text-center text-on-surface-variant">{table.soChoNgoi}</td>
                        <td className="p-3 text-on-surface-variant">{table.viTri || 'Chung'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between border-t border-surface-variant p-6 bg-surface-container-lowest">
          <div>
            <p className="text-xs text-outline">Tổng cộng thanh toán</p>
            <p className="font-display text-2xl font-bold text-secondary">{formatVND(booking.tongTienThanhToan)}</p>
          </div>
          <button 
            onClick={onClose}
            className="rounded-lg bg-primary px-6 py-2.5 text-sm font-semibold text-white transition-all hover:bg-primary-container active:scale-95"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  )
}
