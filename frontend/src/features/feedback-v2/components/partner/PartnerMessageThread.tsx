import type { ComplaintActivityResponse, ComplaintMessageResponse } from '../../types/complaint';

type PartnerMessageThreadProps = {
  messages: ComplaintMessageResponse[];
  activities: ComplaintActivityResponse[];
  onOpenAttachment: (url: string, fileName: string) => void;
};

function roleLabel(role: ComplaintMessageResponse['senderRole'] | ComplaintActivityResponse['actorRole']) {
  if (role === 'KHACH_HANG') return 'Khách hàng';
  if (role === 'DOI_TAC') return 'Đối tác';
  return 'Hệ thống';
}

export function PartnerMessageThread({ messages, activities, onOpenAttachment }: PartnerMessageThreadProps) {
  return (
    <section className="overflow-hidden rounded-xl border border-outline-variant bg-white shadow-sm">
      <div className="border-b border-outline-variant/20 bg-surface-container-low px-5 py-4">
        <h3 className="font-display text-lg font-semibold text-primary">Hành trình trao đổi</h3>
      </div>

      <div className="max-h-[520px] space-y-5 overflow-y-auto bg-surface-container-low/20 p-5">
        {(messages ?? []).map((item, index) => {
          const mine = item.senderRole === 'DOI_TAC';
          return (
            <div key={`${item.createdAt}-${index}`} className={`flex items-start gap-3 ${mine ? 'ml-auto max-w-[85%] flex-row-reverse' : 'max-w-[85%]'}`}>
              <div
                className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-xs font-bold text-white ${
                  mine ? 'bg-primary' : item.senderRole === 'KHACH_HANG' ? 'bg-secondary' : 'bg-outline'
                }`}
              >
                {mine ? 'ĐT' : item.senderRole === 'KHACH_HANG' ? 'KH' : 'HT'}
              </div>
              <div className={`flex flex-col gap-1 ${mine ? 'items-end' : ''}`}>
                <div className={`flex items-baseline gap-2 ${mine ? 'flex-row-reverse' : ''}`}>
                  <span className="text-sm font-semibold">{roleLabel(item.senderRole)}</span>
                  <span className="text-[10px] text-on-surface-variant">{new Date(item.createdAt).toLocaleString('vi-VN')}</span>
                </div>
                <div
                  className={`rounded-2xl p-4 shadow-sm ${
                    mine ? 'rounded-tr-none bg-primary text-white' : 'rounded-tl-none border bg-white text-on-surface-variant'
                  }`}
                >
                  {item.content}
                </div>
                {item.attachments?.length > 0 && (
                  <div className="mt-2 flex flex-wrap gap-2">
                    {item.attachments.map((att) => (
                      <button
                        key={att.id}
                        type="button"
                        onClick={() => onOpenAttachment(att.fileUrl, att.fileName)}
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
        <h4 className="mb-3 font-semibold text-primary">Lịch sử hoạt động</h4>
        <div className="max-h-40 space-y-2 overflow-y-auto">
          {(activities ?? []).map((act) => (
            <div key={act.id} className="rounded-lg border border-outline-variant/30 p-3">
              <p className="text-sm font-semibold">{act.summary}</p>
              <p className="text-xs text-on-surface-variant">
                {roleLabel(act.actorRole)} • {new Date(act.createdAt).toLocaleString('vi-VN')}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
