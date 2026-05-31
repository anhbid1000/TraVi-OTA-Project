import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { CustomerFeedbackLayout } from '../../../features/feedback-v2/components/CustomerFeedbackLayout';
import { complaintServiceV2 } from '../../../features/feedback-v2/services/complaintServiceV2';
import { api } from '../../../services/api';
import type {
  ComplaintMessageCreateRequest,
  ComplaintResponse,
  ResolutionActionRejectRequest,
  ResolutionActionResponse,
  ComplaintMessageResponse,
  ComplaintActivityResponse,
  ComplaintResolutionActionType,
  ComplaintCategory,
  TrangThaiKhieuNai,
} from '../../../features/feedback-v2/types/complaint';
import { shouldShowOverdueBadge } from '../../../utils/sla';

const statusMap: Record<TrangThaiKhieuNai, string> = {
  CHO_PHAN_HOI: 'Chờ phản hồi',
  DANG_XU_LY: 'Đang xử lý',
  CHO_XAC_NHAN_KHACH: 'Chờ xác nhận khách',
  DANG_THUC_HIEN_PHUONG_AN: 'Đang thực hiện phương án',
  DA_GIAI_QUYET: 'Đã giải quyết',
  DA_DONG: 'Đã đóng',
};

const categoryMap: Record<ComplaintCategory, string> = {
  ROOM_QUALITY: 'Chất lượng phòng',
  CLEANLINESS: 'Vệ sinh',
  SERVICE_ATTITUDE: 'Thái độ phục vụ',
  BILLING: 'Thanh toán / Hóa đơn',
  FOOD_QUALITY: 'Chất lượng ẩm thực',
  BOOKING_PROBLEM: 'Vấn đề đặt chỗ',
  FACILITY_PROBLEM: 'Vấn đề cơ sở vật chất',
  OTHER: 'Khác',
};

const actionTypeMap: Record<ComplaintResolutionActionType, string> = {
  FULL_REFUND: 'Hoàn tiền toàn bộ',
  PARTIAL_REFUND: 'Hoàn tiền một phần',
  VOUCHER: 'Voucher',
  DISCOUNT_NEXT_BOOKING: 'Giảm giá lần đặt sau',
  CHANGE_ROOM: 'Đổi phòng',
  OTHER_SOLUTION: 'Phương án khác',
  REJECT_COMPLAINT: 'Từ chối khiếu nại',
};

function severityLabel(severity: ComplaintResponse['severity']) {
  return severity === 'NGHIEM_TRONG' ? 'Nghiêm trọng' : 'Bình thường';
}


function roleLabel(role: ComplaintMessageResponse['senderRole'] | ComplaintActivityResponse['actorRole']) {
  if (role === 'KHACH_HANG') return 'Khách hàng';
  if (role === 'DOI_TAC') return 'Đối tác';
  return 'Hệ thống';
}

function activityLabel(type: ComplaintActivityResponse['activityType']) {
  const map: Record<string, string> = {
    COMPLAINT_CREATED: 'Tạo khiếu nại',
    CUSTOMER_MESSAGE_SENT: 'Khách gửi tin nhắn',
    PARTNER_MESSAGE_SENT: 'Đối tác gửi tin nhắn',
    COMPLAINT_REOPENED: 'Mở lại khiếu nại',
    COMPLAINT_CLOSED: 'Đóng khiếu nại',
    ACTION_PROPOSED: 'Đề xuất phương án',
    ACTION_ACCEPTED: 'Khách chấp nhận phương án',
    ACTION_REJECTED: 'Khách từ chối phương án',
  };
  return map[type] ?? type;
}

export default function MyComplaintDetailPage() {
  const navigate = useNavigate();
  const { complaintId = '' } = useParams();
  const [complaint, setComplaint] = useState<ComplaintResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [message, setMessage] = useState('');
  const [messageFiles, setMessageFiles] = useState<File[]>([]);
  const [sending, setSending] = useState(false);

  const [rejectActionId, setRejectActionId] = useState<string | null>(null);
  const [rejectNote, setRejectNote] = useState('');
  const [rejecting, setRejecting] = useState(false);

  const [acceptingActionId, setAcceptingActionId] = useState<string | null>(null);
  const [closing, setClosing] = useState(false);

  const loadDetail = async () => {
    try {
      setLoading(true);
      setError(null);
      setComplaint(await complaintServiceV2.getCustomerComplaintDetail(complaintId));
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'Không thể tải chi tiết khiếu nại.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (complaintId) void loadDetail();
  }, [complaintId]);

  const closed = complaint?.status === 'DA_DONG';
  const pendingActions = useMemo(() => complaint?.resolutionActions?.filter((a) => a.status === 'PROPOSED') ?? [], [complaint]);

  const handleSend = async () => {
    if (!complaint || !message.trim() || closed) return;
    try {
      setSending(true);
      const payload: ComplaintMessageCreateRequest = { noiDung: message.trim() };
      const updated = await complaintServiceV2.postCustomerMessage(complaint.id, payload, messageFiles);
      setComplaint(updated);
      setMessage('');
      setMessageFiles([]);
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
      const updated = await complaintServiceV2.closeComplaint(complaint.id);
      setComplaint(updated);
    } catch (closeError) {
      setError(closeError instanceof Error ? closeError.message : 'Không thể đóng khiếu nại.');
    } finally {
      setClosing(false);
    }
  };

  const handleAccept = async (actionId: string) => {
    if (!complaint) return;
    try {
      setAcceptingActionId(actionId);
      const updated = await complaintServiceV2.acceptResolutionAction(complaint.id, actionId);
      setComplaint(updated);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể chấp nhận phương án.');
    } finally {
      setAcceptingActionId(null);
    }
  };

  const handleReject = async (actionId: string) => {
    if (!complaint || !rejectNote.trim()) return;
    try {
      setRejecting(true);
      const payload: ResolutionActionRejectRequest = { customerResponseNote: rejectNote.trim() };
      const updated = await complaintServiceV2.rejectResolutionAction(complaint.id, actionId, payload);
      setComplaint(updated);
      setRejectActionId(null);
      setRejectNote('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể từ chối phương án.');
    } finally {
      setRejecting(false);
    }
  };

  const handleOpenAttachment = async (url: string, fileName: string) => {
    console.log('[handleOpenAttachment] Bắt đầu mở file:', { url, fileName });
    if (!url) {
      setError('URL file không tồn tại.');
      return;
    }
    try {
      // Tránh bị lặp /api/api khi backend đã trả URL dạng /api/v1/attachments/{id}
      const requestUrl = url.startsWith('/api/') ? url.replace(/^\/api/, '') : url;
      console.log('[handleOpenAttachment] Request URL:', requestUrl);
      const response = await api.get(requestUrl, { responseType: 'blob' });
      console.log('[handleOpenAttachment] Response blob:', response.data.type, response.data.size);
      const blobUrl = URL.createObjectURL(response.data);
      const opened = window.open(blobUrl, '_blank', 'noopener,noreferrer');
      console.log('[handleOpenAttachment] window.open result:', opened);

      // Fallback nếu trình duyệt chặn popup
      if (!opened) {
        console.log('[handleOpenAttachment] Popup bị chặn, dùng download fallback');
        const a = document.createElement('a');
        a.href = blobUrl;
        a.download = fileName;
        a.click();
      }

      setTimeout(() => URL.revokeObjectURL(blobUrl), 60_000);
    } catch (err) {
      console.error('[handleOpenAttachment] Lỗi:', err);
      setError(err instanceof Error ? err.message : 'Không thể mở file đính kèm.');
    }
  };

  if (loading) {
    return <CustomerFeedbackLayout active="complaints" title="Chi tiết khiếu nại" subtitle="Trao đổi trực tiếp với đối tác và theo dõi tiến độ xử lý."><div className="rounded-xl bg-white p-8 text-center font-semibold">Đang tải chi tiết khiếu nại...</div></CustomerFeedbackLayout>;
  }

  return (
    <CustomerFeedbackLayout active="complaints" title="Chi tiết khiếu nại" subtitle="Trao đổi trực tiếp với đối tác và theo dõi tiến độ xử lý.">
      {error && <div className="mb-4 rounded-xl bg-error-container p-4 text-error">{error}</div>}
      {!complaint && <div className="rounded-xl bg-white p-8 text-center">Không tìm thấy khiếu nại.</div>}

      {complaint && (
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
          <div className="flex flex-col gap-6 lg:col-span-8">
            <section className="rounded-xl border border-outline-variant bg-white p-6 shadow-sm">
              <div className="mb-4 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div>
                  <div className="mb-1 flex items-center gap-3">
                    <span className="rounded bg-surface-container px-2 py-1 text-xs text-on-surface-variant">#{complaint.id}</span>
                    <span className="rounded-full bg-mint-green px-3 py-1 text-xs font-bold uppercase text-on-secondary-container">{statusMap[complaint.status]}</span>
                  </div>
                  <h2 className="font-display text-2xl font-bold text-primary">{complaint.title}</h2>
                </div>
                <div className="flex flex-wrap gap-2">
                  <span className={`rounded-full px-3 py-1 text-sm font-semibold ${complaint.severity === 'NGHIEM_TRONG' ? 'bg-error-container text-error' : 'bg-surface-container text-on-surface-variant'}`}>{severityLabel(complaint.severity)}</span>
                  <span className="rounded-full bg-surface-container px-3 py-1 text-sm font-semibold text-on-surface-variant">{categoryMap[complaint.category]}</span>
                </div>
              </div>

              <div className="grid gap-4 border-t border-outline-variant/30 pt-4 md:grid-cols-2">
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-surface-container"><span className="material-symbols-outlined text-secondary">{complaint.serviceType === 'KHACH_SAN' ? 'hotel' : 'restaurant'}</span></div>
                  <div><p className="text-xs text-on-surface-variant">Loại dịch vụ</p><p className="text-sm font-semibold text-on-surface">{complaint.serviceType === 'KHACH_SAN' ? 'Khách sạn' : 'Nhà hàng'}</p></div>
                </div>
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-surface-container"><span className="material-symbols-outlined text-secondary">calendar_today</span></div>
                  <div><p className="text-xs text-on-surface-variant">Ngày tạo</p><p className="text-sm font-semibold text-on-surface">{new Date(complaint.createdAt).toLocaleString('vi-VN')}</p></div>
                </div>
              </div>
            </section>

            <section className="overflow-hidden rounded-xl border border-outline-variant bg-white shadow-sm">
              <div className="border-b border-outline-variant/20 bg-surface-container-low px-5 py-4">
                <h3 className="font-display text-xl font-semibold text-primary">Hành trình trao đổi</h3>
              </div>

              <div className="max-h-[560px] space-y-5 overflow-y-auto bg-surface-container-low/20 p-5">
                {(complaint.messages ?? []).map((item, index) => {
                  const mine = item.senderRole === 'KHACH_HANG';
                  return (
                    <div key={`${item.createdAt}-${index}`} className={`flex items-start gap-3 ${mine ? 'ml-auto max-w-[85%] flex-row-reverse' : 'max-w-[85%]'}`}>
                      <div className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-xs font-bold text-white ${mine ? 'bg-primary' : item.senderRole === 'DOI_TAC' ? 'bg-secondary' : 'bg-outline'}`}>{mine ? 'ME' : item.senderRole === 'DOI_TAC' ? 'DT' : 'SYS'}</div>
                      <div className={`flex flex-col gap-1 ${mine ? 'items-end' : ''}`}>
                        <div className={`flex items-baseline gap-2 ${mine ? 'flex-row-reverse' : ''}`}>
                          <span className="text-sm font-semibold text-on-surface">{roleLabel(item.senderRole)}</span>
                          <span className="text-[10px] text-on-surface-variant">{new Date(item.createdAt).toLocaleString('vi-VN')}</span>
                        </div>
                        <div className={`rounded-2xl p-4 shadow-sm ${mine ? 'rounded-tr-none bg-primary text-white' : 'rounded-tl-none border border-outline-variant/50 bg-white text-on-surface-variant'}`}>{item.content}</div>
                        {item.attachments?.length > 0 && (
                          <div className="mt-2 flex flex-wrap gap-2">
                            {item.attachments.map((att) => (
                              <button
                                key={att.id}
                                type="button"
                                onClick={() => handleOpenAttachment(att.fileUrl, att.fileName)}
                                className="rounded-lg border border-outline-variant/40 bg-white px-3 py-2 text-xs text-primary hover:bg-surface-container-low"
                              >
                                {att.fileName}
                              </button>
                            ))}
                          </div>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>

              <div className="border-t border-outline-variant p-4">
                {closed && <div className="mb-3 flex items-center justify-center gap-2 rounded-lg border border-dashed border-outline bg-surface-container p-3 text-on-surface-variant"><span className="material-symbols-outlined text-sm">lock</span><span className="text-sm font-semibold">Khiếu nại đã đóng - không thể gửi thêm tin nhắn</span></div>}
                <div className="space-y-3">
                  <textarea className="min-h-24 w-full resize-none rounded-xl bg-surface-container-low px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary disabled:opacity-60" disabled={closed || sending} placeholder="Nhập phản hồi của bạn..." value={message} onChange={(event) => setMessage(event.target.value)} />
                  <div className="flex flex-wrap items-center justify-between gap-3">
                    <input type="file" multiple accept="image/*,.pdf,.doc,.docx" onChange={(e) => setMessageFiles(e.target.files ? Array.from(e.target.files).slice(0, 5) : [])} className="block text-sm text-on-surface-variant file:mr-4 file:rounded-full file:border-0 file:bg-surface-container file:px-4 file:py-2 file:text-sm file:font-semibold file:text-on-surface-variant" />
                    <button className="flex items-center justify-center rounded-xl bg-primary px-5 py-3 font-semibold text-white shadow-md disabled:opacity-60" disabled={closed || sending || !message.trim()} onClick={handleSend} type="button"><span className="material-symbols-outlined mr-2 text-lg">send</span>{sending ? 'Đang gửi...' : 'Gửi tin nhắn'}</button>
                  </div>
                </div>
              </div>
            </section>

            <section className="rounded-xl border border-outline-variant bg-white p-6 shadow-sm">
              <div className="mb-4 flex items-center justify-between">
                <h3 className="font-display text-xl font-semibold text-primary">Phương án xử lý</h3>
                <span className="text-sm text-on-surface-variant">{pendingActions.length} phương án đang chờ</span>
              </div>
              <div className="space-y-4">
                {(complaint.resolutionActions ?? []).length === 0 && <p className="text-sm text-on-surface-variant">Chưa có phương án xử lý nào.</p>}
                {(complaint.resolutionActions ?? []).map((action: ResolutionActionResponse) => (
                  <div key={action.id} className="rounded-xl border border-outline-variant/40 p-4">
                    <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                      <div>
                        <p className="text-xs uppercase tracking-wider text-outline">{actionTypeMap[action.actionType]}</p>
                        <h4 className="text-lg font-semibold text-on-surface">{action.tieuDe}</h4>
                        {action.moTa && <p className="mt-1 text-sm text-on-surface-variant">{action.moTa}</p>}
                      </div>
                      <span className="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">{action.status}</span>
                    </div>

                    <div className="mt-3 grid grid-cols-1 gap-2 text-sm text-on-surface-variant md:grid-cols-2">
                      {action.amount != null && <p>Amount: {action.amount} {action.currency ?? ''}</p>}
                      {action.voucherCode && <p>Voucher: {action.voucherCode}</p>}
                      {action.discountPercent != null && <p>Giảm giá: {action.discountPercent}%</p>}
                      <p>Đề xuất bởi: {action.proposedByPartnerName}</p>
                    </div>

                    {action.status === 'PROPOSED' && (
                      <div className="mt-4 flex flex-wrap gap-2">
                        <button type="button" className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white" onClick={() => handleAccept(action.id)} disabled={acceptingActionId === action.id}>
                          {acceptingActionId === action.id ? 'Đang chấp nhận...' : 'Chấp nhận'}
                        </button>
                        <button type="button" className="rounded-lg border border-outline px-4 py-2 text-sm font-semibold text-on-surface-variant" onClick={() => setRejectActionId(action.id)}>
                          Từ chối
                        </button>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </section>

            <section className="rounded-xl border border-outline-variant bg-white p-6 shadow-sm">
              <h3 className="mb-4 font-display text-xl font-semibold text-primary">Lịch sử hoạt động</h3>
              <div className="space-y-3">
                {(complaint.activities ?? []).length === 0 && <p className="text-sm text-on-surface-variant">Chưa có hoạt động nào.</p>}
                {(complaint.activities ?? []).map((act) => (
                  <div key={act.id} className="rounded-lg border border-outline-variant/30 p-4">
                    <div className="flex items-center justify-between gap-3">
                      <div>
                        <p className="text-sm font-semibold text-on-surface">{activityLabel(act.activityType)}</p>
                        <p className="text-xs text-on-surface-variant">{roleLabel(act.actorRole)} • {act.actorName}</p>
                      </div>
                      <span className="text-xs text-outline">{new Date(act.createdAt).toLocaleString('vi-VN')}</span>
                    </div>
                    <p className="mt-2 text-sm text-on-surface-variant">{act.summary}</p>
                  </div>
                ))}
              </div>
            </section>

            <div className="flex items-center justify-between">
              <button className="flex items-center gap-2 font-semibold text-on-surface-variant transition hover:text-primary" onClick={() => navigate('/user/complaints')} type="button"><span className="material-symbols-outlined">arrow_back</span>Quay lại danh sách</button>
              {complaint.status === 'DA_GIAI_QUYET' && (
                <button className="flex items-center gap-2 rounded-xl bg-error px-6 py-2.5 font-bold text-white disabled:opacity-60" disabled={closed || closing} onClick={handleCloseTicket} type="button">
                  <span className="material-symbols-outlined">check_circle</span>{closing ? 'Đang đóng...' : 'Đóng ticket'}
                </button>
              )}
            </div>
          </div>

          <div className="flex flex-col gap-6 lg:col-span-4">
            <div className="sticky top-28 overflow-hidden rounded-xl border border-outline-variant bg-white shadow-sm">
              <div className="border-b border-outline-variant/30 bg-surface-container-lowest p-5"><h3 className="font-display text-xl font-semibold text-primary">Thông tin ticket</h3></div>
              <div className="flex flex-col gap-5 p-5">
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Mã ticket</label><p className="mt-1 text-sm font-semibold text-on-surface">{complaint.id}</p></div>
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Trạng thái</label><p className="mt-1 text-sm font-semibold text-on-surface">{statusMap[complaint.status]}</p></div>
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Mức độ</label><p className="mt-1 text-sm font-semibold text-on-surface">{severityLabel(complaint.severity)}</p></div>
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Phân loại</label><p className="mt-1 text-sm font-semibold text-on-surface">{categoryMap[complaint.category]}</p></div>
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Quá hạn SLA</label><p className={`mt-1 text-sm font-semibold ${shouldShowOverdueBadge(complaint) ? 'text-error' : 'text-secondary'}`}>{shouldShowOverdueBadge(complaint) ? 'Có' : 'Không'}</p></div>
                <div><label className="text-xs uppercase tracking-wider text-on-surface-variant">Cập nhật lần cuối</label><p className="mt-1 text-sm font-semibold text-on-surface">{new Date(complaint.updatedAt).toLocaleString('vi-VN')}</p></div>
                <Link className="rounded-xl border border-outline px-4 py-3 text-center text-sm font-semibold text-primary transition hover:bg-surface-container-low" to="/user/complaints">Về danh sách khiếu nại</Link>
              </div>
            </div>
          </div>
        </div>
      )}

      {rejectActionId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-lg rounded-2xl bg-white p-6 shadow-2xl">
            <h3 className="text-xl font-bold text-primary">Từ chối phương án</h3>
            <textarea className="mt-4 w-full rounded-xl border border-outline-variant p-3 outline-none focus:ring-2 focus:ring-primary" rows={4} placeholder="Ghi lý do hoặc phản hồi của bạn..." value={rejectNote} onChange={(e) => setRejectNote(e.target.value)} />
            <div className="mt-5 flex justify-end gap-3">
              <button type="button" className="rounded-xl border border-outline px-4 py-2 font-semibold" onClick={() => { setRejectActionId(null); setRejectNote(''); }}>Hủy</button>
              <button type="button" className="rounded-xl bg-error px-4 py-2 font-semibold text-white" disabled={rejecting || !rejectNote.trim()} onClick={() => handleReject(rejectActionId)}>
                {rejecting ? 'Đang gửi...' : 'Xác nhận từ chối'}
              </button>
            </div>
          </div>
        </div>
      )}
    </CustomerFeedbackLayout>
  );
}
