type DetailHeaderProps = {
  title: string
  location: string
  rating: number
  reviewCount: number
  stars?: number
  category?: string
}

export function DetailHeader({
  title,
  location,
  rating,
  reviewCount,
  stars,
  category,
}: DetailHeaderProps) {
  return (
    <section className="space-y-3">
      <div className="flex flex-wrap items-center gap-3 text-sm text-on-surface-variant">
        {typeof stars === 'number' && stars > 0 && (
          <span className="tracking-wide text-tertiary-fixed-dim">{'★'.repeat(stars)}</span>
        )}
        <span>{location}</span>
        {category ? <span className="h-1 w-1 rounded-full bg-outline-variant" /> : null}
        {category ? <span>{category}</span> : null}
      </div>

      <h1 className="font-display text-4xl font-bold text-primary md:text-5xl">{title}</h1>

      <div className="flex flex-wrap items-center gap-3 text-sm">
        <div className="inline-flex items-center gap-2 rounded-lg bg-mint-green px-3 py-1.5">
          <span className="text-lg font-bold text-secondary">{rating.toFixed(1)}</span>
          <span className="font-medium text-on-secondary-container">Xuất sắc</span>
        </div>
        <span className="text-on-surface-variant">{reviewCount.toLocaleString('vi-VN')} đánh giá</span>
      </div>

    </section>
  )
}

