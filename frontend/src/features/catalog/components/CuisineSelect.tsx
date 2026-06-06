import { Utensils } from 'lucide-react'
import type { FilterOption } from '../../hotels/types'
import { formatFilterLabel } from '../../../utils/display'

type CuisineSelectProps = {
  value: string
  onChange: (value: string) => void
  options?: FilterOption[]
}

export function CuisineSelect({ value, onChange, options = [] }: CuisineSelectProps) {
  return (
    <label className="flex min-w-0 cursor-pointer items-center gap-2.5 rounded-xl bg-white px-3 py-2.5 shadow-sm ring-1 ring-outline-variant/50 transition focus-within:ring-primary">
      <Utensils size={15} className="shrink-0 text-primary" />
      <div className="min-w-0 flex-1">
        <p className="text-[10px] font-semibold uppercase tracking-wider text-on-surface-variant">Loại ẩm thực</p>
        <select
          value={value}
          onChange={(event) => onChange(event.target.value)}
          className="w-full cursor-pointer bg-transparent text-sm font-medium text-on-surface outline-none"
        >
          <option value="">Tất cả loại ẩm thực</option>
          {options.map((option) => (
            <option key={option.id} value={option.id}>
              {formatFilterLabel(option.name)}
            </option>
          ))}
        </select>
      </div>
    </label>
  )
}

