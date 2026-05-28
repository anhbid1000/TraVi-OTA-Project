import type { FormEvent } from 'react'
import { CalendarDays, Clock3, Search, Users } from 'lucide-react'
import { CityAutocomplete } from './CityAutocomplete'

// ─── Types ────────────────────────────────────────────────────

type HotelForm = {
  city: string
  checkIn: string
  checkOut: string
  guests: number
}

type RestaurantForm = {
  city: string
  date: string
  time: string
  guests: number
}

type CatalogSearchBarProps =
  | {
      mode: 'hotels'
      form: HotelForm
      onChange: (form: HotelForm) => void
      onSubmit: (event: FormEvent<HTMLFormElement>) => void
    }
  | {
      mode: 'restaurants'
      form: RestaurantForm
      onChange: (form: RestaurantForm) => void
      onSubmit: (event: FormEvent<HTMLFormElement>) => void
    }

// ─── Divider ────────────────────────────────────────────────

function Divider() {
  return <div className="mx-1 h-4 w-px shrink-0 bg-outline-variant" />
}

// ─── Main component ───────────────────────────────────────────

export function CatalogSearchBar(props: CatalogSearchBarProps) {
  if (props.mode === 'hotels') {
    const { form, onChange, onSubmit } = props

    return (
      <form
        onSubmit={onSubmit}
        className="hidden items-center gap-2 rounded-full border border-outline-variant/50 bg-surface-container px-4 py-[16px] text-sm transition-colors focus-within:border-primary lg:flex"
      >
        {/* Location */}
        <Search size={14} className="shrink-0 text-on-surface-variant" />
        <div className="w-36 min-w-0">
          <CityAutocomplete
            compact
            value={form.city}
            onChange={(city) => onChange({ ...form, city })}
            placeholder="Địa điểm"
          />
        </div>

        <Divider />

        {/* Dates */}
        <CalendarDays size={14} className="shrink-0 text-on-surface-variant" />
        <input
          type="date"
          value={form.checkIn}
          onChange={(e) => onChange({ ...form, checkIn: e.target.value })}
          className="w-[7.5rem] cursor-pointer bg-transparent text-on-surface-variant outline-none"
        />
        <span className="shrink-0 select-none text-on-surface-variant/60">–</span>
        <input
          type="date"
          value={form.checkOut}
          onChange={(e) => onChange({ ...form, checkOut: e.target.value })}
          className="w-[7.5rem] cursor-pointer bg-transparent text-on-surface-variant outline-none"
        />

        <Divider />

        {/* Guests */}
        <Users size={14} className="shrink-0 text-on-surface-variant" />
        <input
          type="number"
          min={1}
          max={99}
          value={form.guests}
          onChange={(e) => onChange({ ...form, guests: Math.max(1, Number(e.target.value) || 1) })}
          className="w-7 bg-transparent text-on-surface outline-none"
        />
        <span className="shrink-0 whitespace-nowrap text-on-surface-variant">khách</span>

        {/* Submit */}
        <button
          type="submit"
          className="ml-1 shrink-0 cursor-pointer rounded-xl bg-primary px-4 py-1 text-xs font-bold text-on-primary transition hover:bg-surface-tint active:scale-95"
        >
          Tìm
        </button>
      </form>
    );
  }

  // ── Restaurants ──────────────────────────────────────────────
  const { form, onChange, onSubmit } = props

  return (
    <form
      onSubmit={onSubmit}
      className="hidden items-center gap-2 rounded-full border border-outline-variant/50 bg-surface-container px-4 py-[16px] text-sm transition-colors focus-within:border-primary lg:flex"
    >
      {/* Location */}
      <Search size={14} className="shrink-0 text-on-surface-variant" />
      <div className="w-32 min-w-0">
        <CityAutocomplete
          compact
          value={form.city}
          onChange={(city) => onChange({ ...form, city })}
          placeholder="Địa điểm"
        />
      </div>

      <Divider />

      {/* Date */}
      <CalendarDays size={14} className="shrink-0 text-on-surface-variant" />
      <input
        type="date"
        value={form.date}
        onChange={(e) => onChange({ ...form, date: e.target.value })}
        className="w-[7.5rem] cursor-pointer bg-transparent text-on-surface-variant outline-none"
      />

      <Divider />

      {/* Time */}
      <Clock3 size={14} className="shrink-0 text-on-surface-variant" />
      <input
        type="time"
        value={form.time}
        onChange={(e) => onChange({ ...form, time: e.target.value })}
        className="w-20 cursor-pointer bg-transparent text-on-surface-variant outline-none"
      />

      <Divider />

      {/* Guests */}
      <Users size={14} className="shrink-0 text-on-surface-variant" />
      <input
        type="number"
        min={1}
        max={99}
        value={form.guests}
        onChange={(e) => onChange({ ...form, guests: Math.max(1, Number(e.target.value) || 1) })}
        className="w-7 bg-transparent text-on-surface outline-none"
      />
      <span className="shrink-0 whitespace-nowrap text-on-surface-variant">khách</span>

      {/* Submit */}
      <button
        type="submit"
        className="ml-1 shrink-0 cursor-pointer rounded-xl bg-primary px-4 py-1 text-xs font-bold text-on-primary transition hover:bg-surface-tint active:scale-95"
      >
        Tìm
      </button>
    </form>
  );
}


