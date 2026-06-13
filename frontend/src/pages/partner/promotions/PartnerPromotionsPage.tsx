import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react'
import { partnerAssetService } from '../../../services/partnerAssetService'
import {
  partnerPromotionService,
  type PromotionAnalyticsDaily,
  type PromotionAnalyticsSummary,
  type PromotionResponse,
} from '../../../services/partnerPromotionService'
import type { BusinessProfileResponse } from '../../../types/asset'
import '../../dashboard.css'
import { PromotionAnalyticsPanel } from './PromotionAnalyticsPanel'
import { PromotionCreateForm } from './PromotionCreateForm'
import { PromotionList } from './PromotionList'
import { initialPromotionFormState, type PromotionFormState } from './types'

function toDateTimeLocalValue(value: string) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return ''
  }

  const offsetMs = date.getTimezoneOffset() * 60_000
  return new Date(date.getTime() - offsetMs).toISOString().slice(0, 16)
}

export default function PartnerPromotionsPage() {
  const [profiles, setProfiles] = useState<BusinessProfileResponse[]>([])
  const [promotions, setPromotions] = useState<PromotionResponse[]>([])
  const [selectedPromotionId, setSelectedPromotionId] = useState<number | null>(null)
  const [editingPromotionId, setEditingPromotionId] = useState<number | null>(null)
  const [summary, setSummary] = useState<PromotionAnalyticsSummary | null>(null)
  const [daily, setDaily] = useState<PromotionAnalyticsDaily[]>([])
  const [form, setForm] = useState<PromotionFormState>(initialPromotionFormState)
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

  const resetForm = (businessProfileId?: string) => {
    setEditingPromotionId(null)
    setForm((prev) => ({
      ...initialPromotionFormState,
      businessProfileId: businessProfileId ?? prev.businessProfileId,
    }))
  }

  const handleEdit = (promotion: PromotionResponse) => {
    setError(null)
    setSuccess('')
    setEditingPromotionId(promotion.id)
    setForm({
      campaignType: promotion.loaiUuDai === 'VOUCHER' ? 'VOUCHER' : 'DIRECT',
      tenUuDai: promotion.tenUuDai ?? '',
      moTa: promotion.moTa ?? '',
      businessProfileId: promotion.businessProfileId ?? profiles[0]?.idHoSo ?? '',
      mucGiam: String(promotion.mucGiam ?? ''),
      loaiGiamGia: promotion.loaiGiamGia === 'SO_TIEN_CO_DINH' ? 'SO_TIEN_CO_DINH' : 'PHAN_TRAM',
      giaTriGiamToiDa: promotion.giaTriGiamToiDa != null ? String(promotion.giaTriGiamToiDa) : '',
      ngayBatDau: toDateTimeLocalValue(promotion.ngayBatDau),
      ngayKetThuc: toDateTimeLocalValue(promotion.ngayKetThuc),
      maVoucher: promotion.maVoucher ?? '',
      soLuongPhatHanh: promotion.soLuongPhatHanh != null ? String(promotion.soLuongPhatHanh) : '',
      donHangToiThieu: promotion.donHangToiThieu != null ? String(promotion.donHangToiThieu) : '',
      usageLimitPerUser: promotion.usageLimitPerUser != null ? String(promotion.usageLimitPerUser) : '',
      diemCanDoi: promotion.diemCanDoi != null ? String(promotion.diemCanDoi) : '',
      choPhepDoiBangDiem: Boolean(promotion.choPhepDoiBangDiem),
      phamViApDung: promotion.phamViApDung ?? 'TOAN_SAN',
    })
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
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

      if (editingPromotionId) {
        await partnerPromotionService.updatePromotion(editingPromotionId, {
          ...payloadBase,
          targetType: 'ALL_PLATFORM',
          soLuongPhatHanh: form.campaignType === 'VOUCHER' ? Number(form.soLuongPhatHanh) : undefined,
          donHangToiThieu: form.donHangToiThieu ? Number(form.donHangToiThieu) : undefined,
          usageLimitPerUser: form.usageLimitPerUser ? Number(form.usageLimitPerUser) : undefined,
          diemCanDoi: form.diemCanDoi ? Number(form.diemCanDoi) : undefined,
          choPhepDoiBangDiem: form.choPhepDoiBangDiem,
          phamViApDung: form.campaignType === 'VOUCHER' ? form.phamViApDung : undefined,
        })
      } else if (form.campaignType === 'DIRECT') {
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

      const updatedPromotionId = editingPromotionId
      setSuccess(updatedPromotionId ? 'Cập nhật chiến dịch thành công.' : 'Tạo chiến dịch thành công.')
      resetForm(form.businessProfileId)
      await loadPageData()
      if (updatedPromotionId != null && updatedPromotionId === selectedPromotionId) {
        await loadAnalytics(updatedPromotionId)
      }
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : 'Không thể lưu chiến dịch.')
    } finally {
      setSubmitting(false)
    }
  }

  const handlePauseResume = async (promotion: PromotionResponse) => {
    const canAct = promotion.trangThaiUuDai === 'DANG_CO_HIEU_LUC' || promotion.trangThaiUuDai === 'TAM_DUNG'
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

      <PromotionCreateForm
        profiles={profiles}
        form={form}
        loading={loading}
        submitting={submitting}
        isEditing={editingPromotionId != null}
        onSubmit={handleSubmit}
        onCancelEdit={() => resetForm()}
        onFormChange={setForm}
      />
      <PromotionList
        promotions={sortedPromotions}
        loading={loading}
        actionBusyId={actionBusyId}
        onLoadAnalytics={(promotionId) => void loadAnalytics(promotionId)}
        onEdit={handleEdit}
        onPauseResume={(promotion) => void handlePauseResume(promotion)}
      />
      <PromotionAnalyticsPanel selectedPromotionId={selectedPromotionId} summary={summary} daily={daily} />
    </div>
  )
}
