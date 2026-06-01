import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react'
import { partnerAssetService } from '../../../services/partnerAssetService'
import {
  partnerPromotionService,
  type PromotionAnalyticsDaily,
  type PromotionAnalyticsSummary,
  type PromotionResponse,
} from '../../../services/partnerPromotionService'
import { formatVnd } from '../../../utils/display'
import type { BusinessProfileResponse } from '../../../types/asset'
import '../../dashboard.css'

type CampaignType = 'DIRECT' | 'VOUCHER'

type PromotionFormState = {
  campaignType: CampaignType
  tenUuDai: string
  moTa: string
  businessProfileId: string
  mucGiam: string
  loaiGiamGia: 'PHAN_TRAM' | 'SO_TIEN_CO_DINH'
  giaTriGiamToiDa: string
  ngayBatDau: string
  ngayKetThuc: string
  maVoucher: string
  soLuongPhatHanh: string
  donHangToiThieu: string
  usageLimitPerUser: string
  diemCanDoi: string
  choPhepDoiBangDiem: boolean
  phamViApDung: string
}

function toDateTimeLocal(value?: string | null) {
  if (!value) return ''
  const date = new Date(value)
  const offset = date.getTimezoneOffset()
  const local = new Date(date.getTime() - offset * 60_000)
  return local.toISOString().slice(0, 16)
}

const initialFormState: PromotionFormState = {
  campaignType: 'VOUCHER',
  tenUuDai: '',
  moTa: '',
  businessProfileId: '',
  mucGiam: '',
  loaiGiamGia: 'PHAN_TRAM',
  giaTriGiamToiDa: '',
  ngayBatDau: '',
  ngayKetThuc: '',
  maVoucher: '',
  soLuongPhatHanh: '',
  donHangToiThieu: '',
  usageLimitPerUser: '',
  diemCanDoi: '',
  choPhepDoiBangDiem: false,
  phamViApDung: 'TOAN_SAN',
}

export default function PartnerPromotionsPage() {
  const [profiles, setProfiles] = useState<BusinessProfileResponse[]>([])
  const [promotions, setPromotions] = useState<PromotionResponse[]>([])
  const [selectedPromotionId, setSelectedPromotionId] = useState<number | null>(null)
  const [summary, setSummary] = useState<PromotionAnalyticsSummary | null>(null)
  const [daily, setDaily] = useState<PromotionAnalyticsDaily[]>([])
  const [form, setForm] = useState<PromotionFormState>(initialFormState)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [actionBusyId, setActionBusyId] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState('')

  const loadPageData = useCallback(async () => {
    try {
      setLoading(true)
      setError(null)
      const [profileData, promotionData] = await Promise.all([
        partnerAssetService.getBusinessProfiles(),
        partnerPromotionService.getPromotions(),
      ])
      setProfiles(profileData)
      setPromotions(promotionData)
      if (profileData[0]?.idHoSo) {
        setForm((prev) => ({
          ...prev,
          businessProfileId: prev.businessProfileId || profileData[0].idHoSo,
        }))
      }
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'Không thể tải dữ liệu campaign.')
    } finally {
      setLoading(false)
    }
  }, [])

  const loadAnalytics = async (promotionId: number) => {
    try {
      setError(null)
      const toDate = new Date()
      const fromDate = new Date()
      fromDate.setDate(toDate.getDate() - 30)
      const [summaryData, dailyData] = await Promise.all([
        partnerPromotionService.getPromotionAnalytics(promotionId),
        partnerPromotionService.getPromotionAnalyticsDaily(
          promotionId,
          fromDate.toISOString().slice(0, 10),
          toDate.toISOString().slice(0, 10),
        ),
      ])
      setSelectedPromotionId(promotionId)
      setSummary(summaryData)
      setDaily(dailyData)
    } catch (analyticsError) {
      setError(analyticsError instanceof Error ? analyticsError.message : 'Không thể tải analytics chiến dịch.')
      setSummary(null)
      setDaily([])
    }
  }

  useEffect(() => {
    void loadPageData()
  }, [loadPageData])

  const sortedPromotions = useMemo(
    () =>
      [...promotions].sort(
        (a, b) => new Date(b.updatedAt ?? b.ngayKetThuc).getTime() - new Date(a.updatedAt ?? a.ngayKetThuc).getTime(),
      ),
    [promotions],
  )

  const handleCreate = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    try {
      setSubmitting(true)
      setError(null)
      setSuccess('')

      if (!form.businessProfileId || !form.businessProfileId.trim()) {
        throw new Error('Hồ sơ kinh doanh không hợp lệ.')
      }

      if (!form.ngayBatDau || !form.ngayKetThuc) {
        throw new Error('Ngày bắt đầu và ngày kết thúc không được để trống.')
      }

      const start = new Date(form.ngayBatDau)
      const end = new Date(form.ngayKetThuc)
      if (end <= start) {
        throw new Error('Ngày kết thúc phải sau ngày bắt đầu.')
      }

      const payloadBase = {
        tenUuDai: form.tenUuDai.trim(),
        moTa: form.moTa.trim() || undefined,
        businessProfileId: form.businessProfileId,
        mucGiam: Number(form.mucGiam),
        loaiGiamGia: form.loaiGiamGia,
        giaTriGiamToiDa: form.giaTriGiamToiDa ? Number(form.giaTriGiamToiDa) : undefined,
        ngayBatDau: start.toISOString(),
        ngayKetThuc: end.toISOString(),
      }

      if (form.campaignType === 'DIRECT') {
        await partnerPromotionService.createDirectPromotion(payloadBase)
      } else {
        await partnerPromotionService.createVoucher({
          ...payloadBase,
          maVoucher: form.maVoucher.trim(),
          soLuongPhatHanh: Number(form.soLuongPhatHanh),
          donHangToiThieu: form.donHangToiThieu ? Number(form.donHangToiThieu) : undefined,
          usageLimitPerUser: form.usageLimitPerUser ? Number(form.usageLimitPerUser) : undefined,
          diemCanDoi: form.diemCanDoi ? Number(form.diemCanDoi) : undefined,
          choPhepDoiBangDiem: form.choPhepDoiBangDiem,
          phamViApDung: form.phamViApDung,
        })
      }

      setSuccess('Tạo chiến dịch thành công.')
      setForm((prev) => ({ ...initialFormState, businessProfileId: prev.businessProfileId }))
      await loadPageData()
    } catch (createError) {
      setError(createError instanceof Error ? createError.message : 'Không thể tạo chiến dịch.')
    } finally {
      setSubmitting(false)
    }
  }

  const handlePauseResume = async (promotion: PromotionResponse) => {
    const canAct =
      promotion.trangThaiUuDai === 'DANG_CO_HIEU_LUC' || promotion.trangThaiUuDai === 'TAM_DUNG'
    if (!canAct) return
    try {
      setActionBusyId(promotion.id)
      setError(null)
      setSuccess('')
      if (promotion.trangThaiUuDai === 'DANG_CO_HIEU_LUC') {
        await partnerPromotionService.pausePromotion(promotion.id)
        setSuccess('Đã tạm dừng chiến dịch.')
      } else {
        await partnerPromotionService.resumePromotion(promotion.id)
        setSuccess('Đã kích hoạt lại chiến dịch.')
      }
      await loadPageData()
      if (selectedPromotionId === promotion.id) {
        await loadAnalytics(promotion.id)
      }
    } catch (actionError) {
      setError(actionError instanceof Error ? actionError.message : 'Không thể thay đổi trạng thái chiến dịch.')
    } finally {
      setActionBusyId(null)
    }
  }

  return (
    <div className="space-y-6">
      <header className="partner-page-header split">
        <div>
          <h1>Chiến dịch ưu đãi</h1>
          <p>Quản lý voucher/direct promotion, theo dõi hiệu quả và điều chỉnh theo thời gian thực.</p>
        </div>
      </header>

      {error && <div className="rounded-xl bg-error-container p-4 text-error">{error}</div>}
      {success && <div className="rounded-xl border border-green-300 bg-green-50 p-4 text-green-700">{success}</div>}

      <section className="panel">
        <h2 className="mb-4 text-lg font-semibold text-primary">Tạo campaign mới</h2>
        <form onSubmit={handleCreate} className="grid grid-cols-1 gap-3 md:grid-cols-2">
          <select
            value={form.campaignType}
            onChange={(event) => setForm((prev) => ({ ...prev, campaignType: event.target.value as CampaignType }))}
            className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
          >
            <option value="VOUCHER">Voucher campaign</option>
            <option value="DIRECT">Direct promotion</option>
          </select>
          <select
            value={form.businessProfileId}
            onChange={(event) => setForm((prev) => ({ ...prev, businessProfileId: event.target.value }))}
            className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
            required
          >
            {profiles.length === 0 && <option value="">Chưa có hồ sơ kinh doanh</option>}
            {profiles.map((profile) => (
              <option key={profile.idHoSo} value={profile.idHoSo}>
                {profile.tenCoSo} ({profile.loaiDichVu})
              </option>
            ))}
          </select>
          <input
            value={form.tenUuDai}
            onChange={(event) => setForm((prev) => ({ ...prev, tenUuDai: event.target.value }))}
            className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
            placeholder="Tên ưu đãi"
            required
          />
          <select
            value={form.loaiGiamGia}
            onChange={(event) => setForm((prev) => ({ ...prev, loaiGiamGia: event.target.value as PromotionFormState['loaiGiamGia'] }))}
            className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
          >
            <option value="PHAN_TRAM">Giảm theo %</option>
            <option value="SO_TIEN_CO_DINH">Giảm số tiền cố định</option>
          </select>
          <input
            type="number"
            min={0}
            value={form.mucGiam}
            onChange={(event) => setForm((prev) => ({ ...prev, mucGiam: event.target.value }))}
            className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
            placeholder="Mức giảm"
            required
          />
          <input
            type="number"
            min={0}
            value={form.giaTriGiamToiDa}
            onChange={(event) => setForm((prev) => ({ ...prev, giaTriGiamToiDa: event.target.value }))}
            className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
            placeholder="Giảm tối đa (optional)"
          />
          <input
            type="datetime-local"
            value={form.ngayBatDau}
            onChange={(event) => setForm((prev) => ({ ...prev, ngayBatDau: event.target.value }))}
            className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
            required
          />
          <input
            type="datetime-local"
            value={form.ngayKetThuc}
            onChange={(event) => setForm((prev) => ({ ...prev, ngayKetThuc: event.target.value }))}
            className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
            required
          />
          <textarea
            value={form.moTa}
            onChange={(event) => setForm((prev) => ({ ...prev, moTa: event.target.value }))}
            className="md:col-span-2 rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
            rows={3}
            placeholder="Mô tả chiến dịch"
          />

          {form.campaignType === 'VOUCHER' && (
            <>
              <input
                value={form.maVoucher}
                onChange={(event) => setForm((prev) => ({ ...prev, maVoucher: event.target.value.toUpperCase() }))}
                className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                placeholder="Mã voucher"
                required
              />
              <input
                type="number"
                min={1}
                value={form.soLuongPhatHanh}
                onChange={(event) => setForm((prev) => ({ ...prev, soLuongPhatHanh: event.target.value }))}
                className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                placeholder="Số lượng phát hành"
                required
              />
              <input
                type="number"
                min={0}
                value={form.donHangToiThieu}
                onChange={(event) => setForm((prev) => ({ ...prev, donHangToiThieu: event.target.value }))}
                className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                placeholder="Đơn hàng tối thiểu"
              />
              <input
                type="number"
                min={1}
                value={form.usageLimitPerUser}
                onChange={(event) => setForm((prev) => ({ ...prev, usageLimitPerUser: event.target.value }))}
                className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                placeholder="Giới hạn dùng / khách"
              />
              <select
                value={form.phamViApDung}
                onChange={(event) => setForm((prev) => ({ ...prev, phamViApDung: event.target.value }))}
                className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                required
              >
                <option value="TOAN_SAN">Toàn sàn</option>
                <option value="DOI_TAC">Đối tác</option>
                <option value="CO_SO_CU_THE">Cơ sở cụ thể</option>
                <option value="DICH_VU_CU_THE">Dịch vụ cụ thể</option>
              </select>
              <input
                type="number"
                min={0}
                value={form.diemCanDoi}
                onChange={(event) => setForm((prev) => ({ ...prev, diemCanDoi: event.target.value }))}
                className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
                placeholder="Điểm cần đổi"
              />
              <label className="flex items-center gap-2 rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm">
                <input
                  type="checkbox"
                  checked={form.choPhepDoiBangDiem}
                  onChange={(event) => setForm((prev) => ({ ...prev, choPhepDoiBangDiem: event.target.checked }))}
                />
                Cho phép đổi bằng điểm
              </label>
            </>
          )}

          <div className="md:col-span-2 flex justify-end">
            <button
              type="submit"
              disabled={submitting || loading}
              className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
            >
              {submitting ? 'Đang tạo...' : 'Tạo campaign'}
            </button>
          </div>
        </form>
      </section>

      <section className="panel">
        <h2 className="mb-4 text-lg font-semibold text-primary">Danh sách campaign</h2>
        {loading && <p className="text-sm text-on-surface-variant">Đang tải campaign...</p>}
        {!loading && sortedPromotions.length === 0 && <p className="text-sm text-on-surface-variant">Chưa có campaign nào.</p>}
        <div className="space-y-3">
          {sortedPromotions.map((promotion) => (
            <article key={promotion.id} className="rounded-lg border border-outline-variant/40 p-4">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <h3 className="font-semibold text-on-surface">{promotion.tenUuDai}</h3>
                  <p className="text-sm text-on-surface-variant">
                    {promotion.loaiUuDai} • {promotion.loaiGiamGia === 'PHAN_TRAM' ? `${promotion.mucGiam}%` : formatVnd(promotion.mucGiam)}
                    {promotion.maVoucher ? ` • Mã: ${promotion.maVoucher}` : ''}
                  </p>
                  <p className="mt-1 text-xs text-on-surface-variant">
                    {new Date(promotion.ngayBatDau).toLocaleString('vi-VN')} → {new Date(promotion.ngayKetThuc).toLocaleString('vi-VN')}
                  </p>
                </div>
                <div className="flex flex-wrap gap-2">
                  <span className="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">
                    {promotion.trangThaiUuDai}
                  </span>
                  <button
                    type="button"
                    onClick={() => void loadAnalytics(promotion.id)}
                    className="rounded-md border border-primary px-3 py-1.5 text-xs font-semibold text-primary"
                  >
                    Xem analytics
                  </button>
                  <button
                    type="button"
                    disabled={
                      actionBusyId === promotion.id ||
                      (promotion.trangThaiUuDai !== 'DANG_CO_HIEU_LUC' &&
                        promotion.trangThaiUuDai !== 'TAM_DUNG')
                    }
                    onClick={() => void handlePauseResume(promotion)}
                    className="rounded-md bg-secondary px-3 py-1.5 text-xs font-semibold text-on-secondary disabled:opacity-60"
                  >
                    {actionBusyId === promotion.id
                      ? 'Đang xử lý...'
                      : promotion.trangThaiUuDai === 'DANG_CO_HIEU_LUC'
                        ? 'Tạm dừng'
                        : promotion.trangThaiUuDai === 'TAM_DUNG'
                          ? 'Kích hoạt lại'
                          : promotion.trangThaiUuDai === 'DA_HET_HAN'
                            ? 'Đã hết hạn'
                            : 'Đã lên lịch'}
                  </button>
                </div>
              </div>
            </article>
          ))}
        </div>
      </section>

      <section className="panel">
        <h2 className="mb-4 text-lg font-semibold text-primary">Campaign analytics</h2>
        {!selectedPromotionId && <p className="text-sm text-on-surface-variant">Chọn một campaign để xem analytics.</p>}
        {selectedPromotionId && summary && (
          <div className="space-y-4">
            <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
              <article className="rounded-lg border border-outline-variant/40 p-4">
                <p className="text-xs text-on-surface-variant">Số lượt dùng voucher</p>
                <p className="mt-1 text-xl font-bold text-primary">{summary.usageCount}</p>
              </article>
              <article className="rounded-lg border border-outline-variant/40 p-4">
                <p className="text-xs text-on-surface-variant">Đơn hàng chuyển đổi</p>
                <p className="mt-1 text-xl font-bold text-primary">{summary.bookingCount}</p>
              </article>
              <article className="rounded-lg border border-outline-variant/40 p-4">
                <p className="text-xs text-on-surface-variant">Conversion rate</p>
                <p className="mt-1 text-xl font-bold text-primary">{summary.conversionRate.toFixed(2)}%</p>
              </article>
              <article className="rounded-lg border border-outline-variant/40 p-4">
                <p className="text-xs text-on-surface-variant">Doanh thu tạo ra</p>
                <p className="mt-1 text-xl font-bold text-primary">{formatVnd(summary.generatedRevenue)}</p>
              </article>
              <article className="rounded-lg border border-outline-variant/40 p-4">
                <p className="text-xs text-on-surface-variant">Chi phí giảm giá</p>
                <p className="mt-1 text-xl font-bold text-primary">{formatVnd(summary.discountCost)}</p>
              </article>
            </div>

            <div className="overflow-x-auto rounded-lg border border-outline-variant/40">
              <table className="min-w-full text-sm">
                <thead className="bg-surface-container-low">
                  <tr>
                    <th className="px-3 py-2 text-left">Ngày</th>
                    <th className="px-3 py-2 text-right">Usage</th>
                    <th className="px-3 py-2 text-right">Bookings</th>
                    <th className="px-3 py-2 text-right">Revenue</th>
                    <th className="px-3 py-2 text-right">Discount cost</th>
                    <th className="px-3 py-2 text-right">CR</th>
                  </tr>
                </thead>
                <tbody>
                  {daily.length === 0 && (
                    <tr>
                      <td colSpan={6} className="px-3 py-4 text-center text-on-surface-variant">
                        Chưa có dữ liệu analytics theo ngày.
                      </td>
                    </tr>
                  )}
                  {daily.map((item) => (
                    <tr key={item.ngay} className="border-t border-outline-variant/30">
                      <td className="px-3 py-2">{toDateTimeLocal(item.ngay).slice(0, 10)}</td>
                      <td className="px-3 py-2 text-right">{item.usageCount}</td>
                      <td className="px-3 py-2 text-right">{item.bookingCount}</td>
                      <td className="px-3 py-2 text-right">{formatVnd(item.generatedRevenue)}</td>
                      <td className="px-3 py-2 text-right">{formatVnd(item.discountCost)}</td>
                      <td className="px-3 py-2 text-right">{item.conversionRate.toFixed(2)}%</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </section>
    </div>
  )
}
