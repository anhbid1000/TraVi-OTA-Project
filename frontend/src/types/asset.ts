export type ServiceType = 'KHACH_SAN' | 'NHA_HANG'
export type MenuItemStatus = 'DANG_BAN' | 'TAM_HET' | 'NGUNG_BAN'
export type TableStatus = 'SAN_SANG' | 'TAM_DUNG' | 'NGUNG_SU_DUNG'

export type BusinessProfilePayload = {
  hoSo: {
    tenCoSo: string
    sdtLienHe: string
    emailLienHe?: string
    diaChi?: string
    thanhPho?: string
    quanHuyen?: string
    phuongXa?: string
    kinhDo?: number
    viDo?: number
    loaiDichVu: ServiceType
    maSoThue: string
    giayPhepKinhDoanh: string
    toaDoGPS: string
    chinhSach: {
      loaiChinhSach: string
      noiDung: string
      ngayApDung: string
      gioNhanPhong?: string
      gioTraPhong?: string
      gioMoCua?: string
      gioDongCua?: string
      chinhSachHuy?: string
      chinhSachHoanTien?: string
      quyDinhTreEm?: string
      quyDinhVatNuoi?: string
      ghiChuKhac?: string
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
  trangThaiHoatDong?: 'CHUA_HOAT_DONG' | 'DANG_HOAT_DONG' | 'TAM_DUNG' | 'BI_KHOA'
  thoiGianDangKy: string
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

export type RoomPayload = {
  phong: {
    soPhong: string
    tenPhong: string
    loaiPhong: string
    moTa?: string
    sucChuaToiDa: number
    soGiuong: number
    dienTich: number
    giaCoBan: number
    soLuongPhong: number
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
  tenBan?: string
  viTriSanh: string
  moTa?: string
  soChoNgoi: number
  trangThai?: TableStatus
}

export type TableResponse = {
  id: string
  nhaHangId: string
  viTriSanh: string
  soChoNgoi: number
  trangThai: TableStatus
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



export type MenuPayload = {
  tenThucDon: string
  phanLoai?: string
}

export type MenuResponse = {
  id: string
  nhaHangId: string
  phanLoai?: string
  monAn?: MenuItemResponse[]
  combo?: ComboResponse[]
}
