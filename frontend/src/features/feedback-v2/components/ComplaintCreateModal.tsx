import { useState } from 'react';
import { complaintServiceV2 } from '../services/complaintServiceV2';
import type { CreateComplaintRequest, LoaiDichVu, MucDoKhieuNai, ComplaintCategory } from '../types/complaint';

const CATEGORY_LABELS: Record<ComplaintCategory, string> = {
  ROOM_QUALITY: "Chất lượng phòng",
  CLEANLINESS: "Vệ sinh",
  SERVICE_ATTITUDE: "Thái độ phục vụ",
  BILLING: "Thanh toán / Hóa đơn",
  FOOD_QUALITY: "Chất lượng ẩm thực",
  BOOKING_PROBLEM: "Vấn đề đặt chỗ",
  FACILITY_PROBLEM: "Vấn đề cơ sở vật chất",
  OTHER: "Khác"
};

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
  const [category, setCategory] = useState<ComplaintCategory>('ROOM_QUALITY');
  const [severity, setSeverity] = useState<MucDoKhieuNai>('BINH_THUONG');
  const [files, setFiles] = useState<File[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!open) return null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const selected = Array.from(e.target.files);
      if (selected.length + files.length > 5) {
        setError('Chỉ được phép đính kèm tối đa 5 file/ảnh.');
        return;
      }
      setFiles((prev) => [...prev, ...selected].slice(0, 5));
    }
  };

  const removeFile = (index: number) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async () => {
    if (!title.trim() || !content.trim()) {
      setError('Vui lòng nhập đầy đủ tiêu đề và nội dung khiếu nại.');
      return;
    }
    const payload: CreateComplaintRequest = {
      loaiDichVu: serviceType,
      bookingId: serviceType === 'KHACH_SAN' ? bookingId : undefined,
      reservationId: serviceType === 'NHA_HANG' ? reservationId : undefined,
      tieuDe: title.trim(),
      noiDungTomTat: content.trim(),
      category: category,
      mucDo: severity,
    };

    try {
      setSubmitting(true);
      setError(null);
      await complaintServiceV2.createComplaint(payload, files);
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
      <div className="w-full max-w-xl max-h-[90vh] overflow-y-auto rounded-2xl bg-white p-6 shadow-2xl">
        <div className="mb-5 flex items-start justify-between gap-4">
          <div>
            <h2 className="font-display text-2xl font-bold text-primary">Tạo khiếu nại / ticket</h2>
            <p className="text-sm text-on-surface-variant">Dịch vụ: {serviceName}</p>
          </div>
          <button className="rounded-full p-2 hover:bg-surface-container-low" onClick={onClose} type="button">
            <span className="material-symbols-outlined">close</span>
          </button>
        </div>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-semibold mb-1">Tiêu đề</label>
            <input 
              className="w-full rounded-xl border border-outline-variant px-4 py-2.5 outline-none focus:ring-2 focus:ring-primary" 
              placeholder="VD: Nhận sai hạng phòng..." 
              value={title} 
              onChange={(event) => setTitle(event.target.value)} 
            />
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-semibold mb-1">Phân loại vấn đề</label>
              <select 
                className="w-full rounded-xl border border-outline-variant px-4 py-2.5 outline-none focus:ring-2 focus:ring-primary text-sm" 
                value={category} 
                onChange={(event) => setCategory(event.target.value as ComplaintCategory)}
              >
                {Object.entries(CATEGORY_LABELS).map(([key, label]) => (
                  <option key={key} value={key}>{label}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-semibold mb-1">Mức độ ưu tiên</label>
              <select 
                className="w-full rounded-xl border border-outline-variant px-4 py-2.5 outline-none focus:ring-2 focus:ring-primary text-sm" 
                value={severity} 
                onChange={(event) => setSeverity(event.target.value as MucDoKhieuNai)}
              >
                <option value="BINH_THUONG">Bình thường</option>
                <option value="NGHIEM_TRONG">Nghiêm trọng (Cần xử lý gấp)</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold mb-1">Chi tiết vấn đề</label>
            <textarea 
              className="min-h-32 w-full rounded-xl border border-outline-variant p-4 outline-none focus:ring-2 focus:ring-primary" 
              placeholder="Mô tả cụ thể vấn đề bạn gặp phải, yêu cầu hỗ trợ..." 
              value={content} 
              onChange={(event) => setContent(event.target.value)} 
            />
          </div>

          {/* Upload bằng chứng */}
          <div>
            <label className="block text-sm font-semibold mb-1">Tài liệu đính kèm (Bằng chứng ảnh/video)</label>
            <input 
              type="file" 
              multiple 
              accept="image/*,.pdf,.doc,.docx" 
              onChange={handleFileChange}
              className="block cursor-pointer w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-error-container file:text-error hover:file:bg-error-container/80"
            />
            {files.length > 0 && (
              <div className="mt-2 text-sm text-on-surface-variant flex flex-col gap-1">
                {files.map((file, idx) => (
                  <div key={idx} className="flex items-center gap-2">
                    <span className="material-symbols-outlined text-sm">draft</span>
                    <span className="truncate max-w-[250px]">{file.name}</span>
                    <button 
                      onClick={() => removeFile(idx)}
                      className="text-red-500 hover:text-red-700 ml-auto"
                      type="button"
                    >&times;</button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {error && <p className="mt-4 rounded-lg bg-error-container px-3 py-2 text-sm text-error">{error}</p>}

        <div className="mt-6 flex justify-end gap-3">
          <button className="rounded-xl border border-outline px-5 py-2.5 font-semibold text-on-surface-variant" onClick={onClose} type="button">Hủy</button>
          <button className="rounded-xl bg-error px-5 py-2.5 font-semibold text-white disabled:opacity-60" disabled={submitting} onClick={handleSubmit} type="button">
            {submitting ? 'Đang gửi...' : 'Gửi khiếu nại'}
          </button>
        </div>
      </div>
    </div>
  );
}
