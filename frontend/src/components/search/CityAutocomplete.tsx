import { useMemo, useState } from 'react'
import { MapPin } from 'lucide-react'

// Mock data - tạm thời
const CITY_SUGGESTIONS = [
  'Hà Nội', 'TP. Hồ Chí Minh', 'Đà Nẵng', 'Hải Phòng', 'Cần Thơ',
  'Nha Trang', 'Phú Quốc', 'Hội An', 'Sa Pa', 'Quy Nhơn',
]

type CityAutocompleteProps = {
  value: string
  onChange: (value: string) => void
  placeholder?: string
}

export function CityAutocomplete({
  value,
  onChange,
  placeholder = 'Hội An, Phú Quốc...',
}: CityAutocompleteProps) {
  const [isOpen, setIsOpen] = useState(false)

  const suggestions = useMemo(() => {
    if (!value.trim()) return []
    const lower = value.toLowerCase()
    return CITY_SUGGESTIONS.filter((c) => c.toLowerCase().includes(lower))
  }, [value])

  return (
    <div className="relative flex-1">
      <label className="flex cursor-text items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
        <MapPin size={15} className="shrink-0 text-primary" />
        <div className="w-full">
          <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">
            Địa điểm
          </p>
          <input
            value={value}
            onChange={(e) => {
              onChange(e.target.value)
              setIsOpen(true)
            }}
            onFocus={() => setIsOpen(true)}
            onBlur={() => setTimeout(() => setIsOpen(false), 200)}
            placeholder={placeholder}
            className="w-full bg-transparent text-sm outline-none placeholder:text-on-surface-variant/60"
          />
        </div>
      </label>

      {isOpen && suggestions.length > 0 && (
        <div className="absolute top-full left-0 right-0 z-50 mt-1 rounded-xl border border-outline-variant/50 bg-white shadow-md">
          {suggestions.map((city) => (
            <button
              key={city}
              type="button"
              onClick={() => {
                onChange(city)
                setIsOpen(false)
              }}
              className="block w-full px-4 py-2.5 text-left text-sm text-on-surface hover:bg-surface-container-low transition-colors cursor-pointer first:rounded-t-xl last:rounded-b-xl"
            >
              <MapPin size={13} className="mr-2 inline text-primary" />
              {city}
            </button>
          ))}
        </div>
      )}
    </div>
  )
}

