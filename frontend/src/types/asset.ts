export type ServiceType = 'KHACH_SAN' | 'NHA_HANG'
export type ApprovalStatus = 'CHO_DUYET' | 'DANG_HOAT_DONG' | 'BI_TU_CHOI' | 'BAN_NHAP' | 'BI_KHOA_TAM_THOI'
export type ApprovalDecision = 'APPROVED' | 'REJECTED'
export type MenuItemStatus = 'CO_SAN' | 'TAM_HET'

export type BusinessProfilePayload = {
  hoSo: {
    tenCoSo: string
    sdtLienHe: string
    loaiDichVu: ServiceType
    maSoThue: string
    giayPhepKinhDoanh: string
    toaDoGPS: string
    chinhSach: {
      loaiChinhSach: string
      noiDung: string
      ngayApDung: string
    }
  }
  khachSan?: {
    ten: string
    hangSao: number
    moTa: string
    giaCoBan: number
    isDynamicPricing: boolean
    gioNhanPhong: string
    gioTraPhong: string
  } | null
  nhaHang?: {
    ten: string
    loaiAmThuc: string
    moTa: string
    giaCoBan: number
    isDynamicPricing: boolean
    sucChua: number
    gioMoCua: string
    gioDongCua: string
  } | null
  danhSachAnh: Array<{
    duongDanUrl: string
    moTaAnh: string
    laAnhDaiDien: boolean
    ngayTaiLen?: string
  }>
  tienIchKhachSan: Array<{
    tenTienIch: string
    loaiTienIch: string
    moTa: string
  }>
  tienIchNhaHang: Array<{
    tenTienIch: string
    loaiTienIch: string
    moTa: string
    coThuPhi: boolean
    phiSuDung: number
  }>
}

export type BusinessProfileResponse = {
  idHoSo: string
  doiTacId: string
  doiTacEmail?: string
  doiTacHoTen?: string
  tenCoSo: string
  sdtLienHe: string
  loaiDichVu: ServiceType
  maSoThue: string
  giayPhepKinhDoanh: string
  toaDoGPS: string
  trangThaiKiemDuyet: ApprovalStatus
  thoiGianDangKy: string
  thoiGianDuyet?: string | null
  chinhSach?: {
    id: string
    hoSoKinhDoanhId: string
    loaiChinhSach: string
    noiDung: string
    ngayApDung: string
  } | null
  danhSachTaiSan: Array<{
    idTaiSan: string
    hoSoKinhDoanhId: string
    moTa?: string
    trangThai?: string
    giaCoBan?: number
    isDynamicPricing?: boolean
  }>
}

export type AdminApprovalResponse = BusinessProfileResponse & {
  doiTacEmail: string
  doiTacHoTen: string
  comparison?: {
    oldTenCoSo?: string | null
    oldSdtLienHe?: string | null
    oldLoaiDichVu?: string | null
    oldMaSoThue?: string | null
    oldGiayPhepKinhDoanh?: string | null
    oldToaDoGPS?: string | null
    newTenCoSo?: string | null
    newSdtLienHe?: string | null
    newLoaiDichVu?: string | null
    newMaSoThue?: string | null
    newGiayPhepKinhDoanh?: string | null
    newToaDoGPS?: string | null
  } | null
}

export type RoomPayload = {
  phong: {
    soPhong: string
    loaiPhong: string
    sucChuaToiDa: number
    dienTich: number
    trangThai: string
    tienIch: string[]
    phanTramGiamGia: number
  }
  danhSachAnh: Array<{
    duongDanUrl: string
    moTaAnh: string
    laAnhDaiDien: boolean
  }>
}

export type RoomResponse = {
  id: string
  khachSanId: string
  soPhong: string
  loaiPhong: string
  sucChuaToiDa: number
  dienTich: number
  trangThai: string
  tienIch: string[]
  phanTramGiamGia: number
  danhSachAnh?: Array<{
    id: string
    doiTuongId: string
    duongDanUrl: string
    moTaAnh: string
    laAnhDaiDien: boolean
    ngayTaiLen: string
  }>
}

export type TablePayload = {
  viTriSanh: string
  soChoNgoi: number
  trangThai: number
}

export type TableResponse = {
  id: string
  nhaHangId: string
  viTriSanh: string
  soChoNgoi: number
  trangThai: number
}

export type MenuItemPayload = {
  tenMon: string
  giaBan: number
  trangThai: MenuItemStatus
  duongDanUrl: string
  theNguCanh: string[]
}

export type MenuItemResponse = {
  id: string
  thucDonId: string
  tenMon: string
  giaBan: number
  trangThai: MenuItemStatus
  duongDanUrl?: string
  theNguCanh: string[]
}

export type ComboPayload = {
  tenCombo: string
  moTa: string
  giaCombo: number
  trangThai: number
  ngayBatDau?: string
  ngayKetThuc?: string
  monAnIds: string[]
}

export type ComboResponse = {
  id: string
  thucDonId: string
  tenCombo: string
  moTa?: string
  giaCombo: number
  trangThai: number
  ngayBatDau?: string | null
  ngayKetThuc?: string | null
  monAnIds: string[]
}
