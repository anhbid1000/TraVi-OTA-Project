import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { CustomerFeedbackLayout } from '../../../features/feedback-v2/components/CustomerFeedbackLayout';
import { complaintServiceV2 } from '../../../features/feedback-v2/services/complaintServiceV2';
import type { ComplaintResponse } from '../../../features/feedback-v2/types/complaint';

function statusLabel(status: ComplaintResponse['status']) {
  return ({ CHO_PHAN_HOI: 'Chờ phản hồi', DANG_XU_LY: 'Đang xử lý', DA_GIAI_QUYET: 'Đã giải quyết', DA_DONG: 'Đã đóng' } as const)[status];
}

function severityLabel(severity: ComplaintResponse['severity']) {
  return severity === 'NGHIEM_TRONG' ? 'Nghiêm trọng' : 'Bình thường';
}

export default function MyComplaintDetailPage() {
  const navigate = useNavigate();
  const { complaintId = '' } = useParams();
  const id = complaintId;
  const [complaint, setComplaint] = useState<ComplaintResponse | null>(null);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [closing, setClosing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadDetail = async () => {
    try {
      setLoading(true);
      setError(null);
      setComplaint(await complaintServiceV2.getCustomerComplaintDetail(id));
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'Không thể tải chi tiết khiếu nại.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id) void loadDetail();
  }, [id]);

  const closed = complaint?.status === 'DA_DONG';
  const messages = useMemo(() => complaint?.messages ?? [], [complaint]);

  const handleSend = async () => {
    if (!message.trim() || !complaint || closed) return;
    try {
      setSending(true);
      await complaintServiceV2.postCustomerMessage(complaint.id, { noiDung: message.trim() });
      setMessage('');
      await loadDetail();
    } catch (sendError) {
      setError(sendError instanceof Error ? sendError.message : 'Không thể gửi tin nhắn.');
    } finally {
      setSending(false);
    }
  };

  const handleCloseTicket = async () => {
    if (!complaint || closed) return;
    try {
      setClosing(true);
      await complaintServiceV2.closeComplaint(complaint.id);
      await loadDetail();
    } catch (closeError) {
      setError(closeError instanceof Error ? closeError.message : 'Không thể đóng khiếu nại.');
    } finally {
      setClosing(false);
    }
  };

  return (
    <CustomerFeedbackLayout active="complaints" title="Chi tiết khiếu nại" subtitle="Trao đổi trực tiếp với đối tác và theo dõi tiến độ xử lý.">
      {loading && <div className="rounded-xl bg-white p-8 text-center font-semibold">Đang tải chi tiết khiếu nại...</div>}
      {error && <div className="rounded-xl bg-error-container p-4 text-error">{error}</div>}
      {!loading && !complaint && <div className="rounded-xl bg-white p-8 text-center">Không tìm thấy khiếu nại.</div>}

      {complaint && (
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
          <div className="flex flex-col gap-6 lg:col-span-8">
            <div className="rounded-xl border border-outline-variant bg-white p-6 shadow-sm">
              <div className="mb-4 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div>
                  <div className="mb-1 flex items-center gap-3">
                    <span className="rounded bg-surface-container px-2 py-1 text-xs text-on-surface-variant">#{complaint.id}</span>
                    <span className="rounded-full bg-mint-green px-3 py-1 text-xs font-bold uppercase text-on-secondary-container">{statusLabel(complaint.status)}</span>
                  </div>
                  <h2 className="font-display text-2xl font-bold text-primary">{complaint.title}</h2>
                </div>
                <span className={`rounded-full px-3 py-1 text-sm font-semibold ${complaint.severity === 'NGHIEM_TRONG' ? 'bg-error-container text-error' : 'bg-surface-container text-on-surface-variant'}`}>Mức độ: {severityLabel(complaint.severity)}</span>
              </div>
              <div className="grid gap-4 border-t border-outline-variant/30 pt-4 md:grid-cols-2">
                <div className="flex items-center gap-3"><div className="flex h-10 w-10 items-center justify-center rounded-lg bg-surface-container"><span className="material-symbols-outlined text-secondary">{complaint.serviceType === 'KHACH_SAN' ? 'hotel' : 'restaurant'}</span></div><div><p className="text-xs text-on-surface-variant">Loại dịch vụ</p><p className="text-sm font-semibold text-on-surface">{complaint.serviceType === 'KHACH_SAN' ? 'Khách sạn' : 'Nhà hàng'}</p></div></div>
                <div className="flex items-center gap-3"><div className="flex h-10 w-10 items-center justify-center rounded-lg bg-surface-container"><span className="material-symbols-outlined text-secondary">calendar_today</span></div><div><p className="text-xs text-on-surface-variant">Ngày tạo</p><p className="text-sm font-semibold text-on-surface">{new Date(complaint.createdAt).toLocaleString('vi-VN')}</p></div></div>
              </div>
            </div>

            <div className="flex h-[600px] flex-col overflow-hidden rounded-xl border border-outline-variant bg-white shadow-sm">
              <div className="flex-grow space-y-6 overflow-y-auto bg-surface-container-low/30 p-6">
                {messages.map((item, index) => {
                  const mine = item.senderRole === 'KHACH_HANG';
                  return (
                    <div key={`${item.createdAt}-${index}`} className={`flex max-w-[85%] items-start gap-3 ${mine ? 'ml-auto flex-row-reverse self-end' : ''}`}>
                      <div className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-xs font-bold text-white ${mine ? 'bg-primary' : 'bg-secondary'}`}>{mine ? 'ME' : 'DT'}</div>
                      <div className={`flex flex-col gap-1 ${mine ? 'items-end' : ''}`}>
                        <div className={`flex items-baseline gap-2 ${mine ? 'flex-row-reverse' : ''}`}><span className="text-sm font-semibold text-on-surface">{mine ? 'Bạn' : 'Đối tác'}</span><span className="text-[10px] text-on-surface-variant">{new Date(item.createdAt).toLocaleString('vi-VN')}</span></div>
                        <div className={`rounded-2xl p-4 shadow-sm ${mine ? 'rounded-tr-none bg-primary text-white' : 'rounded-tl-none border border-outline-variant/50 bg-white text-on-surface-variant'}`}>{item.content}</div>
                      </div>
                    </div>
                  );
                })}
              </div>

              <div className="border-t border-outline-variant p-4">
                {closed && <div className="mb-3 flex items-center justify-center gap-2 rounded-lg border border-dashed border-outline bg-surface-container p-3 text-on-surface-variant"><span className="material-symbols-outlined text-sm">lock</span><span className="text-sm font-semibold">Khiếu nại đã đóng - không thể gửi thêm tin nhắn</span></div>}
                <div className="flex items-center gap-3">
                  <textarea className="h-12 max-h-32 flex-grow resize-none rounded-xl bg-surface-container-low px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary disabled:opacity-60" disabled={closed || sending} placeholder="Nhập phản hồi của bạn..." value={message} onChange={(event) => setMessage(event.target.value)} />
                  <button className="flex items-center justify-center rounded-xl bg-primary p-3 text-white shadow-md disabled:opacity-60" disabled={closed || sending || !message.trim()} onClick={handleSend} type="button"><span className="material-symbols-outlined">send</span></button>
                </div>
              </div>
            </div>

            <div className="flex items-center justify-between">
              <button className="flex items-center gap-2 font-semibold text-on-surface-variant transition hover:text-primary" onClick={() => navigate('/user/complaints')} type="button"><span className="material-symbols-outlined">arrow_back</span>Quay lại danh sách</button>
              <button className="flex items-center gap-2 rounded-xl bg-error px-6 py-2.5 font-bold text-white disabled:opacity-60" disabled={closed || closing} onClick={handleCloseTicket} type="button"><span className="material-symbols-outlined">check_circle</span>{closing ? 'Đang đóng...' : 'Đóng ticket'}</button>
            </div>
          </div>

          <div className="flex flex-col gap-6 lg:col-span-4">
            <div className="sticky top-28 overflow-hidden rounded-xl border border-outline-variant bg-white shadow-sm">
              <div className="border-b border-outline-variant/30 bg-surface-container-lowest p-5"><h3 className="font-display text-xl font-semibold text-primary">Thông tin ticket</h3></div>
              <div className="flex flex-col gap-5 p-5">
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Mã ticket</label><p className="mt-1 text-sm font-semibold text-on-surface">{complaint.id}</p></div>
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Trạng thái</label><p className="mt-1 text-sm font-semibold text-on-surface">{statusLabel(complaint.status)}</p></div>
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Mức độ</label><p className="mt-1 text-sm font-semibold text-on-surface">{severityLabel(complaint.severity)}</p></div>
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Quá hạn SLA</label><p className={`mt-1 text-sm font-semibold ${complaint.overdue ? 'text-error' : 'text-secondary'}`}>{complaint.overdue ? 'Có' : 'Không'}</p></div>
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Cập nhật lần cuối</label><p className="mt-1 text-sm font-semibold text-on-surface">{new Date(complaint.updatedAt).toLocaleString('vi-VN')}</p></div>
                <Link className="rounded-xl border border-outline px-4 py-3 text-center text-sm font-semibold text-primary transition hover:bg-surface-container-low" to="/user/complaints">Về danh sách khiếu nại</Link>
              </div>
            </div>
          </div>
        </div>
      )}
    </CustomerFeedbackLayout>
  );
}
