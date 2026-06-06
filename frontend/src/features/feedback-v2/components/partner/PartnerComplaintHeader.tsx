import type { ComplaintResponse } from '../../types/complaint';

type PartnerComplaintHeaderProps = {
  complaint: ComplaintResponse;
};

function severityLabel(severity: ComplaintResponse['severity']) {
  return severity === 'NGHIEM_TRONG' ? 'Ưu tiên cao' : 'Bình thường';
}

const statusMap: Record<ComplaintResponse['status'], string> = {
  CHO_PHAN_HOI: 'Chờ phản hồi',
  DANG_XU_LY: 'Đang xử lý',
  CHO_XAC_NHAN_KHACH: 'Chờ xác nhận',
  DANG_THUC_HIEN_PHUONG_AN: 'Đang thực hiện',
  DA_GIAI_QUYET: 'Đã giải quyết',
  DA_DONG: 'Đã đóng',
};

export function PartnerComplaintHeader({ complaint }: PartnerComplaintHeaderProps) {
  return (
    <section className="flex flex-col justify-between gap-4 border-b border-surface-variant pb-4 sm:flex-row sm:items-end">
      <div>
        <div className="mb-2 flex flex-wrap items-center gap-2">
          <span
            className={`rounded px-2 py-0.5 text-xs font-bold uppercase ${
              complaint.severity === 'NGHIEM_TRONG' ? 'bg-error-container text-error' : 'bg-surface-container text-on-surface-variant'
            }`}
          >
            {severityLabel(complaint.severity)}
          </span>
          <span className="text-xs font-semibold text-on-surface-variant">Ticket #{complaint.id.slice(0, 8)}</span>
          <span className="rounded bg-primary-container px-2 py-0.5 text-xs font-semibold text-white">{statusMap[complaint.status]}</span>
        </div>
        <h1 className="font-display text-3xl font-bold text-primary">{complaint.title}</h1>
        <p className="mt-2 max-w-3xl text-sm text-on-surface-variant">
          Xử lý ticket theo quy trình: nhắn tin → đề xuất phương án → khách xác nhận → thực hiện → hoàn tất.
        </p>
      </div>
    </section>
  );
}
