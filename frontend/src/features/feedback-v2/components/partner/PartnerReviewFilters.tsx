import type { TrangThaiDanhGia } from '../../types/review';

type PartnerReviewFiltersProps = {
  rating: '' | number;
  status: '' | TrangThaiDanhGia;
  onRatingChange: (rating: '' | number) => void;
  onStatusChange: (status: '' | TrangThaiDanhGia) => void;
};

export function PartnerReviewFilters({ rating, status, onRatingChange, onStatusChange }: PartnerReviewFiltersProps) {
  return (
    <section className="rounded-xl border border-outline-variant/50 bg-white p-5 shadow-sm">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h3 className="font-display text-lg font-semibold text-on-surface">Bộ lọc đánh giá</h3>
          <p className="text-sm text-on-surface-variant">Lọc nhanh theo số sao và trạng thái hiển thị.</p>
        </div>

        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:min-w-[420px]">
          <select
            value={rating}
            onChange={(e) => onRatingChange(e.target.value ? Number(e.target.value) : '')}
            className="h-11 rounded-lg border border-outline-variant bg-white px-3 text-sm font-semibold text-on-surface outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
          >
            <option value="">Tất cả số sao</option>
            <option value="5">5 sao</option>
            <option value="4">4 sao</option>
            <option value="3">3 sao</option>
            <option value="2">2 sao</option>
            <option value="1">1 sao</option>
          </select>

          <select
            value={status}
            onChange={(e) => onStatusChange((e.target.value as '' | TrangThaiDanhGia) || '')}
            className="h-11 rounded-lg border border-outline-variant bg-white px-3 text-sm font-semibold text-on-surface outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
          >
            <option value="">Tất cả trạng thái</option>
            <option value="DA_HIEN_THI">Đang hiển thị</option>
            <option value="BI_AN">Bị ẩn</option>
          </select>
        </div>
      </div>
    </section>
  );
}
