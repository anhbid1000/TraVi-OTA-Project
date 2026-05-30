// Types cho Module 5 - Complaint (Customer & Partner)

export interface ComplaintResponse {
  id: string;
  title: string;
  serviceType: LoaiDichVu;
  severity: MucDoKhieuNai;
  status: TrangThaiKhieuNai;
  overdue: boolean;
  createdAt: string;
  updatedAt: string;
  messages: ComplaintMessageResponse[];
}

export interface ComplaintMessageResponse {
  senderRole: VaiTroTinNhan;
  content: string;
  createdAt: string;
}

export interface CreateComplaintRequest {
  loaiDichVu: LoaiDichVu;
  bookingId?: string;
  reservationId?: string;
  tieuDe: string;
  noiDungTomTat: string;
  mucDo: MucDoKhieuNai;
}

export interface ComplaintMessageCreateRequest {
  noiDung: string;
}

export interface ComplaintStatusUpdateRequest {
  status: TrangThaiKhieuNai;
}

// String literal types (thay cho enum để pass 'erasableSyntaxOnly')
export type LoaiDichVu = 'KHACH_SAN' | 'NHA_HANG';
export type TrangThaiKhieuNai = 'CHO_PHAN_HOI' | 'DANG_XU_LY' | 'DA_GIAI_QUYET' | 'DA_DONG';
export type MucDoKhieuNai = 'BINH_THUONG' | 'NGHIEM_TRONG';
export type VaiTroTinNhan = 'KHACH_HANG' | 'DOI_TAC';
