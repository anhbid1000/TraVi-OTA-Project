import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import L from 'leaflet'
import { MapContainer, Marker, TileLayer, useMapEvents } from 'react-leaflet'
import 'leaflet/dist/leaflet.css'
import {
  AlertTriangle,
  ArrowLeft,
  ArrowRight,
  Bed,
  BookOpen,
  BriefcaseBusiness,
  Building2,
  Check,
  CheckCircle2,
  CircleHelp,
  DoorOpen,
  Eye,
  FileText,
  Grid2X2,
  Hotel,
  LayoutGrid,
  LogOut,
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
import type { AuthUser } from '../../types/auth'
import { getApiErrorMessage } from '../../utils/apiError'
import { useAuth } from '../../hooks/useAuth'
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
const HOTEL_AMENITY_OPTIONS = ['Wifi miễn phí', 'Lễ tân 24/7', 'Bãi đỗ xe', 'Hồ bơi', 'Phòng gym', 'Spa', 'Nhà hàng', 'Đưa đón sân bay']
const RESTAURANT_AMENITY_OPTIONS = ['Đặt bàn online', 'Đặt món trước', 'Phòng riêng', 'Bãi đỗ xe', 'Wifi miễn phí', 'Thanh toán thẻ', 'Phục vụ ngoài trời', 'Mang về']
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
  sucChuaToiDa: '2',
  soGiuong: '1',
  dienTich: '45',
  giaCoBan: '500000',
  soLuongPhong: '1',
  phanTramGiamGia: '0',
  tienIch: ['WIFI', 'DIEU_HOA'],
  imageName: '',
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
        moTa,
        giaCoBan: Number(form.giaCoBan),
        isDynamicPricing: false,
        gioNhanPhong: '14:00',
        gioTraPhong: '12:00',
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
  if (errors.tenCoSo || errors.sdtLienHe || errors.moTa || errors.giaCoBan || errors.hangSao || errors.loaiAmThuc || errors.sucChua) {
    return 1
  }
  if (errors.maSoThue || errors.businessLicense) return 2
  if (errors.toaDoGPS) return 3
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

function getProfileFormErrors(form: ProfileFormState, targetStep = 3): ProfileFormErrors {
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

  if (targetStep >= 3 && !parseCoordinates(form.toaDoGPS)) {
    errors.toaDoGPS = 'Tọa độ phải đúng định dạng lat,lng và nằm trong phạm vi hợp lệ.'
  }

  return errors
}

function getFirstError(errors: ProfileFormErrors) {
  return Object.values(errors)[0] ?? ''
}

function getStringClaim(user: AuthUser | null, keys: string[]) {
  if (!user) return ''

  for (const key of keys) {
    const value = user[key]
    if (typeof value === 'string' && value.trim()) {
      return value
    }
  }

  return ''
}

function getPartnerInitials(user: AuthUser | null) {
  const displayName = getStringClaim(user, ['hoTen', 'fullName', 'name', 'username', 'email'])
  if (!displayName) return 'TV'

  const normalized = displayName.includes('@') ? displayName.split('@')[0] : displayName
  const words = normalized.trim().split(/\s+/).filter(Boolean)
  if (words.length === 1) {
    return words[0].slice(0, 2).toUpperCase()
  }

  return `${words[0][0]}${words[words.length - 1][0]}`.toUpperCase()
}

function hydrateProfileForm(profile: BusinessProfileResponse): ProfileFormState {
  return {
    ...initialProfileForm,
    tenCoSo: profile.tenCoSo ?? '',
    sdtLienHe: profile.sdtLienHe ?? '',
    loaiDichVu: profile.loaiDichVu,
    maSoThue: profile.maSoThue ?? '',
    giayPhepKinhDoanh: profile.giayPhepKinhDoanh ?? '',
    toaDoGPS: profile.toaDoGPS ?? initialProfileForm.toaDoGPS,
    giaCoBan: String(profile.danhSachTaiSan?.[0]?.giaCoBan ?? initialProfileForm.giaCoBan),
  }
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

export function PartnerDashboardPage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [activeView, setActiveView] = useState<PartnerView>('business-profile')
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

  const assetId = profile?.danhSachTaiSan?.[0]?.idTaiSan
  const isHotel = profile?.loaiDichVu === 'KHACH_SAN'
  const isRestaurant = profile?.loaiDichVu === 'NHA_HANG'


  const menuStats = useMemo(() => {
    const active = menuItems.filter((item) => item.trangThai === 'DANG_BAN').length
    return { total: menuItems.length, active, outOfStock: menuItems.length - active }
  }, [menuItems])

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
  const partnerAvatarUrl = getStringClaim(user, ['avatarUrl', 'anhDaiDien', 'hinhDaiDien', 'photoUrl', 'picture'])
  const partnerInitials = getPartnerInitials(user)

  useEffect(() => {
    const loadPartnerData = async () => {
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

        const currentAssetId = currentProfile.danhSachTaiSan?.[0]?.idTaiSan
        if (!currentAssetId) {
          return
        }

        if (currentProfile.loaiDichVu === 'KHACH_SAN') {
          const roomList = await partnerAssetService.getRooms(currentAssetId)
          setRooms(roomList)
        }

        if (currentProfile.loaiDichVu === 'NHA_HANG') {
          const [tableList, menuList, comboList] = await Promise.all([
            partnerAssetService.getTables(currentAssetId),
            partnerAssetService.getMenuItems(currentAssetId),
            partnerAssetService.getCombos(currentAssetId),
          ])
          setTables(tableList)
          setMenuItems(menuList)
          setCombos(comboList)
        }
      } catch (err) {
        showError(err, 'Không thể tải dữ liệu bảng điều khiển đối tác.')
      }
    }

    void loadPartnerData()
  }, [])

  const updateProfileField = (name: keyof ProfileFormState, value: ProfileFormValue) => {
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
    const errors = getProfileFormErrors(profileForm, 3)
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
      setStep(3)

      setProfileForm((current) => ({
        ...hydrateProfileForm(response),
        moTa: current.moTa,
        hangSao: current.hangSao,
        loaiAmThuc: current.loaiAmThuc,
        sucChua: current.sucChua,
      }))

      localStorage.setItem('partner:lastProfile', JSON.stringify(response))
      localStorage.removeItem('partner:profileDraft')

      showMessage(
        existingProfileId
          ? 'Hồ sơ kinh doanh đã được cập nhật.'
          : 'Hồ sơ kinh doanh đã được tạo và kích hoạt thành công.',
      )
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
        moTa: '',
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
      tenPhong: room.soPhong, // Fallback
      loaiPhong: room.loaiPhong,
      sucChuaToiDa: String(room.sucChuaToiDa),
      soGiuong: '1',
      dienTich: String(room.dienTich),
      giaCoBan: '500000',
      soLuongPhong: '1',
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

  const deleteRoom = async (roomId: string) => {
    if (!window.confirm('Bạn có chắc muốn xóa phòng này?')) return
    if (!assetId || roomId.startsWith('room-')) {
      setRooms((current) => current.filter((room) => room.id !== roomId))
      showMessage('Đã xóa phòng tạm thời.')
      return
    }
    try {
      await partnerAssetService.deleteRoom(assetId, roomId)
      setRooms((current) => current.filter((room) => room.id !== roomId))
      showMessage('Đã xóa phòng.')
    } catch (err) {
      showError(err, 'Không thể xóa phòng.')
    }
  }

  const deleteTable = async (tableId: string) => {
    if (!window.confirm('Bạn có chắc muốn xóa bàn này?')) return
    if (!assetId || tableId.startsWith('table-')) {
      setTables((current) => current.filter((table) => table.id !== tableId))
      showMessage('Đã xóa bàn tạm thời.')
      return
    }
    try {
      await partnerAssetService.deleteTable(assetId, tableId)
      setTables((current) => current.filter((table) => table.id !== tableId))
      showMessage('Đã xóa bàn.')
    } catch (err) {
      showError(err, 'Không thể xóa bàn.')
    }
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

  const deleteMenuItem = async (itemId: string) => {
    if (!window.confirm('Bạn có chắc muốn xóa món này?')) return
    if (!assetId || itemId.startsWith('menu-')) {
      setMenuItems((current) => current.filter((item) => item.id !== itemId))
      showMessage('Đã xóa món tạm thời.')
      return
    }
    try {
      await partnerAssetService.deleteMenuItem(assetId, itemId)
      setMenuItems((current) => current.filter((item) => item.id !== itemId))
      showMessage('Đã xóa món.')
    } catch (err) {
      showError(err, 'Không thể xóa món.')
    }
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

  const deleteCombo = async (comboId: string) => {
    if (!window.confirm('Bạn có chắc muốn xóa combo này?')) return
    if (!assetId || comboId.startsWith('combo-')) {
      setCombos((current) => current.filter((combo) => combo.id !== comboId))
      showMessage('Đã xóa combo tạm thời.')
      return
    }

    try {
      await partnerAssetService.deleteCombo(assetId, comboId)
      setCombos((current) => current.filter((combo) => combo.id !== comboId))
      showMessage('Đã xóa combo.')
    } catch (err) {
      showError(err, 'Không thể xóa combo.')
    }
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

  const handleLogout = async () => {
    await logout()
    navigate('/partner/login')
  }

  const navItems: Array<{ id: PartnerView; label: string; icon: React.ReactNode }> = [
    { id: 'business-profile', label: 'Hồ sơ kinh doanh', icon: <BriefcaseBusiness size={20} /> },
    { id: 'hotel-setup', label: 'Thiết lập khách sạn', icon: <Bed size={20} /> },
    { id: 'room-management', label: 'Quản lý phòng', icon: <DoorOpen size={20} /> },
    { id: 'restaurant-setup', label: 'Thiết lập nhà hàng', icon: <Utensils size={20} /> },
    { id: 'table-layout', label: 'Bố trí bàn', icon: <Grid2X2 size={20} /> },
    { id: 'menu-management', label: 'Quản lý thực đơn', icon: <BookOpen size={20} /> },
  ]

  return (
    <div className="dashboard-shell partner-shell">
      <aside className="dashboard-sidebar partner-sidebar">
        <div className="partner-sidebar-inner">
          <div className="partner-brand-card">
            <div className="partner-user-avatar large">
              {partnerAvatarUrl ? <img src={partnerAvatarUrl} alt="Ảnh đại diện đối tác" /> : partnerInitials}
            </div>
            <div className="brand-title">
              <strong>Bảng điều khiển TraVi</strong>
              <span>Cổng đối tác</span>
            </div>
          </div>

          <nav className="side-nav partner-nav main-partner-nav">
            <button>
              <LayoutGrid size={20} />
              Tổng quan
            </button>
            <button>
              <CalendarIcon />
              Đặt phòng
            </button>
            <button>
              <AnalyticsIcon />
              Phân tích
            </button>
            <button className="active">
              <BriefcaseBusiness size={20} />
              Quản lý
            </button>
            <button>
              <SettingsIcon />
              Cài đặt
            </button>
          </nav>

          <div className="management-subnav">
            <p>Quản lý</p>
            {navItems.map((item) => (
              <button
                key={item.id}
                className={activeView === item.id ? 'active' : ''}
                onClick={() => setActiveView(item.id)}
              >
                {item.icon}
                {item.label}
              </button>
            ))}
          </div>
        </div>

        <div className="sidebar-footer">
          <button><CircleHelp size={20} /> Trung tâm trợ giúp</button>
          <button onClick={handleLogout}><LogOut size={20} /> Đăng xuất</button>
        </div>
      </aside>

      <main className="dashboard-main partner-main">
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
          />
        )}

        {activeView === 'hotel-setup' && (
          <HotelSetupView profile={profile} profileForm={profileForm} setActiveView={setActiveView} />
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
          <RestaurantSetupView profile={profile} profileForm={profileForm} setActiveView={setActiveView} />
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
      </main>

      {isRoomModalOpen && (
        <RoomModal
          roomForm={roomForm}
          setRoomForm={setRoomForm}
          saveRoom={saveRoom}
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
    </div>
  )
}

function RoomModal({
  roomForm,
  setRoomForm,
  saveRoom,
  isEditing,
  isSaving,
  uploadFile,
  close,
}: {
  roomForm: typeof initialRoomForm
  setRoomForm: React.Dispatch<React.SetStateAction<typeof initialRoomForm>>
  saveRoom: () => void
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
          <label><span>Sức chứa</span><input value={roomForm.sucChuaToiDa} onChange={(e) => setRoomForm((c) => ({ ...c, sucChuaToiDa: e.target.value }))} /></label>
          <label><span>Diện tích (m²)</span><input value={roomForm.dienTich} onChange={(e) => setRoomForm((c) => ({ ...c, dienTich: e.target.value }))} /></label>
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

function CalendarIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="20" viewBox="0 0 24 24" width="20">
      <path d="M7 3v4M17 3v4M4 9h16M6 5h12a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Z" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" />
    </svg>
  )
}

function AnalyticsIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="20" viewBox="0 0 24 24" width="20">
      <path d="M4 19V5M4 19h16M8 16l3-4 3 2 5-7M8 16v-4M14 14v-4M19 7v9" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" />
    </svg>
  )
}

function SettingsIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="20" viewBox="0 0 24 24" width="20">
      <path d="M12 15.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Z" stroke="currentColor" strokeWidth="2" />
      <path d="M19.4 15a1.7 1.7 0 0 0 .34 1.88l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06A1.7 1.7 0 0 0 15 19.4a1.7 1.7 0 0 0-1 1.55V21a2 2 0 0 1-4 0v-.08A1.7 1.7 0 0 0 9 19.4a1.7 1.7 0 0 0-1.88.34l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.7 1.7 0 0 0 4.6 15a1.7 1.7 0 0 0-1.55-1H3a2 2 0 0 1 0-4h.08A1.7 1.7 0 0 0 4.6 9a1.7 1.7 0 0 0-.34-1.88l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.7 1.7 0 0 0 9 4.6a1.7 1.7 0 0 0 1-1.55V3a2 2 0 0 1 4 0v.08A1.7 1.7 0 0 0 15 4.6a1.7 1.7 0 0 0 1.88-.34l.06-.06A2 2 0 0 1 19.8 6.94l-.06.06A1.7 1.7 0 0 0 19.4 9a1.7 1.7 0 0 0 1.55 1H21a2 2 0 0 1 0 4h-.08A1.7 1.7 0 0 0 19.4 15Z" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" />
    </svg>
  )
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
}

function BusinessProfileView({
  step,
  setStep,
  profile,
  profileForm,
  profileErrors,
  isSubmitting,
  updateProfileField,
  saveProfile,
  saveDraft,
  uploadBusinessLicense,
  isUploadingLicense,
  toggleAmenity,
  addCustomAmenity,
}: BusinessProfileViewProps) {
  const currentStepErrors = Object.fromEntries(
    Object.entries(profileErrors).filter(([key]) => {
      if (step === 1) return ['tenCoSo', 'sdtLienHe', 'moTa', 'giaCoBan', 'hangSao', 'loaiAmThuc', 'sucChua'].includes(key)
      if (step === 2) return ['maSoThue', 'businessLicense'].includes(key)
      return ['toaDoGPS'].includes(key)
    }),
  ) as ProfileFormErrors
  const currentStepError = getFirstError(currentStepErrors)

  return (
    <>
      <header className="partner-page-header split">
        <div>
          <h1>Thiết lập hồ sơ kinh doanh</h1>
          <p>Hoàn thiện thông tin cơ sở kinh doanh để kích hoạt hiển thị trên hệ thống.</p>
        </div>
        <div className="status-cluster">
          <span className={`mini-pill ${!profile ? 'yellow' : 'gray'}`}>Chưa lưu</span>
          <span className={`mini-pill ${profile?.trangThaiHoatDong === 'TAM_DUNG' ? 'yellow' : 'gray'}`}>Tạm dừng</span>
          <span className={`mini-pill ${profile?.trangThaiHoatDong === 'BI_KHOA' ? 'red' : 'gray'}`}>Bị khóa</span>
          <span className={`mini-pill ${profile?.trangThaiHoatDong === 'DANG_HOAT_DONG' ? 'green' : 'gray'}`}>Đang hoạt động</span>
        </div>
      </header>

      <div className="partner-profile-grid business-profile-layout">
        <section className="profile-form-area">
          <div className="wizard-steps">
            <button className={step > 1 ? 'done' : step === 1 ? 'active' : ''} onClick={() => setStep(1)}>
              {step > 1 ? <Check size={20} /> : <span>1</span>}
              Cơ bản
            </button>
            <button className={step > 2 ? 'done' : step === 2 ? 'active' : ''} onClick={() => setStep(2)}>
              {step > 2 ? <Check size={20} /> : <span>2</span>}
              Pháp lý
            </button>
            <button className={step > 3 ? 'done' : step === 3 ? 'active' : ''} onClick={() => setStep(3)}>
              {step > 3 ? <Check size={20} /> : <span>3</span>}
              Vị trí
            </button>
            <button className={step > 4 ? 'done' : step === 4 ? 'active' : ''} onClick={() => setStep(4)}>
              {step > 4 ? <Check size={20} /> : <span>4</span>}
              Ảnh & Tiện ích
            </button>
            <button className={step === 5 ? 'active' : ''} onClick={() => setStep(5)}>
              <span>5</span>
              Chính sách
            </button>
          </div>

          <section className="panel partner-card">
            {currentStepError && (
              <div className="form-error-summary">
                <XCircle size={18} />
                <span>{currentStepError}</span>
              </div>
            )}

            {step === 1 && (
              <>
                <div className="accent-heading">
                  <Building2 size={22} />
                  <h2>Thông tin cơ bản</h2>
                </div>
                <div className="business-profile-step">
                  <div className="form-grid single">
                    <label className={profileErrors.tenCoSo ? 'field-error' : ''}>
                      Tên cơ sở
                      <input value={profileForm.tenCoSo} onChange={(event) => updateProfileField('tenCoSo', event.target.value)} placeholder="Ví dụ: TraVi Resort & Spa" />
                      {profileErrors.tenCoSo && <small>{profileErrors.tenCoSo}</small>}
                    </label>
                    <label className={profileErrors.sdtLienHe ? 'field-error' : ''}>
                      Số điện thoại liên hệ
                      <input value={profileForm.sdtLienHe} onChange={(event) => updateProfileField('sdtLienHe', event.target.value)} placeholder="Ví dụ: 0912345678" />
                      {profileErrors.sdtLienHe && <small>{profileErrors.sdtLienHe}</small>}
                    </label>
                    <label>
                      Loại dịch vụ
                      <select value={profileForm.loaiDichVu} onChange={(event) => updateProfileField('loaiDichVu', event.target.value as ServiceType)}>
                        <option value="KHACH_SAN">Khách sạn</option>
                        <option value="NHA_HANG">Nhà hàng</option>
                      </select>
                    </label>
                    <label className={profileErrors.giaCoBan ? 'field-error' : ''}>
                      Giá cơ bản (VND)
                      <input type="number" min="1000" value={profileForm.giaCoBan} onChange={(event) => updateProfileField('giaCoBan', event.target.value)} placeholder="500000" />
                      {profileErrors.giaCoBan && <small>{profileErrors.giaCoBan}</small>}
                    </label>
                    {profileForm.loaiDichVu === 'KHACH_SAN' ? (
                      <label className={profileErrors.hangSao ? 'field-error' : ''}>
                        Hạng sao khách sạn
                        <select value={profileForm.hangSao} onChange={(event) => updateProfileField('hangSao', event.target.value)}>
                          <option value="1">1 sao</option>
                          <option value="2">2 sao</option>
                          <option value="3">3 sao</option>
                          <option value="4">4 sao</option>
                          <option value="5">5 sao</option>
                        </select>
                        {profileErrors.hangSao && <small>{profileErrors.hangSao}</small>}
                      </label>
                    ) : (
                      <>
                        <label className={profileErrors.loaiAmThuc ? 'field-error' : ''}>
                          Loại ẩm thực
                          <input value={profileForm.loaiAmThuc} onChange={(event) => updateProfileField('loaiAmThuc', event.target.value)} placeholder="Việt Nam, Nhật, Hải sản..." />
                          {profileErrors.loaiAmThuc && <small>{profileErrors.loaiAmThuc}</small>}
                        </label>
                        <label className={profileErrors.sucChua ? 'field-error' : ''}>
                          Sức chứa nhà hàng
                          <input type="number" min="1" value={profileForm.sucChua} onChange={(event) => updateProfileField('sucChua', event.target.value)} placeholder="80" />
                          {profileErrors.sucChua && <small>{profileErrors.sucChua}</small>}
                        </label>
                      </>
                    )}
                    <label className={profileErrors.moTa ? 'field-error' : ''}>
                      Mô tả ngắn
                      <textarea value={profileForm.moTa} onChange={(event) => updateProfileField('moTa', event.target.value)} placeholder="Giới thiệu cơ sở kinh doanh trong vài câu..." />
                      {profileErrors.moTa && <small>{profileErrors.moTa}</small>}
                    </label>
                  </div>
                  <div className="business-preview-card">
                    <p><Eye size={14} /> Xem trước hiển thị</p>
                    <img src={heroImage} alt="Ảnh xem trước cơ sở" />
                    <div>
                      <strong>{profileForm.tenCoSo || 'Tên cơ sở của bạn'}</strong>
                      <span>$$$</span>
                    </div>
                    <small>{profileForm.moTa || 'Mô tả của bạn sẽ hiển thị tại đây để giúp khách hiểu rõ trải nghiệm dịch vụ.'}</small>
                    <button>Xem chi tiết</button>
                  </div>
                </div>
              </>
            )}

            {step === 2 && (
              <>
                <div className="accent-heading">
                  <FileText size={22} />
                  <h2>Giấy tờ pháp lý</h2>
                </div>
                <div className="form-grid single">
                  <label className={profileErrors.maSoThue ? 'field-error' : ''}>
                    Mã số thuế
                    <input value={profileForm.maSoThue} onChange={(event) => updateProfileField('maSoThue', event.target.value)} placeholder="Nhập mã số thuế 10 hoặc 13 chữ số" />
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
                      <button onClick={() => updateProfileField('giayPhepKinhDoanh', '')}><Trash2 size={18} /></button>
                    </div>
                  )}
                </div>
              </>
            )}

            {step === 3 && (
              <>
                <div className="accent-heading">
                  <MapPin size={22} />
                  <h2>Vị trí</h2>
                </div>
                <div className="map-section">
                  <BusinessLocationMap
                    value={profileForm.toaDoGPS}
                    onChange={(coordinates) => updateProfileField('toaDoGPS', coordinates)}
                  />
                  <label className={profileErrors.toaDoGPS ? 'field-error' : ''}>
                    Tọa độ GPS
                    <input value={profileForm.toaDoGPS} onChange={(event) => updateProfileField('toaDoGPS', event.target.value)} />
                    {profileErrors.toaDoGPS && <small>{profileErrors.toaDoGPS}</small>}
                  </label>
                </div>
              </>
            )}

            {step === 4 && (
              <>
                <div className="accent-heading">
                  <UploadCloud size={22} />
                  <h2>Ảnh & Tiện ích</h2>
                </div>
                <div className="form-grid single">
                  <div>
                    <label>Tiện ích</label>
                    <div className="tag-row" style={{ marginTop: 8, flexWrap: 'wrap', gap: 10 }}>
                      {(profileForm.loaiDichVu === 'KHACH_SAN' ? HOTEL_AMENITY_OPTIONS : RESTAURANT_AMENITY_OPTIONS).map((amenity) => {
                        const checked = profileForm.loaiDichVu === 'KHACH_SAN'
                          ? profileForm.tienIchKhachSan.includes(amenity)
                          : profileForm.tienIchNhaHang.includes(amenity)
                        return (
                          <label key={amenity} style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
                            <input type="checkbox" checked={checked} onChange={() => toggleAmenity(amenity)} />
                            <span>{amenity}</span>
                          </label>
                        )
                      })}
                    </div>
                    <input
                      style={{ marginTop: 12 }}
                      placeholder="Thêm tiện ích khác rồi nhấn Enter..."
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault()
                          addCustomAmenity(e.currentTarget.value)
                          e.currentTarget.value = ''
                        }
                      }}
                    />
                    <small>Có thể chọn nhanh bằng checkbox hoặc nhập tiện ích riêng.</small>
                  </div>
                  <label>
                    Ảnh cơ sở (URL ảnh tạm thời cho demo)
                    <input 
                      placeholder="https://example.com/image.jpg" 
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          const val = e.currentTarget.value.trim();
                          if (val) {
                            const newArr = [...profileForm.danhSachAnh, {url: val, moTa: '', laAnhDaiDien: profileForm.danhSachAnh.length === 0}];
                            updateProfileField('danhSachAnh', newArr);
                            e.currentTarget.value = '';
                          }
                        }
                      }}
                    />
                    <small>Nhập URL ảnh và ấn Enter để thêm.</small>
                  </label>
                  {profileForm.danhSachAnh.length > 0 && (
                    <div className="uploaded-file-list">
                      {profileForm.danhSachAnh.map((img, i) => (
                        <div key={i} className="uploaded-file">
                          <img src={img.url} alt="thumb" style={{width: 40, height: 40, objectFit: 'cover', borderRadius: 4}} />
                          <div style={{flex: 1, marginLeft: 10}}>
                            <input value={img.moTa} onChange={(e) => {
                              const newArr = [...profileForm.danhSachAnh];
                              newArr[i].moTa = e.target.value;
                              updateProfileField('danhSachAnh', newArr);
                            }} placeholder="Mô tả ảnh..." style={{padding: 4, width: '100%', fontSize: 12}} />
                          </div>
                          <button onClick={() => {
                              const newArr = [...profileForm.danhSachAnh];
                              newArr.splice(i, 1);
                              updateProfileField('danhSachAnh', newArr);
                          }}><Trash2 size={16} /></button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </>
            )}

            {step === 5 && (
              <>
                <div className="accent-heading">
                  <FileText size={22} />
                  <h2>Chính sách & Quy định</h2>
                </div>
                <div className="form-grid single">
                  {profileForm.loaiDichVu === 'KHACH_SAN' ? (
                    <div className="form-grid">
                      <label>Giờ nhận phòng <input type="time" value={profileForm.gioNhanPhong} onChange={e => updateProfileField('gioNhanPhong', e.target.value)} /></label>
                      <label>Giờ trả phòng <input type="time" value={profileForm.gioTraPhong} onChange={e => updateProfileField('gioTraPhong', e.target.value)} /></label>
                    </div>
                  ) : (
                    <div className="form-grid">
                      <label>Giờ mở cửa <input type="time" value={profileForm.gioMoCua} onChange={e => updateProfileField('gioMoCua', e.target.value)} /></label>
                      <label>Giờ đóng cửa <input type="time" value={profileForm.gioDongCua} onChange={e => updateProfileField('gioDongCua', e.target.value)} /></label>
                    </div>
                  )}
                  <label>Chính sách hủy <textarea value={profileForm.chinhSachHuy} onChange={e => updateProfileField('chinhSachHuy', e.target.value)} placeholder="Ví dụ: Miễn phí hủy trước 24h..." /></label>
                  <label>Chính sách trẻ em & thú cưng <textarea value={profileForm.quyDinhTreEm} onChange={e => updateProfileField('quyDinhTreEm', e.target.value)} placeholder="Trẻ em dưới 6 tuổi miễn phí..." /></label>
                  <label>Ghi chú khác <textarea value={profileForm.ghiChuKhac} onChange={e => updateProfileField('ghiChuKhac', e.target.value)} placeholder="Các quy định khác..." /></label>
                </div>
              </>
            )}

          </section>

          <div className="profile-actions">
            <button className="text-btn" onClick={() => setStep(Math.max(1, step - 1))}>
              <ArrowLeft size={18} /> Quay lại
            </button>
            <div>
              <button className="secondary-btn" disabled={isSubmitting} onClick={saveDraft}>
                Lưu bản nháp
              </button>
              {step < 5 ? (
                <button className="primary-btn" onClick={() => setStep(step + 1)}>
                  {step === 1 ? 'Tiếp tục: Giấy tờ pháp lý' : step === 2 ? 'Tiếp tục: Vị trí' : step === 3 ? 'Tiếp tục: Ảnh & Tiện ích' : 'Tiếp tục: Chính sách'}
                  <ArrowRight size={18} />
                </button>
              ) : (
                <button className="primary-btn" disabled={isSubmitting} onClick={saveProfile}>
                  {isSubmitting ? 'Đang lưu hồ sơ...' : profile ? 'Lưu cập nhật hồ sơ' : 'Kích hoạt hồ sơ'}
                  <ArrowRight size={18} />
                </button>
              )}
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
  setActiveView,
}: {
  profile: BusinessProfileResponse | null
  profileForm: ProfileFormState
  setActiveView: (view: PartnerView) => void
}) {
  return (
    <>
      <header className="partner-page-header">
        <h1>Thiết lập khách sạn</h1>
        <p>Cấu hình thông tin khách sạn trước khi quản lý danh sách phòng.</p>
      </header>
      <section className="hotel-setup-grid">
        <div className="panel feature-panel">
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
                <td><div className="room-thumb"><Bed size={22} /></div></td>
                <td><strong>{room.soPhong}</strong></td>
                <td><strong>{room.loaiPhong}</strong><span className="table-subtext">{room.tienIch.join(' · ')}</span></td>
                <td>{room.sucChuaToiDa} Người</td>
                <td>{room.dienTich} m²</td>
                <td><strong>{room.phanTramGiamGia > 0 ? `-${room.phanTramGiamGia}%` : 'Giá cơ bản'}</strong></td>
                <td><span className="badge green">Còn phòng</span></td>
                <td className="table-actions"><button className="icon-btn" onClick={() => startRoomEdit(room)}><Pencil size={16} /></button><button className="icon-btn danger" onClick={() => deleteRoom(room.id)}><Trash2 size={16} /></button></td>
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
  setActiveView,
}: {
  profile: BusinessProfileResponse | null
  profileForm: ProfileFormState
  setActiveView: (view: PartnerView) => void
}) {
  return (
    <>
      <header className="partner-page-header">
        <h1>Thiết lập nhà hàng</h1>
        <p>Chuẩn bị sức chứa nhà hàng, định vị ẩm thực và các công cụ vận hành.</p>
      </header>
      <section className="hotel-setup-grid">
        <div className="panel feature-panel">
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
                <td className="table-actions"><button className="icon-btn" onClick={() => startTableEdit(table)}><Pencil size={16} /></button><button className="icon-btn danger" onClick={() => deleteTable(table.id)}><Trash2 size={16} /></button></td>
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
          <div className="search-box"><Search size={20} /><input value={menuSearch} onChange={(event) => setMenuSearch(event.target.value)} placeholder="Tìm kiếm món ăn..." /></div>
          <button className="primary-btn" onClick={() => setIsMenuModalOpen(true)}><Plus size={18} /> Thêm món ăn</button>
        </div>
      </header>

      <section className="metric-grid partner-metrics">
        <div className="metric-card"><span>Tổng số món</span><strong>{menuStats.total}</strong><Utensils /></div>
        <div className="metric-card"><span>Đang bán</span><strong>{menuStats.active}</strong><CheckCircle2 /></div>
        <div className="metric-card danger"><span>Tạm hết</span><strong>{menuStats.outOfStock}</strong><XCircle /></div>
        <div className="menu-health"><span>Chất lượng thực đơn</span><strong>Cao cấp</strong><p>Cơ sở được đánh giá nổi bật</p></div>
      </section>

      <section className="panel table-panel">
        <table className="data-table menu-table">
          <thead><tr><th>Món ăn</th><th>Giá</th><th>Danh mục / Thẻ</th><th>Trạng thái phục vụ</th><th>Hành động</th></tr></thead>
          <tbody>
            {menuItems.map((item) => (
              <tr key={item.id}>
                <td><div className="dish-cell"><div className="dish-thumb"><Utensils size={18} /></div><div><strong>{item.tenMon}</strong><span>{item.theNguCanh[0] || 'Món ăn'}</span></div></div></td>
                <td><strong className="price-text">{formatMoney(item.giaBan)}</strong></td>
                <td><div className="tag-row">{item.theNguCanh.map((tag) => <span key={tag}>{tag}</span>)}</div></td>
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
                  <button className="icon-btn" onClick={() => startMenuItemEdit(item)} type="button">
                    <Pencil size={16} />
                  </button>
                  <button className="icon-btn danger" onClick={() => deleteMenuItem(item.id)} type="button">
                    <Trash2 size={16} />
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
                  <button className="icon-btn" onClick={() => startComboEdit(combo)} type="button">
                    <Pencil size={16} />
                  </button>
                  <button className="icon-btn danger" onClick={() => deleteCombo(combo.id)} type="button">
                    <Trash2 size={16} />
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
    </>
  )
}

