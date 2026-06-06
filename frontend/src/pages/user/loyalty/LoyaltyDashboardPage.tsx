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

import { LoyaltySummaryCards } from './LoyaltySummaryCards'
import { LoyaltyTierProgress } from './LoyaltyTierProgress'
import { LoyaltyHistory } from './LoyaltyHistory'
import { VoucherWallet } from './VoucherWallet'
import { VoucherExchange } from './VoucherExchange'

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
          <LoyaltySummaryCards summary={summary} availableWalletCount={walletByStatus.available.length} />
          
          <LoyaltyTierProgress progress={progress} />

          <section className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            <LoyaltyHistory history={history} />
            <VoucherWallet wallet={wallet} walletByStatus={walletByStatus} />
          </section>

          <VoucherExchange 
            exchangeables={exchangeables} 
            currentPoints={summary.currentPoints ?? 0} 
            exchangingVoucherId={exchangingVoucherId} 
            onExchange={handleExchange} 
          />
        </div>
      )}
    </CustomerFeedbackLayout>
  )
}
