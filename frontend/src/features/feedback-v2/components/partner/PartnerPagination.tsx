import { ChevronLeft, ChevronRight } from 'lucide-react';

type PartnerPaginationProps = {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
};

export function PartnerPagination({ page, totalPages, onPageChange }: PartnerPaginationProps) {
  return (
    <nav className="flex items-center justify-center gap-1.5 pt-2">
      <button
        type="button"
        disabled={page <= 0}
        onClick={() => onPageChange(Math.max(0, page - 1))}
        className="rounded-lg border border-outline-variant/50 p-2 text-on-surface-variant transition hover:bg-surface-container-low disabled:cursor-not-allowed disabled:opacity-40"
      >
        <ChevronLeft size={16} />
      </button>
      {Array.from({ length: totalPages }, (_, i) => i).map((p) => (
        <button
          key={p}
          type="button"
          onClick={() => onPageChange(p)}
          className={`flex h-10 w-10 items-center justify-center rounded-lg text-sm font-bold transition ${
            p === page ? 'bg-primary text-white shadow-sm' : 'border border-outline-variant/50 text-on-surface hover:bg-surface-container-low'
          }`}
        >
          {p + 1}
        </button>
      ))}
      <button
        type="button"
        disabled={page >= totalPages - 1}
        onClick={() => onPageChange(Math.min(totalPages - 1, page + 1))}
        className="rounded-lg border border-outline-variant/50 p-2 text-on-surface-variant transition hover:bg-surface-container-low disabled:cursor-not-allowed disabled:opacity-40"
      >
        <ChevronRight size={16} />
      </button>
    </nav>
  );
}
