import { Link } from 'react-router-dom';
import type { ComplaintResponse, TrangThaiKhieuNai } from '../../types/complaint';
import { shouldShowOverdueBadge } from '../../../../utils/sla';

type PartnerComplaintTableProps = {
  items: ComplaintResponse[];
  loading: boolean;
};

const statusMap: Record<TrangThaiKhieuNai, string> = {
  CHO_PHAN_HOI: 'Chờ phản hồi',
  DANG_XU_LY: 'Đang xử lý',
  CHO_XAC_NHAN_KHACH: 'Chờ xác nhận',
  DANG_THUC_HIEN_PHUONG_AN: 'Đang thực hiện',
  DA_GIAI_QUYET: 'Đã giải quyết',
  DA_DONG: 'Đã đóng',
};

function severityLabel(severity: ComplaintResponse['severity']) {
  return severity === 'NGHIEM_TRONG' ? 'Nghiêm trọng' : 'Bình thường';
}

function categoryLabel(category: ComplaintResponse['category']) {
  return ({
    ROOM_QUALITY: 'Chất lượng phòng',
    CLEANLINESS: 'Vệ sinh',
    SERVICE_ATTITUDE: 'Thái độ phục vụ',
    BILLING: 'Thanh toán',
    FOOD_QUALITY: 'Chất lượng ẩm thực',
    BOOKING_PROBLEM: 'Vấn đề đặt chỗ',
    FACILITY_PROBLEM: 'Vấn đề CSVC',
    OTHER: 'Khác',
  } as const)[category];
}

export function PartnerComplaintTable({ items, loading }: PartnerComplaintTableProps) {
  return (
    <section className="bg-white rounded-xl border border-outline-variant/50 shadow-sm overflow-hidden flex flex-col">
      {/* Table Header Title */}
      <div className="p-6 border-b border-outline-variant/30 flex justify-between items-center bg-white">
        <h3 className="font-headline-sm text-lg font-bold text-primary">Danh sách khiếu nại ưu tiên</h3>
      </div>

      {/* Table Body */}
      <div className="w-full overflow-x-auto">
        <div className="min-w-[900px] w-full">
          {/* Header Row */}
          <div className="grid grid-cols-12 gap-4 px-6 py-4 bg-surface-container-low border-b border-outline-variant/30 text-xs font-bold uppercase tracking-wider text-on-surface-variant">
            <div className="col-span-2">Mã vụ việc</div>
            <div className="col-span-3">Khách hàng / Tiêu đề</div>
            <div className="col-span-2">Phân loại</div>
            <div className="col-span-2">Mức độ</div>
            <div className="col-span-2">Trạng thái</div>
            <div className="col-span-1 text-right">Chi tiết</div>
          </div>

          {loading && (
            <div className="px-6 py-12 text-center text-sm font-semibold text-on-surface-variant">
              Đang tải danh sách khiếu nại...
            </div>
          )}

          {!loading && items.length === 0 && (
            <div className="px-6 py-12 text-center text-sm font-semibold text-on-surface-variant">
              Hiện tại cơ sở chưa nhận vụ việc khiếu nại nào phù hợp.
            </div>
          )}

          {/* Rows */}
          {!loading && items.map((item) => {
            const isHigh = item.severity === 'NGHIEM_TRONG';
            return (
              <div
                key={item.id}
                className={`grid grid-cols-12 gap-4 px-6 py-4 items-center border-b border-outline-variant/20 hover:bg-surface-container-low/50 transition-colors ${
                  isHigh ? 'bg-red-50/20' : ''
                }`}
              >
                {/* Case ID */}
                <div className="col-span-2">
                  <span className="font-body-sm text-sm font-bold text-on-surface block">
                    #{item.id.toUpperCase()}
                  </span>
                  <div className="flex items-center gap-2 mt-0.5">
                    <span className="text-[10px] text-on-surface-variant">
                      {new Date(item.createdAt).toLocaleDateString('vi-VN')}
                    </span>
                    {shouldShowOverdueBadge(item) && (
                      <span className="rounded bg-red-100 px-2 py-0.5 text-[10px] font-bold text-red-700">Quá hạn 48h</span>
                    )}
                  </div>
                </div>

                {/* Title & Customer Fallback */}
                <div className="col-span-3 flex items-center gap-3">
                  <div className="w-8 h-8 rounded-full bg-emerald-100 flex items-center justify-center font-bold text-xs text-primary shrink-0">
                    KH
                  </div>
                  <div className="overflow-hidden">
                    <span className="font-body-md text-sm font-semibold text-on-surface block truncate" title={item.title}>
                      {item.title}
                    </span>
                    <span className="text-[10px] text-on-surface-variant block uppercase">
                      {item.serviceType === 'KHACH_SAN' ? 'Khách sạn' : 'Nhà hàng'}
                    </span>
                  </div>
                </div>

                {/* Category */}
                <div className="col-span-2 font-body-sm text-sm text-on-surface-variant">
                  {categoryLabel(item.category)}
                </div>

                {/* Severity Badge */}
                <div className="col-span-2">
                  <span
                    className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-md text-xs font-semibold ${
                      isHigh
                        ? 'bg-red-100 text-red-800'
                        : 'bg-emerald-100 text-emerald-800'
                    }`}
                  >
                    <span className={`w-1.5 h-1.5 rounded-full ${isHigh ? 'bg-red-600' : 'bg-emerald-600'}`} />
                    {severityLabel(item.severity)}
                  </span>
                </div>

                {/* Status Badge */}
                <div className="col-span-2">
                  <span className="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-semibold border border-primary text-primary bg-white">
                    {statusMap[item.status]}
                  </span>
                </div>

                {/* Detail Action */}
                <div className="col-span-1 text-right">
                  <Link
                    to={`/partner/complaints/${item.id}`}
                    className="p-2 inline-flex items-center justify-center rounded-full hover:bg-surface-variant text-primary transition-colors"
                  >
                    <span className="material-symbols-outlined text-[20px]">chevron_right</span>
                  </Link>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
