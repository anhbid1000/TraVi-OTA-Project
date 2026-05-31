import { useEffect, useMemo, useState } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { CustomerFeedbackLayout } from '../../../features/feedback-v2/components/CustomerFeedbackLayout';
import { ComplaintCreateModal } from '../../../features/feedback-v2/components/ComplaintCreateModal';
import { ReviewCreateModal } from '../../../features/feedback-v2/components/ReviewCreateModal';
import { bookingServiceV2, type BookingHistoryItemV2 } from '../../../features/feedback-v2/services/bookingServiceV2';

const PAGE_SIZE = 6;

type ServiceFilter = 'ALL' | 'KHACH_SAN' | 'NHA_HANG';
type BookingSort = 'newest' | 'oldest' | 'price_desc' | 'price_asc';

const SERVICE_FILTERS: Array<{ label: string; value: ServiceFilter }> = [
  { label: 'Tất cả', value: 'ALL' },
  { label: 'Hotel', value: 'KHACH_SAN' },
  { label: 'Nhà hàng', value: 'NHA_HANG' },
];

const SORT_OPTIONS: Array<{ label: string; value: BookingSort }> = [
  { label: 'Mới nhất', value: 'newest' },
  { label: 'Cũ nhất', value: 'oldest' },
  { label: 'Giá cao đến thấp', value: 'price_desc' },
  { label: 'Giá thấp đến cao', value: 'price_asc' },
];

function formatPrice(value: number) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
}

function serviceIcon(serviceType: string) {
  return serviceType === 'NHA_HANG' ? 'restaurant' : 'hotel';
}

function statusLabel(status: string) {
  const normalized = status.toUpperCase();
  const labels: Record<string, string> = {
    DA_HOAN_THANH: 'Đã hoàn tất',
    CHO_THANH_TOAN: 'Chờ thanh toán',
    DA_THANH_TOAN: 'Đã thanh toán',
    CHO_XAC_NHAN: 'Chờ xác nhận',
    DA_HUY: 'Đã hủy',
  };
  return labels[normalized] ?? status;
}

function statusClass(status: string) {
  const normalized = status.toUpperCase();
  if (normalized === 'DA_HOAN_THANH' || normalized === 'COMPLETED') return 'bg-mint-green text-on-secondary-container';
  if (normalized === 'DA_HUY') return 'bg-error-container text-error';
  return 'bg-tertiary-fixed text-on-tertiary-fixed-variant';
}

function isCompleted(status: string) {
  const normalized = status.toUpperCase();
  return normalized === 'DA_HOAN_THANH' || normalized === 'COMPLETED';
}

function sortBookings(items: BookingHistoryItemV2[], sort: BookingSort) {
  const copied = [...items];
  switch (sort) {
    case 'oldest':
      return copied.reverse();
    case 'price_desc':
      return copied.sort((a, b) => b.totalPrice - a.totalPrice);
    case 'price_asc':
      return copied.sort((a, b) => a.totalPrice - b.totalPrice);
    case 'newest':
    default:
      return copied;
  }
}

export default function MyBookingsV2() {
  const [bookings, setBookings] = useState<BookingHistoryItemV2[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [keyword, setKeyword] = useState('');
  const [serviceFilter, setServiceFilter] = useState<ServiceFilter>('ALL');
  const [sort, setSort] = useState<BookingSort>('newest');
  const [page, setPage] = useState(0);
  const [selectedBooking, setSelectedBooking] = useState<BookingHistoryItemV2 | null>(null);
  const [reviewOpen, setReviewOpen] = useState(false);
  const [complaintOpen, setComplaintOpen] = useState(false);

  const loadBookings = async () => {
    try {
      setLoading(true);
      setError(null);
      setBookings(await bookingServiceV2.getCustomerBookings());
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'Không thể tải danh sách chuyến đi.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadBookings();
  }, []);

  useEffect(() => {
    setPage(0);
  }, [keyword, serviceFilter, sort]);

  const filteredBookings = useMemo(() => {
    const normalized = keyword.trim().toLowerCase();
    const byKeyword = normalized
      ? bookings.filter((booking) => `${booking.id} ${booking.serviceName}`.toLowerCase().includes(normalized))
      : bookings;
    const byService = serviceFilter === 'ALL' ? byKeyword : byKeyword.filter((booking) => booking.serviceType === serviceFilter);
    return sortBookings(byService, sort);
  }, [bookings, keyword, serviceFilter, sort]);

  const totalPages = Math.max(1, Math.ceil(filteredBookings.length / PAGE_SIZE));
  const currentPage = Math.min(page, totalPages - 1);
  const pagedBookings = filteredBookings.slice(currentPage * PAGE_SIZE, currentPage * PAGE_SIZE + PAGE_SIZE);

  const openReview = (booking: BookingHistoryItemV2) => {
    setSelectedBooking(booking);
    setReviewOpen(true);
  };

  const openComplaint = (booking: BookingHistoryItemV2) => {
    setSelectedBooking(booking);
    setComplaintOpen(true);
  };

  return (
    <CustomerFeedbackLayout active="bookings" title="Chuyến đi của tôi" subtitle="Quản lý đơn đã đặt, viết đánh giá và tạo khiếu nại khi cần.">
      <div className="flex flex-col gap-6">
        <section className="rounded-3xl border border-outline-variant/40 bg-white p-5 shadow-sm">
          <div className="grid grid-cols-1 gap-4 lg:grid-cols-12 lg:items-center">
            <div className="relative lg:col-span-6">
              <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-outline">search</span>
              <input
                className="w-full rounded-xl border border-outline-variant bg-white py-3.5 pl-12 pr-4 text-sm outline-none transition focus:ring-2 focus:ring-primary"
                placeholder="Tìm theo mã đơn hoặc tên dịch vụ"
                value={keyword}
                onChange={(event) => setKeyword(event.target.value)}
              />
            </div>

            <div className="lg:col-span-3">
              <select
                className="w-full cursor-pointer rounded-xl border border-outline-variant bg-white px-4 py-3.5 text-sm font-semibold text-on-surface outline-none transition focus:ring-2 focus:ring-primary"
                value={sort}
                onChange={(event) => setSort(event.target.value as BookingSort)}
                aria-label="Sắp xếp chuyến đi"
              >
                {SORT_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>{option.label}</option>
                ))}
              </select>
            </div>

            <div className="flex flex-wrap justify-start gap-2 lg:col-span-3 lg:justify-end">
              {SERVICE_FILTERS.map((option) => (
                <button
                  key={option.value}
                  className={`rounded-full px-4 py-2.5 text-sm font-semibold transition ${serviceFilter === option.value ? 'bg-primary text-white shadow-sm' : 'bg-surface-container-high text-on-surface-variant hover:bg-mint-green hover:text-primary'}`}
                  type="button"
                  onClick={() => setServiceFilter(option.value)}
                >
                  {option.label}
                </button>
              ))}
            </div>
          </div>
        </section>

        {loading && <div className="rounded-xl bg-white p-8 text-center font-semibold text-on-surface">Đang tải danh sách chuyến đi...</div>}
        {error && <div className="rounded-xl bg-error-container p-4 text-error">{error}</div>}

        <div className="space-y-4">
          {!loading && pagedBookings.length === 0 && (
            <div className="rounded-3xl border-2 border-dashed border-outline-variant bg-white py-20 text-center">
              <h3 className="font-display text-2xl font-bold text-primary">Chưa có chuyến đi phù hợp</h3>
              <p className="mt-2 text-on-surface-variant">Thử đổi bộ lọc, từ khóa hoặc trạng thái sắp xếp.</p>
            </div>
          )}

          {pagedBookings.map((booking) => {
            const completed = isCompleted(booking.status);
            return (
              <article key={booking.id} className="group rounded-xl border border-outline-variant/50 bg-white p-6 shadow-sm transition hover:shadow-md">
                <div className="flex flex-col justify-between gap-6 lg:flex-row">
                  <div className="flex gap-4">
                    <div className="flex h-24 w-24 shrink-0 items-center justify-center overflow-hidden rounded-lg bg-surface-container">
                      {booking.thumbnailUrl ? <img className="h-full w-full object-cover transition duration-500 group-hover:scale-110" src={booking.thumbnailUrl} alt={booking.serviceName} /> : <span className="material-symbols-outlined text-4xl text-primary">{serviceIcon(booking.serviceType)}</span>}
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="material-symbols-outlined text-sm text-primary">{serviceIcon(booking.serviceType)}</span>
                        <span className="text-xs text-on-surface-variant">ID: {booking.id}</span>
                      </div>
                      <h3 className="font-display text-xl font-semibold text-on-surface">{booking.serviceName}</h3>
                      <p className="flex items-center gap-1 text-sm text-on-surface-variant"><span className="material-symbols-outlined text-base">calendar_today</span>{booking.dateLabel}</p>
                      <span className={`mt-2 inline-flex rounded px-2 py-0.5 text-xs font-semibold ${statusClass(booking.status)}`}>{statusLabel(booking.status)}</span>
                    </div>
                  </div>

                  <div className="flex flex-col items-end justify-between gap-4">
                    <div className="text-right">
                      <p className="text-xs uppercase tracking-wider text-on-surface-variant">Tổng tiền</p>
                      <p className="font-display text-2xl font-semibold text-primary">{formatPrice(booking.totalPrice)}</p>
                    </div>
                    <div className="flex flex-wrap justify-end gap-2">
                      {completed && <button className="rounded-lg border border-outline px-4 py-2 text-sm font-semibold text-on-surface-variant hover:bg-surface-container-low" onClick={() => openComplaint(booking)} type="button">Khiếu nại</button>}
                      {completed && !booking.reviewed && <button className="rounded-lg bg-primary px-5 py-2 text-sm font-semibold text-white hover:bg-secondary" onClick={() => openReview(booking)} type="button">Viết đánh giá</button>}
                      {completed && booking.reviewed && <span className="flex items-center gap-1 px-5 py-2 text-sm font-semibold italic text-on-surface-variant"><span className="material-symbols-outlined text-lg">verified</span>Đã đánh giá</span>}
                    </div>
                  </div>
                </div>
              </article>
            );
          })}
        </div>

        {!loading && filteredBookings.length > 0 && (
          <nav className="flex items-center justify-center gap-1.5 pt-2">
            <button
              type="button"
              disabled={currentPage <= 0}
              onClick={() => setPage(Math.max(0, currentPage - 1))}
              className="cursor-pointer rounded-lg border border-outline-variant/50 p-2 text-on-surface-variant transition hover:bg-surface-container-low disabled:cursor-not-allowed disabled:opacity-40"
            >
              <ChevronLeft size={16} />
            </button>
            {Array.from({ length: totalPages }, (_, i) => i).map((p) => (
              <button
                key={p}
                type="button"
                onClick={() => setPage(p)}
                className={`flex h-10 w-10 cursor-pointer items-center justify-center rounded-lg text-sm font-bold transition ${p === currentPage ? 'bg-primary text-on-primary shadow-sm' : 'border border-outline-variant/50 text-on-surface hover:bg-surface-container-low'}`}
              >
                {p + 1}
              </button>
            ))}
            <button
              type="button"
              disabled={currentPage >= totalPages - 1}
              onClick={() => setPage(Math.min(totalPages - 1, currentPage + 1))}
              className="cursor-pointer rounded-lg border border-outline-variant/50 p-2 text-on-surface-variant transition hover:bg-surface-container-low disabled:cursor-not-allowed disabled:opacity-40"
            >
              <ChevronRight size={16} />
            </button>
          </nav>
        )}
      </div>

      {selectedBooking && (
        <ReviewCreateModal
          open={reviewOpen}
          onClose={() => setReviewOpen(false)}
          serviceName={selectedBooking.serviceName}
          serviceType={selectedBooking.serviceType}
          bookingId={selectedBooking.bookingId}
          reservationId={selectedBooking.reservationId}
          onSuccess={loadBookings}
        />
      )}
      {selectedBooking && (
        <ComplaintCreateModal
          open={complaintOpen}
          onClose={() => setComplaintOpen(false)}
          serviceName={selectedBooking.serviceName}
          serviceType={selectedBooking.serviceType}
          bookingId={selectedBooking.bookingId}
          reservationId={selectedBooking.reservationId}
          onSuccess={loadBookings}
        />
      )}

    </CustomerFeedbackLayout>
  );
}
