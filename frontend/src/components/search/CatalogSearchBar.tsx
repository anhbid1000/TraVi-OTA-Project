import { Search, UserRound } from 'lucide-react'
import type { FormEvent } from 'react'

type HotelSearchForm = {
  city: string
  checkIn: string
  checkOut: string
  guests: number
}

type RestaurantSearchForm = {
  city: string
  date: string
  time: string
  guests: number
}

type CatalogSearchBarProps =
  | {
      mode: 'hotels'
      form: HotelSearchForm
      onChange: (next: HotelSearchForm) => void
      onSubmit: (event: FormEvent<HTMLFormElement>) => void
    }
  | {
      mode: 'restaurants'
      form: RestaurantSearchForm
      onChange: (next: RestaurantSearchForm) => void
      onSubmit: (event: FormEvent<HTMLFormElement>) => void
    }

export function CatalogSearchBar(props: CatalogSearchBarProps) {
  if (props.mode === 'hotels') {
    const { form, onChange, onSubmit } = props

    return (
      <form
        onSubmit={onSubmit}
        className="hidden items-center gap-2 rounded-full border border-outline-variant/50 bg-surface-container-low px-4 py-2 text-sm text-on-surface-variant md:flex"
      >
        <Search size={13} />
        <input
          value={form.city}
          onChange={(event) => onChange({ ...form, city: event.target.value })}
          placeholder="Địa điểm"
          className="w-36 bg-transparent font-medium text-on-surface outline-none placeholder:text-on-surface-variant"
        />
        <span className="text-outline-variant">|</span>
        <input
          type="date"
          value={form.checkIn}
          onChange={(event) => onChange({ ...form, checkIn: event.target.value })}
          className="w-32 bg-transparent outline-none"
        />
        <span>–</span>
        <input
          type="date"
          value={form.checkOut}
          onChange={(event) => onChange({ ...form, checkOut: event.target.value })}
          className="w-32 bg-transparent outline-none"
        />
        <span className="text-outline-variant">|</span>
        <label className="inline-flex items-center gap-1">
          <UserRound size={13} />
          <input
            type="number"
            min={1}
            value={form.guests}
            onChange={(event) => onChange({ ...form, guests: Number(event.target.value) || 1 })}
            className="w-14 bg-transparent outline-none"
          />
          khách
        </label>
        <button
          type="submit"
          className="cursor-pointer rounded-full bg-primary px-3 py-1 text-xs font-semibold text-on-primary hover:bg-primary-container"
        >
          Tìm
        </button>
      </form>
    )
  }

  const { form, onChange, onSubmit } = props

  return (
    <form
      onSubmit={onSubmit}
      className="hidden items-center gap-2 rounded-full border border-outline-variant/50 bg-surface-container-low px-4 py-2 text-sm text-on-surface-variant md:flex"
    >
      <Search size={13} />
      <input
        value={form.city}
        onChange={(event) => onChange({ ...form, city: event.target.value })}
        placeholder="Địa điểm"
        className="w-36 bg-transparent font-medium text-on-surface outline-none placeholder:text-on-surface-variant"
      />
      <span className="text-outline-variant">|</span>
      <input
        type="date"
        value={form.date}
        onChange={(event) => onChange({ ...form, date: event.target.value })}
        className="w-32 bg-transparent outline-none"
      />
      <input
        type="time"
        value={form.time}
        onChange={(event) => onChange({ ...form, time: event.target.value })}
        className="w-20 bg-transparent outline-none"
      />
      <span className="text-outline-variant">|</span>
      <label className="inline-flex items-center gap-1">
        <UserRound size={13} />
        <input
          type="number"
          min={1}
          value={form.guests}
          onChange={(event) => onChange({ ...form, guests: Number(event.target.value) || 1 })}
          className="w-14 bg-transparent outline-none"
        />
        khách
      </label>
      <button
        type="submit"
        className="cursor-pointer rounded-full bg-primary px-3 py-1 text-xs font-semibold text-on-primary hover:bg-primary-container"
      >
        Tìm
      </button>
    </form>
  )
}

