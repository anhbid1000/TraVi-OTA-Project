import { Link } from 'react-router-dom'
import { CalendarDays, ChevronRight as ChevronRightIcon, Ticket, Phone as PhoneIcon, Mail as MailIcon, ShieldCheck, Zap, HeadphonesIcon } from 'lucide-react'
import { Navbar } from '../../components/layout/Navbar'
import { useAuth } from '../../hooks/useAuth'
import type { AuthUser } from '../../types/auth'

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

export function SearchPage() {
  const { isAuthenticated, user } = useAuth()
  const displayName = getUserDisplayName(user)

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
              <div className="flex flex-col gap-4 md:flex-row">
                <div className="relative flex-1">
                  <Ticket className="absolute left-3 top-1/2 -translate-y-1/2 text-outline" size={20} />
                  <input 
                    className="w-full rounded-lg border border-outline-variant bg-white py-3 pl-10 pr-4 text-sm outline-none transition-all focus:border-secondary focus:ring-2 focus:ring-secondary" 
                    placeholder="Mã đặt chỗ (Booking ID)" 
                    type="text" 
                  />
                </div>
                <div className="relative flex-1">
                  <PhoneIcon className="absolute left-3 top-1/2 -translate-y-1/2 text-outline" size={20} />
                  <input 
                    className="w-full rounded-lg border border-outline-variant bg-white py-3 pl-10 pr-4 text-sm outline-none transition-all focus:border-secondary focus:ring-2 focus:ring-secondary" 
                    placeholder="Số điện thoại" 
                    type="tel" 
                  />
                </div>
                <button className="rounded-lg bg-primary px-8 py-3 text-sm font-semibold text-white transition-all hover:bg-primary-container active:scale-95">
                  Tra cứu
                </button>
              </div>
            </div>
          </section>

          {/* Booking History Section */}
          <section>
            <div className="mb-8 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
              <h2 className="font-display text-2xl font-bold text-on-surface">Lịch sử đặt chỗ</h2>
              
              {/* Filter Tabs */}
              <div className="flex rounded-lg bg-surface-container p-1">
                <button className="rounded-md bg-white px-6 py-2 text-sm font-semibold text-primary shadow-sm transition-all">Tất cả</button>
                <button className="rounded-md px-6 py-2 text-sm font-semibold text-on-surface-variant transition-all hover:text-primary">Khách sạn</button>
                <button className="rounded-md px-6 py-2 text-sm font-semibold text-on-surface-variant transition-all hover:text-primary">Nhà hàng</button>
              </div>
            </div>

            {/* Booking Cards Grid */}
            <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
              
              {/* Booking Card 1 */}
              <div className="group flex flex-col overflow-hidden rounded-xl border border-surface-variant bg-white transition-all hover:shadow-lg sm:flex-row">
                <div className="h-48 overflow-hidden sm:h-auto sm:w-48">
                  <img 
                    alt="Luxury Resort" 
                    className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105" 
                    src="https://lh3.googleusercontent.com/aida-public/AB6AXuBf4CYuBlWk3p0sn7wnTcTYGSYRDGUQcNhtKnph_T7hINcc503n73PzZdCozRb2yodeDU6utHVJSGlCfMxtXF4FgpBROdgzETkiOLTr-wT2mOhxb_D1-c6G-YIJyP4AqLKPl5BDa-MTnBErKBoJxncm-Nsfa44bKHpFjHeljJNO5-SZjXdFjH2p1-BxyPVMMjdPkrwZ4MKLquZaVpNPzY8B28q39ajoykJRswFiJwQTqfue_LluoKsBHNrNX00ScAioLAd8uT1rimc"
                  />
                </div>
                <div className="flex flex-1 flex-col justify-between p-6">
                  <div>
                    <div className="mb-2 flex items-start justify-between">
                      <h3 className="font-display text-xl font-semibold text-primary">InterContinental Danang</h3>
                      <span className="rounded-full bg-mint-green px-3 py-1 text-xs font-medium text-secondary">Confirmed</span>
                    </div>
                    <div className="mb-4 space-y-1">
                      <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                        <CalendarDays size={16} /> 15/12/2024 - 20/12/2024
                      </p>
                      <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                        <Ticket size={16} /> ID: TV-882910
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center justify-between border-t border-surface-variant pt-4">
                    <div>
                      <p className="text-xs text-outline">Tổng thanh toán</p>
                      <p className="font-display text-xl font-semibold text-secondary">24.500.000đ</p>
                    </div>
                    <button className="flex items-center gap-1 text-sm font-semibold text-primary hover:underline">
                      Chi tiết <ChevronRightIcon size={16} />
                    </button>
                  </div>
                </div>
              </div>

              {/* Booking Card 2 */}
              <div className="group flex flex-col overflow-hidden rounded-xl border border-surface-variant bg-white transition-all hover:shadow-lg sm:flex-row">
                <div className="h-48 overflow-hidden sm:h-auto sm:w-48">
                  <img 
                    alt="Fine Dining" 
                    className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105" 
                    src="https://lh3.googleusercontent.com/aida-public/AB6AXuD7KmynuimtyJArw5zjWmXnOe4JWzbU6oUIR4fOwe2zV7AlpAvHU0WqWWXO1oC2O6bbd7w3Ca39H691N_zrFgCPGj3WLeTx_DpqtdS7v5Q6QRyOaTLlf5wJIUlMSE9U4RhSb-HEe97pDh7pIbcXV3mxVWVt0ahE8-ooQt86E3FZyXIa5vvjyyAABNuXnNORCHG1BrfU9BKGRjYqZRzUwyYKvoxmVSo70DEg_cTb02_R-Sb0O3fVEabrk_pUlH2qW3FKJgt_ndQFB24"
                  />
                </div>
                <div className="flex flex-1 flex-col justify-between p-6">
                  <div>
                    <div className="mb-2 flex items-start justify-between">
                      <h3 className="font-display text-xl font-semibold text-primary">Le Corto Dining</h3>
                      <span className="rounded-full bg-surface-variant px-3 py-1 text-xs font-medium text-on-surface">Completed</span>
                    </div>
                    <div className="mb-4 space-y-1">
                      <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                        <CalendarDays size={16} /> 10/11/2024 • 19:00
                      </p>
                      <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                        <Ticket size={16} /> ID: TV-442105
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center justify-between border-t border-surface-variant pt-4">
                    <div>
                      <p className="text-xs text-outline">Tổng thanh toán</p>
                      <p className="font-display text-xl font-semibold text-secondary">3.200.000đ</p>
                    </div>
                    <button className="flex items-center gap-1 text-sm font-semibold text-primary hover:underline">
                      Chi tiết <ChevronRightIcon size={16} />
                    </button>
                  </div>
                </div>
              </div>

              {/* Booking Card 3 */}
              <div className="group flex flex-col overflow-hidden rounded-xl border border-surface-variant bg-white transition-all hover:shadow-lg sm:flex-row">
                <div className="h-48 overflow-hidden sm:h-auto sm:w-48">
                  <img 
                    alt="Boutique Hotel" 
                    className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105" 
                    src="https://lh3.googleusercontent.com/aida-public/AB6AXuByw0p4F0yUm5Aj-TXh4BZjNYgI93n7UeQX_NWJWrMXYqmTZjsHvOwhBDWe_F0SVfMp8djI7-4-VGsHL7lKepVs8zSVUP94YEuKdmEUM6VpYk9N1Jmxbt-jBvbgRnta_DPQz_d__IdoLl8ko-yK5Y8f1zKA4IjeQVJ3dGjXLAZeYxXkYj8W96cCMuMYRP8A2Okhp4O3lTyjZbo28DEPRz2UM-lFM4u_zPcVcHz7KwQcQOEoqfLhpy3duwTUUO92vOB0eqyrikltjYM"
                  />
                </div>
                <div className="flex flex-1 flex-col justify-between p-6">
                  <div>
                    <div className="mb-2 flex items-start justify-between">
                      <h3 className="font-display text-xl font-semibold text-primary">Silk Path Grand Resort</h3>
                      <span className="rounded-full bg-error-container px-3 py-1 text-xs font-medium text-error">Cancelled</span>
                    </div>
                    <div className="mb-4 space-y-1">
                      <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                        <CalendarDays size={16} /> 05/10/2024 - 08/10/2024
                      </p>
                      <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                        <Ticket size={16} /> ID: TV-119203
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center justify-between border-t border-surface-variant pt-4">
                    <div>
                      <p className="text-xs text-outline">Tổng thanh toán</p>
                      <p className="font-display text-xl font-semibold text-secondary">8.900.000đ</p>
                    </div>
                    <button className="flex items-center gap-1 text-sm font-semibold text-primary hover:underline">
                      Chi tiết <ChevronRightIcon size={16} />
                    </button>
                  </div>
                </div>
              </div>

              {/* Booking Card 4 */}
              <div className="group flex flex-col overflow-hidden rounded-xl border border-surface-variant bg-white transition-all hover:shadow-lg sm:flex-row">
                <div className="h-48 overflow-hidden sm:h-auto sm:w-48">
                  <img 
                    alt="Sea View Hotel" 
                    className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105" 
                    src="https://lh3.googleusercontent.com/aida-public/AB6AXuAtQN5zWJlkxExiHp3dCrV46EIwnnqPlH1RE1PmBxKXTXoDCgqOe_72Z5kNX__dNwro6DnGBSYmbR8McZrPT6hz-L1w7QIeSV5PjO_nWEDLZGWCoBDNzzrvEqdkJQ1cIqYKfA9mlOGWLNmRCyjHVO94-HqkI_QlslUA-QQe_jm1OPsmzt-EOsm6dGgjk29tGQ14FiTIYjbVORiNT2TUbAJhL7CrsMRCgP29L2DLjcB7tv1ediHnfz6h5NsUsPbpnqSDTz3RHgKEWyc"
                  />
                </div>
                <div className="flex flex-1 flex-col justify-between p-6">
                  <div>
                    <div className="mb-2 flex items-start justify-between">
                      <h3 className="font-display text-xl font-semibold text-primary">Amanoi Ninh Thuan</h3>
                      <span className="rounded-full bg-surface-variant px-3 py-1 text-xs font-medium text-on-surface">Completed</span>
                    </div>
                    <div className="mb-4 space-y-1">
                      <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                        <CalendarDays size={16} /> 12/09/2024 - 15/09/2024
                      </p>
                      <p className="flex items-center gap-2 text-sm text-on-surface-variant">
                        <Ticket size={16} /> ID: TV-772911
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center justify-between border-t border-surface-variant pt-4">
                    <div>
                      <p className="text-xs text-outline">Tổng thanh toán</p>
                      <p className="font-display text-xl font-semibold text-secondary">45.000.000đ</p>
                    </div>
                    <button className="flex items-center gap-1 text-sm font-semibold text-primary hover:underline">
                      Chi tiết <ChevronRightIcon size={16} />
                    </button>
                  </div>
                </div>
              </div>

            </div>
            
            <div className="mt-12 text-center pb-20">
              <button className="rounded-lg border-2 border-primary px-8 py-3 text-sm font-semibold text-primary transition-all hover:bg-surface-container-low">
                Xem thêm lịch sử
              </button>
            </div>
          </section>

        </main>
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
              <form 
                className="space-y-6" 
                onSubmit={(e) => {
                  e.preventDefault()
                  // TODO: Implement unauthenticated search
                }}
              >
                <div>
                  <label className="mb-2 block text-sm font-semibold text-primary" htmlFor="booking-id">Mã đặt chỗ</label>
                  <div className="relative group">
                    <Ticket className="absolute left-4 top-1/2 -translate-y-1/2 text-outline" size={24} />
                    <input 
                      className="w-full rounded-xl border border-outline-variant bg-white/80 py-4 pl-12 pr-4 text-base outline-none transition-all placeholder:text-outline-variant focus:border-primary focus:ring-2 focus:ring-primary" 
                      id="booking-id" 
                      placeholder="VD: TRV123456789" 
                      type="text"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                  <div>
                    <label className="mb-2 block text-sm font-semibold text-primary" htmlFor="email">Email xác nhận (Tùy chọn)</label>
                    <div className="relative group">
                      <MailIcon className="absolute left-4 top-1/2 -translate-y-1/2 text-outline" size={24} />
                      <input 
                        className="w-full rounded-xl border border-outline-variant bg-white/80 py-4 pl-12 pr-4 text-base outline-none transition-all placeholder:text-outline-variant focus:border-primary focus:ring-2 focus:ring-primary" 
                        id="email" 
                        placeholder="example@travi.com" 
                        type="email"
                      />
                    </div>
                  </div>
                  <div>
                    <label className="mb-2 block text-sm font-semibold text-primary" htmlFor="phone">Số điện thoại (Tùy chọn)</label>
                    <div className="relative group">
                      <PhoneIcon className="absolute left-4 top-1/2 -translate-y-1/2 text-outline" size={24} />
                      <input 
                        className="w-full rounded-xl border border-outline-variant bg-white/80 py-4 pl-12 pr-4 text-base outline-none transition-all placeholder:text-outline-variant focus:border-primary focus:ring-2 focus:ring-primary" 
                        id="phone" 
                        placeholder="09xx xxx xxx" 
                        type="tel"
                      />
                    </div>
                  </div>
                </div>

                <button 
                  className="mt-8 w-full rounded-xl bg-primary py-4 text-xl font-semibold text-white shadow-md transition-all hover:bg-primary-container active:scale-[0.98]" 
                  type="submit"
                >
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
    </div>
  )
}
