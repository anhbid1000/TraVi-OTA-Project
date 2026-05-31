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
  
  onFilesChange,
  onSend,
  sending,
  disabled,
}: PartnerMessageInputProps) {
  return (
    <div className="border-t border-outline-variant p-4">
      {disabled && (
        <div className="mb-3 rounded-lg border border-dashed border-outline bg-surface-container p-3 text-center text-sm font-semibold text-on-surface-variant">
          Ticket đã đóng - không thể gửi thêm tin nhắn
        </div>
      )}
      <textarea
        disabled={disabled || sending}
        className="min-h-24 w-full resize-none rounded-xl bg-surface-container-low px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary disabled:opacity-60"
        placeholder="Nhập phản hồi cho khách..."
        value={message}
        onChange={(e) => onMessageChange(e.target.value)}
      />
      <div className="mt-3 flex flex-wrap items-center justify-between gap-3">
        <input
          type="file"
          multiple
          accept="image/*,.pdf,.doc,.docx"
          onChange={(e) => onFilesChange(e.target.files ? Array.from(e.target.files).slice(0, 5) : [])}
          className="block text-sm text-on-surface-variant file:mr-4 file:rounded-full file:border-0 file:bg-surface-container file:px-4 file:py-2 file:text-sm file:font-semibold"
        />
        <button
          type="button"
          disabled={disabled || sending || !message.trim()}
          onClick={onSend}
          className="rounded-xl bg-primary px-5 py-3 font-semibold text-white shadow-md disabled:opacity-60"
        >
          {sending ? 'Đang gửi...' : 'Gửi tin nhắn'}
        </button>
      </div>
    </div>
  );
}
