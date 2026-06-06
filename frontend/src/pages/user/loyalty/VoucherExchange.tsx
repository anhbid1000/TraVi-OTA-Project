import { formatVnd } from '../../../utils/display'
import type { ExchangeableVoucher } from '../../../services/loyaltyService'

interface VoucherExchangeProps {
  exchangeables: ExchangeableVoucher[]
  currentPoints: number
  exchangingVoucherId: number | null
  onExchange: (voucherId: number) => Promise<void>
}

export function VoucherExchange({
  exchangeables,
  currentPoints,
  exchangingVoucherId,
  onExchange,
}: VoucherExchangeProps) {
  return (
    <section className="rounded-xl border border-outline-variant/40 bg-white p-6 shadow-sm">
      <h3 className="font-display text-lg font-bold text-primary">Đổi voucher bằng điểm</h3>
      <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
        {exchangeables.length === 0 && (
          <p className="text-sm text-on-surface-variant">Hiện chưa có voucher nào cho phép đổi điểm.</p>
        )}
        {exchangeables.map((voucher) => {
          const requiredPoint = Number(voucher.diemCanDoi ?? 0)
          const enoughPoint = currentPoints >= requiredPoint
          return (
            <article key={voucher.id} className="rounded-lg border border-outline-variant/40 p-4">
              <h4 className="font-semibold text-on-surface">{voucher.tenUuDai}</h4>
              <p className="mt-1 text-sm text-on-surface-variant">{voucher.moTa || 'Voucher ưu đãi dành cho thành viên loyalty.'}</p>
              <p className="mt-2 text-sm font-semibold text-primary">Mã: {voucher.maVoucher}</p>
              <p className="mt-1 text-sm text-on-surface-variant">Giảm: {voucher.loaiGiamGia === 'PHAN_TRAM' ? `${voucher.mucGiam}%` : formatVnd(voucher.mucGiam)}</p>
              <p className="mt-1 text-sm text-on-surface-variant">Điểm cần đổi: {requiredPoint}</p>
              <button
                type="button"
                disabled={!enoughPoint || exchangingVoucherId === voucher.id}
                onClick={() => void onExchange(voucher.id)}
                className="mt-3 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
              >
                {exchangingVoucherId === voucher.id ? 'Đang đổi...' : enoughPoint ? 'Đổi ngay' : 'Không đủ điểm'}
              </button>
            </article>
          )
        })}
      </div>
    </section>
  )
}
