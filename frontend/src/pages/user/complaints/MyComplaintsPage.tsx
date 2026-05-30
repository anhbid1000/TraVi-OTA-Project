import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { CustomerFeedbackLayout } from '../../../features/feedback-v2/components/CustomerFeedbackLayout';
import { complaintServiceV2 } from '../../../features/feedback-v2/services/complaintServiceV2';
import type { ComplaintResponse, TrangThaiKhieuNai } from '../../../features/feedback-v2/types/complaint';

const STATUS_OPTIONS: Array<{ label: string; value: '' | TrangThaiKhieuNai }> = [
  { label: 'Tất cả', value: '' },
  { label: 'Chờ phản hồi', value: 'CHO_PHAN_HOI' },
  { label: 'Đang xử lý', value: 'DANG_XU_LY' },
  { label: 'Đã giải quyết', value: 'DA_GIAI_QUYET' },
  { label: 'Đã đóng', value: 'DA_DONG' },
];

function statusLabel(status: TrangThaiKhieuNai) {
  return ({ CHO_PHAN_HOI: 'Chờ phản hồi', DANG_XU_LY: 'Đang xử lý', DA_GIAI_QUYET: 'Đã giải quyết', DA_DONG: 'Đã đóng' } as const)[status];
}

export default function MyComplaintsPage() {
  const [items, setItems] = useState<ComplaintResponse[]>([]);
  const [status, setStatus] = useState<'' | TrangThaiKhieuNai>('');
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadComplaints = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await complaintServiceV2.getCustomerComplaints(page, 10, status || undefined, undefined, 'updated_desc');
      setItems(data.content ?? []);
      setTotalPages(data.totalPages ?? 1);
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'Không thể tải danh sách khiếu nại.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadComplaints();
  }, [page, status]);

  const filtered = useMemo(() => {
    const q = keyword.trim().toLowerCase();
    if (!q) return items;
    return items.filter((item) => `${item.id} ${item.title}`.toLowerCase().includes(q));
  }, [items, keyword]);

  return (
    <CustomerFeedbackLayout active="complaints" title="Khiếu nại của tôi" subtitle="Theo dõi trạng thái xử lý và trao đổi trực tiếp với đối tác.">
      <section className="mb-8 rounded-3xl border border-outline-variant/40 bg-white p-5 shadow-sm">
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-12 lg:items-center">
          <div className="relative lg:col-span-6">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-outline">search</span>
            <input className="w-full rounded-xl border border-outline-variant bg-white py-3.5 pl-12 pr-4 text-sm outline-none transition focus:ring-2 focus:ring-primary" placeholder="Tìm theo mã hoặc tiêu đề..." value={keyword} onChange={(event) => setKeyword(event.target.value)} />
          </div>
          <div className="flex flex-wrap justify-start gap-2 lg:col-span-6 lg:justify-end">
            {STATUS_OPTIONS.map((option) => (
              <button key={option.value || 'ALL'} className={`rounded-full px-4 py-2.5 text-sm font-semibold transition ${status === option.value ? 'bg-primary text-white shadow-sm' : 'bg-surface-container-high text-on-surface-variant hover:bg-mint-green hover:text-primary'}`} onClick={() => { setPage(0); setStatus(option.value); }} type="button">{option.label}</button>
            ))}
          </div>
        </div>
      </section>

      {loading && <div className="rounded-xl bg-white p-8 text-center font-semibold">Đang tải khiếu nại...</div>}
      {error && <div className="rounded-xl bg-error-container p-4 text-error">{error}</div>}

      <div className="space-y-6">
        {!loading && filtered.length === 0 && <div className="rounded-3xl border-2 border-dashed border-outline-variant bg-white py-20 text-center"><h3 className="font-display text-2xl font-bold text-primary">Không có khiếu nại phù hợp</h3><p className="mt-2 text-on-surface-variant">Thử đổi bộ lọc hoặc tạo khiếu nại từ trang chuyến đi.</p></div>}
        {filtered.map((item) => (
          <div key={item.id} className={`relative overflow-hidden rounded-xl border border-outline-variant bg-white p-6 shadow-sm transition hover:shadow-md ${item.status === 'DA_DONG' ? 'opacity-80' : ''}`}>
            {item.overdue && <div className="absolute right-0 top-0 rounded-bl-xl bg-error px-4 py-1 text-xs font-semibold text-white">Quá hạn</div>}
            <div className="flex flex-col justify-between gap-6 md:flex-row md:items-center">
              <div className="flex-1 space-y-3">
                <div className="flex items-center gap-3"><span className="text-xs uppercase tracking-wider text-outline">#{item.id}</span><span className={`rounded px-2 py-0.5 text-xs font-bold uppercase ${item.severity === 'NGHIEM_TRONG' ? 'bg-error-container text-error' : 'bg-surface-container-high text-on-surface-variant'}`}>{item.severity === 'NGHIEM_TRONG' ? 'Nghiêm trọng' : 'Bình thường'}</span></div>
                <h3 className="font-display text-xl font-semibold text-on-surface hover:text-primary">{item.title}</h3>
                <div className="flex flex-wrap gap-x-6 gap-y-2 text-sm text-on-surface-variant"><span className="flex items-center gap-1.5"><span className="material-symbols-outlined text-outline">{item.serviceType === 'KHACH_SAN' ? 'hotel' : 'restaurant'}</span>{item.serviceType === 'KHACH_SAN' ? 'Khách sạn' : 'Nhà hàng'}</span><span className="flex items-center gap-1.5"><span className="material-symbols-outlined text-outline">schedule</span>Cập nhật: {new Date(item.updatedAt).toLocaleString('vi-VN')}</span></div>
              </div>
              <div className="flex items-center gap-8"><div className="hidden text-right lg:block"><p className="mb-1 text-xs text-outline">Trạng thái</p><span className="rounded-full bg-mint-green px-4 py-1.5 text-sm font-semibold text-on-secondary-container">{statusLabel(item.status)}</span></div><Link className="rounded-xl bg-primary px-6 py-3 text-sm font-semibold text-white hover:bg-secondary" to={`/user/complaints/${item.id}`}>Xem chi tiết</Link></div>
            </div>
          </div>
        ))}
      </div>

      {!loading && totalPages > 0 && (
        <nav className="flex items-center justify-center gap-1.5 pt-8">
          <button type="button" disabled={page <= 0} onClick={() => setPage((p) => Math.max(0, p - 1))} className="cursor-pointer rounded-lg border border-outline-variant/50 p-2 text-on-surface-variant transition hover:bg-surface-container-low disabled:cursor-not-allowed disabled:opacity-40">
            <ChevronLeft size={16} />
          </button>
          {Array.from({ length: totalPages }, (_, i) => i).map((p) => (
            <button key={p} type="button" onClick={() => setPage(p)} className={`flex h-10 w-10 cursor-pointer items-center justify-center rounded-lg text-sm font-bold transition ${p === page ? 'bg-primary text-on-primary shadow-sm' : 'border border-outline-variant/50 text-on-surface hover:bg-surface-container-low'}`}>
              {p + 1}
            </button>
          ))}
          <button type="button" disabled={page >= totalPages - 1} onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))} className="cursor-pointer rounded-lg border border-outline-variant/50 p-2 text-on-surface-variant transition hover:bg-surface-container-low disabled:cursor-not-allowed disabled:opacity-40">
            <ChevronRight size={16} />
          </button>
        </nav>
      )}
    </CustomerFeedbackLayout>
  );
}
