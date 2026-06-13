export type CampaignType = 'DIRECT' | 'VOUCHER'

export type PromotionFormState = {
  campaignType: CampaignType
  tenUuDai: string
  moTa: string
  businessProfileId: string
  mucGiam: string
  loaiGiamGia: 'PHAN_TRAM' | 'SO_TIEN_CO_DINH'
  giaTriGiamToiDa: string
  ngayBatDau: string
  ngayKetThuc: string
  maVoucher: string
  soLuongPhatHanh: string
  donHangToiThieu: string
  usageLimitPerUser: string
  diemCanDoi: string
  choPhepDoiBangDiem: boolean
  phamViApDung: string
}

export const initialPromotionFormState: PromotionFormState = {
  campaignType: 'VOUCHER',
  tenUuDai: '',
  moTa: '',
  businessProfileId: '',
  mucGiam: '',
  loaiGiamGia: 'PHAN_TRAM',
  giaTriGiamToiDa: '',
  ngayBatDau: '',
  ngayKetThuc: '',
  maVoucher: '',
  soLuongPhatHanh: '',
  donHangToiThieu: '',
  usageLimitPerUser: '',
  diemCanDoi: '',
  choPhepDoiBangDiem: false,
  phamViApDung: 'TOAN_SAN',
}
