import { useCallback, useEffect, useMemo, useState } from 'react'
import { CalendarClock, RefreshCw, Search } from 'lucide-react'
import { partnerAssetService } from '../../services/partnerAssetService'
import type {
  PartnerBookingDetail,
  PartnerBookingListItem,
  PartnerBookingStatus,
} from '../../types/asset'
import { getApiErrorMessage } from '../../utils/apiError'

type BookingAction = {
  code: string
  label: string
  targetStatus: PartnerBookingStatus
  needsReason?: boolean
}

const STATUS_OPTIONS: Array<{ value: PartnerBookingStatus; label: string }> = [
  { value: 'CHO_THANH_TOAN', label: 'Chờ thanh toán' },
  { value: 'DA_THANH_TOAN', label: 'Đã thanh toán' },
  { value: 'DA_XAC_NHAN', label: 'Đã xác nhận' },
  { value: 'DANG_PHUC_VU', label: 'Đang phục vụ' },
  { value: 'DA_HOAN_THANH', label: 'Hoàn thành' },
  { value: 'DA_HUY', label: 'Đã hủy' },
]

const ACTION_MAP: Record<string, BookingAction> = {
  CONFIRM: { code: 'CONFIRM', label: 'Xác nhận', targetStatus: 'DA_XAC_NHAN' },
  START_SERVICE: { code: 'START_SERVICE', label: 'Bắt đầu phục vụ', targetStatus: 'DANG_PHUC_VU' },
  COMPLETE: { code: 'COMPLETE', label: 'Hoàn thành', targetStatus: 'DA_HOAN_THANH' },
  REJECT: { code: 'REJECT', label: 'Từ chối', targetStatus: 'DA_HUY', needsReason: true },
}

function formatDateTime(value?: string | null) {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--'
  return date.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function formatDate(value?: string | null) {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--'
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

function formatCurrency(value?: number | null) {
  if (typeof value !== 'number') return '--'
  return value.toLocaleString('vi-VN') + ' đ'
}

function statusLabel(status: PartnerBookingStatus) {
  return STATUS_OPTIONS.find((item) => item.value === status)?.label ?? status
}

function resolveBookingDate(item: PartnerBookingListItem) {
  if (item.serviceType === 'KHACH_SAN') {
    return item.ngayCheckIn ?? item.ngayBatDau ?? item.ngayTao
  }
  return item.ngayBatDau ?? item.ngayTao
}

export function PartnerBookingsPage() {
  const [bookings, setBookings] = useState<PartnerBookingListItem[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const [selectedStatuses, setSelectedStatuses] = useState<PartnerBookingStatus[]>([])
  const [fromDate, setFromDate] = useState('')
  const [toDate, setToDate] = useState('')
  const [keywordInput, setKeywordInput] = useState('')
  const [keyword, setKeyword] = useState('')

  const [page, setPage] = useState(0)
  const [size] = useState(20)
  const [totalPages, setTotalPages] = useState(1)
  const [totalElements, setTotalElements] = useState(0)

  const [selectedBookingId, setSelectedBookingId] = useState<string | null>(null)
  const [detail, setDetail] = useState<PartnerBookingDetail | null>(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [actionLoading, setActionLoading] = useState(false)

  const [rejectModalOpen, setRejectModalOpen] = useState(false)
  const [rejectReason, setRejectReason] = useState('')
  const [pendingRejectAction, setPendingRejectAction] = useState<BookingAction | null>(null)

  const loadBookings = useCallback(async () => {
    try {
      setLoading(true)
      setError('')
      const response = await partnerAssetService.getPartnerBookings({
        status: selectedStatuses.length > 0 ? selectedStatuses : undefined,
        fromDate: fromDate || undefined,
        toDate: toDate || undefined,
        keyword: keyword || undefined,
        page,
        size,
        sort: 'ngayTao,desc',
      })
      setBookings(response.content ?? [])
      setTotalPages(Math.max(response.totalPages ?? 1, 1))
      setTotalElements(response.totalElements ?? 0)
    } catch (fetchError) {
      setError(getApiErrorMessage(fetchError, 'Khong the tai danh sach don dat cho'))
      setBookings([])
      setTotalPages(1)
      setTotalElements(0)
    } finally {
      setLoading(false)
    }
  }, [fromDate, keyword, page, selectedStatuses, size, toDate])

  const loadBookingDetail = useCallback(async (bookingId: string) => {
    try {
      setDetailLoading(true)
      const response = await partnerAssetService.getPartnerBookingDetail(bookingId)
      setDetail(response)
      setSelectedBookingId(bookingId)
    } catch (fetchError) {
      setError(getApiErrorMessage(fetchError, 'Khong the tai chi tiet don'))
    } finally {
      setDetailLoading(false)
    }
  }, [])

  useEffect(() => {
    void loadBookings()
  }, [loadBookings])

  const actions = useMemo(() => {
    if (!detail) return []
    return detail.allowedActions
      .map((actionCode) => ACTION_MAP[actionCode])
      .filter((action): action is BookingAction => Boolean(action))
  }, [detail])

  const toggleStatus = (status: PartnerBookingStatus) => {
    setPage(0)
    setSelectedStatuses((current) =>
      current.includes(status) ? current.filter((item) => item !== status) : [...current, status],
    )
  }

  const handleSearch = () => {
    setPage(0)
    setKeyword(keywordInput.trim())
  }

  const handleUpdateStatus = async (action: BookingAction, reason?: string) => {
    if (!selectedBookingId) return
    try {
      setActionLoading(true)
      await partnerAssetService.updatePartnerBookingStatus(selectedBookingId, {
        targetStatus: action.targetStatus,
        reason,
      })
      await Promise.all([loadBookings(), loadBookingDetail(selectedBookingId)])
    } catch (updateError) {
      setError(getApiErrorMessage(updateError, 'Cap nhat trang thai don that bai'))
    } finally {
      setActionLoading(false)
    }
  }

  const openRejectModal = (action: BookingAction) => {
    setPendingRejectAction(action)
    setRejectReason('')
    setRejectModalOpen(true)
  }

  const submitReject = async () => {
    if (!pendingRejectAction) return
    const trimmedReason = rejectReason.trim()
    if (!trimmedReason) {
      setError('Ly do tu choi don la bat buoc')
      return
    }
    setRejectModalOpen(false)
    await handleUpdateStatus(pendingRejectAction, trimmedReason)
  }

  return (
    <div className="space-y-6 p-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-on-surface">Quản lý đơn đặt chỗ</h1>
          <p className="text-sm text-on-surface-variant">Theo dõi và xử lý đơn khách sạn/nhà hàng theo thời gian thực.</p>
        </div>
        <button
          type="button"
          onClick={() => void loadBookings()}
          className="inline-flex items-center gap-2 rounded-lg border border-outline px-3 py-2 text-sm"
        >
          <RefreshCw size={16} />
          Làm mới
        </button>
      </div>

      <section className="rounded-xl border border-outline/30 bg-white p-4">
        <div className="grid gap-3 md:grid-cols-5">
          <label className="text-sm">
            <span className="mb-1 block text-on-surface-variant">Từ ngày</span>
            <input type="date" value={fromDate} onChange={(event) => { setPage(0); setFromDate(event.target.value) }} className="w-full rounded-lg border border-outline/40 px-3 py-2" />
          </label>
          <label className="text-sm">
            <span className="mb-1 block text-on-surface-variant">Đến ngày</span>
            <input type="date" value={toDate} onChange={(event) => { setPage(0); setToDate(event.target.value) }} className="w-full rounded-lg border border-outline/40 px-3 py-2" />
          </label>
          <label className="text-sm md:col-span-2">
            <span className="mb-1 block text-on-surface-variant">Tìm theo mã đơn / tên khách / SĐT</span>
            <div className="flex gap-2">
              <input
                value={keywordInput}
                onChange={(event) => setKeywordInput(event.target.value)}
                onKeyDown={(event) => {
                  if (event.key === 'Enter') {
                    event.preventDefault()
                    handleSearch()
                  }
                }}
                className="w-full rounded-lg border border-outline/40 px-3 py-2"
                placeholder="VD: DKS202606..."
              />
              <button type="button" onClick={handleSearch} className="rounded-lg border border-outline/40 px-3">
                <Search size={16} />
              </button>
            </div>
          </label>
          <div className="text-sm">
            <span className="mb-1 block text-on-surface-variant">Bộ lọc trạng thái</span>
            <div className="max-h-28 overflow-auto rounded-lg border border-outline/40 px-2 py-1">
              {STATUS_OPTIONS.map((status) => (
                <label key={status.value} className="flex items-center gap-2 py-1">
                  <input
                    type="checkbox"
                    checked={selectedStatuses.includes(status.value)}
                    onChange={() => toggleStatus(status.value)}
                  />
                  <span>{status.label}</span>
                </label>
              ))}
            </div>
          </div>
        </div>
      </section>

      {error ? <div className="rounded-lg border border-red-300 bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div> : null}

      <section className="rounded-xl border border-outline/30 bg-white p-4">
        <div className="mb-3 flex items-center justify-between text-sm text-on-surface-variant">
          <span>Tổng: {totalElements} đơn</span>
          <span>Trang {page + 1}/{totalPages}</span>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead>
              <tr className="border-b border-outline/30 text-left">
                <th className="px-2 py-2">Mã đơn</th>
                <th className="px-2 py-2">Loại</th>
                <th className="px-2 py-2">Tài sản</th>
                <th className="px-2 py-2">Khách</th>
                <th className="px-2 py-2">Ngày sử dụng</th>
                <th className="px-2 py-2">Tổng tiền</th>
                <th className="px-2 py-2">Trạng thái</th>
                <th className="px-2 py-2"></th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={8} className="px-2 py-6 text-center text-on-surface-variant">Đang tải dữ liệu...</td></tr>
              ) : bookings.length === 0 ? (
                <tr><td colSpan={8} className="px-2 py-6 text-center text-on-surface-variant">Không có đơn phù hợp</td></tr>
              ) : bookings.map((item) => (
                <tr key={item.id} className="border-b border-outline/20">
                  <td className="px-2 py-2 font-semibold">{item.maDon}</td>
                  <td className="px-2 py-2">{item.serviceType === 'KHACH_SAN' ? 'Khách sạn' : 'Nhà hàng'}</td>
                  <td className="px-2 py-2">{item.tenTaiSan || '--'}</td>
                  <td className="px-2 py-2">{item.tenNguoiDat || '--'}</td>
                  <td className="px-2 py-2">{formatDateTime(resolveBookingDate(item))}</td>
                  <td className="px-2 py-2">{formatCurrency(item.tongTienThanhToan)}</td>
                  <td className="px-2 py-2">{statusLabel(item.trangThai)}</td>
                  <td className="px-2 py-2 text-right">
                    <button className="rounded-md border border-outline/40 px-2 py-1" onClick={() => void loadBookingDetail(item.id)}>
                      Chi tiết
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="mt-4 flex items-center justify-end gap-2">
          <button
            type="button"
            disabled={page <= 0 || loading}
            onClick={() => setPage((current) => Math.max(current - 1, 0))}
            className="rounded-md border border-outline/40 px-3 py-1 disabled:opacity-40"
          >
            Trước
          </button>
          <button
            type="button"
            disabled={page + 1 >= totalPages || loading}
            onClick={() => setPage((current) => current + 1)}
            className="rounded-md border border-outline/40 px-3 py-1 disabled:opacity-40"
          >
            Sau
          </button>
        </div>
      </section>

      {selectedBookingId ? (
        <div className="fixed inset-0 z-50 flex justify-end bg-black/40">
          <div className="h-full w-full max-w-2xl overflow-y-auto bg-white p-6">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-xl font-bold">Chi tiết đơn</h2>
              <button onClick={() => { setSelectedBookingId(null); setDetail(null) }} className="rounded border px-2 py-1">Đóng</button>
            </div>

            {detailLoading || !detail ? (
              <div className="py-10 text-center text-on-surface-variant">Đang tải chi tiết...</div>
            ) : (
              <div className="space-y-4 text-sm">
                <div className="grid grid-cols-2 gap-3 rounded-lg border border-outline/30 p-3">
                  <p><strong>Mã đơn:</strong> {detail.maDon}</p>
                  <p><strong>Loại:</strong> {detail.serviceType === 'KHACH_SAN' ? 'Khách sạn' : 'Nhà hàng'}</p>
                  <p><strong>Trạng thái:</strong> {statusLabel(detail.trangThai)}</p>
                  <p><strong>Tạo lúc:</strong> {formatDateTime(detail.ngayTao)}</p>
                  <p><strong>Tài sản:</strong> {detail.tenTaiSan || '--'}</p>
                  <p><strong>Khách:</strong> {detail.tenNguoiDat || '--'}</p>
                  <p><strong>SĐT:</strong> {detail.sdtNguoiDat || '--'}</p>
                  <p><strong>Email:</strong> {detail.emailNguoiDat || '--'}</p>
                </div>

                <div className="rounded-lg border border-outline/30 p-3">
                  <p><strong>Địa chỉ:</strong> {detail.diaChiTaiSan || '--'}</p>
                  <p><strong>Ghi chú:</strong> {detail.ghiChu || '--'}</p>
                  <p><strong>Tổng tiền:</strong> {formatCurrency(detail.tongTienThanhToan)}</p>
                  {detail.serviceType === 'NHA_HANG' ? (
                    <p><strong>Tiền cọc:</strong> {formatCurrency(detail.tienCoc)}</p>
                  ) : null}
                </div>

                {detail.serviceType === 'KHACH_SAN' ? (
                  <div className="rounded-lg border border-outline/30 p-3">
                    <h3 className="mb-2 flex items-center gap-2 font-semibold"><CalendarClock size={16} /> Lịch đặt phòng</h3>
                    <p><strong>Check-in:</strong> {formatDate(detail.ngayCheckIn)}</p>
                    <p><strong>Check-out:</strong> {formatDate(detail.ngayCheckOut)}</p>
                    <p><strong>Giờ nhận phòng dự kiến:</strong> {detail.gioNhanPhongDuKien || '--'}</p>
                    <div className="mt-2 space-y-1">
                      {detail.rooms.length === 0 ? <p>Không có chi tiết phòng</p> : detail.rooms.map((room, index) => (
                        <p key={`${room.roomId || index}-${index}`}>
                          {room.tenPhong || 'Phòng'} - SL {room.soLuong ?? 0} - {formatCurrency(room.thanhTien ?? 0)}
                        </p>
                      ))}
                    </div>
                  </div>
                ) : (
                  <div className="rounded-lg border border-outline/30 p-3">
                    <h3 className="mb-2 flex items-center gap-2 font-semibold"><CalendarClock size={16} /> Lịch đặt bàn</h3>
                    <p><strong>Bắt đầu:</strong> {formatDateTime(detail.ngayGioBatDau)}</p>
                    <p><strong>Kết thúc:</strong> {formatDateTime(detail.ngayGioKetThuc)}</p>
                    <div className="mt-2 space-y-1">
                      {detail.tables.length === 0 ? <p>Không có thông tin bàn</p> : detail.tables.map((table, index) => (
                        <p key={`${table.tableId || index}-${index}`}>
                          {table.tenBan || 'Bàn'} ({table.soChoNgoi ?? '--'} chỗ) - {table.viTriSanh || '--'}
                        </p>
                      ))}
                    </div>
                  </div>
                )}

                {detail.cancelReason ? (
                  <div className="rounded-lg border border-red-300 bg-red-50 p-3 text-red-700">
                    <p><strong>Lý do hủy:</strong> {detail.cancelReason}</p>
                  </div>
                ) : null}

                <div className="flex flex-wrap gap-2 border-t border-outline/20 pt-4">
                  {actions.length === 0 ? (
                    <p className="text-on-surface-variant">Đơn hiện không có hành động xử lý.</p>
                  ) : actions.map((action) => (
                    <button
                      key={action.code}
                      type="button"
                      disabled={actionLoading}
                      onClick={() => {
                        if (action.needsReason) {
                          openRejectModal(action)
                          return
                        }
                        void handleUpdateStatus(action)
                      }}
                      className="rounded-lg bg-primary px-3 py-2 text-sm font-semibold text-white disabled:opacity-40"
                    >
                      {action.label}
                    </button>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      ) : null}

      {rejectModalOpen ? (
        <div className="fixed inset-0 z-[60] flex items-center justify-center bg-black/40">
          <div className="w-full max-w-lg rounded-xl bg-white p-5">
            <h3 className="text-lg font-semibold">Nhập lý do từ chối đơn</h3>
            <textarea
              value={rejectReason}
              onChange={(event) => setRejectReason(event.target.value)}
              rows={4}
              maxLength={500}
              className="mt-3 w-full rounded-lg border border-outline/40 px-3 py-2"
              placeholder="Lý do từ chối là bắt buộc"
            />
            <div className="mt-4 flex justify-end gap-2">
              <button
                type="button"
                onClick={() => setRejectModalOpen(false)}
                className="rounded-lg border border-outline/40 px-3 py-2"
              >
                Hủy
              </button>
              <button
                type="button"
                onClick={() => void submitReject()}
                disabled={actionLoading}
                className="rounded-lg bg-red-600 px-3 py-2 text-white disabled:opacity-40"
              >
                Xác nhận từ chối
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  )
}
