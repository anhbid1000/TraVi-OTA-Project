import { Utensils } from 'lucide-react'

const CUISINE_OPTIONS = [
  'Ẩm thực Việt hiện đại',
  'Ẩm thực Pháp cổ điển',
  'Ẩm thực Fusion',
  'Ẩm thực Nhật Bản',
  'Ẩm thực Địa Trung Hải',
  'Ẩm thực Việt truyền thống',
  'BBQ & Nướng',
  'Hải sản',
]

type CuisineSelectProps = {
  value: string
  onChange: (value: string) => void
}

export function CuisineSelect({ value, onChange }: CuisineSelectProps) {
  return (
    <label className="flex cursor-pointer items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
      <Utensils size={15} className="shrink-0 text-primary" />
      <div className="flex flex-col flex-1">
        <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">
          Loại ẩm thực
        </p>
        <select
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="w-full bg-transparent text-sm outline-none font-medium text-on-surface cursor-pointer"
        >
          <option value="">-- Chọn loại ẩm thực --</option>
          {CUISINE_OPTIONS.map((cuisine) => (
            <option key={cuisine} value={cuisine}>
              {cuisine}
            </option>
          ))}
        </select>
      </div>
    </label>
  )
}

