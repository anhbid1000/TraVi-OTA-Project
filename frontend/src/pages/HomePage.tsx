import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import {
  ArrowRight,
  CalendarDays,
  Clock3,
  MapPin,
  Search,
  ShieldCheck,
  Tag,
  UserRound,
  Users,
} from 'lucide-react'
import { Navbar } from '../components/layout/Navbar'
import { useAuth } from '../hooks/useAuth'
import { CityAutocomplete, CuisineSelect } from '../features/catalog/components'
import { SmartNotificationWidget, UserAiRecommendations } from '../features/ai'
import heroImage from "../assets/hero.png"
import { getFeaturedHotels } from '../features/hotels/services/hotelService'
import { getFeaturedRestaurants, getRestaurantFilterOptions } from '../features/restaurants/services/restaurantService'
import type { FilterOption, HotelCatalog } from '../features/hotels/types'
import type { RestaurantCatalog } from '../features/restaurants/types'
import { formatVnd } from '../utils/display'
import {
  HOTEL_SEARCH_CRITERIA_KEY,
  readSearchCriteria,
  RESTAURANT_SEARCH_CRITERIA_KEY,
  writeSearchCriteria,
} from '../features/catalog/utils/searchCriteriaMemory'

// ─── Local types ─────────────────────────────────────────────
type SearchTab = 'luutru' | 'amthuc'

// ─── Static mock data ────────────────────────────────────────
const EXPLORE_DESTINATIONS = [
  {
    id: 1, name: 'Phố Cổ Hội An', desc: 'Tấm thảm thời gian dệt bằng văn hóa và ẩm thực.', large: true,
    image: 'https://images.unsplash.com/photo-1559592413-7cec4d0cae2b?auto=format&fit=crop&w=1200&q=80',
  },
  {
    id: 2, name: 'Thung Lũng Sapa', desc: 'Cao nguyên xanh mướt và bình yên.', large: false,
    image: 'https://images.unsplash.com/photo-1528360983277-13d401cdc186?auto=format&fit=crop&w=800&q=80',
  },
  {
    id: 3, name: 'TP. Hồ Chí Minh', desc: 'Nhịp sống sôi động của miền Nam.', large: false,
    image: 'https://images.unsplash.com/photo-1583417319070-4a69db38a482?auto=format&fit=crop&w=800&q=80',
  },
]

const TRUST_FEATURES = [
  {
    icon: ShieldCheck,
    title: 'Chất Lượng Được Kiểm Duyệt',
    desc: 'Mỗi chỗ lưu trú và trải nghiệm được lựa chọn kỹ lưỡng để đáp ứng tiêu chuẩn cao cấp của TraVi.',
  },
  {
    icon: Users,
    title: 'Hỗ Trợ 24/7',
    desc: 'Đội ngũ chuyên gia địa phương hỗ trợ xuyên suốt, đảm bảo hành trình của bạn luôn trơn tru.',
  },
  {
    icon: Tag,
    title: 'Giá Tốt Nhất',
    desc: 'Ưu đãi độc quyền và định giá minh bạch cho các chỗ lưu trú và ẩm thực cao cấp.',
  },
]

// ─── Helpers ─────────────────────────────────────────────────
function toDateStr(d: Date): string {
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}

function toTimeStr(d: Date): string {
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${hh}:${mm}`
}

function getDefaultRestaurantSlot() {
  const now = new Date()
  const slot = new Date(now)
  slot.setSeconds(0, 0)
  slot.setMinutes(0)
  slot.setHours(slot.getHours() + 1)

  if (slot.getHours() >= 23) {
    slot.setDate(slot.getDate() + 1)
    slot.setHours(19, 0, 0, 0)
  }

  return {
    date: toDateStr(slot),
    time: toTimeStr(slot),
  }
}

function getSavedString(criteria: ReturnType<typeof readSearchCriteria>, key: string, fallback = '') {
  const value = criteria?.[key]
  return typeof value === 'string' ? value : fallback
}

function getSavedNumber(criteria: ReturnType<typeof readSearchCriteria>, key: string, fallback: number) {
  const value = criteria?.[key]
  const parsed = typeof value === 'number' ? value : Number(value)
  return Number.isFinite(parsed) && parsed > 0 ? parsed : fallback
}

// ─── Component ────────────────────────────────────────────────
export function HomePage() {
  const navigate = useNavigate()
  const { isAuthenticated, user } = useAuth()

  // derived values
  const today = useMemo(() => { const d = new Date(); d.setHours(0,0,0,0); return d }, [])
  const tomorrow = useMemo(() => { const d = new Date(today); d.setDate(d.getDate()+1); return d }, [today])
  const defaultRestaurantSlot = useMemo(() => getDefaultRestaurantSlot(), [])
  const savedHotelCriteria = useMemo(() => readSearchCriteria(HOTEL_SEARCH_CRITERIA_KEY), [])
  const savedRestaurantCriteria = useMemo(() => readSearchCriteria(RESTAURANT_SEARCH_CRITERIA_KEY), [])

  // state
  const [activeTab, setActiveTab] = useState<SearchTab>('luutru')
  const [hotelCity, setHotelCity] = useState(getSavedString(savedHotelCriteria, 'city'))
  const [hotelCheckIn, setHotelCheckIn] = useState(getSavedString(savedHotelCriteria, 'checkIn', toDateStr(today)))
  const [hotelCheckOut, setHotelCheckOut] = useState(getSavedString(savedHotelCriteria, 'checkOut', toDateStr(tomorrow)))
  const [hotelGuests, setHotelGuests] = useState(getSavedNumber(savedHotelCriteria, 'guests', 2))
  const [hotelError, setHotelError] = useState('')
  const [rstCity, setRstCity] = useState(getSavedString(savedRestaurantCriteria, 'city'))
  const [rstDate, setRstDate] = useState(getSavedString(savedRestaurantCriteria, 'date', defaultRestaurantSlot.date))
  const [rstTime, setRstTime] = useState(getSavedString(savedRestaurantCriteria, 'time', defaultRestaurantSlot.time))
  const [rstGuests, setRstGuests] = useState(getSavedNumber(savedRestaurantCriteria, 'guests', 4))
  const [rstCuisine, setRstCuisine] = useState(getSavedString(savedRestaurantCriteria, 'cuisine'))
  const [rstError, setRstError] = useState('')
  const [cuisineOptions, setCuisineOptions] = useState<FilterOption[]>([])
  const [featuredHotels, setFeaturedHotels] = useState<HotelCatalog[]>([])
  const [featuredRestaurants, setFeaturedRestaurants] = useState<RestaurantCatalog[]>([])
  const [featuredHotelsError, setFeaturedHotelsError] = useState<string | null>(null)
  const [featuredRestaurantsError, setFeaturedRestaurantsError] = useState<string | null>(null)
  const isCustomer = String(user?.role ?? '').replace(/^ROLE[_-]/, '').toUpperCase() === 'KHACH_HANG'

  useEffect(() => {
    let active = true

    void getFeaturedHotels({
      checkIn: hotelCheckIn,
      checkOut: hotelCheckOut,
      guests: hotelGuests,
      size: 4,
    })
      .then((hotels) => {
        if (active) {
          setFeaturedHotelsError(null)
          setFeaturedHotels(hotels)
        }
      })
      .catch((error) => {
        if (active) {
          setFeaturedHotels([])
          setFeaturedHotelsError('Không thể tải khách sạn nổi bật lúc này. Vui lòng thử lại sau.')
          if (error instanceof Error) {
            console.error(error)
          }
        }
      })

    return () => {
      active = false
    }
  }, [hotelCheckIn, hotelCheckOut, hotelGuests])

  useEffect(() => {
    let active = true

    const selectedDateTime = new Date(`${rstDate}T${rstTime}:00`)
    const now = new Date()
    const safeRestaurantDate = Number.isNaN(selectedDateTime.getTime()) || selectedDateTime < now
      ? defaultRestaurantSlot.date
      : rstDate
    const safeRestaurantTime = Number.isNaN(selectedDateTime.getTime()) || selectedDateTime < now
      ? defaultRestaurantSlot.time
      : rstTime

    void getFeaturedRestaurants({
      date: safeRestaurantDate,
      time: safeRestaurantTime,
      guests: rstGuests,
      size: 3,
    })
      .then((restaurants) => {
        if (active) {
          setFeaturedRestaurantsError(null)
          setFeaturedRestaurants(restaurants)
        }
      })
      .catch((error) => {
        if (active) {
          setFeaturedRestaurants([])
          setFeaturedRestaurantsError('Không thể tải nhà hàng nổi bật lúc này. Vui lòng thử lại sau.')
          if (error instanceof Error) {
            console.error(error)
          }
        }
      })

    return () => {
      active = false
    }
  }, [defaultRestaurantSlot.date, defaultRestaurantSlot.time, rstDate, rstGuests, rstTime])

  useEffect(() => {
    let active = true

    void getRestaurantFilterOptions()
      .then((options) => {
        if (active) {
          setCuisineOptions(options.cuisines)
        }
      })
      .catch((error) => {
        if (error instanceof Error) {
          console.error(error)
        }
      })

    return () => {
      active = false
    }
  }, [])

  // handlers
  const handleHotelSearch = (e: FormEvent) => {
    e.preventDefault()
    setHotelError('')
    if (!hotelCity.trim()) { setHotelError('Vui lòng nhập địa điểm.'); return }
    const ci = new Date(hotelCheckIn + 'T00:00:00')
    const co = new Date(hotelCheckOut + 'T00:00:00')
	    if (ci < today) { setHotelError('Ngày nhận phòng không được ở quá khứ.'); return }
	    if (co <= ci) { setHotelError('Ngày trả phòng phải sau ngày nhận phòng.'); return }
	    if (hotelGuests < 1) { setHotelError('Số khách phải ≥ 1.'); return }
	    const criteria = {
	      city: hotelCity.trim(),
	      checkIn: hotelCheckIn,
	      checkOut: hotelCheckOut,
	      guests: hotelGuests,
	    }
	    writeSearchCriteria(HOTEL_SEARCH_CRITERIA_KEY, criteria)
	    const p = new URLSearchParams({
	      city: criteria.city,
	      checkIn: criteria.checkIn,
	      checkOut: criteria.checkOut,
	      guests: String(criteria.guests),
	      page: '0',
	      size: '10',
	      sort: 'priceAsc',
	    })
	    navigate(`/hotels?${p.toString()}`)
	  }

  const handleRstSearch = (e: FormEvent) => {
    e.preventDefault()
    setRstError('')
    if (!rstCity.trim()) { setRstError('Vui lòng nhập địa điểm.'); return }
    if (rstGuests < 1) { setRstError('Số khách phải ≥ 1.'); return }
    const dt = new Date(`${rstDate}T${rstTime}:00`)
	    if (Number.isNaN(dt.getTime())) { setRstError('Ngày giờ không hợp lệ.'); return }
	    if (dt < new Date()) { setRstError('Ngày giờ dùng bữa không được ở trong quá khứ.'); return }
	    const selectedCuisine = rstCuisine.trim()
	    const criteria = {
	      city: rstCity.trim(),
	      date: rstDate,
	      time: rstTime,
	      guests: rstGuests,
	      ...(selectedCuisine ? { cuisine: selectedCuisine } : {}),
	    }
	    writeSearchCriteria(RESTAURANT_SEARCH_CRITERIA_KEY, criteria)
	    const p = new URLSearchParams({
	      city: criteria.city,
	      date: criteria.date,
	      time: criteria.time,
	      guests: String(criteria.guests),
	      page: '0',
	      size: '10',
	      sort: 'popular',
	    })
	    if (selectedCuisine) p.set('cuisine', selectedCuisine)
	    navigate(`/restaurants?${p.toString()}`)
	  }

  // ─── Render ───────────────────────────────────────────────
  return (
    <div className="min-h-screen bg-surface font-sans text-on-surface antialiased">
      {/* ── Top NavBar ── */}
      <Navbar />

      {/* ── Hero ── */}
      <header className="relative flex min-h-195 items-center justify-center overflow-hidden">
        <div
          className="absolute inset-0 bg-cover bg-center"
          style={{ backgroundImage: `url('${heroImage}')` }}
        />
        <div className="absolute inset-0 bg-linear-to-b from-black/40 via-black/25 to-[#f8f9fb]" />

        <div className="relative z-10 mx-auto w-full max-w-7xl px-5 py-20 text-center md:px-12">
          <h1 className="font-display text-4xl font-bold leading-tight text-white drop-shadow-lg md:text-6xl">
            Khám phá Tâm Hồn Việt Nam
          </h1>
          <p className="mt-5 text-base text-white/90 md:text-xl">
            Chỗ lưu trú sang trọng, ẩm thực tinh tế và hành trình khó quên cho người du lịch hiện
            đại.
          </p>

          {/* Search Widget */}
          <div className="mx-auto mt-12 w-full max-w-8xl rounded-2xl border border-white/40 bg-white/80 p-5 shadow-2xl backdrop-blur-lg">
            <div className="mb-4 flex gap-6 border-b border-outline-variant/40">
              <button
                type="button"
                onClick={() => setActiveTab('luutru')}
                className={`pb-3 text-sm font-bold transition-colors cursor-pointer ${activeTab === 'luutru' ? 'border-b-2 border-primary text-primary' : 'text-on-surface-variant hover:text-primary'}`}
              >
                Lưu trú
              </button>
              <button
                type="button"
                onClick={() => setActiveTab('amthuc')}
                className={`pb-3 text-sm font-bold transition-colors cursor-pointer ${activeTab === 'amthuc' ? 'border-b-2 border-primary text-primary' : 'text-on-surface-variant hover:text-primary'}`}
              >
                Ẩm thực
              </button>
            </div>

            {activeTab === 'luutru' && (
              <form onSubmit={handleHotelSearch} className="space-y-3">
                <div className="grid gap-2 md:grid-cols-5">
                  <CityAutocomplete value={hotelCity} onChange={setHotelCity} />
                  <label className="flex cursor-text items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
                    <CalendarDays size={15} className="shrink-0 text-primary" />
                    <div>
                      <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">
                        Nhận phòng
                      </p>
                      <input
                        type="date"
                        min={toDateStr(today)}
                        value={hotelCheckIn}
                        onChange={(e) => setHotelCheckIn(e.target.value)}
                        className="w-full bg-transparent text-sm outline-none text-center"
                      />
                    </div>
                  </label>
                  <label className="flex cursor-text items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
                    <CalendarDays size={15} className="shrink-0 text-primary" />
                    <div>
                      <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">
                        Trả phòng
                      </p>
                      <input
                        type="date"
                        min={hotelCheckIn || toDateStr(today)}
                        value={hotelCheckOut}
                        onChange={(e) => setHotelCheckOut(e.target.value)}
                        className="w-full bg-transparent text-sm outline-none"
                      />
                    </div>
                  </label>
                  <label className="flex cursor-text items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
                    <UserRound size={15} className="shrink-0 text-primary" />
                    <div>
                      <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">
                        Số khách
                      </p>
                      <input
                        type="number"
                        min={1}
                        value={hotelGuests}
                        onChange={(e) => setHotelGuests(Number(e.target.value))}
                        className="w-full bg-transparent text-sm outline-none text-center"
                      />
                    </div>
                  </label>
                  <button
                    type="submit"
                    className="flex items-center justify-center gap-2 rounded-xl bg-primary px-5 py-3 text-sm font-bold text-on-primary shadow-sm transition hover:bg-primary-container cursor-pointer"
                  >
                    <Search size={15} /> Tìm kiếm
                  </button>
                </div>
                {hotelError && (
                  <p className="text-left text-sm font-medium text-error">{hotelError}</p>
                )}
              </form>
            )}

            {activeTab === 'amthuc' && (
              <form onSubmit={handleRstSearch} className="space-y-3">
                <div className="grid gap-2 md:grid-cols-2 lg:grid-cols-6">
                  <div className="lg:col-span-1">
                    <CityAutocomplete value={rstCity} onChange={setRstCity} />
                  </div>
                  <label className="flex cursor-text items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
                    <CalendarDays size={15} className="shrink-0 text-primary" />
                    <div>
                      <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">
                        Ngày dùng bữa
                      </p>
                      <input
                        type="date"
                        min={toDateStr(today)}
                        value={rstDate}
                        onChange={(e) => setRstDate(e.target.value)}
                        className="w-full bg-transparent text-sm outline-none"
                      />
                    </div>
                  </label>
                  <label className="flex cursor-text items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
                    <Clock3 size={15} className="shrink-0 text-primary" />
                    <div>
                      <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">
                        Giờ dùng bữa
                      </p>
                      <input
                        type="time"
                        value={rstTime}
                        onChange={(e) => setRstTime(e.target.value)}
                        className="w-full bg-transparent text-sm outline-none"
                      />
                    </div>
                  </label>
                  <label className="flex cursor-text items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
                    <UserRound size={15} className="shrink-0 text-primary" />
                    <div>
                      <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">
                        Số khách
                      </p>
                      <input
                        type="number"
                        min={1}
                        value={rstGuests}
                        onChange={(e) => setRstGuests(Number(e.target.value))}
                        className="w-full bg-transparent text-sm outline-none text-center"
                      />
                    </div>
                  </label>
                  <CuisineSelect value={rstCuisine} onChange={setRstCuisine} options={cuisineOptions} />
                  <button
                    type="submit"
                    className="flex h-full min-h-12 items-center justify-center gap-2 rounded-xl bg-primary px-6 py-2.5 text-sm font-bold whitespace-nowrap text-on-primary shadow-sm transition hover:bg-primary-container cursor-pointer"
                  >
                    <Search size={15} /> Tìm bàn
                  </button>
                </div>
                {rstError && <p className="text-left text-sm font-medium text-error">{rstError}</p>}
              </form>
            )}
          </div>
        </div>
      </header>

      {/* ── Main ── */}
      <main className="mx-auto w-full max-w-7xl space-y-24 px-5 py-20 md:px-12">
        {isCustomer && (
          <div className="space-y-6">
            <SmartNotificationWidget enabled={isAuthenticated && isCustomer} />
            <UserAiRecommendations
              enabled={isAuthenticated && isCustomer}
              city={activeTab === 'luutru' ? hotelCity : rstCity}
            />
          </div>
        )}

        {/* Featured Hotels */}
        <section>
          <div className="mb-8 flex items-end justify-between">
            <div>
              <h2 className="font-display text-3xl font-bold text-on-surface">Khách sạn nổi bật</h2>
              <p className="mt-2 text-sm text-on-surface-variant">
                Nghỉ dưỡng sang trọng tại những điểm đến hàng đầu.
              </p>
            </div>
            <Link
              to="/hotels"
              className="flex items-center gap-1 text-sm font-semibold text-primary hover:underline"
            >
              Xem tất cả <ArrowRight size={14} />
            </Link>
          </div>
          <div className="grid gap-5 md:grid-cols-2 lg:grid-cols-4">
            {featuredHotelsError && (
              <p className="md:col-span-2 lg:col-span-4 rounded-xl border border-error-container bg-error-container/30 px-4 py-3 text-sm font-medium text-error">
                {featuredHotelsError}
              </p>
            )}
            {featuredHotels.map((hotel) => (
              <Link
                key={hotel.id}
                to={`/hotels/${hotel.id}?${new URLSearchParams({
                  checkIn: hotelCheckIn,
                  checkOut: hotelCheckOut,
                  guests: String(hotelGuests),
                }).toString()}`}
                className="group overflow-hidden rounded-2xl border border-outline-variant/40 bg-surface-container-lowest shadow-sm transition-all duration-300 hover:-translate-y-1 hover:shadow-md"
              >
                <div className="relative h-52 overflow-hidden">
                  <img
                    src={hotel.thumbnailUrl || ''}
                    alt={hotel.name}
                    loading="lazy"
                    className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110"
                  />
                  <div className="absolute right-3 top-3 flex items-center gap-1 rounded-lg bg-white/90 px-2 py-1 shadow-sm backdrop-blur-sm">
                    <span className="text-xs text-yellow-500">★</span>
                    <span className="text-xs font-semibold text-on-surface">{hotel.rating}</span>
                  </div>
                </div>
                <div className="p-4">
                  <h3 className="truncate font-display text-base font-semibold text-on-surface transition-colors group-hover:text-primary">
                    {hotel.name}
                  </h3>
                  <p className="mt-1 flex items-center gap-1 text-xs text-on-surface-variant">
                    <MapPin size={11} />
                    {hotel.city}
                  </p>
                  <div className="mt-3 flex items-center justify-between">
                    <div>
                      <p className="text-[10px] font-semibold uppercase tracking-wide text-on-surface-variant">
                        Giá từ
                      </p>
                      <p className="mt-1 font-display text-sm font-semibold text-primary">
                        {formatVnd(hotel.minPrice)}
                        <span className="ml-1 text-xs font-normal text-on-surface-variant">/ đêm</span>
                      </p>
                    </div>
                    <span className={`rounded px-2 py-1 text-xs font-medium ${hotel.availableRooms <= 2 ? 'text-red-700 bg-red-50' : 'text-green-700 bg-green-50'}`}>
                      {hotel.availableRooms <= 2 ? 'Sắp hết' : 'Còn phòng'}
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </section>

        {/* Featured Restaurants */}
        <section>
          <div className="mb-8 flex items-end justify-between">
            <div>
              <h2 className="font-display text-3xl font-bold text-on-surface">Nhà hàng nổi bật</h2>
              <p className="mt-2 text-sm text-on-surface-variant">
                Trải nghiệm tinh hoa ẩm thực từ những đầu bếp hàng đầu.
              </p>
            </div>
            <Link
              to="/restaurants"
              className="flex items-center gap-1 text-sm font-semibold text-primary hover:underline"
            >
              Xem tất cả <ArrowRight size={14} />
            </Link>
          </div>
          <div className="grid gap-5 md:grid-cols-3">
            {featuredRestaurantsError && (
              <p className="md:col-span-3 rounded-xl border border-error-container bg-error-container/30 px-4 py-3 text-sm font-medium text-error">
                {featuredRestaurantsError}
              </p>
            )}
            {featuredRestaurants.map((rst) => (
              <Link
                key={rst.id}
                to={`/restaurants/${rst.id}?${new URLSearchParams({
                  date: rstDate,
                  time: rstTime,
                  guests: String(rstGuests),
                }).toString()}`}
                className="group flex gap-4 rounded-2xl border border-outline-variant/40 bg-surface-container-lowest p-4 shadow-sm transition-all duration-300 hover:-translate-y-0.5 hover:shadow-md"
              >
                <div className="h-28 w-28 shrink-0 overflow-hidden rounded-xl">
                  <img
                    src={rst.thumbnailUrl || ''}
                    alt={rst.name}
                    loading="lazy"
                    className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110"
                  />
                </div>
                <div className="flex flex-col justify-between py-0.5">
                  <div>
                    <div className="flex items-start justify-between gap-2">
                      <h3 className="font-display font-semibold text-on-surface transition-colors group-hover:text-primary">
                        {rst.name}
                      </h3>
                      <span className="flex shrink-0 items-center gap-0.5 text-xs font-semibold">
                        <span className="text-yellow-500">★</span>
                        {rst.rating}
                      </span>
                    </div>
                    <p className="mt-1 text-xs text-on-surface-variant">{rst.cuisine}</p>
                  </div>
                  <div className="flex items-end justify-between gap-3 text-xs">
                    <div>
                      <p className="text-[10px] font-semibold uppercase tracking-wide text-on-surface-variant">
                        Khoảng giá
                      </p>
                      <span className="mt-1 block font-semibold leading-relaxed text-primary">
                        {rst.priceRange}
                      </span>
                    </div>
                    <span className="flex items-center gap-0.5 text-on-surface-variant">
                      <MapPin size={10} />
                      {rst.district || rst.city}
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </section>

        {/* Explore Vietnam */}
        <section>
          <div className="mb-8 flex items-end justify-between">
            <div>
              <h2 className="font-display text-3xl font-bold text-on-surface">Khám phá Việt Nam</h2>
              <p className="mt-2 text-sm text-on-surface-variant">
                Những điểm đến kinh điển đang chờ bạn khám phá.
              </p>
            </div>
            <button
              type="button"
              className="hidden items-center gap-1 text-sm font-semibold text-primary hover:underline md:flex"
            >
              Xem tất cả <ArrowRight size={14} />
            </button>
          </div>
          <div className="grid h-120 grid-cols-1 gap-5 md:grid-cols-3">
            <div className="group relative col-span-1 cursor-pointer overflow-hidden rounded-2xl md:col-span-2">
              <div
                className="absolute inset-0 bg-cover bg-center transition-transform duration-700 group-hover:scale-105"
                style={{ backgroundImage: `url(${EXPLORE_DESTINATIONS[0].image})` }}
              />
              <div className="absolute inset-0 bg-linear-to-t from-black/80 via-black/20 to-transparent" />
              <div className="absolute bottom-0 left-0 p-6">
                <h3 className="font-display text-2xl font-bold text-white">
                  {EXPLORE_DESTINATIONS[0].name}
                </h3>
                <p className="mt-1 text-sm text-white/80">{EXPLORE_DESTINATIONS[0].desc}</p>
              </div>
            </div>
            <div className="flex flex-col gap-5">
              {EXPLORE_DESTINATIONS.slice(1).map((dest) => (
                <div
                  key={dest.id}
                  className="group relative flex-1 cursor-pointer overflow-hidden rounded-2xl"
                >
                  <div
                    className="absolute inset-0 bg-cover bg-center transition-transform duration-700 group-hover:scale-105"
                    style={{ backgroundImage: `url(${dest.image})` }}
                  />
                  <div className="absolute inset-0 bg-linear-to-t from-black/70 to-transparent" />
                  <div className="absolute bottom-0 left-0 p-5">
                    <h3 className="font-display text-lg font-bold text-white">{dest.name}</h3>
                    <p className="mt-1 text-xs text-white/80">{dest.desc}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* Why Choose TraVi */}
        <section className="rounded-3xl border border-outline-variant/40 bg-surface-container-lowest p-8 shadow-sm md:p-12">
          <div className="mb-10 text-center">
            <h2 className="font-display text-3xl font-bold text-on-surface">Tại Sao Chọn TraVi?</h2>
            <p className="mt-3 text-sm text-on-surface-variant">
              Nâng tầm hành trình với tiêu chuẩn không thỏa hiệp.
            </p>
          </div>
          <div className="grid gap-6 md:grid-cols-3">
            {TRUST_FEATURES.map((feat) => {
              const Icon = feat.icon;
              return (
                <div
                  key={feat.title}
                  className="flex flex-col items-center rounded-2xl border border-outline-variant/30 bg-surface-bright p-6 text-center"
                >
                  <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-mint-green">
                    <Icon size={28} className="text-primary" />
                  </div>
                  <h3 className="font-display text-lg font-semibold text-on-surface">
                    {feat.title}
                  </h3>
                  <p className="mt-2 text-sm leading-relaxed text-on-surface-variant">
                    {feat.desc}
                  </p>
                </div>
              );
            })}
          </div>
        </section>
      </main>

      {/* ── Footer ── */}
      <footer className="border-t border-outline-variant/30 bg-surface-container-lowest">
        <div className="mx-auto grid w-full max-w-7xl gap-8 px-5 py-12 text-sm md:grid-cols-4 md:px-12">
          <div>
            <Link to="/" className="font-display text-xl font-bold text-primary cursor-pointer">
              TraVi
            </Link>
            <p className="mt-3 leading-relaxed text-on-surface-variant">
              Khám phá tinh hoa của Lush Hospitality. Tuyển chọn những chỗ lưu trú và trải nghiệm
              tốt nhất trên toàn Việt Nam.
            </p>
          </div>
          <div className="space-y-3">
            <h4 className="font-semibold text-on-surface">Công ty</h4>
            <p className="cursor-pointer text-on-surface-variant hover:text-secondary transition-colors">
              Về chúng tôi
            </p>
            <p className="cursor-pointer text-on-surface-variant hover:text-secondary transition-colors">
              Liên hệ
            </p>
          </div>
          <div className="space-y-3">
            <h4 className="font-semibold text-on-surface">Pháp lý</h4>
            <p className="cursor-pointer text-on-surface-variant hover:text-secondary transition-colors">
              Chính sách bảo mật
            </p>
            <p className="cursor-pointer text-on-surface-variant hover:text-secondary transition-colors">
              Điều khoản dịch vụ
            </p>
          </div>
          <div className="space-y-3">
            <h4 className="font-semibold text-on-surface">Đối tác</h4>
            <p className="cursor-pointer text-on-surface-variant hover:text-secondary transition-colors">
              VNPay
            </p>
            <p className="cursor-pointer text-on-surface-variant hover:text-secondary transition-colors">
              MoMo
            </p>
          </div>
        </div>
        <div className="border-t border-outline-variant/30 px-5 py-5 text-center text-xs text-on-surface-variant md:px-12">
          © 2024 TraVi Vietnam. Bảo lưu mọi quyền.
        </div>
      </footer>
    </div>
  );
}
