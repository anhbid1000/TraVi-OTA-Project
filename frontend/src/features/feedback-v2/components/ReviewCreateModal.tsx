import { useState } from 'react';
import { reviewServiceV2 } from '../services/reviewServiceV2';
import type { CreateReviewRequest, LoaiDichVu } from '../types/review';

type ReviewCreateModalProps = {
  open: boolean;
  onClose: () => void;
  serviceName: string;
  serviceType: LoaiDichVu;
  bookingId?: string;
  reservationId?: string;
  onSuccess?: () => void;
};

export function ReviewCreateModal({ open, onClose, serviceName, serviceType, bookingId, reservationId, onSuccess }: ReviewCreateModalProps) {
  const [rating, setRating] = useState(5);
  const [content, setContent] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!open) return null;

  const handleSubmit = async () => {
    if (!content.trim()) {
      setError('Vui lòng nhập nội dung đánh giá.');
      return;
    }
    const payload: CreateReviewRequest = {
      loaiDichVu: serviceType,
      bookingId: serviceType === 'KHACH_SAN' ? bookingId : undefined,
      reservationId: serviceType === 'NHA_HANG' ? reservationId : undefined,
      soSao: rating,
      noiDung: content.trim(),
    };
    try {
      setSubmitting(true);
      setError(null);
      await reviewServiceV2.createReview(payload);
      onSuccess?.();
      onClose();
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : 'Không thể tạo đánh giá.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-xl rounded-2xl bg-white p-6 shadow-2xl">
        <div className="mb-5 flex items-start justify-between gap-4">
          <div>
            <h2 className="font-display text-2xl font-bold text-primary">Viết đánh giá</h2>
            <p className="text-sm text-on-surface-variant">{serviceName}</p>
          </div>
          <button className="rounded-full p-2 hover:bg-surface-container-low" onClick={onClose} type="button">
            <span className="material-symbols-outlined">close</span>
          </button>
        </div>

        <div className="mb-4 flex gap-2">
          {[1, 2, 3, 4, 5].map((star) => (
            <button key={star} className={`text-3xl ${star <= rating ? 'text-tertiary-fixed-dim' : 'text-outline-variant'}`} onClick={() => setRating(star)} type="button">★</button>
          ))}
        </div>

        <textarea
          className="min-h-36 w-full rounded-xl border border-outline-variant p-4 outline-none focus:ring-2 focus:ring-primary"
          placeholder="Chia sẻ trải nghiệm của bạn..."
          value={content}
          onChange={(event) => setContent(event.target.value)}
        />
        {error && <p className="mt-3 rounded-lg bg-error-container px-3 py-2 text-sm text-error">{error}</p>}

        <div className="mt-6 flex justify-end gap-3">
          <button className="rounded-xl border border-outline px-5 py-2.5 font-semibold text-on-surface-variant" onClick={onClose} type="button">Hủy</button>
          <button className="rounded-xl bg-primary px-5 py-2.5 font-semibold text-white disabled:opacity-60" disabled={submitting} onClick={handleSubmit} type="button">
            {submitting ? 'Đang gửi...' : 'Gửi đánh giá'}
          </button>
        </div>
      </div>
    </div>
  );
}
