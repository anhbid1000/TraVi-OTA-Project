// Trang thuộc module khách sạn. File được đặt trong features/hotels để gom UI, hook, service và type cùng miền nghiệp vụ.
import { useMemo, useState } from 'react'

import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { BedDouble, Expand, Users } from 'lucide-react'
import {
  DetailAmenities,
  DetailGallery,
  DetailHeader,
  DetailMapCard,
  DetailTopNav,
  SelectableItemCard,
  StickyBookingBar,
  WeatherAlertCard,
} from '../../detail/components'
import { useHotelDetail } from '../hooks/useHotelDetail'
import { formatVnd } from '../../../utils/display'

function toPositiveNumber(value: string | null, fallback: number) {
  const parsed = Number(value)
  if (Number.isNaN(parsed) || parsed < 1) {
    return fallback
  }
  return parsed
}

function getNights(checkIn: string, checkOut: string) {
  const start = new Date(`${checkIn}T00:00:00`)
  const end = new Date(`${checkOut}T00:00:00`)
  if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime())) {
    return 1
  }
  const days = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24))
  return Math.max(1, days)
}


export function HotelDetailPage() {
  const navigate = useNavigate()
  const { id = '' } = useParams()
  const [searchParams] = useSearchParams()

  const checkIn = searchParams.get('checkIn') ?? ''
  const checkOut = searchParams.get('checkOut') ?? ''
  const guests = toPositiveNumber(searchParams.get('guests'), 2)

  const detailParams = useMemo(
    () => ({ checkIn, checkOut, guests }),
    [checkIn, checkOut, guests],
  )

  const {
    data: hotel,
    loading,
    error,
    refetch,
  } = useHotelDetail(id, detailParams)
  const availableRooms = useMemo(
    () => hotel?.rooms.filter((room) => room.availableQuantity > 0) ?? [],
    [hotel],
  )

  const [selectedRoomId, setSelectedRoomId] = useState<string | null>(null)
  const [roomQuantity, setRoomQuantity] = useState(1)

  const effectiveSelectedRoomId = useMemo(() => {
    if (availableRooms.length === 0) {
      return null
    }

    return availableRooms.some((room) => room.id === selectedRoomId)
      ? selectedRoomId
      : availableRooms[0].id
  }, [availableRooms, selectedRoomId])

  const selectedRoom = useMemo(
    () => availableRooms.find((room) => room.id === effectiveSelectedRoomId) || null,
    [availableRooms, effectiveSelectedRoomId],
  )
  const effectiveRoomQuantity = selectedRoom && selectedRoom.id !== selectedRoomId ? 1 : roomQuantity

  const nights = getNights(checkIn, checkOut)
  const totalPrice = selectedRoom ? selectedRoom.pricePerNight * nights * effectiveRoomQuantity : 0

  if (loading) {
    return (
      <div className="min-h-screen bg-surface text-on-surface">
        <DetailTopNav active="hotels" />
        <main className="mx-auto flex min-h-[60vh] w-full max-w-7xl items-center justify-center px-5 py-16 md:px-12">
          <div className="rounded-2xl border border-outline-variant/40 bg-white p-8 text-center shadow-sm">
            <p className="font-semibold text-on-surface">Đang tải chi tiết khách sạn...</p>
          </div>
        </main>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-surface text-on-surface">
        <DetailTopNav active="hotels" />
        <main className="mx-auto flex min-h-[60vh] w-full max-w-7xl items-center justify-center px-5 py-16 md:px-12">
          <div className="rounded-2xl border border-outline-variant/40 bg-white p-8 text-center shadow-sm">
            <h1 className="font-display text-2xl font-bold text-error">Không thể tải dữ liệu</h1>
            <p className="mt-2 text-on-surface-variant">{error}</p>
            <button
              type="button"
              onClick={() => {
                void refetch()
              }}
              className="mt-5 inline-flex cursor-pointer rounded-xl bg-primary px-5 py-2.5 text-sm font-semibold text-on-primary"
            >
              Thử lại
            </button>
          </div>
        </main>
      </div>
    )
  }

  if (!hotel) {
    return (
      <div className="min-h-screen bg-surface text-on-surface">
        <DetailTopNav active="hotels" />
        <main className="mx-auto flex min-h-[60vh] w-full max-w-7xl items-center justify-center px-5 py-16 md:px-12">
          <div className="rounded-2xl border border-outline-variant/40 bg-white p-8 text-center shadow-sm">
            <h1 className="font-display text-3xl font-bold text-primary">Không tìm thấy khách sạn</h1>
            <p className="mt-2 text-on-surface-variant">Liên kết có thể không còn hợp lệ.</p>
            <Link
              to="/hotels"
              className="mt-5 inline-flex cursor-pointer rounded-xl bg-primary px-5 py-2.5 text-sm font-semibold text-on-primary"
            >
              Quay lại danh sách khách sạn
            </Link>
          </div>
        </main>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-surface text-on-surface antialiased">
      <DetailTopNav active="hotels" />

      <main className="mx-auto w-full max-w-7xl px-5 py-8 pb-44 md:px-12">
        <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
          <nav className="text-sm text-on-surface-variant">
            <Link to="/" className="cursor-pointer hover:text-primary">Trang chủ</Link>
            <span className="mx-2">/</span>
            <Link to="/hotels" className="cursor-pointer hover:text-primary">Khách sạn</Link>
            <span className="mx-2">/</span>
            <span className="font-semibold text-on-surface">{hotel.name}</span>
          </nav>
        </div>

        <DetailGallery images={hotel.images.map((image) => image.url)} title={hotel.name} />

        <div className="mb-10 grid grid-cols-1 gap-4 lg:grid-cols-[1fr_auto] lg:items-start">
          <DetailHeader
            title={hotel.name}
            location={hotel.location}
            rating={hotel.rating}
            reviewCount={hotel.reviewCount}
            stars={hotel.stars}
          />
          <WeatherAlertCard
            latitude={hotel.latitude}
            longitude={hotel.longitude}
            date={checkIn}
            locationName={hotel.location}
          />
        </div>

        <div className="grid grid-cols-1 gap-8 lg:grid-cols-12">
          <section className="space-y-10 lg:col-span-8">
            <article>
              <h2 className="mb-4 font-display text-3xl font-bold text-primary">Giới thiệu</h2>
              <p className="text-lg leading-relaxed text-on-surface-variant">{hotel.description}</p>
            </article>

            <DetailAmenities title="Tiện ích nổi bật" amenities={hotel.amenities} />

            <article className="space-y-4 rounded-2xl border border-outline-variant/30 bg-white p-6 shadow-sm">
              <h3 className="font-display text-2xl font-semibold text-primary">Chính sách khách sạn</h3>
              <div className="space-y-3">
                {hotel.policies.map((policy) => (
                  <div key={policy.title}>
                    <p className="font-semibold text-on-surface">{policy.title}</p>
                    <p className="text-sm text-on-surface-variant">{policy.description}</p>
                  </div>
                ))}
              </div>
            </article>

            <section>
              <h2 className="mb-5 font-display text-3xl font-bold text-primary">Phòng còn trống</h2>

              {availableRooms.length === 0 ? (
                <div className="rounded-2xl border border-outline-variant/30 bg-white p-8 text-center shadow-sm">
                  <p className="font-semibold text-on-surface">Hiện chưa có phòng trống phù hợp.</p>
                  <p className="mt-2 text-sm text-on-surface-variant">Bạn có thể thử đổi ngày hoặc số khách.</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {availableRooms.map((room) => {
                    const isSelected = room.id === effectiveSelectedRoomId
                    return (
                      <SelectableItemCard
                        key={room.id}
                        image={room.image}
                        title={room.name}
                        meta={[
                          `${room.areaM2}m2`,
                          `${room.maxGuests} khách`,
                          room.bedText,
                          ...room.highlights,
                        ]}
                        description={room.description}
                        price={`${formatVnd(room.pricePerNight)} / đêm`}
                        statusLabel={`Còn ${room.availableQuantity} phòng`}
                        statusTone={room.availableQuantity <= 2 ? 'warning' : 'success'}
                        actionLabel={isSelected ? 'Đã chọn' : 'Chọn phòng'}
                        actionDisabled={isSelected}
                        onAction={() => {
                          setSelectedRoomId(room.id)
                          setRoomQuantity(1)
                        }}
                        footer={
                          isSelected ? (
                            <span className="text-xs font-semibold text-primary">Đang áp dụng cho đơn của bạn</span>
                          ) : null
                        }
                      />
                    )
                  })}
                </div>
              )}
            </section>
          </section>

          <aside className="space-y-6 lg:col-span-4">
            <div className="lg:sticky lg:top-28 lg:space-y-6">
               <DetailMapCard address={hotel.address} latitude={hotel.latitude} longitude={hotel.longitude} />

              <article className="rounded-2xl border border-outline-variant/30 bg-white p-6 shadow-sm">
                <h3 className="mb-4 font-display text-2xl font-semibold text-primary">Thông tin đặt phòng</h3>
                <div className="space-y-2 text-sm text-on-surface-variant">
                  <p className="flex items-center gap-2"><Expand size={14} /> Số đêm: {nights}</p>
                  <p className="flex items-center gap-2"><Users size={14} /> Số khách: {guests}</p>
                  <p className="flex items-center gap-2"><BedDouble size={14} /> Phòng chọn: {selectedRoom?.name || 'Chưa chọn'}</p>
                </div>
              </article>
            </div>
          </aside>
        </div>
      </main>

      <StickyBookingBar
        mode="hotel"
        checkIn={checkIn}
        checkOut={checkOut}
        guests={guests}
        selectedRoom={selectedRoom?.name}
        quantity={effectiveRoomQuantity}
        totalPrice={totalPrice}
        canDecreaseQuantity={effectiveRoomQuantity > 1}
        canIncreaseQuantity={Boolean(selectedRoom && effectiveRoomQuantity < selectedRoom.availableQuantity)}
        onDecreaseQuantity={() => setRoomQuantity((quantity) => Math.max(1, quantity - 1))}
        onIncreaseQuantity={() => {
          if (!selectedRoom) {
            return
          }
          setRoomQuantity((quantity) => Math.min(selectedRoom.availableQuantity, quantity + 1))
        }}
        onAction={() => {
          if (!selectedRoom) {
            return
          }

          navigate('/checkout', {
            state: {
              type: 'hotel',
              hotelId: hotel.id,
              roomId: selectedRoom.id,
              checkIn,
              checkOut,
              guests,
              nights,
              quantity: effectiveRoomQuantity,
              totalPrice,
            },
          })
        }}
        actionLabel="Đặt ngay"
        actionDisabled={!selectedRoom}
      />

      <footer className="border-t border-outline-variant/30 bg-surface-container-lowest">
        <div className="mx-auto grid w-full max-w-7xl gap-6 px-5 py-10 text-sm md:grid-cols-4 md:px-12">
          <div>
            <Link to="/" className="font-display text-xl font-bold text-primary">TraVi</Link>
            <p className="mt-2 text-on-surface-variant">© 2024 TraVi Vietnam. Bảo lưu mọi quyền.</p>
          </div>
          <div className="space-y-2 text-on-surface-variant">
            <p>Về chúng tôi</p>
            <p>Liên hệ</p>
          </div>
          <div className="space-y-2 text-on-surface-variant">
            <p>Chính sách bảo mật</p>
            <p>Điều khoản dịch vụ</p>
          </div>
          <div className="space-y-2 text-on-surface-variant">
            <p>VNPay</p>
            <p>MoMo</p>
          </div>
        </div>
      </footer>
    </div>
  )
}
