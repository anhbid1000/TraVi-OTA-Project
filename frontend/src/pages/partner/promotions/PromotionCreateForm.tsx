import type { FormEvent } from 'react'
import type { BusinessProfileResponse } from '../../../types/asset'
import type { CampaignType, PromotionFormState } from './types'

type PromotionCreateFormProps = {
  profiles: BusinessProfileResponse[]
  form: PromotionFormState
  loading: boolean
  submitting: boolean
  isEditing: boolean
  onSubmit: (event: FormEvent<HTMLFormElement>) => void
  onCancelEdit: () => void
  onFormChange: (next: PromotionFormState | ((prev: PromotionFormState) => PromotionFormState)) => void
}

export function PromotionCreateForm({
  profiles,
  form,
  loading,
  submitting,
  isEditing,
  onSubmit,
  onCancelEdit,
  onFormChange,
}: PromotionCreateFormProps) {
  return (
    <section className="panel">
      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <h2 className="text-lg font-semibold text-primary">{isEditing ? 'Cập nhật campaign' : 'Tạo campaign mới'}</h2>
        {isEditing && (
          <button
            type="button"
            onClick={onCancelEdit}
            className="rounded-md border border-outline-variant px-3 py-1.5 text-xs font-semibold text-on-surface"
          >
            Hủy sửa
          </button>
        )}
      </div>

      <form onSubmit={onSubmit} className="grid grid-cols-1 gap-3 md:grid-cols-2">
        <select
          value={form.campaignType}
          onChange={(event) => onFormChange((prev) => ({ ...prev, campaignType: event.target.value as CampaignType }))}
          className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
          disabled={isEditing}
        >
          <option value="VOUCHER">Voucher campaign</option>
          <option value="DIRECT">Direct promotion</option>
        </select>

        <select
          value={form.businessProfileId}
          onChange={(event) => onFormChange((prev) => ({ ...prev, businessProfileId: event.target.value }))}
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
          onChange={(event) => onFormChange((prev) => ({ ...prev, tenUuDai: event.target.value }))}
          className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
          placeholder="Tên ưu đãi"
          required
        />
        <select
          value={form.loaiGiamGia}
          onChange={(event) =>
            onFormChange((prev) => ({ ...prev, loaiGiamGia: event.target.value as PromotionFormState['loaiGiamGia'] }))
          }
          className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
        >
          <option value="PHAN_TRAM">Giảm theo %</option>
          <option value="SO_TIEN_CO_DINH">Giảm số tiền cố định</option>
        </select>
        <input
          type="number"
          min={0}
          value={form.mucGiam}
          onChange={(event) => onFormChange((prev) => ({ ...prev, mucGiam: event.target.value }))}
          className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
          placeholder="Mức giảm"
          required
        />
        <input
          type="number"
          min={0}
          value={form.giaTriGiamToiDa}
          onChange={(event) => onFormChange((prev) => ({ ...prev, giaTriGiamToiDa: event.target.value }))}
          className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
          placeholder="Giảm tối đa"
        />
        <input
          type="datetime-local"
          value={form.ngayBatDau}
          onChange={(event) => onFormChange((prev) => ({ ...prev, ngayBatDau: event.target.value }))}
          className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
          required
        />
        <input
          type="datetime-local"
          value={form.ngayKetThuc}
          onChange={(event) => onFormChange((prev) => ({ ...prev, ngayKetThuc: event.target.value }))}
          className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
          required
        />
        <textarea
          value={form.moTa}
          onChange={(event) => onFormChange((prev) => ({ ...prev, moTa: event.target.value }))}
          className="md:col-span-2 rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
          rows={3}
          placeholder="Mô tả chiến dịch"
        />

        {form.campaignType === 'VOUCHER' && (
          <>
            <input
              value={form.maVoucher}
              onChange={(event) => onFormChange((prev) => ({ ...prev, maVoucher: event.target.value.toUpperCase() }))}
              className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm disabled:bg-surface-container"
              placeholder="Mã voucher"
              required
              disabled={isEditing}
            />
            <input
              type="number"
              min={1}
              value={form.soLuongPhatHanh}
              onChange={(event) => onFormChange((prev) => ({ ...prev, soLuongPhatHanh: event.target.value }))}
              className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
              placeholder="Số lượng phát hành"
              required
            />
            <input
              type="number"
              min={0}
              value={form.donHangToiThieu}
              onChange={(event) => onFormChange((prev) => ({ ...prev, donHangToiThieu: event.target.value }))}
              className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
              placeholder="Đơn hàng tối thiểu"
            />
            <input
              type="number"
              min={1}
              value={form.usageLimitPerUser}
              onChange={(event) => onFormChange((prev) => ({ ...prev, usageLimitPerUser: event.target.value }))}
              className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
              placeholder="Giới hạn dùng / khách"
            />
            <select
              value={form.phamViApDung}
              onChange={(event) => onFormChange((prev) => ({ ...prev, phamViApDung: event.target.value }))}
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
              onChange={(event) => onFormChange((prev) => ({ ...prev, diemCanDoi: event.target.value }))}
              className="rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm"
              placeholder="Điểm cần đổi"
            />
            <label className="flex items-center gap-2 rounded-lg border border-outline-variant bg-white px-3 py-2.5 text-sm">
              <input
                type="checkbox"
                checked={form.choPhepDoiBangDiem}
                onChange={(event) => onFormChange((prev) => ({ ...prev, choPhepDoiBangDiem: event.target.checked }))}
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
            {submitting ? 'Đang xử lý...' : isEditing ? 'Cập nhật campaign' : 'Tạo campaign'}
          </button>
        </div>
      </form>
    </section>
  )
}
