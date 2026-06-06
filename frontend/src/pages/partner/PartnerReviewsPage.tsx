import { useCallback, useEffect, useMemo, useState } from 'react';
import { reviewServiceV2 } from '../../features/feedback-v2/services/reviewServiceV2';
import type { ReviewResponse, TrangThaiDanhGia } from '../../features/feedback-v2/types/review';
import { PartnerPagination } from '../../features/feedback-v2/components/partner/PartnerPagination';
import { PartnerReviewFilters } from '../../features/feedback-v2/components/partner/PartnerReviewFilters';
import { PartnerReviewCard } from '../../features/feedback-v2/components/partner/PartnerReviewCard';
import '../dashboard.css';

export default function PartnerReviewsPage() {
  const [items, setItems] = useState<ReviewResponse[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [keyword, setKeyword] = useState('');
  const [ratingFilter, setRatingFilter] = useState<'' | number>('');
  const [statusFilter, setStatusFilter] = useState<'' | TrangThaiDanhGia>('');
  const [activeReviewId, setActiveReviewId] = useState<string | null>(null);
  const [replyContent, setReplyContent] = useState('');
  const [replying, setReplying] = useState(false);

  const loadReviews = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await reviewServiceV2.getPartnerReviews(page, 10, 'newest');
      setItems(data.content ?? []);
      setTotalPages(data.totalPages ?? 1);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể tải danh sách đánh giá.');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    void loadReviews();
  }, [loadReviews]);

  const filtered = useMemo(() => {
    const q = keyword.trim().toLowerCase();
    return items.filter((item) => {
      const matchKeyword = !q || `${item.customerName} ${item.content} ${item.businessProfileId}`.toLowerCase().includes(q);
      const matchRating = ratingFilter === '' || item.rating === ratingFilter;
      const matchStatus = statusFilter === '' || item.status === statusFilter;
      return matchKeyword && matchRating && matchStatus;
    });
  }, [items, keyword, ratingFilter, statusFilter]);

  const openReply = (review: ReviewResponse) => {
    setActiveReviewId(review.id);
    setReplyContent(review.partnerReply?.content ?? '');
  };

  const handleSubmitReply = async () => {
    if (!activeReviewId || !replyContent.trim()) return;
    try {
      setReplying(true);
      await reviewServiceV2.upsertPartnerReply(activeReviewId, { noiDung: replyContent.trim() });
      setActiveReviewId(null);
      setReplyContent('');
      await loadReviews();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể phản hồi đánh giá.');
    } finally {
      setReplying(false);
    }
  };

  return (
    <>
      <div className="space-y-6">
        <header className="partner-page-header split">
          <div>
            <h1>Quản lý đánh giá</h1>
            <p>Danh sách đánh giá thuộc cơ sở của đối tác</p>
          </div>
          <div className="w-full max-w-md">
            <input
              className="w-full rounded-xl border border-outline-variant bg-white px-4 py-3 text-sm outline-none transition focus:ring-2 focus:ring-primary"
              value={keyword}
              onChange={(event) => setKeyword(event.target.value)}
              placeholder="Tìm theo khách hoặc nội dung..."
            />
          </div>
        </header>

        <section className="panel space-y-6">
          <PartnerReviewFilters
            rating={ratingFilter}
            status={statusFilter}
            onRatingChange={setRatingFilter}
            onStatusChange={setStatusFilter}
          />

          {loading && <div className="rounded-xl bg-white p-8 text-center font-semibold shadow-sm">Đang tải đánh giá...</div>}
          {error && <div className="rounded-xl bg-error-container p-4 text-error">{error}</div>}

          <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            {!loading && filtered.length === 0 && (
              <div className="rounded-xl bg-white p-8 text-center text-on-surface-variant shadow-sm lg:col-span-2">Chưa có đánh giá phù hợp.</div>
            )}
            {filtered.map((review) => (
              <PartnerReviewCard key={review.id} review={review} onReply={openReply} />
            ))}
          </div>

          {!loading && totalPages > 0 && <PartnerPagination page={page} totalPages={totalPages} onPageChange={setPage} />}
        </section>
      </div>
      {activeReviewId && (
        <div className="fixed inset-0 z-[80] flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-2xl rounded-2xl bg-white p-6 shadow-2xl">
            <h3 className="text-xl font-bold text-primary">Phản hồi đánh giá</h3>
            <textarea
              className="mt-4 min-h-40 w-full rounded-xl border border-outline-variant p-4 outline-none focus:ring-2 focus:ring-primary"
              placeholder="Nhập phản hồi chính thức của đối tác..."
              value={replyContent}
              onChange={(e) => setReplyContent(e.target.value)}
            />
            <div className="mt-5 flex justify-end gap-3">
              <button type="button" className="rounded-xl border border-outline px-4 py-2 font-semibold" onClick={() => setActiveReviewId(null)}>
                Hủy
              </button>
              <button
                type="button"
                className="rounded-xl bg-primary px-4 py-2 font-semibold text-white disabled:opacity-60"
                disabled={replying || !replyContent.trim()}
                onClick={handleSubmitReply}
              >
                {replying ? 'Đang gửi...' : 'Gửi phản hồi'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
