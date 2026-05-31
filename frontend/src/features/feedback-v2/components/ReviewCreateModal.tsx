import { useState } from 'react';
import { reviewServiceV2 } from '../services/reviewServiceV2';
import type { CreateReviewRequest, LoaiDichVu, ReviewAspectType } from '../types/review';

const ASPECT_LABELS: Record<ReviewAspectType, string> = {
  CLEANLINESS: 'Sạch sẽ',
  SERVICE: 'Dịch vụ',
  LOCATION: 'Vị trí',
  VALUE: 'Giá trị',
  AMENITIES: 'Tiện nghi',
  FOOD_QUALITY: 'Chất lượng ẩm thực',
  AMBIENCE: 'Không gian',
};

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
  const [aspectScores, setAspectScores] = useState<Partial<Record<ReviewAspectType, number>>>({});
  const [files, setFiles] = useState<File[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!open) return null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const selected = Array.from(e.target.files);
      if (selected.length + files.length > 5) {
        setError('Chỉ được phép tải lên tối đa 5 ảnh.');
        return;
      }
      setFiles((prev) => [...prev, ...selected].slice(0, 5));
    }
  };

  const removeFile = (index: number) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async () => {
    if (!content.trim()) {
      setError('Vui lòng nhập nội dung đánh giá.');
      return;
    }
    
    const aspectScoresPayload = Object.entries(aspectScores).map(([aspect, score]) => ({
      aspect: aspect as ReviewAspectType,
      score: score as number,
    }));

    const payload: CreateReviewRequest = {
      loaiDichVu: serviceType,
      bookingId: serviceType === 'KHACH_SAN' ? bookingId : undefined,
      reservationId: serviceType === 'NHA_HANG' ? reservationId : undefined,
      soSao: rating,
      noiDung: content.trim(),
      aspectScores: aspectScoresPayload.length > 0 ? aspectScoresPayload : undefined,
    };

    try {
      setSubmitting(true);
      setError(null);
      await reviewServiceV2.createReview(payload, files);
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
      <div className="w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-2xl bg-white p-6 shadow-2xl">
        <div className="mb-5 flex items-start justify-between gap-4">
          <div>
            <h2 className="font-display text-2xl font-bold text-primary">Viết đánh giá</h2>
            <p className="text-sm text-on-surface-variant">{serviceName}</p>
          </div>
          <button className="rounded-full p-2 hover:bg-surface-container-low" onClick={onClose} type="button">
            <span className="material-symbols-outlined">close</span>
          </button>
        </div>

        {/* Tổng quan sao */}
        <div className="mb-4">
          <p className="text-sm font-semibold mb-1">Mức độ hài lòng chung</p>
          <div className="flex gap-2">
            {[1, 2, 3, 4, 5].map((star) => (
              <button key={star} className={`text-4xl ${star <= rating ? 'text-[#e6b10b]' : 'text-outline-variant'}`} onClick={() => setRating(star)} type="button">★</button>
            ))}
          </div>
        </div>

        {/* Nội dung đánh giá */}
        <textarea
          className="min-h-32 w-full rounded-xl border border-outline-variant p-4 outline-none focus:ring-2 focus:ring-primary"
          placeholder="Chia sẻ chi tiết trải nghiệm của bạn..."
          value={content}
          onChange={(event) => setContent(event.target.value)}
        />

        {/* Đánh giá chi tiết (Optional) */}
        <div className="mt-4 rounded-xl bg-surface-container-lowest p-4 border border-outline-variant">
          <p className="font-semibold text-sm mb-3">Đánh giá chi tiết (Tùy chọn)</p>
          <div className="grid grid-cols-2 gap-4">
            {Object.entries(ASPECT_LABELS).map(([key, label]) => {
              const aspect = key as ReviewAspectType;
              const score = aspectScores[aspect] || 0;
              return (
                <div key={aspect} className="flex flex-col text-sm">
                  <span className="mb-1 text-on-surface-variant">{label}</span>
                  <div className="flex gap-1">
                    {[1, 2, 3, 4, 5].map(s => (
                      <button 
                        key={s} 
                        type="button" 
                        onClick={() => setAspectScores(prev => ({...prev, [aspect]: s}))} 
                        className={`text-xl ${s <= score ? 'text-[#e6b10b]' : 'text-outline-variant'}`}
                      >★</button>
                    ))}
                  </div>
                </div>
              )
            })}
          </div>
        </div>

        {/* Upload ảnh */}
        <div className="mt-4">
          <p className="font-semibold text-sm mb-2">Đính kèm ảnh (Tối đa 5 ảnh)</p>
          <input 
            type="file" 
            multiple 
            accept="image/*" 
            onChange={handleFileChange}
            className="block cursor-pointer w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-primary/10 file:text-primary hover:file:bg-primary/20"
          />
          {files.length > 0 && (
            <div className="mt-3 flex flex-wrap gap-2">
              {files.map((file, idx) => (
                <div key={idx} className="relative w-16 h-16 rounded overflow-hidden border">
                  <img src={URL.createObjectURL(file)} alt="preview" className="w-full h-full object-cover" />
                  <button 
                    onClick={() => removeFile(idx)}
                    className="absolute top-0 right-0 bg-red-500 text-white w-4 h-4 flex items-center justify-center text-xs"
                    type="button"
                  >&times;</button>
                </div>
              ))}
            </div>
          )}
        </div>

        {error && <p className="mt-4 rounded-lg bg-error-container px-3 py-2 text-sm text-error">{error}</p>}

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
