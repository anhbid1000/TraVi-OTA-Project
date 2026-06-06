import { useCallback, useEffect, useMemo, useState } from 'react';
import { complaintServiceV2 } from '../../../features/feedback-v2/services/complaintServiceV2';
import type { ComplaintCategory, ComplaintResponse, MucDoKhieuNai, TrangThaiKhieuNai } from '../../../features/feedback-v2/types/complaint';
import { PartnerComplaintFilters } from '../../../features/feedback-v2/components/partner/PartnerComplaintFilters';
import { PartnerComplaintTable } from '../../../features/feedback-v2/components/partner/PartnerComplaintTable';
import { PartnerPagination } from '../../../features/feedback-v2/components/partner/PartnerPagination';
import '../../dashboard.css';

export default function PartnerComplaintsPage() {
  const [items, setItems] = useState<ComplaintResponse[]>([]);
  const [status, setStatus] = useState<'' | TrangThaiKhieuNai>('');
  const [keyword, setKeyword] = useState('');
  const [severity, setSeverity] = useState<'' | MucDoKhieuNai>('');
  const [category, setCategory] = useState<'' | ComplaintCategory>('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await complaintServiceV2.getPartnerComplaints(
        page,
        10,
        status || undefined,
        severity || undefined,
        category || undefined,
        'updated_desc'
      );
      setItems(data.content ?? []);
      setTotalPages(data.totalPages ?? 1);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể tải danh sách khiếu nại.');
    } finally {
      setLoading(false);
    }
  }, [category, page, severity, status]);

  useEffect(() => {
    void load();
  }, [load]);

  const filtered = useMemo(() => {
    const q = keyword.trim().toLowerCase();
    if (!q) return items;
    return items.filter((item) => `${item.id} ${item.title} ${item.category}`.toLowerCase().includes(q));
  }, [items, keyword]);

  return (
    <div className="space-y-6">
      <header className="partner-page-header split">
        <div>
          <h1>Trung tâm khiếu nại</h1>
          <p>Quản lý ticket khiếu nại của cơ sở</p>
        </div>
        <div className="w-full max-w-md">
          <input
            className="w-full rounded-xl border border-outline-variant bg-white px-4 py-3 text-sm outline-none transition focus:ring-2 focus:ring-primary"
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="Tìm theo mã, tiêu đề hoặc phân loại..."
          />
        </div>
      </header>

      <section className="panel space-y-6">
        <PartnerComplaintFilters
          status={status}
          severity={severity}
          category={category}
          onStatusChange={(s) => { setPage(0); setStatus(s); }}
          onSeverityChange={(s) => { setPage(0); setSeverity(s); }}
          onCategoryChange={(c) => { setPage(0); setCategory(c); }}
        />

        {error && <div className="rounded-xl bg-error-container p-4 text-error">{error}</div>}

        <PartnerComplaintTable items={filtered} loading={loading} />

        {!loading && totalPages > 0 && <PartnerPagination page={page} totalPages={totalPages} onPageChange={setPage} />}
      </section>
    </div>
  );
}
