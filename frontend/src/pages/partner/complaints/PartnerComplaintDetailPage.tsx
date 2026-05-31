import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { complaintServiceV2 } from '../../../features/feedback-v2/services/complaintServiceV2';
import { api } from '../../../services/api';
import type {
  ComplaintMessageCreateRequest,
  ComplaintResolutionActionType,
  ComplaintResponse,
  ResolutionActionCompleteRequest,
  ResolutionActionCreateRequest,
  ResolutionActionResponse,
} from '../../../features/feedback-v2/types/complaint';
import { PartnerComplaintHeader } from '../../../features/feedback-v2/components/partner/PartnerComplaintHeader';
import { PartnerMessageThread } from '../../../features/feedback-v2/components/partner/PartnerMessageThread';
import { PartnerMessageInput } from '../../../features/feedback-v2/components/partner/PartnerMessageInput';
import '../../dashboard.css';

const ACTION_TYPE_OPTIONS: Array<{ value: ComplaintResolutionActionType; label: string }> = [
  { value: 'FULL_REFUND', label: 'Hoàn tiền toàn bộ' },
  { value: 'PARTIAL_REFUND', label: 'Hoàn tiền một phần' },
  { value: 'VOUCHER', label: 'Tặng voucher' },
  { value: 'DISCOUNT_NEXT_BOOKING', label: 'Giảm giá lần đặt tiếp theo' },
  { value: 'CHANGE_ROOM', label: 'Đổi phòng' },
  { value: 'OTHER_SOLUTION', label: 'Phương án khác' },
  { value: 'REJECT_COMPLAINT', label: 'Từ chối khiếu nại' },
];

function actionTypeLabel(type: ComplaintResolutionActionType) {
  return ACTION_TYPE_OPTIONS.find((i) => i.value === type)?.label ?? type;
}

function actionStatusLabel(status: ResolutionActionResponse['status']) {
  return ({
    PROPOSED: 'Đang chờ khách xác nhận',
    CUSTOMER_ACCEPTED: 'Khách đã chấp nhận',
    CUSTOMER_REJECTED: 'Khách đã từ chối',
    IN_PROGRESS: 'Đang thực hiện',
    COMPLETED: 'Đã hoàn tất',
    CANCELLED: 'Đã hủy',
  } as const)[status];
}

export default function PartnerComplaintDetailPage() {
  const navigate = useNavigate();
  const { complaintId = '' } = useParams();

  const [complaint, setComplaint] = useState<ComplaintResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [message, setMessage] = useState('');
  const [messageFiles, setMessageFiles] = useState<File[]>([]);
  const [sending, setSending] = useState(false);

  const [creatingAction, setCreatingAction] = useState(false);
  const [actionBusyId, setActionBusyId] = useState<string | null>(null);
  const [completeTargetActionId, setCompleteTargetActionId] = useState<string | null>(null);
  const [completeNote, setCompleteNote] = useState('');
  const [completeFiles, setCompleteFiles] = useState<File[]>([]);

  const [actionForm, setActionForm] = useState<ResolutionActionCreateRequest>({
    actionType: 'VOUCHER',
    tieuDe: '',
    moTa: '',
    currency: 'VND',
  });

  const loadDetail = async () => {
    try {
      setLoading(true);
      setError(null);
      setComplaint(await complaintServiceV2.getPartnerComplaintDetail(complaintId));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể tải chi tiết khiếu nại.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (complaintId) void loadDetail();
  }, [complaintId]);

  const closed = complaint?.status === 'DA_DONG';
  const canProposeAction = !!complaint && !closed && (complaint.status === 'CHO_PHAN_HOI' || complaint.status === 'DANG_XU_LY');

  const activeActions = useMemo(
    () => (complaint?.resolutionActions ?? []).filter((a) => ['PROPOSED', 'CUSTOMER_ACCEPTED', 'IN_PROGRESS'].includes(a.status)),
    [complaint]
  );

  const handleOpenAttachment = async (url: string, fileName: string) => {
    if (!url) return setError('URL file không tồn tại.');
    try {
      const requestUrl = url.startsWith('/api/') ? url.replace(/^\/api/, '') : url;
      const response = await api.get(requestUrl, { responseType: 'blob' });
      const blobUrl = URL.createObjectURL(response.data);
      const opened = window.open(blobUrl, '_blank', 'noopener,noreferrer');
      if (!opened) {
        const a = document.createElement('a');
        a.href = blobUrl;
        a.download = fileName;
        a.click();
      }
      setTimeout(() => URL.revokeObjectURL(blobUrl), 60_000);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể mở file đính kèm.');
    }
  };

  const handleSend = async () => {
    if (!complaint || closed || !message.trim()) return;
    try {
      setSending(true);
      const payload: ComplaintMessageCreateRequest = { noiDung: message.trim() };
      const updated = await complaintServiceV2.postPartnerMessage(complaint.id, payload, messageFiles);
      setComplaint(updated);
      setMessage('');
      setMessageFiles([]);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể gửi tin nhắn.');
    } finally {
      setSending(false);
    }
  };

  const handleCreateAction = async () => {
    if (!complaint || !canProposeAction || !actionForm.tieuDe.trim()) return;
    try {
      setCreatingAction(true);
      const payload: ResolutionActionCreateRequest = {
        ...actionForm,
        tieuDe: actionForm.tieuDe.trim(),
        moTa: actionForm.moTa?.trim() || undefined,
        amount: actionForm.amount != null ? Number(actionForm.amount) : undefined,
        discountPercent: actionForm.discountPercent != null ? Number(actionForm.discountPercent) : undefined,
      };

      const updated = await complaintServiceV2.createResolutionAction(complaint.id, payload);
      setComplaint(updated);
      setActionForm({ actionType: 'VOUCHER', tieuDe: '', moTa: '', currency: 'VND' });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể tạo phương án xử lý.');
    } finally {
      setCreatingAction(false);
    }
  };

  const handleStartAction = async (actionId: string) => {
    if (!complaint || closed) return;
    try {
      setActionBusyId(actionId);
      const updated = await complaintServiceV2.startResolutionAction(complaint.id, actionId);
      setComplaint(updated);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể bắt đầu thực hiện phương án.');
    } finally {
      setActionBusyId(null);
    }
  };

  const handleCompleteAction = async (actionId: string) => {
    if (!complaint || closed) return;
    try {
      setActionBusyId(actionId);
      const payload: ResolutionActionCompleteRequest = {
        partnerCompletionNote: completeNote.trim() || undefined,
      };
      const updated = await complaintServiceV2.completeResolutionAction(complaint.id, actionId, payload, completeFiles);
      setComplaint(updated);
      setCompleteTargetActionId(null);
      setCompleteNote('');
      setCompleteFiles([]);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể hoàn tất phương án.');
    } finally {
      setActionBusyId(null);
    }
  };

  if (loading) {
    return <div className="panel text-center font-semibold">Đang tải chi tiết ticket...</div>;
  }

  return (
    <div className="space-y-6">
      <header className="partner-page-header">
        <div className="flex items-center justify-between gap-3">
          <button type="button" onClick={() => navigate('/partner/complaints')} className="flex items-center gap-2 font-semibold text-primary">
            <span className="material-symbols-outlined">arrow_back</span>
            Quay lại danh sách
          </button>
        </div>
      </header>

      <section className="space-y-6">
          {error && <div className="rounded-xl bg-error-container p-4 text-error">{error}</div>}
          {!complaint && <div className="panel text-center">Không tìm thấy khiếu nại.</div>}

          {complaint && (
            <>
              <PartnerComplaintHeader complaint={complaint} />

              {/* Resolution Actions Panel */}
              <section className="rounded-xl border border-outline-variant/50 bg-white p-6 shadow-sm space-y-5">
                <div className="flex flex-wrap items-center justify-between gap-3">
                  <div>
                    <h3 className="font-display text-lg font-semibold text-primary">Phương án xử lý</h3>
                    <p className="text-sm text-on-surface-variant">Partner đề xuất và thực hiện phương án theo state machine của complaint.</p>
                  </div>
                  <span className="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">
                    {activeActions.length} phương án đang hoạt động
                  </span>
                </div>

                {!canProposeAction && (
                  <div className="rounded-lg bg-surface-container p-3 text-sm text-on-surface-variant">
                    Trạng thái hiện tại không cho phép đề xuất phương án mới hoặc ticket đã đóng.
                  </div>
                )}

                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                  <select
                    disabled={!canProposeAction || creatingAction}
                    value={actionForm.actionType}
                    onChange={(e) => setActionForm((prev) => ({ ...prev, actionType: e.target.value as ComplaintResolutionActionType }))}
                    className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                  >
                    {ACTION_TYPE_OPTIONS.map((o) => (
                      <option key={o.value} value={o.value}>{o.label}</option>
                    ))}
                  </select>

                  <input
                    disabled={!canProposeAction || creatingAction}
                    value={actionForm.tieuDe}
                    onChange={(e) => setActionForm((prev) => ({ ...prev, tieuDe: e.target.value }))}
                    placeholder="Tiêu đề phương án xử lý"
                    className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                  />

                  <input
                    disabled={!canProposeAction || creatingAction}
                    type="number"
                    value={actionForm.amount ?? ''}
                    onChange={(e) => setActionForm((prev) => ({ ...prev, amount: e.target.value ? Number(e.target.value) : undefined }))}
                    placeholder="Số tiền (nếu có)"
                    className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                  />

                  <input
                    disabled={!canProposeAction || creatingAction}
                    value={actionForm.currency ?? 'VND'}
                    onChange={(e) => setActionForm((prev) => ({ ...prev, currency: e.target.value }))}
                    placeholder="Đơn vị tiền tệ (VND/USD...)"
                    className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                  />

                  <input
                    disabled={!canProposeAction || creatingAction}
                    value={actionForm.voucherCode ?? ''}
                    onChange={(e) => setActionForm((prev) => ({ ...prev, voucherCode: e.target.value }))}
                    placeholder="Mã voucher (nếu có)"
                    className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                  />

                  <input
                    disabled={!canProposeAction || creatingAction}
                    type="number"
                    value={actionForm.discountPercent ?? ''}
                    onChange={(e) => setActionForm((prev) => ({ ...prev, discountPercent: e.target.value ? Number(e.target.value) : undefined }))}
                    placeholder="% giảm giá (nếu có)"
                    className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                  />
                </div>

                <textarea
                  disabled={!canProposeAction || creatingAction}
                  value={actionForm.moTa ?? ''}
                  onChange={(e) => setActionForm((prev) => ({ ...prev, moTa: e.target.value }))}
                  placeholder="Mô tả chi tiết phương án..."
                  rows={3}
                  className="w-full rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                />

                <div className="flex justify-end">
                  <button
                    type="button"
                    disabled={!canProposeAction || creatingAction || !actionForm.tieuDe.trim()}
                    onClick={handleCreateAction}
                    className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
                  >
                    {creatingAction ? 'Đang tạo phương án...' : 'Đề xuất phương án'}
                  </button>
                </div>

                <div className="space-y-3">
                  {(complaint.resolutionActions ?? []).length === 0 && (
                    <div className="rounded-lg border border-dashed border-outline-variant p-4 text-sm text-on-surface-variant">
                      Chưa có phương án xử lý nào.
                    </div>
                  )}

                  {(complaint.resolutionActions ?? []).map((action) => (
                    <article key={action.id} className="rounded-lg border border-outline-variant/50 p-4">
                      <div className="flex flex-wrap items-center justify-between gap-2">
                        <div>
                          <h4 className="font-semibold text-on-surface">{action.tieuDe}</h4>
                          <p className="text-xs text-on-surface-variant">{actionTypeLabel(action.actionType)}</p>
                        </div>
                        <span className="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">
                          {actionStatusLabel(action.status)}
                        </span>
                      </div>

                      {action.moTa && <p className="mt-2 text-sm text-on-surface-variant">{action.moTa}</p>}

                      <div className="mt-2 flex flex-wrap gap-3 text-xs text-on-surface-variant">
                        {action.amount != null && <span>Số tiền: {action.amount} {action.currency ?? ''}</span>}
                        {action.voucherCode && <span>Voucher: {action.voucherCode}</span>}
                        {action.discountPercent != null && <span>Giảm giá: {action.discountPercent}%</span>}
                        {action.partnerCompletionNote && <span>Ghi chú hoàn tất: {action.partnerCompletionNote}</span>}
                      </div>

                      <div className="mt-3 flex flex-wrap gap-2">
                        {action.status === 'CUSTOMER_ACCEPTED' && (
                          <button
                            type="button"
                            disabled={closed || actionBusyId === action.id}
                            onClick={() => handleStartAction(action.id)}
                            className="rounded-md bg-primary px-3 py-1.5 text-xs font-semibold text-white disabled:opacity-60"
                          >
                            {actionBusyId === action.id ? 'Đang bắt đầu...' : 'Bắt đầu thực hiện'}
                          </button>
                        )}

                        {(action.status === 'CUSTOMER_ACCEPTED' || action.status === 'IN_PROGRESS') && (
                          <button
                            type="button"
                            disabled={closed}
                            onClick={() => setCompleteTargetActionId(action.id)}
                            className="rounded-md border border-primary px-3 py-1.5 text-xs font-semibold text-primary disabled:opacity-60"
                          >
                            Hoàn tất phương án
                          </button>
                        )}
                      </div>
                    </article>
                  ))}
                </div>
              </section>

              {/* Message Thread + Input */}
              <div className="grid grid-cols-1 gap-6">
                <PartnerMessageThread
                  messages={complaint.messages ?? []}
                  activities={complaint.activities ?? []}
                  onOpenAttachment={handleOpenAttachment}
                />

                <PartnerMessageInput
                  message={message}
                  onMessageChange={setMessage}
                  files={messageFiles}
                  onFilesChange={setMessageFiles}
                  onSend={handleSend}
                  sending={sending}
                  disabled={closed}
                />
              </div>
            </>
          )}
      </section>

    {completeTargetActionId && (
        <div className="fixed inset-0 z-[80] flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-lg rounded-2xl bg-white p-6 shadow-2xl">
            <h3 className="text-lg font-bold text-primary">Hoàn tất phương án xử lý</h3>
            <p className="mt-1 text-sm text-on-surface-variant">Thêm ghi chú và bằng chứng hoàn tất (nếu có).</p>

            <textarea
              value={completeNote}
              onChange={(e) => setCompleteNote(e.target.value)}
              rows={4}
              placeholder="Ghi chú hoàn tất..."
              className="mt-4 w-full rounded-lg border border-outline-variant px-3 py-2.5 text-sm"
            />

            <input
              className="mt-3 block text-sm"
              type="file"
              multiple
              accept="image/*,.pdf,.doc,.docx"
              onChange={(e) => setCompleteFiles(e.target.files ? Array.from(e.target.files).slice(0, 5) : [])}
            />

            <div className="mt-5 flex justify-end gap-3">
              <button
                type="button"
                onClick={() => {
                  setCompleteTargetActionId(null);
                  setCompleteNote('');
                  setCompleteFiles([]);
                }}
                className="rounded-xl border border-outline px-4 py-2 font-semibold"
              >
                Hủy
              </button>
              <button
                type="button"
                disabled={!!actionBusyId}
                onClick={() => handleCompleteAction(completeTargetActionId)}
                className="rounded-xl bg-primary px-4 py-2 font-semibold text-white disabled:opacity-60"
              >
                {actionBusyId === completeTargetActionId ? 'Đang hoàn tất...' : 'Xác nhận hoàn tất'}
              </button>
            </div>
          </div>
        </div>
    )}
    </div>
  );
}
