import { ChevronLeft, ChevronRight, Grid2x2, ImageOff, X } from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import { createPortal } from 'react-dom'

type DetailGalleryProps = {
  images: string[]
  title: string
  onViewAll?: () => void
}

const FALLBACK_IMAGE =
  'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80'

function resolveImage(images: string[], index: number) {
  return images[index] || images[0] || FALLBACK_IMAGE
}

export function DetailGallery({ images, title, onViewAll }: DetailGalleryProps) {
  const galleryImages = useMemo(() => images.filter(Boolean), [images])
  const hasImages = galleryImages.length > 0
  const [selectedIndex, setSelectedIndex] = useState<number | null>(null)
  const selectedImage = useMemo(
    () => (selectedIndex === null ? '' : resolveImage(galleryImages, selectedIndex)),
    [galleryImages, selectedIndex],
  )

  const openImage = (index: number) => {
    setSelectedIndex(index)
    onViewAll?.()
  }

  useEffect(() => {
    if (selectedIndex === null) {
      return undefined
    }

    const previousOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setSelectedIndex(null)
        return
      }

      if (galleryImages.length <= 1) {
        return
      }

      if (event.key === 'ArrowLeft') {
        setSelectedIndex((current) => (current === null ? 0 : (current - 1 + galleryImages.length) % galleryImages.length))
      }

      if (event.key === 'ArrowRight') {
        setSelectedIndex((current) => (current === null ? 0 : (current + 1) % galleryImages.length))
      }
    }

    window.addEventListener('keydown', handleKeyDown)

    return () => {
      document.body.style.overflow = previousOverflow
      window.removeEventListener('keydown', handleKeyDown)
    }
  }, [galleryImages.length, selectedIndex])

  const showPrevious = () => {
    setSelectedIndex((current) => {
      if (current === null) {
        return 0
      }
      return (current - 1 + galleryImages.length) % galleryImages.length
    })
  }

  const showNext = () => {
    setSelectedIndex((current) => {
      if (current === null) {
        return 0
      }
      return (current + 1) % galleryImages.length
    })
  }

  const openLastPreview = () => {
    if (!hasImages) {
      return
    }

    openImage(Math.min(3, galleryImages.length - 1))
  }

  return (
    <section className="mb-8">
      <div className="grid gap-3 overflow-hidden rounded-2xl lg:grid-cols-4 lg:grid-rows-2">
        <div className="relative h-60 overflow-hidden rounded-2xl bg-surface-container-high lg:col-span-2 lg:row-span-2 lg:h-full">
          {hasImages ? (
            <button
              type="button"
              onClick={() => openImage(0)}
              className="block h-full w-full cursor-zoom-in"
              aria-label={`Xem ảnh chính của ${title}`}
            >
              <img
                src={resolveImage(galleryImages, 0)}
                alt={`${title} - ảnh chính`}
                className="h-full w-full object-cover"
                onError={(event) => {
                  event.currentTarget.src = FALLBACK_IMAGE
                }}
              />
            </button>
          ) : (
            <div className="flex h-full items-center justify-center text-on-surface-variant">
              <ImageOff size={28} />
            </div>
          )}
        </div>

        <div className="hidden overflow-hidden rounded-2xl bg-surface-container-high lg:block lg:col-span-2 lg:row-span-1">
          <button
            type="button"
            onClick={() => openImage(1)}
            className="block h-full w-full cursor-zoom-in"
            aria-label={`Xem ảnh 2 của ${title}`}
          >
            <img
              src={resolveImage(galleryImages, 1)}
              alt={`${title} - ảnh 2`}
              className="h-full w-full object-cover"
              onError={(event) => {
                event.currentTarget.src = FALLBACK_IMAGE
              }}
            />
          </button>
        </div>

        <div className="hidden overflow-hidden rounded-2xl bg-surface-container-high lg:block lg:col-span-1 lg:row-span-1">
          <button
            type="button"
            onClick={() => openImage(2)}
            className="block h-full w-full cursor-zoom-in"
            aria-label={`Xem ảnh 3 của ${title}`}
          >
            <img
              src={resolveImage(galleryImages, 2)}
              alt={`${title} - ảnh 3`}
              className="h-full w-full object-cover"
              onError={(event) => {
                event.currentTarget.src = FALLBACK_IMAGE
              }}
            />
          </button>
        </div>

        <button
          type="button"
          onClick={openLastPreview}
          disabled={!hasImages}
          className="relative hidden cursor-pointer overflow-hidden rounded-2xl lg:flex lg:items-center lg:justify-center"
        >
          <img
            src={resolveImage(galleryImages, 3)}
            alt={`${title} - xem thêm ảnh`}
            className="absolute inset-0 h-full w-full object-cover"
            onError={(event) => {
              event.currentTarget.src = FALLBACK_IMAGE
            }}
          />
          <div className="absolute inset-0 bg-primary/70 transition hover:bg-primary/60" />
          <div className="relative z-10 text-center text-on-primary">
            <Grid2x2 size={24} className="mx-auto mb-2" />
            <p className="text-sm font-semibold">Xem tất cả ảnh</p>
          </div>
        </button>
      </div>

      {selectedIndex !== null && typeof document !== 'undefined' && createPortal(
        <div
          className="fixed inset-0 z-[120] flex items-center justify-center bg-black/90 p-4"
          onClick={() => setSelectedIndex(null)}
        >
          <button
            type="button"
            onClick={() => setSelectedIndex(null)}
            className="absolute right-4 top-4 z-10 inline-flex h-11 w-11 cursor-pointer items-center justify-center rounded-full bg-white/10 text-white backdrop-blur transition hover:bg-white/20"
            aria-label="Đóng xem ảnh"
          >
            <X size={20} />
          </button>

          {galleryImages.length > 1 && (
            <button
              type="button"
              onClick={(event) => {
                event.stopPropagation()
                showPrevious()
              }}
              className="absolute left-4 top-1/2 z-10 inline-flex h-11 w-11 -translate-y-1/2 cursor-pointer items-center justify-center rounded-full bg-white/10 text-white backdrop-blur transition hover:bg-white/20"
              aria-label="Ảnh trước"
            >
              <ChevronLeft size={20} />
            </button>
          )}

          <div
            className="relative mx-auto flex w-full max-w-6xl flex-col items-center gap-4"
            onClick={(event) => event.stopPropagation()}
          >
            <img
              src={selectedImage}
              alt={`${title} - ảnh xem lớn`}
              className="max-h-[78vh] max-w-full rounded-2xl object-contain shadow-2xl"
              onError={(event) => {
                event.currentTarget.src = FALLBACK_IMAGE
              }}
            />
            <div className="flex flex-wrap items-center justify-center gap-2 text-white/90">
              <span className="rounded-full bg-white/10 px-3 py-1 text-sm font-medium backdrop-blur">
                {selectedIndex + 1} / {galleryImages.length}
              </span>
              {galleryImages.map((image, index) => (
                <button
                  key={`${image}-${index}`}
                  type="button"
                  onClick={() => setSelectedIndex(index)}
                  className={`h-14 w-14 overflow-hidden rounded-xl border-2 transition ${
                    selectedIndex === index ? 'border-white' : 'border-transparent opacity-70 hover:opacity-100'
                  }`}
                  aria-label={`Xem ảnh ${index + 1}`}
                >
                  <img src={image} alt={`${title} - ảnh thu nhỏ ${index + 1}`} className="h-full w-full object-cover" />
                </button>
              ))}
            </div>
          </div>

          {galleryImages.length > 1 && (
            <button
              type="button"
              onClick={(event) => {
                event.stopPropagation()
                showNext()
              }}
              className="absolute right-4 top-1/2 z-10 inline-flex h-11 w-11 -translate-y-1/2 cursor-pointer items-center justify-center rounded-full bg-white/10 text-white backdrop-blur transition hover:bg-white/20"
              aria-label="Ảnh tiếp theo"
            >
              <ChevronRight size={20} />
            </button>
          )}
        </div>,
        document.body,
      )}
    </section>
  )
}
