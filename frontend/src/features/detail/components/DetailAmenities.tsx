import type { LucideIcon } from 'lucide-react'
import { Sparkles } from 'lucide-react'

type AmenityItem = {
  name: string
  icon?: LucideIcon
}

type DetailAmenitiesProps = {
  title: string
  amenities: AmenityItem[]
}

export function DetailAmenities({ title, amenities }: DetailAmenitiesProps) {
  return (
    <section className="space-y-5">
      <h2 className="text-xs font-semibold uppercase tracking-[0.18em] text-on-surface-variant">{title}</h2>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {amenities.map((amenity) => {
          const Icon = amenity.icon || Sparkles
          return (
            <div key={amenity.name} className="flex items-center gap-2.5 text-sm text-on-surface">
              <Icon size={16} className="text-secondary" />
              <span>{amenity.name}</span>
            </div>
          )
        })}
      </div>
    </section>
  )
}

