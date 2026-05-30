import { useState } from 'react';
import { complaintServiceV2 } from '../services/complaintServiceV2';
import type { CreateComplaintRequest, LoaiDichVu, MucDoKhieuNai } from '../types/complaint';

type ComplaintCreateModalProps = {
  open: boolean;
  onClose: () => void;
  serviceName: string;
  serviceType: LoaiDichVu;
  bookingId?: string;
  reservationId?: string;
  onSuccess?: () => void;
};

export function ComplaintCreateModal({ open, onClose, serviceName, serviceType, bookingId, reservationId, onSuccess }: ComplaintCreateModalProps) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [severity, setSeverity] = useState<MucDoKhieuNai>('BINH_THUONG');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!open) return null;

  const handleSubmit = async () => {
    if (!title.trim() || !content.trim()) {
      setError('Vui lòng nhập tiêu đề và nội dung khiếu nại.');
      return;
    }
    const payload: CreateComplaintRequest = {
      loaiDichVu: serviceType,
      bookingId: serviceType === 'KHACH_SAN' ? bookingId : undefined,
      reservationId: serviceType === 'NHA_HANG' ? reservationId : undefined,
      tieuDe: title.trim(),
      noiDungTomTat: content.trim(),
      mucDo: severity,
    };
    try {
      setSubmitting(true);
      setError(null);
      await complaintServiceV2.createComplaint(payload);
      onSuccess?.();
      onClose();
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : 'Không thể tạo khiếu nại.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-xl rounded-2xl bg-white p-6 shadow-2xl">
        <div className="mb-5 flex items-start justify-between gap-4">
          <div>
            <h2 className="font-display text-2xl font-bold text-primary">Tạo khiếu nại</h2>
            <p className="text-sm text-on-surface-variant">{serviceName}</p>
          </div>
          <button className="rounded-full p-2 hover:bg-surface-container-low" onClick={onClose} type="button">
            <span className="material-symbols-outlined">close</span>
          </button>
        </div>

        <div className="space-y-4">
          <input className="w-full rounded-xl border border-outline-variant px-4 py-3 outline-none focus:ring-2 focus:ring-primary" placeholder="Tiêu đề khiếu nại" value={title} onChange={(event) => setTitle(event.target.value)} />
          <select className="w-full rounded-xl border border-outline-variant px-4 py-3 outline-none focus:ring-2 focus:ring-primary" value={severity} onChange={(event) => setSeverity(event.target.value as MucDoKhieuNai)}>
            <option value="BINH_THUONG">Bình thường</option>
            <option value="NGHIEM_TRONG">Nghiêm trọng</option>
          </select>
          <textarea className="min-h-36 w-full rounded-xl border border-outline-variant p-4 outline-none focus:ring-2 focus:ring-primary" placeholder="Mô tả vấn đề bạn gặp phải..." value={content} onChange={(event) => setContent(event.target.value)} />
        </div>
        {error && <p className="mt-3 rounded-lg bg-error-container px-3 py-2 text-sm text-error">{error}</p>}

        <div className="mt-6 flex justify-end gap-3">
          <button className="rounded-xl border border-outline px-5 py-2.5 font-semibold text-on-surface-variant" onClick={onClose} type="button">Hủy</button>
          <button className="rounded-xl bg-primary px-5 py-2.5 font-semibold text-white disabled:opacity-60" disabled={submitting} onClick={handleSubmit} type="button">
            {submitting ? 'Đang gửi...' : 'Gửi khiếu nại'}
          </button>
        </div>
      </div>
    </div>
  );
}
