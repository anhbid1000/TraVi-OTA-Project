import type { ComplaintCategory, MucDoKhieuNai, TrangThaiKhieuNai } from '../../types/complaint';

type PartnerComplaintFiltersProps = {
  status: '' | TrangThaiKhieuNai;
  severity: '' | MucDoKhieuNai;
  category: '' | ComplaintCategory;
  onStatusChange: (status: '' | TrangThaiKhieuNai) => void;
  onSeverityChange: (severity: '' | MucDoKhieuNai) => void;
  onCategoryChange: (category: '' | ComplaintCategory) => void;
};

const STATUS_OPTIONS: Array<{ label: string; value: '' | TrangThaiKhieuNai }> = [
  { label: 'Tất cả', value: '' },
  { label: 'Chờ phản hồi', value: 'CHO_PHAN_HOI' },
  { label: 'Đang xử lý', value: 'DANG_XU_LY' },
  { label: 'Chờ xác nhận', value: 'CHO_XAC_NHAN_KHACH' },
  { label: 'Đang thực hiện', value: 'DANG_THUC_HIEN_PHUONG_AN' },
  { label: 'Đã giải quyết', value: 'DA_GIAI_QUYET' },
  { label: 'Đã đóng', value: 'DA_DONG' },
];

export function PartnerComplaintFilters({
  status,
  severity,
  category,
  onStatusChange,
  onSeverityChange,
  onCategoryChange,
}: PartnerComplaintFiltersProps) {
  return (
    <section className="rounded-xl border border-outline-variant/50 bg-white p-5 shadow-sm">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h3 className="font-display text-lg font-semibold text-on-surface">Bộ lọc khiếu nại</h3>
          <p className="text-sm text-on-surface-variant">Lọc theo trạng thái, mức độ và phân loại nghiệp vụ.</p>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          {/* Status Tabs */}
          <div className="flex flex-wrap gap-2">
            {STATUS_OPTIONS.map((opt) => (
              <button
                key={opt.value || 'ALL'}
                type="button"
                onClick={() => onStatusChange(opt.value)}
                className={`rounded-full px-4 py-2.5 text-sm font-semibold transition ${
                  status === opt.value
                    ? 'bg-primary text-white shadow-sm'
                    : 'bg-surface-container-high text-on-surface-variant hover:bg-mint-green hover:text-primary'
                }`}
              >
                {opt.label}
              </button>
            ))}
          </div>

          {/* Severity & Category Selects */}
          <div className="flex items-center gap-2">
            <select
              value={severity}
              onChange={(e) => onSeverityChange((e.target.value as '' | MucDoKhieuNai) || '')}
              className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm text-on-surface focus:border-primary focus:ring-2 focus:ring-primary/20"
            >
              <option value="">Tất cả mức độ</option>
              <option value="BINH_THUONG">Bình thường</option>
              <option value="NGHIEM_TRONG">Nghiêm trọng</option>
            </select>

            <select
              value={category}
              onChange={(e) => onCategoryChange((e.target.value as '' | ComplaintCategory) || '')}
              className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm text-on-surface focus:border-primary focus:ring-2 focus:ring-primary/20"
            >
              <option value="">Tất cả phân loại</option>
              <option value="ROOM_QUALITY">Chất lượng phòng</option>
              <option value="CLEANLINESS">Vệ sinh</option>
              <option value="SERVICE_ATTITUDE">Thái độ phục vụ</option>
              <option value="BILLING">Thanh toán</option>
              <option value="FOOD_QUALITY">Chất lượng ẩm thực</option>
              <option value="BOOKING_PROBLEM">Vấn đề đặt chỗ</option>
              <option value="FACILITY_PROBLEM">Vấn đề CSVC</option>
              <option value="OTHER">Khác</option>
            </select>
          </div>
        </div>
      </div>
    </section>
  );
}
