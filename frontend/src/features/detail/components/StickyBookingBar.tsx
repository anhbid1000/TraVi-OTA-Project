import { CalendarDays, Clock3, Minus, Plus, Users } from 'lucide-react'

type HotelStickyBookingProps = {
  mode: 'hotel'
  checkIn: string
  checkOut: string
  guests: number
  selectedRoom?: string
  selectedRooms?: Array<{
    id: string
    name: string
    quantity: number
    availableQuantity: number
  }>
  quantity: number
  totalPrice: number
  canDecreaseQuantity?: boolean
  canIncreaseQuantity?: boolean
  onDecreaseQuantity?: () => void
  onIncreaseQuantity?: () => void
  onDecreaseRoomQuantity?: (roomId: string) => void
  onIncreaseRoomQuantity?: (roomId: string) => void
  onAction: () => void
  actionLabel?: string
  actionDisabled?: boolean
}

type RestaurantStickyBookingProps = {
  mode: 'restaurant'
  date: string
  time: string
  guests: number
  selectedPreOrderItems: number
  totalPrice: number
  onAction: () => void
  actionLabel?: string
  actionDisabled?: boolean
}

type StickyBookingBarProps = HotelStickyBookingProps | RestaurantStickyBookingProps

function formatPrice(price: number) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(price)
}

export function StickyBookingBar(props: StickyBookingBarProps) {
  return (
    <div className="fixed inset-x-0 bottom-0 z-50 border-t border-outline-variant/30 bg-white/90 backdrop-blur-xl">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-3 px-4 py-3 md:flex-row md:items-center md:justify-between md:px-12">
        {props.mode === 'hotel' ? (
          <div className="flex flex-wrap items-center gap-4 text-sm">
            <div className="flex items-center gap-1.5 text-on-surface-variant">
              <CalendarDays size={14} />
              <span>{props.checkIn || '--'} - {props.checkOut || '--'}</span>
            </div>
            <div className="flex items-center gap-1.5 text-on-surface-variant">
              <Users size={14} />
              <span>{props.guests} khách</span>
            </div>
            {props.selectedRooms && props.selectedRooms.length > 0 ? (
              <div className="flex flex-wrap items-center gap-2">
                <span className="font-semibold text-on-surface">Phòng đã chọn:</span>
                {props.selectedRooms.map((room) => (
                  <div key={room.id} className="inline-flex items-center gap-2 rounded-xl bg-surface-container-low px-2 py-1">
                    <span className="max-w-40 truncate font-medium text-on-surface">{room.name}</span>
                    <button
                      type="button"
                      onClick={() => props.onDecreaseRoomQuantity?.(room.id)}
                      className="cursor-pointer rounded p-1 text-on-surface-variant hover:bg-surface-container"
                    >
                      <Minus size={14} />
                    </button>
                    <span className="w-6 text-center font-semibold text-on-surface">{room.quantity}</span>
                    <button
                      type="button"
                      onClick={() => props.onIncreaseRoomQuantity?.(room.id)}
                      disabled={room.quantity >= room.availableQuantity}
                      className="cursor-pointer rounded p-1 text-on-surface-variant hover:bg-surface-container disabled:cursor-not-allowed disabled:opacity-40"
                    >
                      <Plus size={14} />
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-on-surface">
                <span className="font-semibold">Phòng:</span> {props.selectedRoom || 'Chưa chọn'}
              </div>
            )}
            {props.selectedRoom && (!props.selectedRooms || props.selectedRooms.length === 0) && (props.onDecreaseQuantity || props.onIncreaseQuantity) ? (
              <div className="inline-flex items-center gap-2 rounded-xl bg-surface-container-low px-2 py-1">
                <button
                  type="button"
                  onClick={props.onDecreaseQuantity}
                  disabled={!props.canDecreaseQuantity}
                  className="cursor-pointer rounded p-1 text-on-surface-variant hover:bg-surface-container disabled:cursor-not-allowed disabled:opacity-40"
                >
                  <Minus size={14} />
                </button>
                <span className="w-6 text-center font-semibold text-on-surface">{props.quantity}</span>
                <button
                  type="button"
                  onClick={props.onIncreaseQuantity}
                  disabled={!props.canIncreaseQuantity}
                  className="cursor-pointer rounded p-1 text-on-surface-variant hover:bg-surface-container disabled:cursor-not-allowed disabled:opacity-40"
                >
                  <Plus size={14} />
                </button>
              </div>
            ) : null}
          </div>
        ) : (
          <div className="flex flex-wrap items-center gap-4 text-sm">
            <div className="flex items-center gap-1.5 text-on-surface-variant">
              <CalendarDays size={14} />
              <span>{props.date || '--'}</span>
            </div>
            <div className="flex items-center gap-1.5 text-on-surface-variant">
              <Clock3 size={14} />
              <span>{props.time || '--'}</span>
            </div>
            <div className="flex items-center gap-1.5 text-on-surface-variant">
              <Users size={14} />
              <span>{props.guests} khách</span>
            </div>
            <div className="text-on-surface">
              <span className="font-semibold">Món đã chọn:</span> {props.selectedPreOrderItems}
            </div>
          </div>
        )}

        <div className="flex items-center justify-between gap-3 md:justify-end">
          <div className="text-right">
            <p className="text-xs text-on-surface-variant">Tổng tạm tính</p>
            <p className="font-display text-xl font-bold text-on-surface">{formatPrice(props.totalPrice)}</p>
          </div>
          <button
            type="button"
            onClick={props.onAction}
            disabled={props.actionDisabled}
            className="cursor-pointer rounded-xl bg-primary px-6 py-2.5 text-sm font-bold text-on-primary transition hover:bg-primary-container disabled:cursor-not-allowed disabled:opacity-60"
          >
            {props.actionLabel || (props.mode === 'hotel' ? 'Đặt ngay' : 'Đặt bàn')}
          </button>
        </div>
      </div>
    </div>
  )
}
