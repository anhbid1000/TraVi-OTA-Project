import type { ReactNode } from 'react'

type StatusTone = 'neutral' | 'success' | 'warning' | 'danger'

type SelectableItemCardProps = {
  image: string
  title: string
  meta: string[]
  description: string
  price: string
  statusLabel?: string
  statusTone?: StatusTone
  actionLabel?: string
  onAction?: () => void
  actionDisabled?: boolean
  footer?: ReactNode
}

const FALLBACK_IMAGE =
  'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=800&q=80'

function getStatusToneClass(statusTone: StatusTone) {
  if (statusTone === 'success') return 'bg-mint-green text-on-secondary-container'
  if (statusTone === 'warning') return 'bg-tertiary-fixed text-on-tertiary-fixed-variant'
  if (statusTone === 'danger') return 'bg-error-container text-on-error-container'
  return 'bg-surface-container-low text-on-surface-variant'
}

export function SelectableItemCard({
  image,
  title,
  meta,
  description,
  price,
  statusLabel,
  statusTone = 'neutral',
  actionLabel,
  onAction,
  actionDisabled,
  footer,
}: SelectableItemCardProps) {
  return (
    <article className="overflow-hidden rounded-2xl border border-outline-variant/30 bg-white shadow-sm transition-shadow hover:shadow-md">
      <div className="flex flex-col md:flex-row">
        <div className="h-52 overflow-hidden md:h-auto md:w-1/3">
          <img
            src={image || FALLBACK_IMAGE}
            alt={title}
            className="h-full w-full object-cover"
            onError={(event) => {
              event.currentTarget.src = FALLBACK_IMAGE
            }}
          />
        </div>

        <div className="flex flex-1 flex-col justify-between p-5">
          <div>
            <div className="mb-2 flex flex-wrap items-start justify-between gap-2">
              <h3 className="font-display text-2xl font-semibold text-primary">{title}</h3>
              <p className="text-xl font-bold text-secondary">{price}</p>
            </div>

            <div className="mb-3 flex flex-wrap items-center gap-3 text-sm text-on-surface-variant">
              {meta.map((item) => (
                <span key={item}>{item}</span>
              ))}
            </div>

            <p className="text-sm leading-relaxed text-on-surface-variant">{description}</p>
          </div>

          <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
            <div className="flex items-center gap-2">
              {statusLabel ? (
                <span className={`rounded-md px-2 py-1 text-xs font-semibold ${getStatusToneClass(statusTone)}`}>
                  {statusLabel}
                </span>
              ) : null}
              {footer}
            </div>

            {actionLabel && onAction ? (
              <button
                type="button"
                onClick={onAction}
                disabled={actionDisabled}
                className="cursor-pointer rounded-xl bg-primary px-5 py-2 text-sm font-semibold text-on-primary transition hover:bg-primary-container disabled:cursor-not-allowed disabled:opacity-60"
              >
                {actionLabel}
              </button>
            ) : null}
          </div>
        </div>
      </div>
    </article>
  )
}

