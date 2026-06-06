import { MapPin } from 'lucide-react'

type DetailMapCardProps = {
  address: string
  latitude?: number
  longitude?: number
}

function buildMapEmbedUrl(latitude: number, longitude: number) {
  const offset = 0.01
  const left = longitude - offset
  const right = longitude + offset
  const top = latitude + offset
  const bottom = latitude - offset

  return `https://www.openstreetmap.org/export/embed.html?bbox=${left}%2C${bottom}%2C${right}%2C${top}&layer=mapnik&marker=${latitude}%2C${longitude}`
}

export function DetailMapCard({ address, latitude, longitude }: DetailMapCardProps) {
  const hasCoordinates = typeof latitude === 'number' && typeof longitude === 'number'
  const openMapHref = hasCoordinates
    ? `https://www.google.com/maps?q=${latitude},${longitude}`
    : `https://www.google.com/maps/search/${encodeURIComponent(address)}`

  return (
    <article className="overflow-hidden rounded-3xl border border-outline-variant/30 bg-white p-2 shadow-sm">
      {hasCoordinates ? (
        <div className="overflow-hidden rounded-2xl">
          <iframe
            title="Bản đồ địa điểm"
            src={buildMapEmbedUrl(latitude, longitude)}
            className="h-64 w-full border-0"
            loading="lazy"
            referrerPolicy="no-referrer-when-downgrade"
          />
        </div>
      ) : (
        <div className="flex h-64 items-center justify-center rounded-2xl border border-dashed border-outline-variant/60 bg-surface-container-low text-on-surface-variant">
          <div className="text-center">
            <MapPin size={22} className="mx-auto mb-2" />
            <p className="text-sm">Không có tọa độ bản đồ</p>
          </div>
        </div>
      )}

      <div className="p-4">
        <p className="text-sm text-on-surface-variant">{address}</p>
        <a
          href={openMapHref}
          target="_blank"
          rel="noreferrer"
          className="mt-4 inline-flex cursor-pointer items-center gap-2 rounded-xl bg-surface-container-low px-4 py-2 text-sm font-semibold text-primary transition hover:bg-mint-green"
        >
          <MapPin size={14} />
          Mở bản đồ
        </a>
      </div>
    </article>
  )
}

