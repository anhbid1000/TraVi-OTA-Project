import { useEffect, useMemo, useState } from 'react'
import { reviewServiceV2 } from '../../services/reviewServiceV2'
import type { ReviewResponse } from '../../types/review'

function formatAspectLabel(aspect: string) {
  const labels: Record<string, string> = {
    CLEANLINESS: 'Sạch sẽ',
    SERVICE: 'Dịch vụ',
    LOCATION: 'Vị trí',
    VALUE: 'Giá trị',
    AMENITIES: 'Tiện ích',
    FOOD_QUALITY: 'Chất lượng món ăn',
    AMBIENCE: 'Không gian',
  }
  return labels[aspect] || aspect
}

function isImageAttachment(mimeType?: string, fileName?: string, fileType?: string) {
  if (fileType === 'IMAGE') return true
  if (mimeType && mimeType.startsWith('image/')) return true
  return Boolean(fileName && /\.(png|jpe?g|webp|gif|bmp|svg)$/i.test(fileName))
}
function resolveFileUrl(fileUrl: string) {
  try {
    return new URL(fileUrl, window.location.origin).href
  } catch {
    return fileUrl
  }
}

type ReviewSort = 'newest' | 'oldest' | 'rating_desc' | 'rating_asc'

interface PublicReviewBlockProps {
  businessProfileId: string
}

export function PublicReviewBlock({ businessProfileId }: PublicReviewBlockProps) {
  const [reviews, setReviews] = useState<ReviewResponse[]>([])
  const [page, setPage] = useState(0)
  const [size] = useState(5)
  const [totalPages, setTotalPages] = useState(0)
  const [sort, setSort] = useState<ReviewSort>('newest')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let mounted = true
    async function loadReviews() {
      if (!businessProfileId) return
      setLoading(true)
      setError(null)
      try {
        const data = await reviewServiceV2.getPublicReviews(businessProfileId, page, size, sort)
        if (!mounted) return
        setReviews(data.content ?? [])
        setTotalPages(data.totalPages ?? 0)
      } catch (e) {
        if (!mounted) return
        setError(e instanceof Error ? e.message : 'Không thể tải đánh giá công khai.')
      } finally {
        if (mounted) setLoading(false)
      }
    }

    void loadReviews()
    return () => {
      mounted = false
    }
  }, [businessProfileId, page, size, sort])

  const averageRating = useMemo(() => {
    if (reviews.length === 0) return 0
    return reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length
  }, [reviews])

  return (
    <section className="space-y-4 rounded-2xl border border-outline-variant/30 bg-white p-6 shadow-sm">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h3 className="font-display text-2xl font-semibold text-primary">Đánh giá từ khách hàng</h3>
          <p className="text-sm text-on-surface-variant">
            {reviews.length > 0 ? `Điểm trung bình trang hiện tại: ${averageRating.toFixed(1)} / 5` : 'Đánh giá công khai từ khách đã sử dụng dịch vụ.'}
          </p>
        </div>

        <select
          value={sort}
          onChange={(e) => {
            setPage(0)
            setSort(e.target.value as ReviewSort)
          }}
          className="rounded-lg border border-outline-variant bg-surface px-3 py-2 text-sm"
        >
          <option value="newest">Mới nhất</option>
          <option value="oldest">Cũ nhất</option>
          <option value="rating_desc">Sao cao → thấp</option>
          <option value="rating_asc">Sao thấp → cao</option>
        </select>
      </div>

      {loading ? <p className="text-sm text-on-surface-variant">Đang tải đánh giá...</p> : null}
      {error ? <p className="text-sm text-error">{error}</p> : null}

      {!loading && !error && reviews.length === 0 ? (
        <div className="rounded-xl border border-dashed border-outline-variant p-6 text-center text-sm text-on-surface-variant">
          Chưa có đánh giá công khai.
        </div>
      ) : null}

      <div className="space-y-4">
        {reviews.map((review) => {
          const imageAttachments = (review.attachments ?? []).filter((att) => isImageAttachment(att.mimeType, att.fileName, att.fileType))
          const otherAttachments = (review.attachments ?? []).filter((att) => !isImageAttachment(att.mimeType, att.fileName, att.fileType))

          return (
          <article key={review.id} className="rounded-xl border border-outline-variant/40 p-4">
            <div className="flex items-center justify-between gap-3">
              <p className="font-semibold text-on-surface">{review.customerName}</p>
              <p className="text-sm text-amber-600">{'★'.repeat(review.rating)}</p>
            </div>
            <p className="mt-2 text-sm text-on-surface-variant">{review.content}</p>

            {review.aspectScores?.length ? (
              <div className="mt-3 flex flex-wrap gap-2">
                {review.aspectScores.map((item) => (
                  <span key={item.aspect} className="rounded-full bg-surface-container-low px-3 py-1 text-xs font-medium text-on-surface">
                    {formatAspectLabel(item.aspect)}: {item.score}/5
                  </span>
                ))}
              </div>
            ) : null}

            {imageAttachments.length ? (
              <div className="mt-3 grid grid-cols-2 gap-2 sm:grid-cols-3">
                {imageAttachments.map((att) => (
                  <a key={att.id} href={resolveFileUrl(att.fileUrl)} target="_blank" rel="noreferrer" className="overflow-hidden rounded-lg border border-outline-variant/30">
                    <img src={resolveFileUrl(att.fileUrl)} alt={att.fileName || 'Ảnh đánh giá'} className="h-28 w-full object-cover" />
                  </a>
                ))}
              </div>
            ) : null}

            {otherAttachments.length ? (
              <div className="mt-3 flex flex-wrap gap-2">
                {otherAttachments.map((att) => (
                  <a key={att.id} href={att.fileUrl} target="_blank" rel="noreferrer" className="rounded-md border px-2 py-1 text-xs text-primary hover:underline">
                    {att.fileName || 'Tệp đính kèm'}
                  </a>
                ))}
              </div>
            ) : null}

            {review.partnerReply ? (
              <div className="mt-3 rounded-md bg-surface p-3 text-sm">
                <p className="font-medium text-primary">Phản hồi từ đối tác</p>
                <p className="mt-1 text-on-surface-variant">{review.partnerReply.content}</p>
              </div>
            ) : null}
          </article>
          )
        })}
      </div>

      <div className="flex items-center justify-end gap-2">
        <button
          type="button"
          onClick={() => setPage((prev) => Math.max(0, prev - 1))}
          disabled={page <= 0}
          className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50"
        >
          Trước
        </button>
        <span className="text-sm text-on-surface-variant">Trang {page + 1} / {Math.max(1, totalPages)}</span>
        <button
          type="button"
          onClick={() => setPage((prev) => (prev + 1 < totalPages ? prev + 1 : prev))}
          disabled={page + 1 >= totalPages}
          className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50"
        >
          Sau
        </button>
      </div>
    </section>
  )
}
