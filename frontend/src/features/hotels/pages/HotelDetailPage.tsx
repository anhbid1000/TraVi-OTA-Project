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
import { formatDateInputValue, getDefaultHotelStay } from '../../../utils/date'
import type { RoomAvailability, RoomCombinationOption } from '../types'

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

function getStayWeatherEndDate(checkIn: string, checkOut: string) {
  const start = new Date(`${checkIn}T00:00:00`)
  const end = new Date(`${checkOut}T00:00:00`)
  if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime()) || end <= start) {
    return checkIn
  }
  end.setDate(end.getDate() - 1)
  return formatDateInputValue(end)
}

function describeRoomCombo(combo: RoomCombinationOption) {
  return combo.items
    .map((item) => `${item.quantity}x ${item.roomType} (${item.capacityPerRoom} khách/phòng)`)
    .join(', ')
}

function getRoomComboTitle(combo: RoomCombinationOption, index: number) {
  const mainTypes = combo.items.map((item) => item.roomType).join(' + ')
  return mainTypes || `Tổ hợp phòng ${index + 1}`
}

type SelectedRoomEntry = {
  room: RoomAvailability
  quantity: number
}


export function HotelDetailPage() {
  const navigate = useNavigate()
  const { id = '' } = useParams()
  const [searchParams] = useSearchParams()

  const defaultStay = useMemo(() => getDefaultHotelStay(), [])
  const checkIn = searchParams.get('checkIn') || defaultStay.checkIn
  const checkOut = searchParams.get('checkOut') || defaultStay.checkOut
  const guests = toPositiveNumber(searchParams.get('guests'), defaultStay.guests)

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
  const roomCombinationOptions = hotel?.roomCombinationOptions ?? []

  const [selectedRooms, setSelectedRooms] = useState<Record<string, number>>({})
  const [selectedComboIndex, setSelectedComboIndex] = useState<number | null>(null)
  const [selectionType, setSelectionType] = useState<'room' | 'combo' | null>(null)
  const selectedRoomEntries = useMemo<SelectedRoomEntry[]>(
    () => availableRooms
      .map((room) => ({
        room,
        quantity: Math.min(selectedRooms[room.id] || 0, room.availableQuantity),
      }))
      .filter((entry) => entry.quantity > 0),
    [availableRooms, selectedRooms],
  )
  const selectedCombo = selectionType === 'combo' && selectedComboIndex !== null
    ? roomCombinationOptions[selectedComboIndex] || null
    : null

  const nights = getNights(checkIn, checkOut)
  const weatherEndDate = getStayWeatherEndDate(checkIn, checkOut)
  const selectedRoomSummary = selectedRoomEntries
    .map((entry) => `${entry.quantity}x ${entry.room.name}`)
    .join(', ')
  const selectedOptionLabel = selectedCombo
    ? `${selectedCombo.totalRooms} phòng: ${describeRoomCombo(selectedCombo)}`
    : selectedRoomSummary
  const selectedQuantity = selectedCombo
    ? selectedCombo.totalRooms
    : selectedRoomEntries.reduce((sum, entry) => sum + entry.quantity, 0)
  const totalPrice = selectedCombo
    ? selectedCombo.totalPricePerNight * nights
    : selectedRoomEntries.reduce((sum, entry) => sum + entry.room.pricePerNight * entry.quantity * nights, 0)

  const setRoomSelectionQuantity = (roomId: string, quantity: number) => {
    const room = availableRooms.find((item) => item.id === roomId)
    if (!room) {
      return
    }

    setSelectionType('room')
    setSelectedComboIndex(null)
    setSelectedRooms((current) => {
      const nextQuantity = Math.min(room.availableQuantity, Math.max(0, quantity))
      const next = { ...current }
      if (nextQuantity <= 0) {
        delete next[roomId]
      } else {
        next[roomId] = nextQuantity
      }
      return next
    })
  }

  const increaseRoomSelection = (roomId: string) => {
    setRoomSelectionQuantity(roomId, (selectedRooms[roomId] || 0) + 1)
  }

  const decreaseRoomSelection = (roomId: string) => {
    setRoomSelectionQuantity(roomId, (selectedRooms[roomId] || 0) - 1)
  }

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
            endDate={weatherEndDate}
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

            {roomCombinationOptions.length > 0 && (
              <section>
                <h2 className="mb-5 font-display text-3xl font-bold text-primary">Gợi ý phòng cho {guests} khách</h2>
                <div className="space-y-4">
                  {roomCombinationOptions.map((combo, idx) => {
                    const isSelected = selectionType === 'combo' && selectedComboIndex === idx
                    return (
                      <SelectableItemCard
                        key={`${combo.totalRooms}-${combo.totalCapacity}-${idx}`}
                        image=""
                        title={getRoomComboTitle(combo, idx)}
                        meta={[
                          `${combo.totalRooms} phòng`,
                          `Chứa tối đa ${combo.totalCapacity} khách`,
                          ...combo.items.map((item) => `${item.quantity}x ${item.roomType}`),
                        ]}
                        description={describeRoomCombo(combo)}
                        price={`${formatVnd(combo.totalPricePerNight)} / đêm`}
                        statusLabel={isSelected ? 'Đang áp dụng' : `Phù hợp ${guests} khách`}
                        statusTone={isSelected ? 'success' : 'neutral'}
                        actionLabel={isSelected ? 'Đã chọn' : 'Chọn tổ hợp'}
                        actionDisabled={isSelected}
	                        onAction={() => {
	                          setSelectionType('combo')
	                          setSelectedComboIndex(idx)
	                          setSelectedRooms({})
	                        }}
                        footer={
                          isSelected ? (
                            <span className="text-xs font-semibold text-primary">
                              Tổng {formatVnd(combo.totalPricePerNight * nights)} cho {nights} đêm
                            </span>
                          ) : null
                        }
                      />
                    )
                  })}
                </div>
              </section>
            )}

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
	                    const selectedQuantityForRoom = selectedRooms[room.id] || 0
	                    const isSelected = selectionType === 'room' && selectedQuantityForRoom > 0
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
	                        statusLabel={isSelected ? `Đã chọn ${selectedQuantityForRoom}` : `Còn ${room.availableQuantity} phòng`}
	                        statusTone={isSelected ? 'success' : room.availableQuantity <= 2 ? 'warning' : 'success'}
	                        actionLabel={selectedQuantityForRoom >= room.availableQuantity ? 'Đã chọn tối đa' : isSelected ? 'Thêm phòng' : 'Chọn phòng'}
	                        actionDisabled={selectedQuantityForRoom >= room.availableQuantity}
	                        onAction={() => increaseRoomSelection(room.id)}
	                        footer={
	                          isSelected ? (
	                            <span className="text-xs font-semibold text-primary">
	                              Điều chỉnh số lượng ở thanh đặt phòng
	                            </span>
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
	                  <p className="flex items-center gap-2"><BedDouble size={14} /> Phòng chọn: {selectedOptionLabel || 'Chưa chọn'}</p>
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
	        selectedRoom={selectedCombo ? selectedOptionLabel : undefined}
	        selectedRooms={selectedCombo ? undefined : selectedRoomEntries.map((entry) => ({
	          id: entry.room.id,
	          name: entry.room.name,
	          quantity: entry.quantity,
	          availableQuantity: entry.room.availableQuantity,
	        }))}
	        quantity={selectedQuantity}
	        totalPrice={totalPrice}
	        onDecreaseRoomQuantity={decreaseRoomSelection}
	        onIncreaseRoomQuantity={increaseRoomSelection}
	        onAction={() => {
	          if (!selectedCombo && selectedRoomEntries.length === 0) {
	            return
	          }

	          navigate('/checkout', {
	            state: {
	              type: 'hotel',
	              hotelId: hotel.id,
	              roomId: selectedRoomEntries.length === 1 ? selectedRoomEntries[0].room.id : undefined,
	              roomSelections: selectedRoomEntries.map((entry) => ({
	                roomId: entry.room.id,
	                roomName: entry.room.name,
	                quantity: entry.quantity,
	                pricePerNight: entry.room.pricePerNight,
	              })),
	              roomCombination: selectedCombo?.items,
	              roomCombinationTotalRooms: selectedCombo?.totalRooms,
	              roomCombinationTotalCapacity: selectedCombo?.totalCapacity,
	              roomCombinationPricePerNight: selectedCombo?.totalPricePerNight,
	              checkIn,
	              checkOut,
	              guests,
	              nights,
	              quantity: selectedQuantity,
	              totalPrice,
	            },
	          })
	        }}
	        actionLabel="Đặt ngay"
	        actionDisabled={!selectedCombo && selectedRoomEntries.length === 0}
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
