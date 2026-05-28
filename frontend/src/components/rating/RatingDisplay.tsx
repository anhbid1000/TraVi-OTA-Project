type RatingDisplayProps = {
  rating: number
  reviewLabel?: string
  size?: 'sm' | 'md' | 'lg'
}

export function RatingDisplay({ rating, reviewLabel, size = 'md' }: RatingDisplayProps) {
  const sizeClass =
    size === 'sm'
      ? 'rounded-lg px-2 py-1 text-xs'
      : size === 'lg'
        ? 'rounded-2xl px-4 py-3'
        : 'rounded-xl px-3 py-2'

  const textSizeClass = size === 'sm' ? 'text-sm' : size === 'lg' ? 'text-3xl' : 'text-2xl'

  return (
    <div className={`${sizeClass} shrink-0 bg-surface-container-low text-center`}>
      <p className={`font-display ${textSizeClass} font-bold text-primary`}>{rating}</p>
      {reviewLabel && <p className="text-xs text-on-surface-variant">{reviewLabel}</p>}
    </div>
  )
}

