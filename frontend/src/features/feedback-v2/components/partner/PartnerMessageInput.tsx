type PartnerMessageInputProps = {
  message: string;
  onMessageChange: (value: string) => void;
  files: File[];
  onFilesChange: (files: File[]) => void;
  onSend: () => void;
  sending: boolean;
  disabled: boolean;
};

export function PartnerMessageInput({
  message,
  onMessageChange,
  files,
  onFilesChange,
  onSend,
  sending,
  disabled,
}: PartnerMessageInputProps) {
  return (
    <section className="panel space-y-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h3 className="text-lg font-semibold text-primary">Phản hồi khách hàng</h3>
          <p className="text-sm text-on-surface-variant">Soạn nội dung rõ ràng, lịch sự và thêm bằng chứng khi cần.</p>
        </div>
        <span className="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">
          Tối đa 5 tệp
        </span>
      </div>

      {disabled && (
        <div className="mb-3 rounded-lg border border-dashed border-outline bg-surface-container p-3 text-center text-sm font-semibold text-on-surface-variant">
          Ticket đã đóng - không thể gửi thêm tin nhắn
        </div>
      )}

      <textarea
        disabled={disabled || sending}
        className="min-h-32 w-full resize-none rounded-2xl border border-outline-variant bg-white px-4 py-3 text-[15px] leading-relaxed text-on-surface outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/30 disabled:bg-surface-container disabled:opacity-70"
        placeholder="Nhập phản hồi cho khách..."
        value={message}
        onChange={(e) => onMessageChange(e.target.value)}
      />

      <div className="space-y-3">
        <div className="rounded-xl border border-outline-variant/70 bg-surface-container-low px-3 py-2.5">
          <input
            type="file"
            multiple
            accept="image/*,.pdf,.doc,.docx"
            onChange={(e) => onFilesChange(e.target.files ? Array.from(e.target.files).slice(0, 5) : [])}
            className="block w-full text-sm text-on-surface-variant file:mr-3 file:rounded-lg file:border-0 file:bg-primary file:px-3.5 file:py-2 file:text-sm file:font-semibold file:text-white hover:file:bg-primary/90"
          />
        </div>

        {files.length > 0 && (
          <div className="flex flex-wrap gap-2">
            {files.map((file) => (
              <span key={`${file.name}-${file.size}`} className="inline-flex max-w-full items-center rounded-full bg-mint-green px-3 py-1 text-xs font-semibold text-primary">
                {file.name}
              </span>
            ))}
          </div>
        )}

        <div className="flex flex-wrap items-center justify-between gap-3">
          <span className="text-sm text-on-surface-variant">
            {message.trim().length > 0 ? `${message.trim().length} ký tự` : 'Chưa nhập nội dung'}
          </span>

          <button
            type="button"
            disabled={disabled || sending || !message.trim()}
            onClick={onSend}
            className="inline-flex items-center justify-center gap-2 rounded-xl bg-primary px-5 py-3 font-semibold text-white shadow-md transition hover:bg-primary/90 disabled:opacity-60"
          >
            <span className="material-symbols-outlined text-lg">send</span>
            {sending ? 'Đang gửi...' : 'Gửi tin nhắn'}
          </button>
        </div>
      </div>
    </section>
  );
}
