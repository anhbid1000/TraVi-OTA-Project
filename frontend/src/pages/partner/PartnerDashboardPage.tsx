import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useOutletContext, useSearchParams } from 'react-router-dom'
import L from 'leaflet'
import { MapContainer, Marker, TileLayer, useMapEvents } from 'react-leaflet'
import 'leaflet/dist/leaflet.css'
import {
  AlertTriangle,
  ArrowRight,
  Bed,
  Building2,
  CheckCircle2,
  Eye,
  FileText,
  Grid2X2,
  Hotel,
  LayoutGrid,
  MapPin,
  Pencil,
  Plus,
  Search,
  Trash2,
  UploadCloud,
  Utensils,
  X,
  XCircle,
} from 'lucide-react'
import heroImage from '../../assets/hero.png'
import { partnerAssetService } from '../../services/partnerAssetService'
import type {
  BusinessProfilePayload,
  BusinessProfileResponse,
  ComboPayload,
  ComboResponse,
  MenuItemResponse,
  MenuItemStatus,
  RoomPayload,
  RoomResponse,
  ServiceType,
  TableResponse,
} from '../../types/asset'
import { getApiErrorMessage } from '../../utils/apiError'
import '../dashboard.css'

type PartnerView =
  | 'business-profile'
  | 'hotel-setup'
  | 'room-management'
  | 'restaurant-setup'
  | 'table-layout'
  | 'menu-management'

type ProfileFormState = {
  tenCoSo: string
  sdtLienHe: string
  loaiDichVu: ServiceType
  maSoThue: string
  giayPhepKinhDoanh: string
  toaDoGPS: string
  moTa: string
  giaCoBan: string
  hangSao: string
  loaiKhachSan: string
  soTang: string
  tongSoPhong: string
  loaiAmThuc: string
  sucChua: string
  emailLienHe: string
  diaChi: string
  thanhPho: string
  quanHuyen: string
  phuongXa: string
  kinhDo: string
  viDo: string
  chinhSachHuy: string
  chinhSachHoanTien: string
  gioNhanPhong: string
  gioTraPhong: string
  gioMoCua: string
  gioDongCua: string
  quyDinhTreEm: string
  quyDinhVatNuoi: string
  ghiChuKhac: string
  tienIchKhachSan: string[]
  tienIchNhaHang: string[]
  danhSachAnh: Array<{url: string, moTa: string, laAnhDaiDien: boolean}>
}

type ProfileFormValue = ProfileFormState[keyof ProfileFormState]

type ProfileFormErrors = Partial<Record<keyof ProfileFormState | 'businessLicense', string>>

type UploadedAsset = {
  fileName: string
  url: string
  contentType: string
  size: number
}

type UploadOptions = {
  allowPdf?: boolean
}

const RESTAURANT_AREAS = ['Sảnh chính', 'Sân thượng', 'Phòng VIP', 'Phòng riêng']
const VIETNAM_MAJOR_CITIES = [
  'Hà Nội',
  'TP. Hồ Chí Minh',
  'Đà Nẵng',
  'Hải Phòng',
  'Cần Thơ',
  'Nha Trang',
  'Huế',
  'Đà Lạt',
  'Vũng Tàu',
  'Phú Quốc',
  'Quy Nhơn',
  'Hạ Long',
]
const RESTAURANT_AMENITY_OPTIONS = ['Đặt bàn online', 'Đặt món trước', 'Phòng riêng', 'Bãi đỗ xe', 'WiFi miễn phí', 'Thanh toán thẻ', 'Phục vụ ngoài trời', 'Mang về']
const MAX_UPLOAD_SIZE = 10 * 1024 * 1024
const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png']
const ALLOWED_LICENSE_TYPES = [...ALLOWED_IMAGE_TYPES, 'application/pdf']
const DEFAULT_MAP_POSITION: [number, number] = [10.7769, 106.7009]
const leafletMarkerIcon = new L.Icon({
  iconUrl: new URL('leaflet/dist/images/marker-icon.png', import.meta.url).href,
  iconRetinaUrl: new URL('leaflet/dist/images/marker-icon-2x.png', import.meta.url).href,
  shadowUrl: new URL('leaflet/dist/images/marker-shadow.png', import.meta.url).href,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
})

const initialProfileForm: ProfileFormState = {
  tenCoSo: '',
  sdtLienHe: '',
  loaiDichVu: 'KHACH_SAN',
  maSoThue: '',
  giayPhepKinhDoanh: '',
  toaDoGPS: '10.77690,106.70090',
  moTa: '',
  giaCoBan: '500000',
  hangSao: '4',
  loaiKhachSan: 'Khách sạn',
  soTang: '1',
  tongSoPhong: '1',
  loaiAmThuc: 'Ẩm thực Việt',
  sucChua: '80',
  emailLienHe: '',
  diaChi: '',
  thanhPho: '',
  quanHuyen: '',
  phuongXa: '',
  kinhDo: '106.70090',
  viDo: '10.77690',
  chinhSachHuy: '',
  chinhSachHoanTien: '',
  gioNhanPhong: '14:00',
  gioTraPhong: '12:00',
  gioMoCua: '08:00',
  gioDongCua: '22:00',
  quyDinhTreEm: '',
  quyDinhVatNuoi: '',
  ghiChuKhac: '',
  tienIchKhachSan: [],
  tienIchNhaHang: [],
  danhSachAnh: [],
}

const initialRoomForm = {
  soPhong: '',
  tenPhong: '',
  loaiPhong: 'Phòng Deluxe',
  moTa: '',
  sucChuaToiDa: '2',
  soGiuong: '1',
  dienTich: '45',
  giaCoBan: '500000',
  soLuongPhong: '1',
  phanTramGiamGia: '0',
  tienIch: ['WIFI', 'DIEU_HOA'],
  imageName: '',
}
const ROOM_AMENITY_BY_LABEL: Record<string, string> = {
  'wi-fi': 'WIFI',
  wifi: 'WIFI',
  'điều hòa': 'DIEU_HOA',
  'dieu hoa': 'DIEU_HOA',
  tv: 'TV',
  'tủ lạnh': 'TU_LANH',
  'tu lanh': 'TU_LANH',
  'ban công': 'BAN_CONG',
  'ban cong': 'BAN_CONG',
  'bồn tắm': 'BON_TAM',
  'bon tam': 'BON_TAM',
  'view đẹp': 'VIEW_DEP',
  'view dep': 'VIEW_DEP',
}
function normalizeAmenityLabel(value: string) {
  return value.trim().toLowerCase()
}
function getAllowedRoomAmenities(hotelAmenities: string[]) {
  return hotelAmenities
    .map((name) => ({ label: name, value: ROOM_AMENITY_BY_LABEL[normalizeAmenityLabel(name)] }))
    .filter((item): item is { label: string; value: string } => Boolean(item.value))
}

const initialMenuForm = {
  tenMon: '',
  giaBan: '85000',
  duongDanUrl: '',
  theNguCanh: 'VEGETARIAN,BEST SELLER',
}

const initialComboForm = {
  tenCombo: '',
  moTa: '',
  giaCombo: '150000',
  trangThai: '1',
  ngayBatDau: '',
  ngayKetThuc: '',
  monAnIds: [] as string[],
}

function makeLocalId(prefix: string) {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`
}

function buildBusinessPayload(form: ProfileFormState): BusinessProfilePayload {
  const tenCoSo = form.tenCoSo.trim()
  const moTa = form.moTa.trim()
  
  const mappedImages = form.danhSachAnh.map(img => ({
    duongDanUrl: img.url,
    moTaAnh: img.moTa || 'Ảnh cơ sở',
    laAnhDaiDien: img.laAnhDaiDien
  }));
  
  const base = {
    hoSo: {
      tenCoSo,
      sdtLienHe: normalizePhone(form.sdtLienHe),
      emailLienHe: form.emailLienHe.trim(),
      diaChi: form.diaChi.trim(),
      thanhPho: form.thanhPho.trim(),
      quanHuyen: form.quanHuyen.trim(),
      phuongXa: form.phuongXa.trim(),
      kinhDo: Number(form.kinhDo),
      viDo: Number(form.viDo),
      loaiDichVu: form.loaiDichVu,
      maSoThue: onlyDigits(form.maSoThue),
      giayPhepKinhDoanh: form.giayPhepKinhDoanh.trim(),
      toaDoGPS: form.toaDoGPS.replace(/\s+/g, ''),
      chinhSach: {
        loaiChinhSach: 'CHINH_SACH_CHUNG',
        noiDung: form.ghiChuKhac || 'Chính sách chung',
        ngayApDung: new Date().toISOString().slice(0, 10),
        gioNhanPhong: form.gioNhanPhong,
        gioTraPhong: form.gioTraPhong,
        gioMoCua: form.gioMoCua,
        gioDongCua: form.gioDongCua,
        chinhSachHuy: form.chinhSachHuy,
        chinhSachHoanTien: form.chinhSachHoanTien,
        quyDinhTreEm: form.quyDinhTreEm,
        quyDinhVatNuoi: form.quyDinhVatNuoi,
        ghiChuKhac: form.ghiChuKhac,
      },
    },
    danhSachAnh: mappedImages.length > 0 ? mappedImages : [{ duongDanUrl: 'https://via.placeholder.com/800x400?text=No+Image', moTaAnh: 'Ảnh mặc định', laAnhDaiDien: true }],
    tienIchKhachSan: form.loaiDichVu === 'KHACH_SAN'
      ? form.tienIchKhachSan.map(t => ({ tenTienIch: t, loaiTienIch: 'CO_BAN', moTa: t }))
      : [],
    tienIchNhaHang: form.loaiDichVu === 'NHA_HANG'
      ? form.tienIchNhaHang.map(t => ({ tenTienIch: t, loaiTienIch: 'CO_BAN', moTa: t, coThuPhi: false, phiSuDung: 0 }))
      : [],
  }

  if (form.loaiDichVu === 'KHACH_SAN') {
    return {
      ...base,
      khachSan: {
        ten: tenCoSo,
        hangSao: Number(form.hangSao),
        loaiKhachSan: form.loaiKhachSan.trim(),
        moTa,
        giaCoBan: Number(form.giaCoBan),
        isDynamicPricing: false,
        gioNhanPhong: form.gioNhanPhong,
        gioTraPhong: form.gioTraPhong,
        gioNhanPhongMacDinh: form.gioNhanPhong,
        gioTraPhongMacDinh: form.gioTraPhong,
        soTang: Number(form.soTang),
        tongSoPhong: Number(form.tongSoPhong),
      },
      nhaHang: null,

    }
  }

  return {
    ...base,
    khachSan: null,
    nhaHang: {
      ten: tenCoSo,
      loaiAmThuc: form.loaiAmThuc.trim(),
      moTa,
      giaCoBan: Number(form.giaCoBan),
      isDynamicPricing: false,
      sucChua: Number(form.sucChua),
      gioMoCua: '08:00',
      gioDongCua: '22:00',
    },

  }
}

function formatMoney(value: number) {
  return `${value.toLocaleString('vi-VN')} VND`
}

function normalizePhone(value: string) {
  return value.replace(/\s+/g, '')
}

function onlyDigits(value: string) {
  return value.replace(/\D/g, '')
}

function isIntegerBetween(value: string, min: number, max: number) {
  const numberValue = Number(value)
  return Number.isInteger(numberValue) && numberValue >= min && numberValue <= max
}

function parseCoordinates(value: string) {
  const normalized = value.replace(/\s+/g, '')
  const [latText, lngText] = normalized.split(',')
  if (!latText || !lngText) return null

  const lat = Number(latText)
  const lng = Number(lngText)
  if (!Number.isFinite(lat) || !Number.isFinite(lng)) return null
  if (lat < -90 || lat > 90 || lng < -180 || lng > 180) return null

  return { lat, lng }
}

function getMapPosition(value: string): [number, number] {
  const coordinates = parseCoordinates(value)
  return coordinates ? [coordinates.lat, coordinates.lng] : DEFAULT_MAP_POSITION
}

function MapClickHandler({ onChange }: { onChange: (value: string) => void }) {
  useMapEvents({
    click(event) {
      onChange(`${event.latlng.lat.toFixed(5)},${event.latlng.lng.toFixed(5)}`)
    },
  })

  return null
}

function BusinessLocationMap({
  value,
  onChange,
}: {
  value: string
  onChange: (value: string) => void
}) {
  const position = getMapPosition(value)
  const mapKey = `${position[0]}-${position[1]}`

  return (
    <div className="real-map-picker">
      <MapContainer center={position} className="leaflet-map" key={mapKey} scrollWheelZoom zoom={14}>
        <TileLayer
          attribution="&copy; OpenStreetMap contributors"
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <Marker icon={leafletMarkerIcon} position={position} />
        <MapClickHandler onChange={onChange} />
      </MapContainer>
      <div className="map-helper">
        <MapPin size={16} />
        <span>Bấm trực tiếp trên bản đồ để chọn vị trí cơ sở.</span>
      </div>
    </div>
  )
}

function getTableArea(viTriSanh: string) {
  return RESTAURANT_AREAS.find((area) => viTriSanh.startsWith(area)) ?? (viTriSanh.split('#')[0].trim() || viTriSanh)
}

function getProfileErrorStep(errors: ProfileFormErrors) {
  if (
    errors.tenCoSo || errors.sdtLienHe || errors.moTa || errors.giaCoBan || errors.hangSao ||
    errors.loaiKhachSan || errors.soTang || errors.tongSoPhong || errors.loaiAmThuc || errors.sucChua
  ) {
    return 1
  }
  if (errors.maSoThue || errors.businessLicense) return 2
  if (errors.toaDoGPS) return 3
  if (errors.danhSachAnh) return 4
  if (errors.gioNhanPhong || errors.gioTraPhong || errors.gioMoCua || errors.gioDongCua || errors.chinhSachHuy || errors.quyDinhTreEm || errors.ghiChuKhac) return 5
  return 1
}

function isPositiveNumber(value: string) {
  return Number.isFinite(Number(value)) && Number(value) > 0
}

function validateUploadFile(file: File, options: UploadOptions = {}) {
  const allowedTypes = options.allowPdf === false ? ALLOWED_IMAGE_TYPES : ALLOWED_LICENSE_TYPES

  if (file.size > MAX_UPLOAD_SIZE) {
    return 'File không được vượt quá 10MB.'
  }

  if (!allowedTypes.includes(file.type)) {
    return options.allowPdf === false ? 'Chỉ hỗ trợ ảnh JPG hoặc PNG.' : 'Chỉ hỗ trợ file PDF, JPG hoặc PNG.'
  }

  return ''
}

function getProfileFormErrors(form: ProfileFormState, targetStep = 5): ProfileFormErrors {
  const errors: ProfileFormErrors = {}
  const phone = normalizePhone(form.sdtLienHe)
  const taxCode = onlyDigits(form.maSoThue)

  if (targetStep >= 1) {
    if (!form.tenCoSo.trim()) errors.tenCoSo = 'Vui lòng nhập tên cơ sở.'
    else if (form.tenCoSo.trim().length > 200) errors.tenCoSo = 'Tên cơ sở không được vượt quá 200 ký tự.'

    if (!/^(\+84|0)\d{9}$/.test(phone)) {
      errors.sdtLienHe = 'Số điện thoại phải bắt đầu bằng 0 hoặc +84 và đủ 10 chữ số.'
    }

    if (!form.moTa.trim()) errors.moTa = 'Vui lòng nhập mô tả ngắn.'
    else if (form.moTa.trim().length > 1000) errors.moTa = 'Mô tả không được vượt quá 1000 ký tự.'

    if (!isPositiveNumber(form.giaCoBan)) errors.giaCoBan = 'Giá cơ bản phải lớn hơn 0.'

    if (form.loaiDichVu === 'KHACH_SAN' && !isIntegerBetween(form.hangSao, 1, 5)) {
      errors.hangSao = 'Hạng sao phải từ 1 đến 5.'
    }
    if (form.loaiDichVu === 'KHACH_SAN') {
      if (!form.loaiKhachSan.trim()) errors.loaiKhachSan = 'Vui lòng nhập loại khách sạn.'
      else if (form.loaiKhachSan.trim().length > 100) errors.loaiKhachSan = 'Loại khách sạn không được vượt quá 100 ký tự.'
      if (!isIntegerBetween(form.soTang, 1, 1000)) errors.soTang = 'Số tầng phải là số nguyên lớn hơn hoặc bằng 1.'
      if (!isIntegerBetween(form.tongSoPhong, 1, 100000)) errors.tongSoPhong = 'Tổng số phòng phải là số nguyên lớn hơn hoặc bằng 1.'
    }

    if (form.loaiDichVu === 'NHA_HANG') {
      if (!form.loaiAmThuc.trim()) errors.loaiAmThuc = 'Vui lòng nhập loại ẩm thực.'
      else if (form.loaiAmThuc.trim().length > 100) errors.loaiAmThuc = 'Loại ẩm thực không được vượt quá 100 ký tự.'
      if (!isIntegerBetween(form.sucChua, 1, 10000)) errors.sucChua = 'Sức chứa phải là số nguyên lớn hơn 0.'
    }
  }

  if (targetStep >= 2) {
    if (!/^(\d{10}|\d{13})$/.test(taxCode)) {
      errors.maSoThue = 'Mã số thuế phải gồm 10 hoặc 13 chữ số.'
    }
    if (!form.giayPhepKinhDoanh.trim()) errors.businessLicense = 'Vui lòng tải lên giấy phép kinh doanh.'
    else if (form.giayPhepKinhDoanh.trim().length > 500) errors.businessLicense = 'Đường dẫn file giấy phép quá dài.'
  }

  // Vị trí (tọa độ) là optional ở bước tạo hồ sơ kinh doanh.
  // Nếu người dùng có nhập thì vẫn validate format.
  if (targetStep >= 3 && form.toaDoGPS.trim() && !parseCoordinates(form.toaDoGPS)) {
    errors.toaDoGPS = 'Tọa độ phải đúng định dạng lat,lng và nằm trong phạm vi hợp lệ.'
  }

  // Ảnh, tiện ích, chính sách/quy định được chuyển sang bước cấu hình khách sạn/nhà hàng.
  // Ở bước tạo hồ sơ kinh doanh, các field này là optional.
  if (targetStep >= 4 && form.danhSachAnh.length > 0) {
    // no-op: keep for future stricter validate if needed
  }

  if (targetStep >= 5) {
    // Policy/time fields are optional at business-profile creation stage.
    // If user provides them, basic sanity checks can be added later.
  }

  return errors
}
function getFirstError(errors: ProfileFormErrors) {
  return Object.values(errors)[0] ?? ''
}

function hydrateProfileForm(profile: BusinessProfileResponse): ProfileFormState {
  const primaryAsset = profile.taiSan ?? profile.danhSachTaiSan?.[0]

  return {
    ...initialProfileForm,
    tenCoSo: profile.tenCoSo ?? '',
    sdtLienHe: profile.sdtLienHe ?? '',
    emailLienHe: profile.emailLienHe ?? '',
    diaChi: profile.diaChi ?? '',
    thanhPho: profile.thanhPho ?? '',
    quanHuyen: profile.quanHuyen ?? '',
    phuongXa: profile.phuongXa ?? '',
    kinhDo: profile.kinhDo != null ? String(profile.kinhDo) : initialProfileForm.kinhDo,
    viDo: profile.viDo != null ? String(profile.viDo) : initialProfileForm.viDo,
    loaiDichVu: profile.loaiDichVu,
    maSoThue: profile.maSoThue ?? '',
    giayPhepKinhDoanh: profile.giayPhepKinhDoanh ?? '',
    toaDoGPS: profile.toaDoGPS ?? initialProfileForm.toaDoGPS,
    moTa: primaryAsset?.moTa ?? '',
    giaCoBan: String(primaryAsset?.giaCoBan ?? initialProfileForm.giaCoBan),
    chinhSachHuy: profile.chinhSach?.chinhSachHuy ?? '',
    chinhSachHoanTien: profile.chinhSach?.chinhSachHoanTien ?? '',
    quyDinhTreEm: profile.chinhSach?.quyDinhTreEm ?? '',
    quyDinhVatNuoi: profile.chinhSach?.quyDinhVatNuoi ?? '',
    ghiChuKhac: profile.chinhSach?.ghiChuKhac ?? '',
    gioNhanPhong: profile.chinhSach?.gioNhanPhong ?? '',
    gioTraPhong: profile.chinhSach?.gioTraPhong ?? '',
    gioMoCua: profile.chinhSach?.gioMoCua ?? '',
    gioDongCua: profile.chinhSach?.gioDongCua ?? '',
  }
}

function getBusinessCoverImage(form: ProfileFormState) {
  return form.danhSachAnh.find((image) => image.laAnhDaiDien)?.url ?? form.danhSachAnh[0]?.url ?? heroImage
}

function getRoomCoverImage(room: RoomResponse) {
  return room.danhSachAnh?.find((image) => image.laAnhDaiDien)?.duongDanUrl ?? room.danhSachAnh?.[0]?.duongDanUrl ?? ''
}

function getMenuItemCoverImage(item: MenuItemResponse) {
  return item.duongDanUrl ?? ''
}


function buildLocalRoom(roomForm: typeof initialRoomForm, id = makeLocalId('room')): RoomResponse {
  return {
    id,
    khachSanId: 'local-hotel',
    soPhong: roomForm.soPhong,
    loaiPhong: roomForm.loaiPhong,
    sucChuaToiDa: Number(roomForm.sucChuaToiDa),
    dienTich: Number(roomForm.dienTich),
    trangThai: 'DANG_BAN',
    tienIch: roomForm.tienIch,
    phanTramGiamGia: Number(roomForm.phanTramGiamGia),
    danhSachAnh: roomForm.imageName
      ? [{
          id: makeLocalId('image'),
          doiTuongId: id,
          duongDanUrl: roomForm.imageName,
          moTaAnh: 'Anh phong',
          laAnhDaiDien: true,
          ngayTaiLen: new Date().toISOString().slice(0, 10),
        }]
      : [],
  }
}

function buildLocalTables(quantity: number, selectedArea: string, seats: string, offset = 0): TableResponse[] {
  return Array.from({ length: quantity }, (_, index) => ({
    id: makeLocalId('table'),
    nhaHangId: 'local-restaurant',
    viTriSanh: `${selectedArea} #${offset + index + 1}`,
    soChoNgoi: Number(seats),
    trangThai: 'SAN_SANG',
  }))
}

type ProfessionalNotificationProps = {
  type: 'success' | 'error'
  message: string
  onClose: () => void
}

function ProfessionalNotification({ type, message, onClose }: ProfessionalNotificationProps) {
  const isError = type === 'error'

  const looksLikeValidation =
    /vui lòng|phải|không được|không hợp lệ|tọa độ|mã số|giấy phép|số điện thoại/i.test(message)

  const title = isError
    ? looksLikeValidation
      ? 'Thông tin chưa hợp lệ'
      : 'Không thể hoàn tất thao tác'
    : 'Thao tác đã được ghi nhận'

  const subtitle = isError
    ? looksLikeValidation
      ? 'Kiểm tra các trường đang được đánh dấu rồi thử lại.'
      : 'Hệ thống chưa xử lý được yêu cầu. Bạn có thể kiểm tra backend hoặc thử lại sau.'
    : 'Dữ liệu đã được cập nhật trên giao diện.'

  return (
    <div
      className={`professional-notification ${type}`}
      role={isError ? 'alert' : 'status'}
      aria-live="polite"
    >
      <div className="professional-notification-icon">
        {isError ? <AlertTriangle size={22} /> : <CheckCircle2 size={22} />}
      </div>

      <div className="professional-notification-body">
        <div className="professional-notification-header">
          <div>
            <strong>{title}</strong>
            <span>{subtitle}</span>
          </div>

          <button
            type="button"
            className="professional-notification-close"
            onClick={onClose}
            aria-label="Đóng thông báo"
          >
            <X size={16} />
          </button>
        </div>

        <p>{message}</p>

        <div className="professional-notification-actions">
          <button type="button" onClick={onClose}>
            {isError ? 'Tôi đã hiểu' : 'Đóng'}
          </button>
        </div>
      </div>
    </div>
  )
}
type ConfirmationDialogState = {
  title: string
  message: string
  confirmText: string
  cancelText?: string
  details?: string
  onConfirm: () => Promise<void> | void
}

function ConfirmationDialog({
  confirmation,
  isConfirming,
  onCancel,
  onConfirm,
}: {
  confirmation: ConfirmationDialogState
  isConfirming: boolean
  onCancel: () => void
  onConfirm: () => void
}) {
  return (
    <div
      role="presentation"
      onClick={onCancel}
      style={{
        position: 'fixed',
        inset: 0,
        zIndex: 9999,
        display: 'grid',
        placeItems: 'center',
        padding: 24,
        background: 'rgba(11, 20, 15, 0.52)',
        backdropFilter: 'blur(4px)',
      }}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="delete-confirm-title"
        onClick={(event) => event.stopPropagation()}
        style={{
          width: 'min(520px, 100%)',
          display: 'grid',
          gridTemplateColumns: '56px minmax(0, 1fr)',
          gap: 18,
          padding: 24,
          borderRadius: 22,
          border: '1px solid rgba(193, 18, 31, 0.18)',
          background: '#ffffff',
          boxShadow: '0 28px 90px rgba(11, 20, 15, 0.32)',
        }}
      >
        <div
          style={{
            width: 56,
            height: 56,
            borderRadius: 18,
            display: 'grid',
            placeItems: 'center',
            color: '#c1121f',
            background: '#fff0f0',
          }}
        >
          <AlertTriangle size={26} />
        </div>

        <div style={{ minWidth: 0 }}>
          <div
            style={{
              display: 'flex',
              alignItems: 'flex-start',
              justifyContent: 'space-between',
              gap: 14,
            }}
          >
            <div>
              <h2
                id="delete-confirm-title"
                style={{
                  margin: 0,
                  color: '#102016',
                  fontSize: 22,
                  lineHeight: 1.25,
                }}
              >
                {confirmation.title}
              </h2>
              <p
                style={{
                  margin: '8px 0 0',
                  color: '#4f5b54',
                  lineHeight: 1.55,
                  fontWeight: 700,
                }}
              >
                {confirmation.message}
              </p>
            </div>

            <button
              type="button"
              onClick={onCancel}
              aria-label="Đóng hộp xác nhận"
              style={{
                width: 36,
                height: 36,
                minWidth: 36,
                border: '1px solid #dce4de',
                borderRadius: 12,
                display: 'grid',
                placeItems: 'center',
                color: '#26342c',
                background: '#ffffff',
                cursor: 'pointer',
              }}
            >
              <X size={18} />
            </button>
          </div>

          {confirmation.details && (
            <div
              style={{
                marginTop: 14,
                padding: '12px 14px',
                borderRadius: 14,
                color: '#7a1b22',
                background: '#fff6f6',
                border: '1px solid #ffd6d9',
                fontWeight: 800,
                lineHeight: 1.45,
              }}
            >
              {confirmation.details}
            </div>
          )}

          <div
            style={{
              display: 'flex',
              justifyContent: 'flex-end',
              gap: 10,
              marginTop: 22,
            }}
          >
            <button className="secondary-btn" type="button" onClick={onCancel} disabled={isConfirming}>
              {confirmation.cancelText ?? 'Hủy'}
            </button>
            <button className="danger-btn" type="button" onClick={onConfirm} disabled={isConfirming}>
              {isConfirming ? 'Đang xử lý...' : confirmation.confirmText}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
type PartnerLayoutContext = {
  activeView: PartnerView
  setActiveView: React.Dispatch<React.SetStateAction<PartnerView>>
}

export function PartnerDashboardPage() {
  const { activeView, setActiveView } = useOutletContext<PartnerLayoutContext>()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const [step, setStep] = useState(1)
  const [profileForm, setProfileForm] = useState<ProfileFormState>(() => {
    const draft = localStorage.getItem('partner:profileDraft')
    return draft ? ({ ...initialProfileForm, ...(JSON.parse(draft) as ProfileFormState) }) : initialProfileForm
  })
  const [profile, setProfile] = useState<BusinessProfileResponse | null>(null)
  const [rooms, setRooms] = useState<RoomResponse[]>([])
  const [tables, setTables] = useState<TableResponse[]>([])
  const [menuItems, setMenuItems] = useState<MenuItemResponse[]>([])
  const [combos, setCombos] = useState<ComboResponse[]>([])
  const [roomForm, setRoomForm] = useState(initialRoomForm)
  const [tableForm, setTableForm] = useState({ soLuong: '6', soChoNgoi: '4', viTriSanh: 'Sảnh chính' })
  const [menuForm, setMenuForm] = useState(initialMenuForm)
  const [comboForm, setComboForm] = useState(initialComboForm)
  const [selectedArea, setSelectedArea] = useState('Sảnh chính')
  const [isRoomModalOpen, setIsRoomModalOpen] = useState(false)
  const [isMenuModalOpen, setIsMenuModalOpen] = useState(false)
  const [isComboModalOpen, setIsComboModalOpen] = useState(false)
  const [editingRoomId, setEditingRoomId] = useState<string | null>(null)
  const [editingTableId, setEditingTableId] = useState<string | null>(null)
  const [editingMenuItemId, setEditingMenuItemId] = useState<string | null>(null)
  const [editingComboId, setEditingComboId] = useState<string | null>(null)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [profileErrors, setProfileErrors] = useState<ProfileFormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isUploadingLicense, setIsUploadingLicense] = useState(false)
  const [isRoomSaving, setIsRoomSaving] = useState(false)
  const [isTableSaving, setIsTableSaving] = useState(false)
  const [isMenuSaving, setIsMenuSaving] = useState(false)
  const [isComboSaving, setIsComboSaving] = useState(false)
  const [roomSearch, setRoomSearch] = useState('')
  const [menuSearch, setMenuSearch] = useState('')
  const [confirmation, setConfirmation] = useState<ConfirmationDialogState | null>(null)
  const [isConfirming, setIsConfirming] = useState(false)
  const runConfirmedAction = async () => {
    if (!confirmation) return

    setIsConfirming(true)

    try {
      await confirmation.onConfirm()
      setConfirmation(null)
    } finally {
      setIsConfirming(false)
    }
  }
  const assetId = profile?.taiSan?.idTaiSan ?? profile?.danhSachTaiSan?.[0]?.idTaiSan
  const isHotel = profile?.loaiDichVu === 'KHACH_SAN'
  const isRestaurant = profile?.loaiDichVu === 'NHA_HANG'


  const menuStats = useMemo(() => {
    const active = menuItems.filter((item) => item.trangThai === 'DANG_BAN').length
    return { total: menuItems.length, active, outOfStock: menuItems.length - active }
  }, [menuItems])

  useEffect(() => {
    const view = searchParams.get('view')
    if (view === 'business-profile' || view === 'hotel-setup' || view === 'room-management' || view === 'restaurant-setup' || view === 'table-layout' || view === 'menu-management') {
      setActiveView(view)
    }
  }, [searchParams, setActiveView])

  const filteredRooms = useMemo(() => {
    const keyword = roomSearch.trim().toLowerCase()
    if (!keyword) return rooms
    return rooms.filter((room) =>
      [room.soPhong, room.loaiPhong, room.trangThai, ...room.tienIch]
        .join(' ')
        .toLowerCase()
        .includes(keyword),
    )
  }, [roomSearch, rooms])

  const filteredMenuItems = useMemo(() => {
    const keyword = menuSearch.trim().toLowerCase()
    if (!keyword) return menuItems
    return menuItems.filter((item) =>
      [item.tenMon, String(item.giaBan), item.trangThai, ...item.theNguCanh]
        .join(' ')
        .toLowerCase()
        .includes(keyword),
    )
  }, [menuItems, menuSearch])

  const [availableHotelAmenities, setAvailableHotelAmenities] = useState<Array<{ id: string; tenTienIch: string; loaiTienIch: string; moTa: string }>>([])
  const [selectedHotelAmenityIds, setSelectedHotelAmenityIds] = useState<string[]>([])
  const [availableRestaurantAmenities, setAvailableRestaurantAmenities] = useState<string[]>(RESTAURANT_AMENITY_OPTIONS)
  
  useEffect(() => {
    const loadProfile = async () => {
      try {
        const profiles = await partnerAssetService.getBusinessProfiles()
        const currentProfile = profiles[0] ?? null
        if (!currentProfile) {
          localStorage.removeItem('partner:lastProfile')
          setProfile(null)
          return
        }

        setProfile(currentProfile)
        setProfileForm(hydrateProfileForm(currentProfile))
        localStorage.setItem('partner:lastProfile', JSON.stringify(currentProfile))

        const currentAssetId = currentProfile.taiSan?.idTaiSan ?? currentProfile.danhSachTaiSan?.[0]?.idTaiSan
        if (!currentAssetId) {
          return
        }

        if (currentProfile.loaiDichVu === 'KHACH_SAN') {
          const [roomList, amenitiesList, hotelDetail] = await Promise.all([
            partnerAssetService.getRooms(currentAssetId),
            partnerAssetService.getHotelAmenities().catch(() => []),
            partnerAssetService.getHotelDetail(currentAssetId).catch(() => null),
          ])
          setRooms(roomList)
          setAvailableHotelAmenities(amenitiesList)
          if (hotelDetail) {
            setProfileForm((current) => {
              const hotelAmenityNames = Array.isArray(hotelDetail.tienIch)
                ? hotelDetail.tienIch.map((item) => item.tenTienIch)
                : current.tienIchKhachSan
              const hotelAmenityIds = Array.isArray(hotelDetail.tienIch)
                ? hotelDetail.tienIch.map((item) => item.id)
                : []
              const hotelImagesRaw = Array.isArray(hotelDetail.danhSachAnh)
                ? hotelDetail.danhSachAnh.map((image) => ({
                    url: image.duongDanUrl,
                    moTa: image.moTaAnh || 'Ảnh khách sạn',
                    laAnhDaiDien: Boolean(image.laAnhDaiDien),
                  }))
                : current.danhSachAnh
              const hotelImages =
                hotelImagesRaw.length > 0 && !hotelImagesRaw.some((item) => item.laAnhDaiDien)
                  ? hotelImagesRaw.map((item, index) => ({ ...item, laAnhDaiDien: index === 0 }))
                  : hotelImagesRaw
              setSelectedHotelAmenityIds(hotelAmenityIds)

              return {
                ...current,
                loaiDichVu: 'KHACH_SAN',
                tenCoSo: hotelDetail.ten ?? current.tenCoSo,
                moTa: hotelDetail.moTa ?? current.moTa,
                giaCoBan: hotelDetail.giaCoBan != null ? String(hotelDetail.giaCoBan) : current.giaCoBan,
                hangSao: hotelDetail.hangSao != null ? String(hotelDetail.hangSao) : current.hangSao,
                loaiKhachSan: hotelDetail.loaiKhachSan ?? current.loaiKhachSan,
                gioNhanPhong: hotelDetail.gioNhanPhong ?? current.gioNhanPhong,
                gioTraPhong: hotelDetail.gioTraPhong ?? current.gioTraPhong,
                soTang: hotelDetail.soTang != null ? String(hotelDetail.soTang) : current.soTang,
                tongSoPhong: hotelDetail.tongSoPhong != null ? String(hotelDetail.tongSoPhong) : current.tongSoPhong,
                tienIchKhachSan: hotelAmenityNames,
                danhSachAnh: hotelImages,
              }
            })
          }
        }

        if (currentProfile.loaiDichVu === 'NHA_HANG') {
          const [tableList, menuList, comboList, restaurantDetail, restaurantAmenities] = await Promise.all([
            partnerAssetService.getTables(currentAssetId),
            partnerAssetService.getMenuItems(currentAssetId),
            partnerAssetService.getCombos(currentAssetId),
            partnerAssetService.getRestaurantDetail(currentAssetId).catch(() => null),
            partnerAssetService.getRestaurantAmenities().catch(() => []),
          ])
          setTables(tableList)
          setMenuItems(menuList)
          setCombos(comboList)
          if (restaurantAmenities.length > 0) {
            setAvailableRestaurantAmenities(restaurantAmenities)
          }
          if (restaurantDetail) {
            setProfileForm((current) => {
              const restaurantAmenityNames = Array.isArray(restaurantDetail.tienIch)
                ? restaurantDetail.tienIch.map((item) => item.tenTienIch)
                : current.tienIchNhaHang
              const restaurantImagesRaw = Array.isArray(restaurantDetail.danhSachAnh)
                ? restaurantDetail.danhSachAnh.map((image) => ({
                    url: image.duongDanUrl,
                    moTa: image.moTaAnh || 'Ảnh nhà hàng',
                    laAnhDaiDien: Boolean(image.laAnhDaiDien),
                  }))
                : current.danhSachAnh
              const restaurantImages =
                restaurantImagesRaw.length > 0 && !restaurantImagesRaw.some((item) => item.laAnhDaiDien)
                  ? restaurantImagesRaw.map((item, index) => ({ ...item, laAnhDaiDien: index === 0 }))
                  : restaurantImagesRaw
              return {
                ...current,
                loaiDichVu: 'NHA_HANG',
                tenCoSo: restaurantDetail.ten ?? current.tenCoSo,
                moTa: restaurantDetail.moTa ?? current.moTa,
                giaCoBan: restaurantDetail.giaCoBan != null ? String(restaurantDetail.giaCoBan) : current.giaCoBan,
                loaiAmThuc: restaurantDetail.loaiAmThuc ?? current.loaiAmThuc,
                sucChua: restaurantDetail.sucChua != null ? String(restaurantDetail.sucChua) : current.sucChua,
                gioMoCua: restaurantDetail.gioMoCua ?? current.gioMoCua,
                gioDongCua: restaurantDetail.gioDongCua ?? current.gioDongCua,
                tienIchNhaHang: restaurantAmenityNames,
                danhSachAnh: restaurantImages,
              }
            })
          }
        }
      } catch (err) {
        showError(err, 'Không thể tải dữ liệu bảng điều khiển đối tác.')
      }
    }

    void loadProfile()
  }, [])

  const updateProfileField = (name: keyof ProfileFormState, value: ProfileFormValue) => {
    if (name === 'loaiDichVu' && profile) {
      return
    }
    setProfileForm((current) => ({ ...current, [name]: value }))
    setProfileErrors((current) => ({ ...current, [name]: undefined }))
    if (name === 'giayPhepKinhDoanh') {
      setProfileErrors((current) => ({ ...current, businessLicense: undefined }))
    }
  }

  const showError = (err: unknown, fallback: string) => {
    setError(getApiErrorMessage(err, fallback))
    setMessage('')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const showMessage = (text: string) => {
    setMessage(text)
    setError('')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const goToProfileStep = (nextStep: number) => {
    if (nextStep <= step) {
      setStep(nextStep)
      setError('')
      return
    }

    const errors = getProfileFormErrors(profileForm, nextStep - 1)
    setProfileErrors(errors)

    const validationMessage = getFirstError(errors)
    if (validationMessage) {
      setError(validationMessage)
      setMessage('')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }

    setError('')
    setStep(nextStep)
  }

  const validateRoomForm = () => {
    if (!roomForm.soPhong.trim()) return 'Vui lòng nhập số phòng.'
    if (!roomForm.loaiPhong.trim()) return 'Vui lòng nhập loại phòng.'
    if (!isPositiveNumber(roomForm.sucChuaToiDa)) return 'Sức chứa phòng phải lớn hơn 0.'
    if (!isPositiveNumber(roomForm.dienTich)) return 'Diện tích phòng phải lớn hơn 0.'
    if (Number(roomForm.phanTramGiamGia) < 0 || Number(roomForm.phanTramGiamGia) > 100) return 'Phần trăm giảm giá phải từ 0 đến 100.'
    if (!roomForm.imageName.trim()) return 'Vui lòng tải lên ít nhất một ảnh phòng.'
    return ''
  }

  const validateTableForm = () => {
    const quantity = Number(tableForm.soLuong)
    if (!Number.isInteger(quantity) || quantity < 1 || quantity > 50) return 'Số lượng bàn phải từ 1 đến 50.'
    if (!isPositiveNumber(tableForm.soChoNgoi)) return 'Số chỗ ngồi phải lớn hơn 0.'
    if (!selectedArea.trim()) return 'Vui lòng chọn khu vực bàn.'
    return ''
  }

  const validateMenuForm = () => {
    if (!menuForm.tenMon.trim()) return 'Vui lòng nhập tên món.'
    if (!isPositiveNumber(menuForm.giaBan)) return 'Giá món phải lớn hơn 0.'
    if (!menuForm.duongDanUrl.trim()) return 'Vui lòng tải lên ảnh món ăn.'
    return ''
  }

  const validateComboForm = () => {
    if (!comboForm.tenCombo.trim()) return 'Vui lòng nhập tên combo.'
    if (!isPositiveNumber(comboForm.giaCombo)) return 'Giá combo phải lớn hơn 0.'
    if (!comboForm.monAnIds.length) return 'Vui lòng chọn ít nhất một món cho combo.'
    return ''
  }

  const saveProfile = async () => {
    if (profile && profileForm.loaiDichVu !== profile.loaiDichVu) {
      setError('Không thể thay đổi loại hình dịch vụ sau khi đã tạo hồ sơ. Vui lòng tạo hồ sơ mới nếu cần chuyển loại hình.')
      setMessage('')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }

    const errors = getProfileFormErrors(profileForm, 5)
    setProfileErrors(errors)

    const validationMessage = getFirstError(errors)
    if (validationMessage) {
      setStep(getProfileErrorStep(errors))
      setError(validationMessage)
      setMessage('')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }

    setIsSubmitting(true)
    setError('')
    setMessage('')

    try {
      const payload = buildBusinessPayload(profileForm)
      const existingProfileId = profile?.idHoSo

      const response = existingProfileId
        ? await partnerAssetService.updateBusinessProfile(existingProfileId, payload)
        : await partnerAssetService.createBusinessProfile(payload)

      setProfile(response)
      setStep(5)
      window.dispatchEvent(new Event('partner:profile-created'))

      setProfileForm((current) => {
        const hydrated = hydrateProfileForm(response)

        return {
          ...hydrated,
          moTa: current.moTa,
          hangSao: current.hangSao,
          loaiKhachSan: current.loaiKhachSan,
          soTang: current.soTang,
          tongSoPhong: current.tongSoPhong,
          loaiAmThuc: current.loaiAmThuc,
          sucChua: current.sucChua,
          emailLienHe: current.emailLienHe,
          diaChi: current.diaChi,
          thanhPho: current.thanhPho,
          quanHuyen: current.quanHuyen,
          phuongXa: current.phuongXa,
          kinhDo: current.kinhDo,
          viDo: current.viDo,
          chinhSachHuy: current.chinhSachHuy,
          chinhSachHoanTien: current.chinhSachHoanTien,
          gioNhanPhong: current.gioNhanPhong,
          gioTraPhong: current.gioTraPhong,
          gioMoCua: current.gioMoCua,
          gioDongCua: current.gioDongCua,
          quyDinhTreEm: current.quyDinhTreEm,
          quyDinhVatNuoi: current.quyDinhVatNuoi,
          ghiChuKhac: current.ghiChuKhac,
          tienIchKhachSan: current.tienIchKhachSan,
          tienIchNhaHang: current.tienIchNhaHang,
          danhSachAnh: current.danhSachAnh,
        }
      })

      localStorage.setItem('partner:lastProfile', JSON.stringify(response))
      localStorage.removeItem('partner:profileDraft')

      showMessage(
        existingProfileId
          ? 'Hồ sơ kinh doanh đã được cập nhật.'
          : response.loaiDichVu === 'KHACH_SAN'
            ? 'Hồ sơ kinh doanh đã được tạo thành công. Vui lòng tiếp tục cấu hình thông tin khách sạn và khai báo phòng trước khi mở bán.'
            : 'Hồ sơ kinh doanh đã được tạo thành công. Vui lòng tiếp tục cấu hình thông tin nhà hàng trước khi mở bán.',
      )

      if (!existingProfileId) {
        if (response.loaiDichVu === 'KHACH_SAN') {
          setActiveView('hotel-setup')
          navigate('/partner/hotel', { replace: true })
        } else {
          setActiveView('restaurant-setup')
          navigate('/partner/restaurant', { replace: true })
        }
      }
    } catch (err) {
      showError(
        err,
        'Không thể lưu hồ sơ kinh doanh. Vui lòng kiểm tra backend, quyền đăng nhập hoặc dữ liệu gửi lên.',
      )
    } finally {
      setIsSubmitting(false)
    }
  }

  const saveDraft = () => {
    localStorage.setItem('partner:profileDraft', JSON.stringify(profileForm))
    showMessage('Đã lưu bản nháp cục bộ trên trình duyệt. Hãy hoàn thành các bước để lưu lên hệ thống.')
  }

  const saveRoom = async () => {
    const validationMessage = validateRoomForm()
    if (validationMessage) {
      setError(validationMessage)
      setMessage('')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }

    const payload: RoomPayload = {
      phong: {
        soPhong: roomForm.soPhong,
        tenPhong: roomForm.tenPhong?.trim() || roomForm.soPhong,
        loaiPhong: roomForm.loaiPhong,
        moTa: roomForm.moTa?.trim() || '',
        sucChuaToiDa: Number(roomForm.sucChuaToiDa),
        soGiuong: Number(roomForm.soGiuong),
        dienTich: Number(roomForm.dienTich),
        giaCoBan: Number(roomForm.giaCoBan),
        soLuongPhong: Number(roomForm.soLuongPhong),
        trangThai: 'SAN_SANG',
        tienIch: roomForm.tienIch,
        phanTramGiamGia: Number(roomForm.phanTramGiamGia),
      },
      danhSachAnh: roomForm.imageName
        ? [{ duongDanUrl: roomForm.imageName, moTaAnh: 'Anh phong', laAnhDaiDien: true }]
        : [],
    }

    if (!assetId || !isHotel) {
      const saved = buildLocalRoom(roomForm, editingRoomId ?? undefined)
      setRooms((current) =>
        editingRoomId ? current.map((room) => (room.id === editingRoomId ? saved : room)) : [...current, saved],
      )
      setRoomForm(initialRoomForm)
      setEditingRoomId(null)
      setIsRoomModalOpen(false)
      showMessage('Đã lưu phòng tạm thời. Tạo hồ sơ khách sạn để đồng bộ lên backend.')
      return
    }

    setIsRoomSaving(true)
    try {
      const saved = editingRoomId
        ? await partnerAssetService.updateRoom(assetId, editingRoomId, payload)
        : await partnerAssetService.createRoom(assetId, payload)
      setRooms((current) =>
        editingRoomId ? current.map((room) => (room.id === editingRoomId ? saved : room)) : [...current, saved],
      )
      setRoomForm(initialRoomForm)
      setEditingRoomId(null)
      setIsRoomModalOpen(false)
      showMessage(editingRoomId ? 'Đã cập nhật phòng.' : 'Đã thêm phòng mới.')
    } catch (err) {
      showError(err, editingRoomId ? 'Không thể cập nhật phòng.' : 'Không thể thêm phòng mới.')
    } finally {
      setIsRoomSaving(false)
    }
  }

  const startRoomEdit = (room: RoomResponse) => {
    setEditingRoomId(room.id)
    setRoomForm({
      soPhong: room.soPhong,
      tenPhong: room.tenPhong?.trim() || room.soPhong,
      loaiPhong: room.loaiPhong,
      moTa: room.moTa?.trim() || '',
      sucChuaToiDa: String(room.sucChuaToiDa),
      soGiuong: String(room.soGiuong ?? 1),
      dienTich: String(room.dienTich),
      giaCoBan: String(room.giaCoBan ?? ''),
      soLuongPhong: String(room.soLuongPhong ?? 1),
      phanTramGiamGia: String(room.phanTramGiamGia ?? 0),
      tienIch: room.tienIch,
      imageName: room.danhSachAnh?.find((image) => image.laAnhDaiDien)?.duongDanUrl ?? room.danhSachAnh?.[0]?.duongDanUrl ?? '',
    })
    setIsRoomModalOpen(true)
  }

  const uploadFile = async (file: File, options: UploadOptions = {}): Promise<UploadedAsset | null> => {
    const validationMessage = validateUploadFile(file, options)
    if (validationMessage) {
      setError(validationMessage)
      setMessage('')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return null
    }

    try {
      return await partnerAssetService.uploadPartnerAsset(file)
    } catch (err) {
      showError(err, 'Không thể tải tệp lên. Vui lòng kiểm tra backend và thử lại.')
      return null
    }
  }

  const uploadBusinessLicense = async (file: File) => {
    setIsUploadingLicense(true)
    try {
      const uploaded = await uploadFile(file)
      if (uploaded) {
        updateProfileField('giayPhepKinhDoanh', uploaded.url)
        showMessage('Đã tải lên giấy phép kinh doanh.')
      }
    } finally {
      setIsUploadingLicense(false)
    }
  }

  const deleteRoom = (roomId: string) => {
    setConfirmation({
      title: 'Xóa phòng này?',
      message: 'Phòng sẽ bị gỡ khỏi danh sách quản lý và khách hàng sẽ không còn thấy phòng này trên hệ thống.',
      details: 'Hành động này không thể hoàn tác sau khi xác nhận.',
      confirmText: 'Xóa phòng',
      cancelText: 'Giữ lại',
      onConfirm: async () => {
        setRooms((current) => current.filter((room) => room.id !== roomId))

        if (!assetId || roomId.startsWith('room-')) {
          showMessage('Đã xóa phòng tạm thời.')
          return
        }

        try {
          await partnerAssetService.deleteRoom(assetId, roomId)
          showMessage('Đã xóa phòng.')
        } catch (err) {
          console.error(err)
          showMessage('Đã gỡ phòng khỏi giao diện. Backend hiện chưa cho quyền xóa nên dữ liệu có thể hiện lại sau khi tải lại trang.')
        }
      },
    })
  }

  const deleteTable = (tableId: string) => {
    setConfirmation({
      title: 'Xóa bàn này?',
      message: 'Bàn sẽ bị xóa khỏi sơ đồ bố trí hiện tại của nhà hàng.',
      details: 'Bạn nên chỉ xóa khi chắc chắn bàn này không còn được sử dụng trong vận hành.',
      confirmText: 'Xóa bàn',
      cancelText: 'Giữ lại',
      onConfirm: async () => {
        setTables((current) => current.filter((table) => table.id !== tableId))

        if (!assetId || tableId.startsWith('table-')) {
          showMessage('Đã xóa bàn tạm thời.')
          return
        }

        try {
          await partnerAssetService.deleteTable(assetId, tableId)
          showMessage('Đã xóa bàn.')
        } catch (err) {
          console.error(err)
          showMessage('Đã gỡ bàn khỏi giao diện. Backend hiện chưa cho quyền xóa nên dữ liệu có thể hiện lại sau khi tải lại trang.')
        }
      },
    })
  }

  const createTables = async () => {
    const validationMessage = validateTableForm()
    if (validationMessage) {
      setError(validationMessage)
      setMessage('')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }

    if (editingTableId && (!assetId || editingTableId.startsWith('table-'))) {
      setTables((current) =>
        current.map((table) =>
          table.id === editingTableId
            ? { ...table, viTriSanh: selectedArea, soChoNgoi: Number(tableForm.soChoNgoi), trangThai: 'SAN_SANG' }
            : table,
        ),
      )
      setEditingTableId(null)
      setTableForm((current) => ({ ...current, soLuong: '1' }))
      showMessage('Đã cập nhật bàn tạm thời.')
      return
    }

    if (!assetId || !isRestaurant) {
      const quantity = Number(tableForm.soLuong)
      const localTables = buildLocalTables(quantity, selectedArea, tableForm.soChoNgoi, tables.length)
      setTables((current) => [...current, ...localTables])
      showMessage('Đã tạo bàn tạm thời. Tạo hồ sơ nhà hàng để đồng bộ lên backend.')
      return
    }

    setIsTableSaving(true)
    try {
      if (editingTableId) {
        const updated = await partnerAssetService.updateTable(assetId, editingTableId, {
          viTriSanh: selectedArea,
          soChoNgoi: Number(tableForm.soChoNgoi),
          trangThai: 'SAN_SANG',
        })
        setTables((current) => current.map((table) => (table.id === editingTableId ? updated : table)))
        setEditingTableId(null)
        setTableForm((current) => ({ ...current, soLuong: '1' }))
        showMessage('Đã cập nhật bàn.')
        return
      }

      const quantity = Number(tableForm.soLuong)
      const createdTables: TableResponse[] = []
      for (let index = 1; index <= quantity; index += 1) {
        createdTables.push(
          await partnerAssetService.createTable(assetId, {
            viTriSanh: `${selectedArea} #${index}`,
            soChoNgoi: Number(tableForm.soChoNgoi),
            trangThai: 'SAN_SANG',
          }),
        )
      }
      setTables((current) => [...current, ...createdTables])
      showMessage('Đã khai báo sơ đồ bàn.')
    } catch (err) {
      showError(err, editingTableId ? 'Không thể cập nhật bàn.' : 'Không thể tạo sơ đồ bàn.')
    } finally {
      setIsTableSaving(false)
    }
  }

  const createSingleTable = async () => {
    if (!isPositiveNumber(tableForm.soChoNgoi) || !selectedArea.trim()) {
      setError('Vui lòng chọn khu vực và số chỗ ngồi hợp lệ.')
      setMessage('')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }

    if (!assetId || !isRestaurant) {
      const created = buildLocalTables(1, selectedArea, tableForm.soChoNgoi, tables.length)[0]
      setTables((current) => [...current, created])
      setTableForm((current) => ({ ...current, soLuong: '1' }))
      setEditingTableId(null)
      showMessage('Đã thêm một bàn tạm thời.')
      return
    }

    setIsTableSaving(true)
    try {
      const created = await partnerAssetService.createTable(assetId, {
        viTriSanh: `${selectedArea} #${tables.length + 1}`,
        soChoNgoi: Number(tableForm.soChoNgoi),
        trangThai: 'SAN_SANG',
      })
      setTables((current) => [...current, created])
      setTableForm((current) => ({ ...current, soLuong: '1' }))
      setEditingTableId(null)
      showMessage('Đã thêm một bàn.')
    } catch (err) {
      showError(err, 'Không thể thêm bàn.')
    } finally {
      setIsTableSaving(false)
    }
  }

  const startTableEdit = (table: TableResponse) => {
    const area = RESTAURANT_AREAS.find((item) => table.viTriSanh.startsWith(item)) ?? table.viTriSanh
    setEditingTableId(table.id)
    setSelectedArea(area)
    setTableForm({
      soLuong: '1',
      soChoNgoi: String(table.soChoNgoi),
      viTriSanh: table.viTriSanh,
    })
    showMessage('Đã chọn bàn để sửa. Cập nhật thông tin ở form bên trên rồi bấm Cập nhật bàn.')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const createMenuItem = async () => {
    const validationMessage = validateMenuForm()
    if (validationMessage) {
      setError(validationMessage)
      setMessage('')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }

    const payload = {
      tenMon: menuForm.tenMon,
      giaBan: Number(menuForm.giaBan),
      trangThai: 'DANG_BAN' as MenuItemStatus,
      duongDanUrl: menuForm.duongDanUrl,
      theNguCanh: menuForm.theNguCanh.split(',').map((item) => item.trim()).filter(Boolean),
    }

    if (!assetId || !isRestaurant || editingMenuItemId?.startsWith('menu-')) {
      const saved: MenuItemResponse = {
        id: editingMenuItemId ?? makeLocalId('menu'),
        thucDonId: 'local-menu',
        ...payload,
      }
      setMenuItems((current) =>
        editingMenuItemId ? current.map((item) => (item.id === editingMenuItemId ? saved : item)) : [...current, saved],
      )
      setMenuForm(initialMenuForm)
      setEditingMenuItemId(null)
      setIsMenuModalOpen(false)
      showMessage(editingMenuItemId ? 'Đã cập nhật món tạm thời.' : 'Đã thêm món tạm thời.')
      return
    }

    setIsMenuSaving(true)
    try {
      const saved = editingMenuItemId
        ? await partnerAssetService.updateMenuItem(assetId, editingMenuItemId, payload)
        : await partnerAssetService.createMenuItem(assetId, payload)
      setMenuItems((current) =>
        editingMenuItemId ? current.map((item) => (item.id === editingMenuItemId ? saved : item)) : [...current, saved],
      )
      setMenuForm(initialMenuForm)
      setEditingMenuItemId(null)
      setIsMenuModalOpen(false)
      showMessage(editingMenuItemId ? 'Đã cập nhật món.' : 'Đã thêm món mới.')
    } catch (err) {
      showError(err, editingMenuItemId ? 'Không thể cập nhật món.' : 'Không thể thêm món mới.')
    } finally {
      setIsMenuSaving(false)
    }
  }

  const startMenuItemEdit = (item: MenuItemResponse) => {
    setEditingMenuItemId(item.id)
    setMenuForm({
      tenMon: item.tenMon,
      giaBan: String(item.giaBan),
      duongDanUrl: item.duongDanUrl ?? '',
      theNguCanh: item.theNguCanh.join(','),
    })
    setIsMenuModalOpen(true)
  }

  const toggleMenuStatus = async (item: MenuItemResponse) => {
    const nextStatus: MenuItemStatus = item.trangThai === 'DANG_BAN' ? 'TAM_HET' : 'DANG_BAN'
    if (!assetId || item.id.startsWith('menu-')) {
      setMenuItems((current) =>
        current.map((menuItem) => (menuItem.id === item.id ? { ...menuItem, trangThai: nextStatus } : menuItem)),
      )
      return
    }

    try {
      const updated = await partnerAssetService.updateMenuItemStatus(assetId, item.id, nextStatus)
      setMenuItems((current) => current.map((menuItem) => (menuItem.id === item.id ? updated : menuItem)))
      showMessage(nextStatus === 'DANG_BAN' ? 'Món đã được bật lại.' : 'Món đã chuyển sang tạm hết.')
    } catch (err) {
      showError(err, 'Không thể cập nhật trạng thái món.')
    }
  }

  const deleteMenuItem = (itemId: string) => {
    setConfirmation({
      title: 'Xóa món ăn này?',
      message: 'Món ăn sẽ bị gỡ khỏi thực đơn điện tử và không còn xuất hiện trong danh sách phục vụ.',
      details: 'Nếu món chỉ tạm hết, bạn nên dùng công tắc trạng thái thay vì xóa.',
      confirmText: 'Xóa món',
      cancelText: 'Giữ lại',
      onConfirm: async () => {
        setMenuItems((current) => current.filter((item) => item.id !== itemId))

        if (!assetId || itemId.startsWith('menu-')) {
          showMessage('Đã xóa món tạm thời.')
          return
        }

        try {
          await partnerAssetService.deleteMenuItem(assetId, itemId)
          showMessage('Đã xóa món.')
        } catch (err) {
          console.error(err)
          showMessage('Đã gỡ món khỏi giao diện. Backend hiện chưa cho quyền xóa nên dữ liệu có thể hiện lại sau khi tải lại trang.')
        }
      },
    })
  }

  const saveCombo = async () => {
    const validationMessage = validateComboForm()
    if (validationMessage) {
      setError(validationMessage)
      setMessage('')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }

    const payload: ComboPayload = {
      tenCombo: comboForm.tenCombo.trim(),
      moTa: comboForm.moTa.trim(),
      giaCombo: Number(comboForm.giaCombo),
      trangThai: Number(comboForm.trangThai),
      ngayBatDau: comboForm.ngayBatDau || undefined,
      ngayKetThuc: comboForm.ngayKetThuc || undefined,
      monAnIds: comboForm.monAnIds,
    }

    if (!assetId || !isRestaurant) {
      const localCombo: ComboResponse = {
        id: editingComboId || makeLocalId('combo'),
        thucDonId: 'local-menu',
        ...payload,
      }
      setCombos((current) =>
        editingComboId
          ? current.map((combo) => (combo.id === editingComboId ? localCombo : combo))
          : [...current, localCombo],
      )
      setComboForm(initialComboForm)
      setEditingComboId(null)
      setIsComboModalOpen(false)
      showMessage(editingComboId ? 'Đã cập nhật combo tạm thời.' : 'Đã tạo combo tạm thời.')
      return
    }

    setIsComboSaving(true)
    try {
      const saved = editingComboId
        ? await partnerAssetService.updateCombo(assetId, editingComboId, payload)
        : await partnerAssetService.createCombo(assetId, payload)

      setCombos((current) =>
        editingComboId
          ? current.map((combo) => (combo.id === saved.id ? saved : combo))
          : [...current, saved],
      )
      setComboForm(initialComboForm)
      setEditingComboId(null)
      setIsComboModalOpen(false)
      showMessage(editingComboId ? 'Đã cập nhật combo.' : 'Đã tạo combo.')
    } catch (err) {
      showError(err, editingComboId ? 'Không thể cập nhật combo.' : 'Không thể tạo combo.')
    } finally {
      setIsComboSaving(false)
    }
  }

  const startComboEdit = (combo: ComboResponse) => {
    setEditingComboId(combo.id)
    setComboForm({
      tenCombo: combo.tenCombo,
      moTa: combo.moTa || '',
      giaCombo: String(combo.giaCombo),
      trangThai: String(combo.trangThai),
      ngayBatDau: combo.ngayBatDau || '',
      ngayKetThuc: combo.ngayKetThuc || '',
      monAnIds: combo.monAnIds || [],
    })
    setIsComboModalOpen(true)
  }

  const deleteCombo = (comboId: string) => {
    setConfirmation({
      title: 'Xóa combo này?',
      message: 'Combo sẽ bị gỡ khỏi khu vực combo trong thực đơn.',
      details: 'Các món lẻ trong combo vẫn được giữ lại, chỉ xóa cấu hình combo hiện tại.',
      confirmText: 'Xóa combo',
      cancelText: 'Giữ lại',
      onConfirm: async () => {
        setCombos((current) => current.filter((combo) => combo.id !== comboId))

        if (!assetId || comboId.startsWith('combo-')) {
          showMessage('Đã xóa combo tạm thời.')
          return
        }

        try {
          await partnerAssetService.deleteCombo(assetId, comboId)
          showMessage('Đã xóa combo.')
        } catch (err) {
          console.error(err)
          showMessage('Đã gỡ combo khỏi giao diện. Backend hiện chưa cho quyền xóa nên dữ liệu có thể hiện lại sau khi tải lại trang.')
        }
      },
    })
  }

  const toggleAmenity = (amenity: string) => {
    const field = profileForm.loaiDichVu === 'KHACH_SAN' ? 'tienIchKhachSan' : 'tienIchNhaHang'
    const current = profileForm[field]
    const next = current.includes(amenity)
      ? current.filter((item) => item !== amenity)
      : [...current, amenity]
    updateProfileField(field, next)
  }

  const addCustomAmenity = (value: string) => {
    const amenity = value.trim()
    if (!amenity) return
    const field = profileForm.loaiDichVu === 'KHACH_SAN' ? 'tienIchKhachSan' : 'tienIchNhaHang'
    if (!profileForm[field].includes(amenity)) {
      updateProfileField(field, [...profileForm[field], amenity])
    }
  }

  return (
    <>
      {/* Persistent notification */}
        {(message || error) && (
          <ProfessionalNotification
            type={error ? 'error' : 'success'}
            message={error || message}
            onClose={() => {
              setMessage('')
              setError('')
            }}
          />
        )}

        {activeView === 'business-profile' && (
          <BusinessProfileView
            step={step}
            setStep={goToProfileStep}
            profile={profile}
            profileForm={profileForm}
            profileErrors={profileErrors}
            isSubmitting={isSubmitting}
            updateProfileField={updateProfileField}
            saveProfile={saveProfile}
            saveDraft={saveDraft}
            uploadFile={uploadFile}
            uploadBusinessLicense={uploadBusinessLicense}
            isUploadingLicense={isUploadingLicense}
            toggleAmenity={toggleAmenity}
            addCustomAmenity={addCustomAmenity}
            availableHotelAmenities={availableHotelAmenities}
          />
        )}

        {activeView === 'hotel-setup' && (
          <HotelSetupView
            profile={profile}
            profileForm={profileForm}
            updateProfileField={updateProfileField}
            setActiveView={setActiveView}
            saveProfile={saveProfile}
            isSubmitting={isSubmitting}
            uploadFile={uploadFile}
            availableHotelAmenities={availableHotelAmenities}
            selectedHotelAmenityIds={selectedHotelAmenityIds}
            onToggleHotelAmenity={(amenityId) =>
              setSelectedHotelAmenityIds((current) =>
                current.includes(amenityId) ? current.filter((id) => id !== amenityId) : [...current, amenityId],
              )
            }
            onSaveHotelAmenities={async () => {
              const hotelId = profile?.taiSan?.idTaiSan ?? profile?.danhSachTaiSan?.[0]?.idTaiSan
              if (!hotelId) return
              try {
                const updatedHotel = await partnerAssetService.updateHotelAmenities(hotelId, selectedHotelAmenityIds)
                const amenityNames = Array.isArray(updatedHotel?.tienIch)
                  ? updatedHotel.tienIch.map((item: { tenTienIch: string }) => item.tenTienIch)
                  : profileForm.tienIchKhachSan
                setProfileForm((current) => ({ ...current, tienIchKhachSan: amenityNames }))
                showMessage('Đã lưu tiện ích khách sạn thành công.')
              } catch (err) {
                showError(err, 'Không thể lưu tiện ích khách sạn.')
              }
            }}
          />
        )}

        {activeView === 'room-management' && (
          <RoomManagementView
            rooms={filteredRooms}
            roomSearch={roomSearch}
            setRoomSearch={setRoomSearch}
            setIsRoomModalOpen={(open) => {
              if (open) {
                setEditingRoomId(null)
                setRoomForm(initialRoomForm)
              }
              setIsRoomModalOpen(open)
            }}
            startRoomEdit={startRoomEdit}
            deleteRoom={deleteRoom}
          />
        )}

        {activeView === 'restaurant-setup' && (
          <RestaurantSetupView
            profile={profile}
            profileForm={profileForm}
            updateProfileField={updateProfileField}
            setActiveView={setActiveView}
            saveProfile={saveProfile}
            isSubmitting={isSubmitting}
            uploadFile={uploadFile}
            availableRestaurantAmenities={availableRestaurantAmenities}
            onSaveRestaurantAmenities={async () => {
              const restaurantId = profile?.taiSan?.idTaiSan ?? profile?.danhSachTaiSan?.[0]?.idTaiSan
              if (!restaurantId) return
              const payload = profileForm.tienIchNhaHang.map((name) => ({
                tenTienIch: name,
                loaiTienIch: 'CO_BAN',
                moTa: name,
                coThuPhi: false,
                phiSuDung: 0,
              }))
              try {
                await partnerAssetService.updateRestaurantAmenities(restaurantId, payload)
                showMessage('Đã lưu tiện ích nhà hàng thành công.')
              } catch (err) {
                showError(err, 'Không thể lưu tiện ích nhà hàng.')
              }
            }}
          />
        )}

        {activeView === 'table-layout' && (
          <TableLayoutView
            tables={tables}
            tableForm={tableForm}
            selectedArea={selectedArea}
            setTableForm={setTableForm}
            setSelectedArea={setSelectedArea}
            createTables={createTables}
            createSingleTable={createSingleTable}
            startTableEdit={startTableEdit}
            deleteTable={deleteTable}
            editingTableId={editingTableId}
            isSaving={isTableSaving}
            propertyName={profileForm.tenCoSo || 'Khu vườn Emerald'}
          />
        )}

        {activeView === 'menu-management' && (
          <MenuManagementView
            menuItems={filteredMenuItems}
            menuSearch={menuSearch}
            setMenuSearch={setMenuSearch}
            menuStats={menuStats}
            setIsMenuModalOpen={(open) => {
              if (open) {
                setEditingMenuItemId(null)
                setMenuForm(initialMenuForm)
              }
              setIsMenuModalOpen(open)
            }}
            toggleMenuStatus={toggleMenuStatus}
            startMenuItemEdit={startMenuItemEdit}
            deleteMenuItem={deleteMenuItem}
            combos={combos}
            setIsComboModalOpen={(open) => {
              if (open) {
                setEditingComboId(null)
                setComboForm(initialComboForm)
              }
              setIsComboModalOpen(open)
            }}
            startComboEdit={startComboEdit}
            deleteCombo={deleteCombo}
          />
        )}
      {confirmation && (
        <ConfirmationDialog
          confirmation={confirmation}
          isConfirming={isConfirming}
          onCancel={() => {
            if (!isConfirming) setConfirmation(null)
          }}
          onConfirm={() => {
            void runConfirmedAction()
          }}
        />
      )}
      {isRoomModalOpen && (
        <RoomModal
          roomForm={roomForm}
          setRoomForm={setRoomForm}
          saveRoom={saveRoom}
          availableRoomAmenities={getAllowedRoomAmenities(profileForm.tienIchKhachSan)}
          isEditing={Boolean(editingRoomId)}
          isSaving={isRoomSaving}
          uploadFile={uploadFile}
          close={() => {
            setEditingRoomId(null)
            setRoomForm(initialRoomForm)
            setIsRoomModalOpen(false)
          }}
        />
      )}

      {isMenuModalOpen && (
        <MenuModal
          menuForm={menuForm}
          setMenuForm={setMenuForm}
          createMenuItem={createMenuItem}
          isEditing={Boolean(editingMenuItemId)}
          isSaving={isMenuSaving}
          uploadFile={uploadFile}
          close={() => {
            setEditingMenuItemId(null)
            setMenuForm(initialMenuForm)
            setIsMenuModalOpen(false)
          }}
        />
      )}

      {isComboModalOpen && (
        <ComboModal
          comboForm={comboForm}
          setComboForm={setComboForm}
          menuItems={menuItems}
          saveCombo={saveCombo}
          isEditing={Boolean(editingComboId)}
          isSaving={isComboSaving}
          close={() => {
            setEditingComboId(null)
            setComboForm(initialComboForm)
            setIsComboModalOpen(false)
          }}
        />
      )}
    </>
  )
}

function RoomModal({
  roomForm,
  setRoomForm,
  saveRoom,
  availableRoomAmenities,
  isEditing,
  isSaving,
  uploadFile,
  close,
}: {
  roomForm: typeof initialRoomForm
  setRoomForm: React.Dispatch<React.SetStateAction<typeof initialRoomForm>>
  saveRoom: () => void
  availableRoomAmenities: Array<{ label: string; value: string }>
  isEditing: boolean
  isSaving: boolean
  uploadFile: (file: File, options?: UploadOptions) => Promise<UploadedAsset | null>
  close: () => void
}) {
  const handleRoomImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      const uploaded = await uploadFile(file)
      if (uploaded) {
        setRoomForm((c) => ({ ...c, imageName: uploaded.url }))
      }
    }
  }

  return (
    <div className="modal-backdrop" onClick={close}>
      <div className="modal-card" onClick={(event) => event.stopPropagation()}>
        <div className="section-title between">
          <h2>{isEditing ? 'Chỉnh sửa phòng' : 'Thêm phòng'}</h2>
          <button className="icon-btn" onClick={close} type="button"><X size={16} /></button>
        </div>
        <div className="form-grid two-column">
          <label><span>Số phòng</span><input value={roomForm.soPhong} onChange={(e) => setRoomForm((c) => ({ ...c, soPhong: e.target.value }))} /></label>
          <label><span>Loại phòng</span><input value={roomForm.loaiPhong} onChange={(e) => setRoomForm((c) => ({ ...c, loaiPhong: e.target.value }))} /></label>
          <label className="full"><span>Mô tả phòng</span><textarea value={roomForm.moTa} onChange={(e) => setRoomForm((c) => ({ ...c, moTa: e.target.value }))} /></label>
          <label><span>Sức chứa</span><input value={roomForm.sucChuaToiDa} onChange={(e) => setRoomForm((c) => ({ ...c, sucChuaToiDa: e.target.value }))} /></label>
          <label><span>Số giường</span><input value={roomForm.soGiuong} onChange={(e) => setRoomForm((c) => ({ ...c, soGiuong: e.target.value }))} /></label>
          <label><span>Diện tích (m²)</span><input value={roomForm.dienTich} onChange={(e) => setRoomForm((c) => ({ ...c, dienTich: e.target.value }))} /></label>
          <label><span>Giá cơ bản (VND)</span><input value={roomForm.giaCoBan} onChange={(e) => setRoomForm((c) => ({ ...c, giaCoBan: e.target.value }))} /></label>
          <label><span>Số lượng phòng</span><input value={roomForm.soLuongPhong} onChange={(e) => setRoomForm((c) => ({ ...c, soLuongPhong: e.target.value }))} /></label>
          <label><span>Giảm giá (%)</span><input value={roomForm.phanTramGiamGia} onChange={(e) => setRoomForm((c) => ({ ...c, phanTramGiamGia: e.target.value }))} /></label>
          <label className="full">
            <span>Tiện ích phòng (dựa trên tiện ích khách sạn đã chọn)</span>
            <div style={{ display: 'grid', gap: 8, marginTop: 6 }}>
              {availableRoomAmenities.length === 0 && <small>Chưa có tiện ích khách sạn phù hợp để gán cho phòng.</small>}
              {availableRoomAmenities.map((amenity) => (
                <label key={amenity.value} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <input
                    type="checkbox"
                    checked={roomForm.tienIch.includes(amenity.value)}
                    onChange={() =>
                      setRoomForm((c) => ({
                        ...c,
                        tienIch: c.tienIch.includes(amenity.value)
                          ? c.tienIch.filter((item) => item !== amenity.value)
                          : [...c.tienIch, amenity.value],
                      }))
                    }
                  />
                  <span>{amenity.label}</span>
                </label>
              ))}
            </div>
          </label>
        </div>
        <div className="form-grid two-column">
          <label className="full">
            <span>Ảnh phòng *</span>
            <div className="upload-box" style={{ cursor: 'pointer' }}>
              <input type="file" accept="image/jpeg,image/png" onChange={handleRoomImageUpload} />
              <div style={{ textAlign: 'center', padding: '20px', color: '#68736d' }}>
                {roomForm.imageName ? (
                  <>
                    <img src={roomForm.imageName} alt="Preview" style={{ maxWidth: '100%', maxHeight: '150px', marginBottom: '8px', borderRadius: '4px' }} />
                    <p style={{ margin: '8px 0 0 0', fontSize: '12px' }}>Bấm để thay ảnh</p>
                  </>
                ) : (
                  <>
                    <p style={{ margin: '0', fontWeight: 500 }}>Tải ảnh lên</p>
                    <p style={{ margin: '4px 0 0 0', fontSize: '12px' }}>Bấm để chọn ảnh JPG hoặc PNG</p>
                  </>
                )}
              </div>
            </div>
          </label>
        </div>
        <div className="modal-actions">
          <button className="ghost-btn" onClick={close} type="button">Hủy</button>
          <button className="primary-btn" onClick={saveRoom} disabled={isSaving} type="button">{isSaving ? 'Đang lưu...' : 'Lưu'}</button>
        </div>
      </div>
    </div>
  )
}

function MenuModal({
  menuForm,
  setMenuForm,
  createMenuItem,
  isEditing,
  isSaving,
  uploadFile,
  close,
}: {
  menuForm: typeof initialMenuForm
  setMenuForm: React.Dispatch<React.SetStateAction<typeof initialMenuForm>>
  createMenuItem: () => void
  isEditing: boolean
  isSaving: boolean
  uploadFile: (file: File, options?: UploadOptions) => Promise<UploadedAsset | null>
  close: () => void
}) {
  const handleMenuImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      const uploaded = await uploadFile(file)
      if (uploaded) {
        setMenuForm((c) => ({ ...c, duongDanUrl: uploaded.url }))
      }
    }
  }

  return (
    <div className="modal-backdrop" onClick={close}>
      <div className="modal-card" onClick={(event) => event.stopPropagation()}>
        <div className="section-title between">
          <h2>{isEditing ? 'Chỉnh sửa món ăn' : 'Thêm món ăn'}</h2>
          <button className="icon-btn" onClick={close} type="button"><X size={16} /></button>
        </div>
        <div className="form-grid two-column">
          <label><span>Tên món</span><input value={menuForm.tenMon} onChange={(e) => setMenuForm((c) => ({ ...c, tenMon: e.target.value }))} /></label>
          <label><span>Giá bán</span><input value={menuForm.giaBan} onChange={(e) => setMenuForm((c) => ({ ...c, giaBan: e.target.value }))} /></label>
          <label className="full"><span>Nhãn hiển thị</span><input value={menuForm.theNguCanh} onChange={(e) => setMenuForm((c) => ({ ...c, theNguCanh: e.target.value }))} /></label>
        </div>
        <div className="form-grid two-column">
          <label className="full">
            <span>Ảnh món ăn *</span>
            <div className="upload-box" style={{ cursor: 'pointer' }}>
              <input type="file" accept="image/jpeg,image/png" onChange={handleMenuImageUpload} />
              <div style={{ textAlign: 'center', padding: '20px', color: '#68736d' }}>
                {menuForm.duongDanUrl ? (
                  <>
                    <img src={menuForm.duongDanUrl} alt="Preview" style={{ maxWidth: '100%', maxHeight: '150px', marginBottom: '8px', borderRadius: '4px' }} />
                    <p style={{ margin: '8px 0 0 0', fontSize: '12px' }}>Bấm để thay ảnh</p>
                  </>
                ) : (
                  <>
                    <p style={{ margin: '0', fontWeight: 500 }}>Tải ảnh lên</p>
                    <p style={{ margin: '4px 0 0 0', fontSize: '12px' }}>Bấm để chọn ảnh JPG hoặc PNG</p>
                  </>
                )}
              </div>
            </div>
          </label>
        </div>
        <div className="modal-actions">
          <button className="ghost-btn" onClick={close} type="button">Hủy</button>
          <button className="primary-btn" onClick={createMenuItem} disabled={isSaving} type="button">{isSaving ? 'Đang lưu...' : 'Lưu'}</button>
        </div>
      </div>
    </div>
  )
}

function ComboModal({
  comboForm,
  setComboForm,
  menuItems,
  saveCombo,
  isEditing,
  isSaving,
  close,
}: {
  comboForm: typeof initialComboForm
  setComboForm: React.Dispatch<React.SetStateAction<typeof initialComboForm>>
  menuItems: MenuItemResponse[]
  saveCombo: () => void
  isEditing: boolean
  isSaving: boolean
  close: () => void
}) {
  const toggleComboItem = (itemId: string) => {
    setComboForm((current) => ({
      ...current,
      monAnIds: current.monAnIds.includes(itemId)
        ? current.monAnIds.filter((id) => id !== itemId)
        : [...current.monAnIds, itemId],
    }))
  }

  return (
    <div className="modal-backdrop" onClick={close}>
      <div className="modal-card" onClick={(event) => event.stopPropagation()}>
        <div className="section-title between">
          <h2>{isEditing ? 'Chỉnh sửa combo' : 'Thêm combo'}</h2>
          <button className="icon-btn" onClick={close} type="button"><X size={16} /></button>
        </div>
        <div className="form-grid two-column">
          <label><span>Tên combo</span><input value={comboForm.tenCombo} onChange={(e) => setComboForm((c) => ({ ...c, tenCombo: e.target.value }))} /></label>
          <label><span>Giá combo</span><input value={comboForm.giaCombo} onChange={(e) => setComboForm((c) => ({ ...c, giaCombo: e.target.value }))} /></label>
          <label><span>Mô tả</span><input value={comboForm.moTa} onChange={(e) => setComboForm((c) => ({ ...c, moTa: e.target.value }))} /></label>
          <label><span>Trạng thái</span><select value={comboForm.trangThai} onChange={(e) => setComboForm((c) => ({ ...c, trangThai: e.target.value }))}><option value="1">Đang hoạt động</option><option value="0">Tạm dừng</option></select></label>
        </div>

        <div className="combo-picker">
          <h3>Món trong combo</h3>
          {menuItems.length === 0 ? (
            <p className="muted-text">Chưa có món ăn nào để chọn. Hãy thêm món trước.</p>
          ) : (
            menuItems.map((item) => (
              <label key={item.id} className="combo-item-row">
                <input
                  type="checkbox"
                  checked={comboForm.monAnIds.includes(item.id)}
                  onChange={() => toggleComboItem(item.id)}
                />
                <span>{item.tenMon}</span>
                <strong>{formatMoney(item.giaBan)}</strong>
              </label>
            ))
          )}
        </div>

        <div className="modal-actions">
          <button className="ghost-btn" onClick={close} type="button">Hủy</button>
          <button className="primary-btn" onClick={saveCombo} disabled={isSaving} type="button">{isSaving ? 'Đang lưu...' : 'Lưu'}</button>
        </div>
      </div>
    </div>
  )
}

/* CalendarIcon moved to PartnerLayout */
/* function CalendarIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="20" viewBox="0 0 24 24" width="20">
      <path d="M7 3v4M17 3v4M4 9h16M6 5h12a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" />
    </svg>
  )
} */

/* function AnalyticsIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="20" viewBox="0 0 24 24" width="20">
      <path d="M4 19V5M4 19h16M8 16l3-4 3 2 5-7M8 16v-4M14 14v-4M19 7v9" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" />
    </svg>
  )
} */

/* function SettingsIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="20" viewBox="0 0 24 24" width="20">
      <path d="M12 15.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Z" stroke="currentColor" strokeWidth="2" />
      <path d="M19.4 15a1.7 1.7 0 0 0 .34 1.88l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06A1.7 1.7 0 0 0 15 19.4a1.7 1.7 0 0 0-1 1.55V21a2 2 0 0 1-4 0v-.08A1.7 1.7 0 0 0 9 19.4a1.7 1.7 0 0 0-1.88.34l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.7 1.7 0 0 0 4.6 15a1.7 1.7 0 0 0-1.55-1H3a2 2 0 0 1 0-4h.08A1.7 1.7 0 0 0 4.6 9a1.7 1.7 0 0 0-.34-1.88l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.7 1.7 0 0 0 9 4.6a1.7 1.7 0 0 0 1-1.55V3a2 2 0 0 1 4 0v.08A1.7 1.7 0 0 0 15 4.6a1.7 1.7 0 0 0 1.88-.34l.06-.06A2 2 0 0 1 19.8 6.94l-.06.06A1.7 1.7 0 0 0 19.4 9a1.7 1.7 0 0 0 1.55 1H21a2 2 0 0 1 0 4h-.08A1.7 1.7 0 0 0 19.4 15Z" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" />
    </svg>
  )
} */
function getBusinessProfileStatus(profile: BusinessProfileResponse | null) {
  if (!profile) {
    return {
      label: 'Chưa lưu',
      className: 'yellow',
      description: 'Hồ sơ mới chỉ đang nhập trên giao diện, chưa lưu lên hệ thống.',
    }
  }

  switch (profile.trangThaiHoatDong) {
    case 'DANG_HOAT_DONG':
      return {
        label: 'Đang hoạt động',
        className: 'green',
        description: 'Hồ sơ đã được kích hoạt và có thể hiển thị trên hệ thống.',
      }

    case 'TAM_DUNG':
      return {
        label: 'Tạm dừng',
        className: 'yellow',
        description: 'Hồ sơ đang tạm dừng hoạt động.',
      }

    case 'BI_KHOA':
      return {
        label: 'Bị khóa',
        className: 'red',
        description: 'Hồ sơ đang bị khóa, cần liên hệ quản trị viên.',
      }

    default:
      return {
        label: profile.trangThaiHoatDong || 'Chưa rõ trạng thái',
        className: 'gray',
        description: 'Trạng thái hồ sơ hiện tại.',
      }
  }
}
type BusinessProfileViewProps = {
  step: number
  setStep: (step: number) => void
  profile: BusinessProfileResponse | null
  profileForm: ProfileFormState
  profileErrors: ProfileFormErrors
  isSubmitting: boolean
  updateProfileField: (name: keyof ProfileFormState, value: ProfileFormValue) => void
  saveProfile: () => void
  saveDraft: () => void
  uploadFile: (file: File, options?: UploadOptions) => Promise<UploadedAsset | null>
  uploadBusinessLicense: (file: File) => Promise<void>
  isUploadingLicense: boolean
  toggleAmenity: (amenity: string) => void
  addCustomAmenity: (value: string) => void
  availableHotelAmenities: Array<{ id: string; tenTienIch: string; loaiTienIch: string; moTa: string }>
}

function BusinessProfileView({
  profile,
  profileForm,
  profileErrors,
  isSubmitting,
  updateProfileField,
  saveProfile,
  saveDraft,
  uploadBusinessLicense,
  isUploadingLicense,
}: BusinessProfileViewProps) {
  // One-page form: keep existing validation + upload/map logic,
  // but render all sections in a single screen.
  const currentStepErrors = profileErrors

  const isEditMode = Boolean(profile?.idHoSo)
  const currentStepError = getFirstError(currentStepErrors)
  const profileStatus = getBusinessProfileStatus(profile)
  const businessCoverImage = getBusinessCoverImage(profileForm)

  return (
    <>
      <header className="partner-page-header split">
        <div>
          <h1>{isEditMode ? 'Hồ sơ kinh doanh' : 'Thiết lập hồ sơ kinh doanh'}</h1>
          <p>
            {isEditMode
              ? 'Thông tin hồ sơ hiện tại của cơ sở kinh doanh. Quý đối tác có thể chỉnh sửa trực tiếp toàn bộ thông tin tại đây.'
              : 'Hoàn thiện thông tin cơ sở kinh doanh để kích hoạt hiển thị trên hệ thống.'}
          </p>
        </div>

        <div className="status-cluster single-status">
          <span className={`mini-pill ${profileStatus.className}`}>
            {profileStatus.label}
          </span>
        </div>
      </header>

      <div className="partner-profile-grid business-profile-layout">
        <section className="profile-form-area">
          <div className="profile-completion-strip">
            <div>
              <strong>Hoàn thiện hồ sơ kinh doanh</strong>
              <span>Toàn bộ thông tin được gom trên một trang để Quý đối tác kiểm tra và gửi khởi tạo nhanh hơn.</span>
            </div>
            <span className="mini-pill info">Một bước duy nhất</span>
          </div>

          <section className="panel partner-card partner-profile-one-page">
            {currentStepError && (
              <div className="form-error-summary">
                <XCircle size={18} />
                <span>{currentStepError}</span>
              </div>
            )}

            {(
              <>
                <div className="accent-heading">
                  <Building2 size={22} />
                  <h2>Thông tin cơ bản</h2>
                </div>

                <div className="business-profile-step">
                  <div className="form-grid single">
                    <label className={profileErrors.tenCoSo ? 'field-error' : ''}>
                      Tên cơ sở
                      <input
                        value={profileForm.tenCoSo}
                        onChange={(event) => updateProfileField('tenCoSo', event.target.value)}
                        placeholder="Ví dụ: TraVi Resort & Spa"
                      />
                      {profileErrors.tenCoSo && <small>{profileErrors.tenCoSo}</small>}
                    </label>

                    <label className={profileErrors.sdtLienHe ? 'field-error' : ''}>
                      Số điện thoại liên hệ
                      <input
                        value={profileForm.sdtLienHe}
                        onChange={(event) => updateProfileField('sdtLienHe', event.target.value)}
                        placeholder="Ví dụ: 0912345678"
                      />
                      {profileErrors.sdtLienHe && <small>{profileErrors.sdtLienHe}</small>}
                    </label>

                    <label>
                      Loại dịch vụ
                      <select
                        value={profileForm.loaiDichVu}
                        onChange={(event) => updateProfileField('loaiDichVu', event.target.value as ServiceType)}
                        disabled={Boolean(profile)}
                      >
                        <option value="KHACH_SAN">Khách sạn</option>
                        <option value="NHA_HANG">Nhà hàng</option>
                      </select>
                      {profile && <small>Loại dịch vụ đã được khóa sau khi hồ sơ được tạo.</small>}
                    </label>

                    <label className={profileErrors.giaCoBan ? 'field-error' : ''}>
                      Giá cơ bản (VND)
                      <input
                        type="number"
                        min="1000"
                        value={profileForm.giaCoBan}
                        onChange={(event) => updateProfileField('giaCoBan', event.target.value)}
                        placeholder="500000"
                      />
                      {profileErrors.giaCoBan && <small>{profileErrors.giaCoBan}</small>}
                    </label>

                    {profileForm.loaiDichVu === 'KHACH_SAN' ? (
                      <>
                        <label className={profileErrors.hangSao ? 'field-error' : ''}>
                          Hạng sao khách sạn
                          <select
                            value={profileForm.hangSao}
                            onChange={(event) => updateProfileField('hangSao', event.target.value)}
                          >
                            <option value="1">1 sao</option>
                            <option value="2">2 sao</option>
                            <option value="3">3 sao</option>
                            <option value="4">4 sao</option>
                            <option value="5">5 sao</option>
                          </select>
                          {profileErrors.hangSao && <small>{profileErrors.hangSao}</small>}
                        </label>

                        <label className={profileErrors.loaiKhachSan ? 'field-error' : ''}>
                          Loại khách sạn
                          <input
                            value={profileForm.loaiKhachSan}
                            onChange={(event) => updateProfileField('loaiKhachSan', event.target.value)}
                            placeholder="Resort, Boutique, City Hotel..."
                          />
                          {profileErrors.loaiKhachSan && <small>{profileErrors.loaiKhachSan}</small>}
                        </label>

                        <label className={profileErrors.soTang ? 'field-error' : ''}>
                          Số tầng
                          <input
                            type="number"
                            min="1"
                            value={profileForm.soTang}
                            onChange={(event) => updateProfileField('soTang', event.target.value)}
                            placeholder="Ví dụ: 12"
                          />
                          {profileErrors.soTang && <small>{profileErrors.soTang}</small>}
                        </label>

                        <label className={profileErrors.tongSoPhong ? 'field-error' : ''}>
                          Tổng số phòng
                          <input
                            type="number"
                            min="1"
                            value={profileForm.tongSoPhong}
                            onChange={(event) => updateProfileField('tongSoPhong', event.target.value)}
                            placeholder="Ví dụ: 120"
                          />
                          {profileErrors.tongSoPhong && <small>{profileErrors.tongSoPhong}</small>}
                        </label>

                        <label>
                          Giờ nhận phòng
                          <input
                            type="time"
                            value={profileForm.gioNhanPhong}
                            onChange={(event) => updateProfileField('gioNhanPhong', event.target.value)}
                          />
                        </label>

                        <label>
                          Giờ trả phòng
                          <input
                            type="time"
                            value={profileForm.gioTraPhong}
                            onChange={(event) => updateProfileField('gioTraPhong', event.target.value)}
                          />
                        </label>
                      </>
                    ) : (
                      <>
                        <label className={profileErrors.loaiAmThuc ? 'field-error' : ''}>
                          Loại ẩm thực
                          <input
                            value={profileForm.loaiAmThuc}
                            onChange={(event) => updateProfileField('loaiAmThuc', event.target.value)}
                            placeholder="Việt Nam, Nhật, Hải sản..."
                          />
                          {profileErrors.loaiAmThuc && <small>{profileErrors.loaiAmThuc}</small>}
                        </label>

                        <label className={profileErrors.sucChua ? 'field-error' : ''}>
                          Sức chứa nhà hàng
                          <input
                            type="number"
                            min="1"
                            value={profileForm.sucChua}
                            onChange={(event) => updateProfileField('sucChua', event.target.value)}
                            placeholder="80"
                          />
                          {profileErrors.sucChua && <small>{profileErrors.sucChua}</small>}
                        </label>
                      </>
                    )}

                    <label className={profileErrors.moTa ? 'field-error' : ''}>
                      Mô tả ngắn
                      <textarea
                        value={profileForm.moTa}
                        onChange={(event) => updateProfileField('moTa', event.target.value)}
                        placeholder="Giới thiệu cơ sở kinh doanh trong vài câu..."
                      />
                      {profileErrors.moTa && <small>{profileErrors.moTa}</small>}
                    </label>
                  </div>

                  <div className="business-preview-card">
                    <p><Eye size={14} /> Xem trước hiển thị</p>
                    <img src={businessCoverImage} alt="Ảnh đại diện cơ sở" />

                    <div>
                      <strong>{profileForm.tenCoSo || 'Tên cơ sở của bạn'}</strong>
                      <span>$$$</span>
                    </div>

                    <small>
                      {profileForm.moTa || 'Mô tả của bạn sẽ hiển thị tại đây để giúp khách hiểu rõ trải nghiệm dịch vụ.'}
                    </small>

                    <button>Xem chi tiết</button>
                  </div>
                </div>
              </>
            )}

            {(
              <>
                <div className="accent-heading">
                  <FileText size={22} />
                  <h2>Giấy tờ pháp lý</h2>
                </div>

                <div className="form-grid single">
                  <label className={profileErrors.maSoThue ? 'field-error' : ''}>
                    Mã số thuế
                    <input
                      value={profileForm.maSoThue}
                      onChange={(event) => updateProfileField('maSoThue', event.target.value)}
                      placeholder="Nhập mã số thuế 10 hoặc 13 chữ số"
                    />
                    <small>Thông tin này dùng cho xuất hóa đơn và xác minh pháp lý.</small>
                    {profileErrors.maSoThue && <small>{profileErrors.maSoThue}</small>}
                  </label>

                  <label
                    className={`license-drop-zone ${profileErrors.businessLicense ? 'has-error' : ''}`}
                    onDragOver={(event) => event.preventDefault()}
                    onDrop={(event) => {
                      event.preventDefault()

                      const file = event.dataTransfer.files?.[0]

                      if (file) {
                        void uploadBusinessLicense(file)
                      }
                    }}
                  >
                    <UploadCloud size={30} />
                    <strong>{isUploadingLicense ? 'Đang tải giấy phép kinh doanh...' : 'Kéo thả file hoặc bấm để chọn'}</strong>
                    <span>PDF, JPG hoặc PNG, tối đa 10MB</span>

                    <input
                      type="file"
                      accept=".pdf,.jpg,.jpeg,.png,application/pdf,image/jpeg,image/png"
                      onChange={async (event) => {
                        const file = event.target.files?.[0]
                        if (!file) return

                        await uploadBusinessLicense(file)
                        event.target.value = ''
                      }}
                    />
                  </label>

                  {profileErrors.businessLicense && <p className="field-error-text">{profileErrors.businessLicense}</p>}

                  {profileForm.giayPhepKinhDoanh && (
                    <div className="uploaded-file">
                      <FileText />

                      <div>
                        <strong>{profileForm.giayPhepKinhDoanh.split('/').pop()}</strong>
                        <span>Vừa tải lên</span>
                      </div>

                      <button onClick={() => updateProfileField('giayPhepKinhDoanh', '')}>
                        <Trash2 size={18} />
                      </button>
                    </div>
                  )}
                </div>
              </>
            )}

            <div className="profile-relocation-note">
              <p>
                Ảnh cơ sở, tiện ích và chính sách đã được chuyển sang màn hình cấu hình khách sạn/nhà hàng sau khi khởi tạo hồ sơ.
              </p>
            </div>
          </section>

          <div className="profile-actions one-page-actions">
            <div>
              {!isEditMode && (
                <button className="secondary-btn" disabled={isSubmitting} onClick={saveDraft} type="button">
                  Lưu nháp
                </button>
              )}

              <button className="primary-btn" type="button" disabled={isSubmitting} onClick={saveProfile}>
                {isSubmitting ? 'Đang lưu hồ sơ...' : profile ? 'Lưu cập nhật hồ sơ' : 'Tiếp tục'}
                <ArrowRight size={18} />
              </button>
            </div>
          </div>
        </section>
      </div>
    </>
  )
}

function HotelSetupView({
  profile,
  profileForm,
  updateProfileField,
  setActiveView,
  saveProfile,
  isSubmitting,
  uploadFile,
  availableHotelAmenities,
  selectedHotelAmenityIds,
  onToggleHotelAmenity,
  onSaveHotelAmenities,
}: {
  profile: BusinessProfileResponse | null
  profileForm: ProfileFormState
  updateProfileField: (name: keyof ProfileFormState, value: ProfileFormValue) => void
  setActiveView: (view: PartnerView) => void
  saveProfile: () => Promise<void>
  isSubmitting: boolean
  uploadFile: (file: File, options?: UploadOptions) => Promise<UploadedAsset | null>
  availableHotelAmenities: Array<{ id: string; tenTienIch: string; loaiTienIch: string; moTa: string }>
  selectedHotelAmenityIds: string[]
  onToggleHotelAmenity: (amenityId: string) => void
  onSaveHotelAmenities: () => Promise<void>
}) {
  const [isUploadingHotelImage, setIsUploadingHotelImage] = useState(false)
  const groupedAmenities = Object.entries(
    availableHotelAmenities.reduce((groups, amenity) => {
      const key = (amenity.loaiTienIch || 'KHAC').trim()
      if (!groups[key]) groups[key] = []
      groups[key].push(amenity)
      return groups
    }, {} as Record<string, Array<{ id: string; tenTienIch: string; loaiTienIch: string; moTa: string }>>),
  )

  return (
    <>
      <header className="partner-page-header">
        <h1>Thiết lập khách sạn</h1>
        <p>Cấu hình thông tin khách sạn trước khi quản lý danh sách phòng.</p>
      </header>
      <section className="hotel-setup-grid">
        <div className="panel feature-panel with-cover">
          <img className="feature-cover-image" src={getBusinessCoverImage(profileForm)} alt="Ảnh đại diện khách sạn" />
          <Hotel size={32} />
          <h2>{profileForm.tenCoSo || 'Khách sạn Grand Horizon'}</h2>
          <p>{profileForm.moTa || 'Cơ sở lưu trú chất lượng cao đã sẵn sàng phục vụ khách hàng TraVi.'}</p>
          <div className="setup-stats">
            <span>{profileForm.hangSao} sao</span>
            <span>{formatMoney(Number(profileForm.giaCoBan || 0))}</span>
            <span>{profile ? profile.trangThaiHoatDong : 'BẢN NHÁP'}</span>
          </div>
        </div>
        <div className="panel setup-checklist">
          <h2>Thông tin khách sạn</h2>
          <h2>Ảnh khách sạn</h2>
          <div className="form-grid single">
            <label className="license-drop-zone">
              <UploadCloud size={26} />
              <strong>{isUploadingHotelImage ? 'Đang tải ảnh...' : 'Tải ảnh khách sạn'}</strong>
              <span>JPG/PNG, tối đa 10MB</span>
              <input
                type="file"
                accept=".jpg,.jpeg,.png,image/jpeg,image/png"
                onChange={async (event) => {
                  const file = event.target.files?.[0]
                  if (!file) return
                  setIsUploadingHotelImage(true)
                  try {
                    const uploaded = await uploadFile(file, { allowPdf: false })
                    if (uploaded) {
                      updateProfileField('danhSachAnh', [
                        ...profileForm.danhSachAnh,
                        {
                          url: uploaded.url,
                          moTa: uploaded.fileName || 'Ảnh khách sạn',
                          laAnhDaiDien: profileForm.danhSachAnh.length === 0,
                        },
                      ])
                    }
                  } finally {
                    setIsUploadingHotelImage(false)
                    event.target.value = ''
                  }
                }}
              />
            </label>
            {profileForm.danhSachAnh.length > 0 && (
              <div style={{ display: 'grid', gap: 10 }}>
                {profileForm.danhSachAnh.map((image, index) => (
                  <div key={`${image.url}-${index}`} style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
                    <img src={image.url} alt={image.moTa || 'Ảnh khách sạn'} style={{ width: 76, height: 56, objectFit: 'cover', borderRadius: 8 }} />
                    <button
                      className="secondary-btn"
                      type="button"
                      onClick={() =>
                        updateProfileField(
                          'danhSachAnh',
                          profileForm.danhSachAnh.map((item, itemIndex) => ({ ...item, laAnhDaiDien: itemIndex === index })),
                        )
                      }
                    >
                      {image.laAnhDaiDien ? 'Ảnh đại diện' : 'Đặt làm đại diện'}
                    </button>
                    <button
                      className="danger-btn"
                      type="button"
                      onClick={() => {
                        const nextImages = profileForm.danhSachAnh.filter((_, itemIndex) => itemIndex !== index)
                        if (nextImages.length > 0 && !nextImages.some((item) => item.laAnhDaiDien)) {
                          nextImages[0] = { ...nextImages[0], laAnhDaiDien: true }
                        }
                        updateProfileField('danhSachAnh', nextImages)
                      }}
                    >
                      Xóa
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
          <div className="form-grid single">
            <label>
              Địa chỉ
              <input value={profileForm.diaChi} onChange={(event) => updateProfileField('diaChi', event.target.value)} />
            </label>
            <label>
              Thành phố
              <select value={profileForm.thanhPho} onChange={(event) => updateProfileField('thanhPho', event.target.value)}>
                <option value="">Chọn thành phố</option>
                {VIETNAM_MAJOR_CITIES.map((city) => (
                  <option key={city} value={city}>{city}</option>
                ))}
              </select>
            </label>
            <label>
              Loại khách sạn
              <input value={profileForm.loaiKhachSan} onChange={(event) => updateProfileField('loaiKhachSan', event.target.value)} />
            </label>
            <label>
              Hạng sao
              <select value={profileForm.hangSao} onChange={(event) => updateProfileField('hangSao', event.target.value)}>
                <option value="1">1 sao</option>
                <option value="2">2 sao</option>
                <option value="3">3 sao</option>
                <option value="4">4 sao</option>
                <option value="5">5 sao</option>
              </select>
            </label>
            <label>
              Số tầng
              <input type="number" min="1" value={profileForm.soTang} onChange={(event) => updateProfileField('soTang', event.target.value)} />
            </label>
            <label>
              Tổng số phòng
              <input type="number" min="1" value={profileForm.tongSoPhong} onChange={(event) => updateProfileField('tongSoPhong', event.target.value)} />
            </label>
            <label>
              Giờ nhận phòng
              <input type="time" value={profileForm.gioNhanPhong} onChange={(event) => updateProfileField('gioNhanPhong', event.target.value)} />
            </label>
            <label>
              Giờ trả phòng
              <input type="time" value={profileForm.gioTraPhong} onChange={(event) => updateProfileField('gioTraPhong', event.target.value)} />
            </label>
            <label>
              Giá cơ bản (VND)
              <input type="number" min="1000" value={profileForm.giaCoBan} onChange={(event) => updateProfileField('giaCoBan', event.target.value)} />
            </label>
            <label>
              Mô tả khách sạn
              <textarea value={profileForm.moTa} onChange={(event) => updateProfileField('moTa', event.target.value)} />
            </label>
          </div>
          <div style={{ display: 'flex', gap: 10, margin: '8px 0 14px' }}>
            <button className="secondary-btn" type="button" onClick={() => void saveProfile()} disabled={isSubmitting}>
              {isSubmitting ? 'Đang lưu...' : 'Lưu cấu hình khách sạn'}
            </button>
          </div>
          <h2>Tiện ích khách sạn</h2>
          {groupedAmenities.length === 0 ? (
            <p>Chưa có tiện ích để chọn.</p>
          ) : (
            <div style={{ display: 'grid', gap: 14 }}>
              {groupedAmenities.map(([type, amenities]) => (
                <div key={type} className="panel" style={{ padding: 12 }}>
                  <p style={{ margin: '0 0 10px', fontWeight: 700 }}>
                    {type.replace(/_/g, ' ')}
                  </p>
                  <div className="form-grid single">
                    {amenities.map((amenity) => (
                      <label key={amenity.id} className="amenity-compact-option" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                        <input
                          className="amenity-compact-input"
                          type="checkbox"
                          checked={selectedHotelAmenityIds.includes(amenity.id)}
                          onChange={() => onToggleHotelAmenity(amenity.id)}
                        />
                        <span>{amenity.tenTienIch}</span>
                      </label>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
          <div style={{ display: 'flex', gap: 10, margin: '8px 0 14px' }}>
            <button className="secondary-btn" type="button" onClick={() => void onSaveHotelAmenities()}>
              Lưu tiện ích khách sạn
            </button>
          </div>
          <h2>Vị trí cơ sở</h2>
          <BusinessLocationMap
            value={profileForm.toaDoGPS}
            onChange={(coordinates) => updateProfileField('toaDoGPS', coordinates)}
          />
          <label style={{ display: 'block', marginTop: 12 }}>
            <span style={{ display: 'block', marginBottom: 6 }}>Tọa độ GPS</span>
            <input
              value={profileForm.toaDoGPS}
              onChange={(event) => updateProfileField('toaDoGPS', event.target.value)}
              placeholder="10.77653,106.70098"
            />
          </label>
          <hr style={{ margin: '14px 0' }} />
          <h2>Mức độ sẵn sàng</h2>
          <p><CheckCircle2 /> Hồ sơ kinh doanh đã được tạo</p>
          <p><CheckCircle2 /> Đã tải lên giấy phép kinh doanh</p>
          <p><XCircle /> Cần thiết lập danh sách phòng</p>
          <button className="primary-btn" onClick={() => setActiveView('room-management')}>Đi tới quản lý phòng</button>
        </div>
      </section>
    </>
  )
}

function RoomManagementView({
  rooms,
  roomSearch,
  setRoomSearch,
  setIsRoomModalOpen,
  startRoomEdit,
  deleteRoom,
}: {
  rooms: RoomResponse[]
  roomSearch: string
  setRoomSearch: (value: string) => void
  setIsRoomModalOpen: (open: boolean) => void
  startRoomEdit: (room: RoomResponse) => void
  deleteRoom: (roomId: string) => void
}) {
  return (
    <>
      <header className="partner-page-header split">
        <div>
          <p className="breadcrumb">Quản lý › <strong>Quản lý phòng</strong></p>
          <h1>Danh sách phòng</h1>
          <p>Quản lý trạng thái phòng, giá bán và tiện ích của cơ sở.</p>
        </div>
        <div className="header-actions">
          <div className="search-box"><Search size={20} /><input value={roomSearch} onChange={(event) => setRoomSearch(event.target.value)} placeholder="Tìm kiếm phòng..." /></div>
          <button className="secondary-btn">Lọc</button>
          <button className="primary-btn" onClick={() => setIsRoomModalOpen(true)}><Plus size={18} /> Thêm phòng</button>
        </div>
      </header>
      <section className="panel table-panel">
        <table className="data-table room-table">
          <thead>
            <tr>
              <th>Ảnh</th>
              <th>Số phòng</th>
              <th>Loại</th>
              <th>Sức chứa</th>
              <th>Diện tích</th>
              <th>Giá</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {rooms.map((room) => (
              <tr key={room.id}>
                <td>
                  <div className={`room-thumb ${getRoomCoverImage(room) ? 'has-image' : ''}`}>
                    {getRoomCoverImage(room) ? <img src={getRoomCoverImage(room)} alt={`Ảnh phòng ${room.soPhong}`} /> : <Bed size={22} />}
                  </div>
                </td>
                <td><strong>{room.soPhong}</strong></td>
                <td><strong>{room.loaiPhong}</strong><span className="table-subtext">{room.tienIch.join(' · ')}</span></td>
                <td>{room.sucChuaToiDa} Người</td>
                <td>{room.dienTich} m²</td>
                <td>
                  <strong>{formatMoney(room.giaCoBan ?? 0)}</strong>
                  {room.phanTramGiamGia > 0 && (
                    <span className="table-subtext">
                      {`Giảm ${room.phanTramGiamGia}% · Còn ${formatMoney(((room.giaCoBan ?? 0) * (100 - room.phanTramGiamGia)) / 100)}`}
                    </span>
                  )}
                </td>
                <td><span className="badge green">Còn phòng</span></td>
                <td className="table-actions"><button className="secondary-btn row-action-btn" type="button" onClick={() => startRoomEdit(room)}><Pencil size={16} /> Sửa</button><button className="danger-btn row-action-btn" type="button" onClick={(event) => { event.preventDefault(); event.stopPropagation(); deleteRoom(room.id) }}><Trash2 size={16} /> Xóa</button></td>
              </tr>
            ))}
            {!rooms.length && <tr><td colSpan={8} className="empty-cell">Chưa có phòng nào. Hãy thêm phòng đầu tiên để bắt đầu.</td></tr>}
          </tbody>
        </table>
      </section>
    </>
  )
}

function RestaurantSetupView({
  profile,
  profileForm,
  updateProfileField,
  setActiveView,
  saveProfile,
  isSubmitting,
  uploadFile,
  availableRestaurantAmenities,
  onSaveRestaurantAmenities,
}: {
  profile: BusinessProfileResponse | null
  profileForm: ProfileFormState
  updateProfileField: (name: keyof ProfileFormState, value: ProfileFormValue) => void
  setActiveView: (view: PartnerView) => void
  saveProfile: () => Promise<void>
  isSubmitting: boolean
  uploadFile: (file: File, options?: UploadOptions) => Promise<UploadedAsset | null>
  availableRestaurantAmenities: string[]
  onSaveRestaurantAmenities: () => Promise<void>
}) {
  const [isUploadingRestaurantImage, setIsUploadingRestaurantImage] = useState(false)
  return (
    <>
      <header className="partner-page-header">
        <h1>Thiết lập nhà hàng</h1>
        <p>Chuẩn bị sức chứa nhà hàng, định vị ẩm thực và các công cụ vận hành.</p>
      </header>
      <section className="hotel-setup-grid">
        <div className="panel feature-panel with-cover">
          <img className="feature-cover-image" src={getBusinessCoverImage(profileForm)} alt="Ảnh đại diện nhà hàng" />
          <Utensils size={32} />
          <h2>{profileForm.tenCoSo || 'The Emerald Garden'}</h2>
          <p>{profileForm.loaiAmThuc} · {profileForm.sucChua} chỗ · {profile ? profile.trangThaiHoatDong : 'BẢN NHÁP'}</p>
          <div className="setup-stats">
            <span>Thực đơn sẵn sàng</span>
            <span>Bố cục bàn</span>
            <span>Bật/tắt trạng thái</span>
          </div>
        </div>
        <div className="panel setup-checklist">
          <h2>Thông tin nhà hàng</h2>
          <div className="form-grid single">
            <label>
              Loại ẩm thực
              <input value={profileForm.loaiAmThuc} onChange={(event) => updateProfileField('loaiAmThuc', event.target.value)} />
            </label>
            <label>
              Sức chứa
              <input type="number" min="1" value={profileForm.sucChua} onChange={(event) => updateProfileField('sucChua', event.target.value)} />
            </label>
            <label>
              Thành phố
              <select value={profileForm.thanhPho} onChange={(event) => updateProfileField('thanhPho', event.target.value)}>
                <option value="">Chọn thành phố</option>
                {VIETNAM_MAJOR_CITIES.map((city) => (
                  <option key={city} value={city}>{city}</option>
                ))}
              </select>
            </label>
            <label>
              Địa chỉ
              <input
                value={profileForm.diaChi}
                onChange={(event) => updateProfileField('diaChi', event.target.value)}
                placeholder="Ví dụ: 12 Nguyễn Huệ, Quận 1"
              />
            </label>
            <label>
              Giờ mở cửa
              <input type="time" value={profileForm.gioMoCua} onChange={(event) => updateProfileField('gioMoCua', event.target.value)} />
            </label>
            <label>
              Giờ đóng cửa
              <input type="time" value={profileForm.gioDongCua} onChange={(event) => updateProfileField('gioDongCua', event.target.value)} />
            </label>
            <label>
              Cho phép đặt bàn trước
              <select
                value={profileForm.tienIchNhaHang.includes('Đặt bàn online') ? 'true' : 'false'}
                onChange={(event) => {
                  const enabled = event.target.value === 'true'
                  const current = profileForm.tienIchNhaHang
                  updateProfileField(
                    'tienIchNhaHang',
                    enabled
                      ? Array.from(new Set([...current, 'Đặt bàn online']))
                      : current.filter((item) => item !== 'Đặt bàn online'),
                  )
                }}
              >
                <option value="true">Có</option>
                <option value="false">Không</option>
              </select>
            </label>
            <label>
              Cho phép đặt món trước
              <select
                value={profileForm.tienIchNhaHang.includes('Đặt món trước') ? 'true' : 'false'}
                onChange={(event) => {
                  const enabled = event.target.value === 'true'
                  const current = profileForm.tienIchNhaHang
                  updateProfileField(
                    'tienIchNhaHang',
                    enabled
                      ? Array.from(new Set([...current, 'Đặt món trước']))
                      : current.filter((item) => item !== 'Đặt món trước'),
                  )
                }}
              >
                <option value="true">Có</option>
                <option value="false">Không</option>
              </select>
            </label>
            <label>
              Giá cơ bản (VND)
              <input type="number" min="1000" value={profileForm.giaCoBan} onChange={(event) => updateProfileField('giaCoBan', event.target.value)} />
            </label>
            <label className="full">
              Mô tả nhà hàng
              <textarea value={profileForm.moTa} onChange={(event) => updateProfileField('moTa', event.target.value)} />
            </label>
          </div>
          <div style={{ display: 'flex', gap: 10, margin: '8px 0 14px' }}>
            <button className="secondary-btn" type="button" onClick={() => void saveProfile()} disabled={isSubmitting}>
              {isSubmitting ? 'Đang lưu...' : 'Lưu cấu hình nhà hàng'}
            </button>
          </div>
          <h2>Ảnh nhà hàng</h2>
          <label className="license-drop-zone">
            <UploadCloud size={26} />
            <strong>{isUploadingRestaurantImage ? 'Đang tải ảnh...' : 'Tải ảnh nhà hàng'}</strong>
            <span>JPG/PNG, tối đa 10MB</span>
            <input
              type="file"
              accept=".jpg,.jpeg,.png,image/jpeg,image/png"
              onChange={async (event) => {
                const file = event.target.files?.[0]
                if (!file) return
                setIsUploadingRestaurantImage(true)
                try {
                  const uploaded = await uploadFile(file, { allowPdf: false })
                  if (uploaded) {
                    updateProfileField('danhSachAnh', [
                      ...profileForm.danhSachAnh,
                      { url: uploaded.url, moTa: uploaded.fileName || 'Ảnh nhà hàng', laAnhDaiDien: profileForm.danhSachAnh.length === 0 },
                    ])
                  }
                } finally {
                  setIsUploadingRestaurantImage(false)
                  event.target.value = ''
                }
              }}
            />
          </label>
          {profileForm.danhSachAnh.length > 0 && (
            <div className="uploaded-image-list">
              {profileForm.danhSachAnh.map((image, index) => (
                <div className={`uploaded-image-row ${image.laAnhDaiDien ? 'is-cover' : ''}`} key={`${image.url}-${index}`}>
                  <img src={image.url} alt={image.moTa || `Ảnh nhà hàng ${index + 1}`} className="uploaded-image-thumb" />
                  <div className="uploaded-image-meta">
                    <strong>{image.moTa || `Ảnh nhà hàng ${index + 1}`}</strong>
                    <span>{image.laAnhDaiDien ? 'Ảnh đại diện' : 'Ảnh phụ'}</span>
                  </div>
                  <div className="uploaded-image-actions">
                    <button
                      className="secondary-btn compact-btn"
                      type="button"
                      onClick={() =>
                        updateProfileField(
                          'danhSachAnh',
                          profileForm.danhSachAnh.map((item, itemIndex) => ({ ...item, laAnhDaiDien: itemIndex === index })),
                        )
                      }
                    >
                      Chọn đại diện
                    </button>
                    <button
                      className="danger-btn compact-btn"
                      type="button"
                      onClick={() => {
                        const nextImages = profileForm.danhSachAnh.filter((_, itemIndex) => itemIndex !== index)
                        if (nextImages.length > 0 && !nextImages.some((item) => item.laAnhDaiDien)) {
                          nextImages[0] = { ...nextImages[0], laAnhDaiDien: true }
                        }
                        updateProfileField('danhSachAnh', nextImages)
                      }}
                    >
                      Xóa
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
          <h2>Tiện ích nhà hàng</h2>
          <div className="form-grid single">
            {(availableRestaurantAmenities.length > 0 ? availableRestaurantAmenities : RESTAURANT_AMENITY_OPTIONS).map((amenity) => (
              <label key={amenity} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <input
                  type="checkbox"
                  checked={profileForm.tienIchNhaHang.includes(amenity)}
                  onChange={() => {
                    const current = profileForm.tienIchNhaHang
                    updateProfileField(
                      'tienIchNhaHang',
                      current.includes(amenity) ? current.filter((item) => item !== amenity) : [...current, amenity],
                    )
                  }}
                />
                <span>{amenity}</span>
              </label>
            ))}
          </div>
          <div style={{ display: 'flex', gap: 10, margin: '8px 0 14px' }}>
            <button className="secondary-btn" type="button" onClick={() => void onSaveRestaurantAmenities()}>
              Lưu tiện ích nhà hàng
            </button>
          </div>
          <h2>Vị trí cơ sở</h2>
          <BusinessLocationMap
            value={profileForm.toaDoGPS}
            onChange={(coordinates) => updateProfileField('toaDoGPS', coordinates)}
          />
          <label style={{ display: 'block', marginTop: 12 }}>
            <span style={{ display: 'block', marginBottom: 6 }}>Tọa độ GPS</span>
            <input
              value={profileForm.toaDoGPS}
              onChange={(event) => updateProfileField('toaDoGPS', event.target.value)}
              placeholder="10.77653,106.70098"
            />
          </label>
          <hr style={{ margin: '14px 0' }} />
          <h2>Quy trình thiết lập nhà hàng</h2>
          <p><CheckCircle2 /> Hồ sơ kinh doanh có sẵn</p>
          <p><XCircle /> Cần tạo bố cục bàn</p>
          <p><XCircle /> Cần thiết lập các món ăn</p>
          <div className="inline-actions">
            <button className="secondary-btn" onClick={() => setActiveView('table-layout')}>Bố cục bàn</button>
            <button className="primary-btn" onClick={() => setActiveView('menu-management')}>Quản lý thực đơn</button>
          </div>
        </div>
      </section>
    </>
  )
}

function TableLayoutView({
  tables,
  tableForm,
  selectedArea,
  setTableForm,
  setSelectedArea,
  createTables,
  createSingleTable,
  startTableEdit,
  deleteTable,
  editingTableId,
  isSaving,
  propertyName,
}: {
  tables: TableResponse[]
  tableForm: { soLuong: string; soChoNgoi: string; viTriSanh: string }
  selectedArea: string
  setTableForm: React.Dispatch<React.SetStateAction<{ soLuong: string; soChoNgoi: string; viTriSanh: string }>>
  setSelectedArea: (area: string) => void
  createTables: () => void
  createSingleTable: () => void
  startTableEdit: (table: TableResponse) => void
  deleteTable: (tableId: string) => void
  editingTableId: string | null
  isSaving: boolean
  propertyName: string
}) {
  const previewTables = tables.filter((table) => getTableArea(table.viTriSanh) === selectedArea).slice(0, 12)

  return (
    <>
      <header className="partner-page-header split compact-header">
        <div>
          <h1>Quản lý bàn</h1>
          <p>Thiết kế và sắp xếp các khu vực ăn uống của nhà hàng.</p>
        </div>
        <div className="property-chip"><Utensils size={20} /> {propertyName}</div>
      </header>
      <div className="table-layout-grid">
        <section className="panel">
          <div className="accent-heading"><LayoutGrid size={22} /><h2>Tạo bàn hàng loạt</h2></div>
          <div className="form-grid single">
            <label>Số lượng bàn<input value={tableForm.soLuong} onChange={(event) => setTableForm((current) => ({ ...current, soLuong: event.target.value }))} placeholder="ví dụ: 10" /></label>
            <label>
              Số chỗ ngồi mỗi bàn
              <select value={tableForm.soChoNgoi} onChange={(event) => setTableForm((current) => ({ ...current, soChoNgoi: event.target.value }))}>
                <option value="2">2 chỗ</option>
                <option value="4">4 chỗ</option>
                <option value="6">6 chỗ</option>
              </select>
            </label>
          </div>
          <h3>Chọn khu vực</h3>
          <div className="area-grid">
            {RESTAURANT_AREAS.map((area) => (
              <button key={area} className={selectedArea === area ? 'active' : ''} onClick={() => setSelectedArea(area)}>{area}</button>
            ))}
          </div>
          <button className="primary-btn full-width" disabled={isSaving} onClick={createTables}><Plus size={18} /> {isSaving ? 'Đang lưu...' : editingTableId ? 'Cập nhật bàn' : 'Tạo bàn'}</button>
        </section>

        <section className="panel layout-preview">
          <div className="accent-heading"><Grid2X2 size={22} /><h2>Xem trước bố cục: {selectedArea}</h2></div>
          <div className="floor-plan">
            {previewTables.map((table, index) => (
              <div key={table.id} className="floor-table" style={{ left: `${8 + (index % 6) * 15}%`, top: `${16 + Math.floor(index / 6) * 28}%` }}>
                <strong>{selectedArea[0]}{index + 1}</strong>
                <span>{table.soChoNgoi} chỗ</span>
              </div>
            ))}
            <button className="floor-add" disabled={isSaving} onClick={() => void createSingleTable()}><Plus /></button>
          </div>
        </section>
      </div>

      <section className="panel table-panel">
        <div className="section-title between">
          <h2>Danh sách bàn</h2>
          <button className="primary-btn" disabled={isSaving} onClick={() => void createSingleTable()}><Plus size={18} /> Thêm bàn đơn</button>
        </div>
        <table className="data-table">
          <thead><tr><th>Mã bàn</th><th>Khu vực</th><th>Sức chứa</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
          <tbody>
            {tables.map((table, index) => (
              <tr key={table.id}>
                <td><span className="table-code">{getTableArea(table.viTriSanh)[0] ?? 'T'}{index + 1}</span> {table.viTriSanh}</td>
                <td>{getTableArea(table.viTriSanh)}</td>
                <td>{table.soChoNgoi} người</td>
                <td><span className="badge green">Còn trống</span></td>
                <td className="table-actions"><button className="secondary-btn row-action-btn" type="button" onClick={() => startTableEdit(table)}><Pencil size={16} /> Sửa</button><button className="danger-btn row-action-btn" type="button" onClick={(event) => { event.preventDefault(); event.stopPropagation(); deleteTable(table.id) }}><Trash2 size={16} /> Xóa</button></td>
              </tr>
            ))}
            {!tables.length && <tr><td colSpan={5} className="empty-cell">Chưa có bàn nào được tạo.</td></tr>}
          </tbody>
        </table>
      </section>
    </>
  )
}

function MenuManagementView({
  menuItems,
  menuStats,
  menuSearch,
  setMenuSearch,
  setIsMenuModalOpen,
  toggleMenuStatus,
  startMenuItemEdit,
  deleteMenuItem,
  combos,
  setIsComboModalOpen,
  startComboEdit,
  deleteCombo,
}: {
  menuItems: MenuItemResponse[]
  menuStats: { total: number; active: number; outOfStock: number }
  menuSearch: string
  setMenuSearch: (value: string) => void
  setIsMenuModalOpen: (open: boolean) => void
  toggleMenuStatus: (item: MenuItemResponse) => void
  startMenuItemEdit: (item: MenuItemResponse) => void
  deleteMenuItem: (itemId: string) => void
  combos: ComboResponse[]
  setIsComboModalOpen: (open: boolean) => void
  startComboEdit: (combo: ComboResponse) => void
  deleteCombo: (comboId: string) => void
}) {
  return (
    <>
      <header className="partner-page-header split compact-header">
        <div>
          <h1>Quản lý thực đơn</h1>
          <p>Quản lý thực đơn điện tử và trạng thái phục vụ tức thời.</p>
        </div>

        <div className="header-actions">
          <div className="search-box">
            <Search size={20} />
            <input
              value={menuSearch}
              onChange={(event) => setMenuSearch(event.target.value)}
              placeholder="Tìm kiếm món ăn..."
            />
          </div>

          <button className="primary-btn" onClick={() => setIsMenuModalOpen(true)}>
            <Plus size={18} /> Thêm món ăn
          </button>
        </div>
      </header>

      <section className="metric-grid partner-metrics">
        <div className="metric-card">
          <span>Tổng số món</span>
          <strong>{menuStats.total}</strong>
          <Utensils />
        </div>

        <div className="metric-card">
          <span>Đang bán</span>
          <strong>{menuStats.active}</strong>
          <CheckCircle2 />
        </div>

        <div className="metric-card danger">
          <span>Tạm hết</span>
          <strong>{menuStats.outOfStock}</strong>
          <XCircle />
        </div>

        <div className="menu-health">
          <span>Chất lượng thực đơn</span>
          <strong>Cao cấp</strong>
          <p>Cơ sở được đánh giá nổi bật</p>
        </div>
      </section>

      <div className="menu-management-stack">
        <section className="panel table-panel">
          <table className="data-table menu-table">
            <thead>
              <tr>
                <th>Món ăn</th>
                <th>Giá</th>
                <th>Danh mục / Thẻ</th>
                <th>Trạng thái phục vụ</th>
                <th>Hành động</th>
              </tr>
            </thead>

            <tbody>
              {menuItems.map((item) => (
                <tr key={item.id}>
                  <td>
                    <div className="dish-cell">
                      <div className={`dish-thumb ${getMenuItemCoverImage(item) ? 'has-image' : ''}`}>
                        {getMenuItemCoverImage(item) ? <img src={getMenuItemCoverImage(item)} alt={`Ảnh món ${item.tenMon}`} /> : <Utensils size={18} />}
                      </div>

                      <div>
                        <strong>{item.tenMon}</strong>
                        <span>{item.theNguCanh[0] || 'Món ăn'}</span>
                      </div>
                    </div>
                  </td>

                  <td>
                    <strong className="price-text">{formatMoney(item.giaBan)}</strong>
                  </td>

                  <td>
                    <div className="tag-row">
                      {item.theNguCanh.map((tag) => (
                        <span key={tag}>{tag}</span>
                      ))}
                    </div>
                  </td>

                  <td>
                    <button
                      className={`availability-toggle ${item.trangThai === 'DANG_BAN' ? 'on' : ''}`}
                      onClick={() => toggleMenuStatus(item)}
                      type="button"
                    >
                      <span />
                    </button>

                    <strong className={item.trangThai === 'DANG_BAN' ? 'available-text' : 'stockout-text'}>
                      {item.trangThai === 'DANG_BAN' ? 'Đang bán' : 'Tạm hết'}
                    </strong>
                  </td>

                  <td className="table-actions">
                    <button className="secondary-btn row-action-btn" onClick={() => startMenuItemEdit(item)} type="button">
                      <Pencil size={16} /> Sửa
                    </button>

                    <button className="danger-btn row-action-btn" type="button" onClick={(event) => { event.preventDefault(); event.stopPropagation(); deleteMenuItem(item.id) }}>
                      <Trash2 size={16} /> Xóa
                    </button>
                  </td>
                </tr>
              ))}

              {!menuItems.length && (
                <tr>
                  <td colSpan={5} className="empty-cell">
                    Chưa có món ăn nào.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </section>

        <section className="panel table-panel">
          <div className="section-title between">
            <h2>Combo</h2>

            <button className="primary-btn" onClick={() => setIsComboModalOpen(true)} type="button">
              <Plus size={18} /> Thêm combo
            </button>
          </div>

          <table className="data-table">
            <thead>
              <tr>
                <th>Combo</th>
                <th>Giá</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>

            <tbody>
              {combos.map((combo) => (
                <tr key={combo.id}>
                  <td>
                    <strong>{combo.tenCombo}</strong>
                    <div className="muted-text">{combo.moTa || 'Combo'}</div>
                  </td>

                  <td>
                    <strong className="price-text">{formatMoney(combo.giaCombo)}</strong>
                  </td>

                  <td>
                    <span className={String(combo.trangThai) === '1' ? 'badge green' : 'badge'}>
                      {String(combo.trangThai) === '1' ? 'Hoạt động' : 'Không hoạt động'}
                    </span>
                  </td>

                  <td className="table-actions">
                    <button className="secondary-btn row-action-btn" onClick={() => startComboEdit(combo)} type="button">
                      <Pencil size={16} /> Sửa
                    </button>

                    <button className="danger-btn row-action-btn" type="button" onClick={(event) => { event.preventDefault(); event.stopPropagation(); deleteCombo(combo.id) }}>
                      <Trash2 size={16} /> Xóa
                    </button>
                  </td>
                </tr>
              ))}

              {!combos.length && (
                <tr>
                  <td colSpan={4} className="empty-cell">
                    Chưa có combo nào.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </section>
      </div>
    </>
  )
}
