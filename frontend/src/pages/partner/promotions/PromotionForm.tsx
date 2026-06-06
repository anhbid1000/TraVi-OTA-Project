import type { FormEvent } from 'react'
import type { BusinessProfileResponse } from '../../../types/asset'
import type { PromotionFormState, CampaignType } from './types'

interface PromotionFormProps {
  form: PromotionFormState
  setForm: React.Dispatch<React.SetStateAction<PromotionFormState>>
  profiles: BusinessProfileResponse[]
  onSubmit: (event: FormEvent<HTMLFormElement>) => Promise<void>
  submitting: boolean
  loading: boolean
}

export function PromotionForm({
  form,
  setForm,
  profiles,
  onSubmit,
  submitting,
  loading,
}: PromotionFormProps) {
  return (
    <form onSubmit={(e) => { e.preventDefault(); void onSubmit(e); }} className="grid grid-cols-1 gap-3 md:grid-cols-2">
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
  )
}
