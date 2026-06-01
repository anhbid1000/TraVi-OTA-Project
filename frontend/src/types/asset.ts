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
    loaiKhachSan?: string
    moTa: string
    giaCoBan: number
    isDynamicPricing: boolean
    gioNhanPhong: string
    gioTraPhong: string
    gioNhanPhongMacDinh?: string
    gioTraPhongMacDinh?: string
    soTang?: number
    tongSoPhong?: number
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
  trangThaiHoatDong?: 'CHUA_HOAT_DONG' | 'DANG_HOAT_DONG' | 'TAM_DUNG' | 'BI_KHOA'
  thoiGianDangKy: string
  thoiGianCapNhat?: string
  chinhSach?: {
    id: string
    hoSoKinhDoanhId: string
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
  } | null
  taiSan?: {
    idTaiSan: string
    idHoSo: string
    loaiTaiSan: ServiceType | string
    ten?: string
    moTa?: string
    trangThai?: string
    giaCoBan?: number
    isDynamicPricing?: boolean
  } | null
  danhSachTaiSan?: Array<{
    idTaiSan: string
    hoSoKinhDoanhId?: string
    idHoSo?: string
    loaiTaiSan?: ServiceType | string
    ten?: string
    moTa?: string
    trangThai?: string
    giaCoBan?: number
    isDynamicPricing?: boolean
  }>
}

export type PartnerDashboardOverviewResponse = {
  businessProfileId: string
  assetId: string
  tenCoSo: string
  loaiDichVu: ServiceType
  tuNgay: string
  denNgay: string
  tongQuan: {
    tongDoanhThu: number
    tyLeTangTruongDoanhThu: number
    tyLeLapDay: number
    tyLeTangTruongLapDay: number
    giaTrungBinhMoiDem: number
    tyLeTangTruongGiaTrungBinh: number
    tongLuotDat: number
    tyLeTangTruongLuotDat: number
  }
  doanhThuTheoNgay: Array<{
    ngay: string
    doanhThu: number
  }>
  tinhTrangHomNay: {
    soPhongDaDat: number
    soPhongConTrong: number
    tongSoPhongKhaDung: number
    tyLeDaDat: number
  }
  datChoGanDay: Array<{
    id: string
    maDon: string
    tenKhach: string
    ngayNhanPhong: string
    ngayTraPhong: string
    loaiPhong: string
    tongTien: number
    trangThai: string
  }>
  topPhongNhuCau: Array<{
    roomId: string
    tenPhong: string
    loaiPhong: string
    soLuotDat: number
    giaTrungBinh: number
    anhDaiDienUrl?: string | null
  }>
}

export type PartnerAiInsightResponse = {
  partnerId: string
  businessProfileId: string
  businessName: string
  serviceType: ServiceType | string
  fromDate: string
  toDate: string
  healthScore: number
  profileSummary: string
  revenue: number
  revenueGrowthPercent: number
  bookings: number
  bookingGrowthPercent: number
  averageRating: number
  reviewCount: number
  modelVersion: string
  insights: Array<{
    id: string
    insightType: string
    severity: 'HIGH' | 'MEDIUM' | 'LOW' | string
    title: string
    message: string
    recommendedAction?: string | null
    metricName?: string | null
    metricValue?: number | null
    createdAt: string
  }>
}

export type RestaurantDashboardPeriod = 'TODAY' | 'WEEK' | 'MONTH'

export type PartnerRestaurantDashboardOverviewResponse = {
  businessProfileId: string
  restaurantId: string
  tenNhaHang: string
  period: RestaurantDashboardPeriod
  tuThoiDiem: string
  denThoiDiem: string
  tongQuan: {
    tongDoanhThu: number
    tyLeTangTruongDoanhThu: number
    tyLeLapDayBan: number
    soBanDangPhucVu: number
    tongSoBanKhaDung: number
    giaTriDonTrungBinh: number
    tyLeTangTruongGiaTriDon: number
    tongLuotKhach: number
    chenhLechLuotKhach: number
  }
  doanhThuTheoGio: Array<{
    gio: number
    doanhThu: number
  }>
  khungGioCaoDiem: Array<{
    tenKhungGio: string
    moTa: string
    tyLe: number
  }>
  donGanDay: Array<{
    id: string
    maDon: string
    tenKhachHang: string
    thoiGian: string
    banSo: string
    tongTien: number
    trangThai: string
  }>
  monBanChay: Array<{
    monAnId: string
    tenMon: string
    tongSoLuong: number
    giaTrungBinh: number
    anhMon?: string | null
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
  tenPhong?: string
  loaiPhong: string
  moTa?: string
  sucChuaToiDa: number
  soGiuong?: number
  dienTich: number
  giaCoBan?: number
  soLuongPhong?: number
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
  tenBan?: string
  viTriSanh: string
  moTa?: string
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
  moTa?: string
  giaBan: number
  danhMucMon?: string
  trangThai: MenuItemStatus
  duongDanUrl?: string
  theNguCanh: string[]
  deleted?: boolean
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
  tenThucDon?: string
  phanLoai?: string
  trangThai?: string
  monAn?: MenuItemResponse[]
  combo?: ComboResponse[]
}
