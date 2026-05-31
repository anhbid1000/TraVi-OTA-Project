import { useEffect, useMemo, useState } from 'react';
import { complaintServiceV2 } from '../../../features/feedback-v2/services/complaintServiceV2';
import type { ComplaintCategory, ComplaintResponse, MucDoKhieuNai, TrangThaiKhieuNai } from '../../../features/feedback-v2/types/complaint';
import { PartnerSidebar } from '../../../features/feedback-v2/components/partner/PartnerSidebar';
import { PartnerTopbar } from '../../../features/feedback-v2/components/partner/PartnerTopbar';
import { PartnerComplaintFilters } from '../../../features/feedback-v2/components/partner/PartnerComplaintFilters';
import { PartnerComplaintTable } from '../../../features/feedback-v2/components/partner/PartnerComplaintTable';
import { PartnerPagination } from '../../../features/feedback-v2/components/partner/PartnerPagination';

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

  const load = async () => {
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
  };

  useEffect(() => {
    void load();
  }, [page, status, severity, category]);

  const filtered = useMemo(() => {
    const q = keyword.trim().toLowerCase();
    if (!q) return items;
    return items.filter((item) => `${item.id} ${item.title} ${item.category}`.toLowerCase().includes(q));
  }, [items, keyword]);

  return (
    <div className="flex min-h-screen bg-background text-on-background">
      <PartnerSidebar active="complaints" />

      <div className="flex-1 md:ml-64">
        <PartnerTopbar
          title="Trung tâm khiếu nại"
          subtitle="Quản lý ticket khiếu nại của cơ sở"
          searchValue={keyword}
          onSearchChange={setKeyword}
          searchPlaceholder="Tìm theo mã, tiêu đề hoặc phân loại..."
        />

        <main className="mx-auto w-full max-w-7xl space-y-6 p-5 md:p-8">
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
        </main>
      </div>
    </div>
  );
}
