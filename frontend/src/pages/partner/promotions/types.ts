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
