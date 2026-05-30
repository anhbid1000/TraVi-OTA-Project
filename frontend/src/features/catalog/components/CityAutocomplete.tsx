import { MapPin } from 'lucide-react'
import { useMemo, useState } from 'react'
import { useCitySuggestions } from '../hooks/useCitySuggestions'

type CityAutocompleteProps = {
  value: string
  onChange: (value: string) => void
  placeholder?: string
  /** Renders only the raw input + dropdown, no outer card wrapper */
  compact?: boolean
}

export function CityAutocomplete({
  value,
  onChange,
  placeholder = 'Hội An, Phú Quốc...',
  compact = false,
}: CityAutocompleteProps) {
  const [isOpen, setIsOpen] = useState(false)
  const { cities } = useCitySuggestions()

  const suggestions = useMemo(() => {
    const suggestionPool = cities
    if (!value.trim()) {
      return suggestionPool.slice(0, 6)
    }
    const lowerValue = value.toLowerCase()
    return suggestionPool.filter((city) => city.toLowerCase().includes(lowerValue)).slice(0, 6)
  }, [cities, value])

  const dropdown = isOpen && suggestions.length > 0 && (
    <div className="absolute inset-x-0 top-full z-50 mt-1 overflow-hidden rounded-xl border border-outline-variant/50 bg-white shadow-md">
      {suggestions.map((city) => (
        <button
          key={city}
          type="button"
          onMouseDown={(event) => event.preventDefault()}
          onClick={() => { onChange(city); setIsOpen(false) }}
          className="flex w-full cursor-pointer items-center gap-2 px-3 py-2.5 text-left text-sm text-on-surface transition-colors hover:bg-surface-container-low"
        >
          <MapPin size={13} className="text-primary" />
          <span>{city}</span>
        </button>
      ))}
    </div>
  )

  if (compact) {
    return (
      <div className="relative min-w-0 w-full">
        <input
          value={value}
          onChange={(event) => { onChange(event.target.value); setIsOpen(true) }}
          onFocus={() => setIsOpen(true)}
          onBlur={() => setTimeout(() => setIsOpen(false), 150)}
          placeholder={placeholder}
          className="w-full bg-transparent text-sm font-medium text-on-surface outline-none placeholder:text-on-surface-variant/70"
        />
        {dropdown}
      </div>
    )
  }

  return (
    <div className="relative min-w-0 flex-1">
      <label className="flex cursor-text items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
        <MapPin size={15} className="shrink-0 text-primary" />
        <div className="w-full min-w-0">
          <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">Địa điểm</p>
          <input
            value={value}
            onChange={(event) => { onChange(event.target.value); setIsOpen(true) }}
            onFocus={() => setIsOpen(true)}
            onBlur={() => setTimeout(() => setIsOpen(false), 150)}
            placeholder={placeholder}
            className="w-full bg-transparent text-sm outline-none placeholder:text-on-surface-variant/60"
          />
        </div>
      </label>
      {dropdown}
    </div>
  )
}

