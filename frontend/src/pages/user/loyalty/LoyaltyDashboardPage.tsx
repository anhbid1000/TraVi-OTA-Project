import { useEffect, useMemo, useState } from 'react'
import { CustomerFeedbackLayout } from '../../../features/feedback-v2/components/CustomerFeedbackLayout'
import {
  loyaltyService,
  type ExchangeableVoucher,
  type LoyaltyProgress,
  type LoyaltySummary,
  type PointHistory,
  type WalletVoucher,
} from '../../../services/loyaltyService'
import { formatVnd } from '../../../utils/display'

function pointTypeLabel(type: string) {
  const map: Record<string, string> = {
    EARNED_FROM_BOOKING: 'Tích điểm từ đặt chỗ',
    EXCHANGE_VOUCHER: 'Đổi voucher',
    MILESTONE_BONUS: 'Thưởng mốc',
  }
  return map[type] ?? type
}

function voucherStatusLabel(status: string) {
  const map: Record<string, string> = {
    CHUA_DUNG: 'Có thể dùng',
    RESERVED: 'Đang giữ',
    DA_DUNG: 'Đã dùng',
    EXPIRED: 'Hết hạn',
    CANCELLED: 'Đã hủy',
  }
  return map[status] ?? status
}

export default function LoyaltyDashboardPage() {
  const [summary, setSummary] = useState<LoyaltySummary | null>(null)
  const [progress, setProgress] = useState<LoyaltyProgress | null>(null)
  const [history, setHistory] = useState<PointHistory[]>([])
  const [wallet, setWallet] = useState<WalletVoucher[]>([])
  const [exchangeables, setExchangeables] = useState<ExchangeableVoucher[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [exchangingVoucherId, setExchangingVoucherId] = useState<number | null>(null)
  const [successMessage, setSuccessMessage] = useState('')

  const loadData = async () => {
    try {
      setLoading(true)
      setError(null)
      const [summaryData, progressData, historyData, walletData, exchangeData] = await Promise.all([
        loyaltyService.getSummary(),
        loyaltyService.getProgress(),
        loyaltyService.getHistory(30),
        loyaltyService.getWalletVouchers(),
        loyaltyService.getExchangeableVouchers(),
      ])
      setSummary(summaryData)
      setProgress(progressData)
      setHistory(historyData)
      setWallet(walletData)
      setExchangeables(exchangeData)
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'Không thể tải dữ liệu loyalty.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadData()
  }, [])

  const walletByStatus = useMemo(
    () => ({
      available: wallet.filter((item) => item.trangThai === 'CHUA_DUNG'),
      reserved: wallet.filter((item) => item.trangThai === 'RESERVED'),
      used: wallet.filter((item) => item.trangThai === 'DA_DUNG'),
      expiringSoon: wallet.filter((item) => {
        if (!item.expiredAt) return false
        const diff = new Date(item.expiredAt).getTime() - Date.now()
        return diff > 0 && diff <= 7 * 24 * 60 * 60 * 1000
      }),
    }),
    [wallet],
  )

  const handleExchange = async (voucherId: number) => {
    try {
      setExchangingVoucherId(voucherId)
      setError(null)
      setSuccessMessage('')
      const response = await loyaltyService.exchangeVoucher(voucherId)
      setSuccessMessage(response.message || 'Đổi voucher thành công.')
      await loadData()
    } catch (exchangeError) {
      setError(exchangeError instanceof Error ? exchangeError.message : 'Đổi voucher thất bại.')
    } finally {
      setExchangingVoucherId(null)
    }
  }

  return (
    <CustomerFeedbackLayout active="loyalty" title="Loyalty & ví voucher" subtitle="Theo dõi điểm thưởng, hạng thành viên và đổi ưu đãi bằng điểm.">
      {loading && <div className="rounded-xl bg-white p-8 text-center font-semibold">Đang tải dữ liệu loyalty...</div>}
      {error && <div className="rounded-xl bg-error-container p-4 text-error">{error}</div>}
      {successMessage && <div className="rounded-xl border border-green-300 bg-green-50 p-4 text-green-700">{successMessage}</div>}

      {!loading && summary && (
        <div className="space-y-6">
          <section className="grid grid-cols-1 gap-4 md:grid-cols-4">
            <article className="rounded-xl border border-outline-variant/40 bg-white p-5 shadow-sm">
              <p className="text-sm text-on-surface-variant">Điểm hiện có</p>
              <p className="mt-2 text-3xl font-bold text-primary">{summary.currentPoints ?? 0}</p>
            </article>
            <article className="rounded-xl border border-outline-variant/40 bg-white p-5 shadow-sm">
              <p className="text-sm text-on-surface-variant">Hạng hiện tại</p>
              <p className="mt-2 text-2xl font-bold text-primary">{summary.currentTier}</p>
            </article>
            <article className="rounded-xl border border-outline-variant/40 bg-white p-5 shadow-sm">
              <p className="text-sm text-on-surface-variant">Tổng chi tiêu</p>
              <p className="mt-2 text-2xl font-bold text-primary">{formatVnd(summary.totalSpending ?? 0)}</p>
            </article>
            <article className="rounded-xl border border-outline-variant/40 bg-white p-5 shadow-sm">
              <p className="text-sm text-on-surface-variant">Voucher khả dụng</p>
              <p className="mt-2 text-2xl font-bold text-primary">{walletByStatus.available.length}</p>
            </article>
          </section>

          <section className="rounded-xl border border-outline-variant/40 bg-white p-6 shadow-sm">
            <h2 className="font-display text-xl font-bold text-primary">Tiến độ hạng thành viên</h2>
            <p className="mt-2 text-sm text-on-surface-variant">
              {progress?.nextTier ? `Còn ${formatVnd(progress.remainingSpending ?? 0)} để lên hạng ${progress.nextTier}.` : 'Bạn đã đạt hạng cao nhất.'}
            </p>
            <div className="mt-4 h-3 w-full overflow-hidden rounded-full bg-surface-container">
              <div className="h-full rounded-full bg-primary" style={{ width: `${Math.max(0, Math.min(100, Number(progress?.progressPercent ?? 0)))}%` }} />
            </div>
            <p className="mt-2 text-sm text-on-surface-variant">{Number(progress?.progressPercent ?? 0).toFixed(1)}%</p>
          </section>

          <section className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            <article className="rounded-xl border border-outline-variant/40 bg-white p-6 shadow-sm">
              <h3 className="font-display text-lg font-bold text-primary">Lịch sử điểm gần đây</h3>
              <div className="mt-4 space-y-3">
                {history.length === 0 && <p className="text-sm text-on-surface-variant">Chưa có giao dịch điểm.</p>}
                {history.map((item) => (
                  <div key={item.id} className="rounded-lg border border-outline-variant/30 p-3">
                    <div className="flex items-center justify-between gap-3">
                      <p className="text-sm font-semibold text-on-surface">{pointTypeLabel(item.loaiGiaoDichDiem)}</p>
                      <p className={`text-sm font-bold ${item.soDiemThayDoi >= 0 ? 'text-secondary' : 'text-error'}`}>
                        {item.soDiemThayDoi > 0 ? '+' : ''}{item.soDiemThayDoi}
                      </p>
                    </div>
                    <p className="mt-1 text-xs text-on-surface-variant">{new Date(item.createdAt).toLocaleString('vi-VN')}</p>
                    {item.ghiChu && <p className="mt-1 text-sm text-on-surface-variant">{item.ghiChu}</p>}
                  </div>
                ))}
              </div>
            </article>

            <article className="rounded-xl border border-outline-variant/40 bg-white p-6 shadow-sm">
              <h3 className="font-display text-lg font-bold text-primary">Ví voucher</h3>
              <p className="mt-1 text-sm text-on-surface-variant">
                {walletByStatus.available.length} khả dụng • {walletByStatus.reserved.length} đang giữ • {walletByStatus.used.length} đã dùng • {walletByStatus.expiringSoon.length} sắp hết hạn
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
          </section>

          <section className="rounded-xl border border-outline-variant/40 bg-white p-6 shadow-sm">
            <h3 className="font-display text-lg font-bold text-primary">Đổi voucher bằng điểm</h3>
            <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
              {exchangeables.length === 0 && <p className="text-sm text-on-surface-variant">Hiện chưa có voucher nào cho phép đổi điểm.</p>}
              {exchangeables.map((voucher) => {
                const requiredPoint = Number(voucher.diemCanDoi ?? 0)
                const enoughPoint = (summary.currentPoints ?? 0) >= requiredPoint
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
                      onClick={() => handleExchange(voucher.id)}
                      className="mt-3 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
                    >
                      {exchangingVoucherId === voucher.id ? 'Đang đổi...' : enoughPoint ? 'Đổi ngay' : 'Không đủ điểm'}
                    </button>
                  </article>
                )
              })}
            </div>
          </section>
        </div>
      )}
    </CustomerFeedbackLayout>
  )
}
