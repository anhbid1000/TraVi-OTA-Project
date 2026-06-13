import type { WalletVoucher } from '../../../services/loyaltyService'
import { voucherStatusLabel } from './loyaltyUi'

type WalletGroups = {
  available: WalletVoucher[]
  reserved: WalletVoucher[]
  used: WalletVoucher[]
  expiringSoon: WalletVoucher[]
}

type VoucherWalletPanelProps = {
  wallet: WalletVoucher[]
  walletByStatus: WalletGroups
}

export function VoucherWalletPanel({ wallet, walletByStatus }: VoucherWalletPanelProps) {
  return (
    <article className="rounded-xl border border-outline-variant/40 bg-white p-6 shadow-sm">
      <h3 className="font-display text-lg font-bold text-primary">Ví voucher</h3>
      <p className="mt-1 text-sm text-on-surface-variant">
        {walletByStatus.available.length} khả dụng • {walletByStatus.reserved.length} đang giữ •{' '}
        {walletByStatus.used.length} đã dùng • {walletByStatus.expiringSoon.length} sắp hết hạn
      </p>
      <div className="mt-4 space-y-3">
        {wallet.length === 0 && <p className="text-sm text-on-surface-variant">Bạn chưa có voucher nào trong ví.</p>}
        {wallet.slice(0, 12).map((item) => (
          <div key={item.id} className="rounded-lg border border-outline-variant/30 p-3">
            <div className="flex items-center justify-between gap-3">
              <p className="font-semibold text-on-surface">{item.tenUuDai || item.maVoucher}</p>
              <span className="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">
                {voucherStatusLabel(item.trangThai)}
              </span>
            </div>
            <p className="mt-1 text-sm text-on-surface-variant">Mã: {item.maVoucherCaNhan || item.maVoucher}</p>
          </div>
        ))}
      </div>
    </article>
  )
}
