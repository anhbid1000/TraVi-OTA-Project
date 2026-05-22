import { useEffect, useMemo, useRef, useState, type PointerEvent } from 'react'
import {
  AlertCircle,
  BarChart3,
  Bell,
  Building2,
  Check,
  CheckCircle2,
  ClipboardCheck,
  Download,
  FileText,
  Filter,
  HelpCircle,
  LayoutDashboard,
  LogOut,
  RotateCcw,
  Search,
  Settings,
  Sparkles,
  Users,
  X,
  XCircle,
  ZoomIn,
  ZoomOut,
} from 'lucide-react'
import { Table } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import 'antd/dist/reset.css'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { API_BASE_URL } from '../../services/api'
import { adminApprovalService } from '../../services/adminApprovalService'
import type { AdminApprovalResponse, ServiceType } from '../../types/asset'
import { getApiErrorMessage } from '../../utils/apiError'
import '../dashboard.css'

type ServiceFilter = 'ALL' | ServiceType
type RequestFilter = 'ALL' | 'NEW' | 'UPDATE'

const serviceLabels: Record<ServiceType, string> = {
  KHACH_SAN: 'HOTEL',
  NHA_HANG: 'RESTAURANT',
}

function formatDate(value?: string | null) {
  if (!value) return '-'
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

function money(value?: number) {
  if (typeof value !== 'number') return '-'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

function getDocumentUrl(path?: string) {
  if (!path) return ''
  if (/^https?:\/\//.test(path) || path.startsWith('data:image')) return path
  return `${API_BASE_URL.replace(/\/api$/, '')}${path.startsWith('/') ? path : `/${path}`}`
}

function isUpdateProfile(item: AdminApprovalResponse) {
  return Boolean(item.comparison)
}

function getPriority(item: AdminApprovalResponse) {
  const submittedAt = item.thoiGianDangKy ? new Date(item.thoiGianDangKy).getTime() : Date.now()
  const ageHours = (Date.now() - submittedAt) / 36e5
  return ageHours >= 48 ? 'Ưu tiên cao' : 'Trung bình'
}

export function AdminApprovalsPage() {
  const navigate = useNavigate()
  const { logout } = useAuth()
  const [approvals, setApprovals] = useState<AdminApprovalResponse[]>([])
  const [selectedApproval, setSelectedApproval] = useState<AdminApprovalResponse | null>(null)
  const [serviceFilter, setServiceFilter] = useState<ServiceFilter>('ALL')
  const [requestFilter, setRequestFilter] = useState<RequestFilter>('ALL')
  const [searchTerm, setSearchTerm] = useState('')
  const [rejectReason, setRejectReason] = useState('')
  const [rejectError, setRejectError] = useState('')
  const [isDetailOpen, setIsDetailOpen] = useState(false)
  const [isRejectOpen, setIsRejectOpen] = useState(false)
  const [documentZoom, setDocumentZoom] = useState(1)
  const [documentRotation, setDocumentRotation] = useState(0)
  const [documentOffset, setDocumentOffset] = useState({ x: 0, y: 0 })
  const [dragStart, setDragStart] = useState<{ x: number; y: number; baseX: number; baseY: number } | null>(null)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const viewerRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    loadApprovals()
  }, [])

  const filteredApprovals = useMemo(() => {
    const keyword = searchTerm.trim().toLowerCase()
    return approvals.filter((item) => {
      const matchesKeyword =
        !keyword ||
        [item.idHoSo, item.tenCoSo, item.doiTacHoTen, item.doiTacEmail, item.maSoThue]
          .filter(Boolean)
          .some((value) => String(value).toLowerCase().includes(keyword))
      const matchesService = serviceFilter === 'ALL' || item.loaiDichVu === serviceFilter
      const matchesRequest =
        requestFilter === 'ALL' ||
        (requestFilter === 'UPDATE' ? isUpdateProfile(item) : !isUpdateProfile(item))
      return matchesKeyword && matchesService && matchesRequest
    })
  }, [approvals, requestFilter, searchTerm, serviceFilter])

  const stats = useMemo(() => {
    const updates = approvals.filter(isUpdateProfile).length
    return {
      total: approvals.length,
      newProfiles: approvals.length - updates,
      updates,
      urgent: approvals.filter((item) => getPriority(item) === 'Ưu tiên cao').length,
    }
  }, [approvals])

  const selectedComparison = selectedApproval?.comparison
  const selectedComparisonRows = selectedApproval
    ? [
        ['Tên cơ sở', selectedComparison?.oldTenCoSo, selectedComparison?.newTenCoSo ?? selectedApproval.tenCoSo],
        ['Số điện thoại', selectedComparison?.oldSdtLienHe, selectedComparison?.newSdtLienHe ?? selectedApproval.sdtLienHe],
        ['Loại dịch vụ', selectedComparison?.oldLoaiDichVu, selectedComparison?.newLoaiDichVu ?? selectedApproval.loaiDichVu],
        ['Mã số thuế', selectedComparison?.oldMaSoThue, selectedComparison?.newMaSoThue ?? selectedApproval.maSoThue],
        ['Giấy phép', selectedComparison?.oldGiayPhepKinhDoanh, selectedComparison?.newGiayPhepKinhDoanh ?? selectedApproval.giayPhepKinhDoanh],
        ['Tọa độ GPS', selectedComparison?.oldToaDoGPS, selectedComparison?.newToaDoGPS ?? selectedApproval.toaDoGPS],
      ]
    : []

  async function loadApprovals() {
    setIsLoading(true)
    setError('')
    try {
      const response = await adminApprovalService.getPendingApprovals()
      setApprovals(response)
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tải danh sách hồ sơ chờ duyệt.'))
    } finally {
      setIsLoading(false)
    }
  }

  async function openDetail(id: string) {
    setError('')
    try {
      const detail = await adminApprovalService.getApprovalDetail(id)
      setSelectedApproval(detail)
      setDocumentZoom(1)
      setDocumentRotation(0)
      setDocumentOffset({ x: 0, y: 0 })
      setIsDetailOpen(true)
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tải chi tiết hồ sơ.'))
    }
  }

  async function approveSelected() {
    if (!selectedApproval) return
    setError('')
    try {
      const updated = await adminApprovalService.updateApprovalStatus(selectedApproval.idHoSo, 'APPROVED')
      setApprovals((current) => current.filter((item) => item.idHoSo !== updated.idHoSo))
      setSelectedApproval(null)
      setIsDetailOpen(false)
      setMessage('Hồ sơ đã được phê duyệt.')
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể phê duyệt hồ sơ.'))
    }
  }

  async function rejectSelected() {
    if (!selectedApproval) return
    if (!rejectReason.trim()) {
      setRejectError('Vui lòng nhập lý do từ chối trước khi submit.')
      return
    }
    setError('')
    setRejectError('')
    try {
      const updated = await adminApprovalService.updateApprovalStatus(
        selectedApproval.idHoSo,
        'REJECTED',
        rejectReason.trim(),
      )
      setApprovals((current) => current.filter((item) => item.idHoSo !== updated.idHoSo))
      setSelectedApproval(null)
      setIsRejectOpen(false)
      setIsDetailOpen(false)
      setRejectReason('')
      setMessage('Hồ sơ đã bị từ chối và lý do đã được gửi cho đối tác.')
    } catch (err) {
      setRejectError(getApiErrorMessage(err, 'Không thể từ chối hồ sơ.'))
    }
  }

  async function handleLogout() {
    await logout()
    navigate('/admin/login', { replace: true })
  }

  function startPan(event: PointerEvent<HTMLDivElement>) {
    if (documentZoom <= 1) return
    viewerRef.current?.setPointerCapture(event.pointerId)
    setDragStart({
      x: event.clientX,
      y: event.clientY,
      baseX: documentOffset.x,
      baseY: documentOffset.y,
    })
  }

  function movePan(event: PointerEvent<HTMLDivElement>) {
    if (!dragStart) return
    setDocumentOffset({
      x: dragStart.baseX + event.clientX - dragStart.x,
      y: dragStart.baseY + event.clientY - dragStart.y,
    })
  }

  function endPan() {
    setDragStart(null)
  }

  const documentUrl = getDocumentUrl(selectedApproval?.giayPhepKinhDoanh)
  const selectedAsset = selectedApproval?.danhSachTaiSan?.[0]
  const canPreviewImage = Boolean(documentUrl && !documentUrl.toLowerCase().endsWith('.pdf'))
  const columns: ColumnsType<AdminApprovalResponse> = [
    {
      title: 'Mã hồ sơ',
      dataIndex: 'idHoSo',
      key: 'idHoSo',
      width: 150,
    },
    {
      title: 'Tên cơ sở',
      dataIndex: 'tenCoSo',
      key: 'tenCoSo',
      render: (_, approval) => (
        <div className="facility-cell">
          <div className="facility-thumb">{approval.loaiDichVu === 'KHACH_SAN' ? 'KS' : 'NH'}</div>
          <span>{approval.tenCoSo}</span>
        </div>
      ),
    },
    {
      title: 'Tên đối tác',
      dataIndex: 'doiTacHoTen',
      key: 'doiTacHoTen',
      render: (_, approval) => approval.doiTacHoTen || approval.doiTacEmail,
    },
    {
      title: 'Loại dịch vụ',
      dataIndex: 'loaiDichVu',
      key: 'loaiDichVu',
      width: 130,
      render: (value: ServiceType) => <span className="type-chip">{serviceLabels[value]}</span>,
    },
    {
      title: 'Ngày gửi duyệt',
      dataIndex: 'thoiGianDangKy',
      key: 'thoiGianDangKy',
      width: 160,
      render: (value: string) => formatDate(value),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'trangThaiKiemDuyet',
      key: 'trangThaiKiemDuyet',
      width: 120,
      render: () => <span className="status-chip pending">Chờ duyệt</span>,
    },
    {
      title: 'Mức ưu tiên',
      key: 'priority',
      width: 130,
      render: (_, approval) => (
        <span className={getPriority(approval) === 'Ưu tiên cao' ? 'priority-chip high' : 'priority-chip'}>
          {getPriority(approval)}
        </span>
      ),
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 120,
      render: (_, approval) => (
        <button className="link-action" onClick={() => openDetail(approval.idHoSo)}>
          Xem chi tiết
        </button>
      ),
    },
  ]

  return (
    <div className="admin-dashboard-shell">
      <aside className="admin-sidebar">
        <div className="admin-brand">
          <strong>TraVi Admin</strong>
          <span>Management Portal</span>
        </div>
        <nav className="admin-nav">
          <button>
            <LayoutDashboard size={18} /> Overview
          </button>
          <button className="active">
            <ClipboardCheck size={18} /> Partner Approvals
          </button>
          <button>
            <BarChart3 size={18} /> Revenue Analytics
          </button>
          <button>
            <Users size={18} /> User Management
          </button>
          <button>
            <Sparkles size={18} /> AI Configuration
          </button>
        </nav>
        <div className="admin-sidebar-footer">
          <span className="system-status">System Status: Active</span>
          <button>
            <Settings size={18} /> Settings
          </button>
          <button onClick={handleLogout}>
            <LogOut size={18} /> Đăng xuất
          </button>
        </div>
      </aside>

      <main className="admin-main">
        <header className="admin-topbar">
          <div className="admin-search">
            <Search size={16} />
            <input
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
              placeholder="Tìm kiếm hồ sơ, đối tác..."
            />
          </div>
          <div className="admin-topbar-actions">
            <Bell size={18} />
            <HelpCircle size={18} />
            <div className="admin-user-pill">
              <span>Administrator</span>
              <strong>SUPER ADMIN</strong>
            </div>
            <div className="admin-avatar">AD</div>
          </div>
        </header>

        <section className="admin-page-heading">
          <h1>Hồ sơ Đối tác chờ duyệt</h1>
          <p>Xem xét và phê duyệt các hồ sơ kinh doanh mới hoặc hồ sơ cập nhật từ các nhà cung cấp dịch vụ du lịch.</p>
        </section>

        {(message || error) && (
          <div className={error ? 'alert error' : 'alert success'}>{error || message}</div>
        )}

        <section className="admin-metric-grid">
          <div className="admin-metric-card">
            <span className="metric-icon green"><ClipboardCheck size={22} /></span>
            <small>+12%</small>
            <strong>{stats.total}</strong>
            <p>Tổng hồ sơ chờ duyệt</p>
          </div>
          <div className="admin-metric-card">
            <span className="metric-icon mint"><CheckCircle2 size={22} /></span>
            <small>Mới</small>
            <strong>{stats.newProfiles}</strong>
            <p>Hồ sơ đăng ký mới</p>
          </div>
          <div className="admin-metric-card">
            <span className="metric-icon amber"><RotateCcw size={22} /></span>
            <small>Update</small>
            <strong>{stats.updates}</strong>
            <p>Hồ sơ cập nhật lại</p>
          </div>
          <div className="admin-metric-card danger">
            <span className="metric-icon red"><XCircle size={22} /></span>
            <small>Cần lưu ý</small>
            <strong>{stats.urgent}</strong>
            <p>Hồ sơ cần xử lý gấp</p>
          </div>
        </section>

        <section className="approval-filter-bar">
          <label>
            <Filter size={17} />
            <select value={serviceFilter} onChange={(event) => setServiceFilter(event.target.value as ServiceFilter)}>
              <option value="ALL">Tất cả dịch vụ</option>
              <option value="KHACH_SAN">Khách sạn</option>
              <option value="NHA_HANG">Nhà hàng</option>
            </select>
          </label>
          <label>
            <Building2 size={17} />
            <select value={requestFilter} onChange={(event) => setRequestFilter(event.target.value as RequestFilter)}>
              <option value="ALL">Loại yêu cầu</option>
              <option value="NEW">Đăng ký mới</option>
              <option value="UPDATE">Cập nhật hồ sơ</option>
            </select>
          </label>
          <button className="admin-export-btn" onClick={loadApprovals}>
            <Download size={17} /> {isLoading ? 'Đang tải...' : 'Xuất báo cáo'}
          </button>
        </section>

        <section className="admin-table-panel">
          <Table
            className="admin-approval-ant-table"
            rowKey="idHoSo"
            columns={columns}
            dataSource={filteredApprovals}
            loading={isLoading}
            pagination={{
              pageSize: 10,
              showSizeChanger: false,
              showTotal: (total) => `Hiển thị ${total} của ${approvals.length} hồ sơ`,
            }}
            scroll={{ x: 980 }}
          />
        </section>
      </main>

      {isDetailOpen && selectedApproval && (
        <div className="approval-detail-overlay">
          <div className="approval-detail-modal">
            <header className="detail-topbar">
              <div>
                <p>Admin Dashboard › Hồ sơ đối tác › Chi tiết kiểm duyệt</p>
                <h2>Hồ sơ: {selectedApproval.doiTacHoTen || selectedApproval.tenCoSo}</h2>
              </div>
              <button className="close-btn" onClick={() => setIsDetailOpen(false)}>
                <X size={18} />
              </button>
            </header>

            <div className="detail-content-grid">
              <section className="detail-stack">
                <div className="detail-card">
                  <div className="detail-card-title">
                    <Users size={18} />
                    <h3>Thông tin đối tác</h3>
                    <span className="status-chip pending">Đang chờ duyệt</span>
                  </div>
                  <dl className="compact-detail-list">
                    <div><dt>Họ và tên</dt><dd>{selectedApproval.doiTacHoTen || '-'}</dd></div>
                    <div><dt>Email</dt><dd>{selectedApproval.doiTacEmail || '-'}</dd></div>
                    <div><dt>Số điện thoại</dt><dd>{selectedApproval.sdtLienHe}</dd></div>
                    <div><dt>Ngày đăng ký</dt><dd>{formatDate(selectedApproval.thoiGianDangKy)}</dd></div>
                  </dl>
                </div>

                <div className="detail-card">
                  <div className="detail-card-title">
                    <Building2 size={18} />
                    <h3>Thông tin cơ sở</h3>
                  </div>
                  <dl className="compact-detail-list">
                    <div><dt>Tên cơ sở</dt><dd>{selectedApproval.tenCoSo}</dd></div>
                    <div><dt>Loại dịch vụ</dt><dd>{serviceLabels[selectedApproval.loaiDichVu]}</dd></div>
                    <div><dt>Mã số thuế</dt><dd>{selectedApproval.maSoThue}</dd></div>
                    <div><dt>Địa chỉ/GPS</dt><dd>{selectedApproval.toaDoGPS}</dd></div>
                  </dl>
                </div>

                <div className="detail-card">
                  <div className="detail-card-title">
                    <FileText size={18} />
                    <h3>Chi tiết dịch vụ</h3>
                  </div>
                  <div className="rating-row">★ ★ ★ ★ ☆ <span>5 Sao</span></div>
                  <dl className="compact-detail-list">
                    <div><dt>Chính sách</dt><dd>{selectedApproval.chinhSach?.loaiChinhSach || '-'}</dd></div>
                    <div><dt>Nội dung</dt><dd>{selectedApproval.chinhSach?.noiDung || '-'}</dd></div>
                    <div><dt>Giá tham khảo</dt><dd>{money(selectedAsset?.giaCoBan)}</dd></div>
                  </dl>
                </div>
              </section>

              <section className="document-column">
                <div className="document-card">
                  <div className="document-toolbar">
                    <div>
                      <FileText size={18} />
                      <h3>Giấy phép kinh doanh (ĐKKD)</h3>
                    </div>
                    <div>
                      <button onClick={() => setDocumentZoom((value) => Math.min(2.4, value + 0.15))} title="Phóng to">
                        <ZoomIn size={17} />
                      </button>
                      <button onClick={() => setDocumentZoom((value) => Math.max(0.7, value - 0.15))} title="Thu nhỏ">
                        <ZoomOut size={17} />
                      </button>
                      <button onClick={() => setDocumentRotation((value) => value + 90)} title="Xoay">
                        <RotateCcw size={17} />
                      </button>
                      <a href={documentUrl || undefined} target="_blank" rel="noreferrer" className="download-doc-btn">
                        <Download size={17} /> Tải xuống
                      </a>
                    </div>
                  </div>

                  <div
                    ref={viewerRef}
                    className="license-viewer"
                    onPointerDown={startPan}
                    onPointerMove={movePan}
                    onPointerUp={endPan}
                    onPointerCancel={endPan}
                  >
                    {canPreviewImage ? (
                      <img
                        src={documentUrl}
                        alt="Giấy phép kinh doanh"
                        style={{
                          transform: `translate(${documentOffset.x}px, ${documentOffset.y}px) scale(${documentZoom}) rotate(${documentRotation}deg)`,
                        }}
                        draggable={false}
                      />
                    ) : (
                      <div
                        className="license-placeholder"
                        style={{
                          transform: `translate(${documentOffset.x}px, ${documentOffset.y}px) scale(${documentZoom}) rotate(${documentRotation}deg)`,
                        }}
                      >
                        <FileText size={64} />
                        <strong>{selectedApproval.giayPhepKinhDoanh || 'Chưa có giấy phép'}</strong>
                        <span>Admin có thể tải file gốc để kiểm tra nếu backend lưu PDF hoặc tên file.</span>
                      </div>
                    )}
                    <div className="document-note">
                      <AlertCircle size={16} />
                      Admin cần kiểm tra mã số thuế và thông tin pháp lý trên văn bản gốc này trước khi phê duyệt.
                    </div>
                  </div>
                </div>

                <div className="compare-card">
                  <div className="detail-card-title">
                    <RotateCcw size={18} />
                    <h3>So sánh dữ liệu thay đổi</h3>
                  </div>
                  <div className="compare-table">
                    <div className="compare-row header">
                      <span>Trường thông tin</span>
                      <span>Dữ liệu cũ</span>
                      <span>Dữ liệu mới</span>
                    </div>
                    {selectedComparisonRows.map(([label, oldValue, newValue]) => (
                      <div className="compare-row" key={label}>
                        <span>{label}</span>
                        <span>{oldValue || 'Chưa có'}</span>
                        <strong>{newValue || 'Chưa có'}</strong>
                      </div>
                    ))}
                  </div>
                </div>
              </section>
            </div>

            <footer className="approval-action-footer">
              <em>Thao tác cuối bởi: Admin Root (Vừa xong)</em>
              <div>
                <button className="secondary-btn" onClick={() => setIsDetailOpen(false)}>Đóng</button>
                <button className="danger-btn" onClick={() => setIsRejectOpen(true)}>
                  <X size={17} /> Từ Chối
                </button>
                <button className="primary-btn" onClick={approveSelected}>
                  <Check size={17} /> Phê Duyệt
                </button>
              </div>
            </footer>
          </div>
        </div>
      )}

      {isRejectOpen && selectedApproval && (
        <div className="modal-backdrop">
          <div className="modal-card small reject-modal">
            <button className="close-btn" onClick={() => setIsRejectOpen(false)}>
              <X size={18} />
            </button>
            <h2>Lý do từ chối</h2>
            <p>Nhập lý do rõ ràng để đối tác biết cần bổ sung hoặc chỉnh sửa phần nào.</p>
            <textarea
              className="reason-input"
              value={rejectReason}
              onChange={(event) => {
                setRejectReason(event.target.value)
                setRejectError('')
              }}
              placeholder="Ví dụ: Ảnh giấy phép kinh doanh bị mờ, mã số thuế không khớp với thông tin đăng ký..."
            />
            {rejectError && <div className="form-error-summary">{rejectError}</div>}
            <button className="danger-btn full-width" onClick={rejectSelected}>
              <XCircle size={18} /> Submit từ chối
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
