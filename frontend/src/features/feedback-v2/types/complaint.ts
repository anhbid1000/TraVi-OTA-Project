// Types cho Module 5 - Complaint (Customer & Partner)

// --- Base enums (string-literal để pass erasableSyntaxOnly) ---
export type LoaiDichVu = 'KHACH_SAN' | 'NHA_HANG';

export type TrangThaiKhieuNai =
  | 'CHO_PHAN_HOI'
  | 'DANG_XU_LY'
  | 'CHO_XAC_NHAN_KHACH'
  | 'DANG_THUC_HIEN_PHUONG_AN'
  | 'DA_GIAI_QUYET'
  | 'DA_DONG';

export type MucDoKhieuNai = 'BINH_THUONG' | 'NGHIEM_TRONG';

export type ComplaintCategory =
  | 'ROOM_QUALITY'
  | 'CLEANLINESS'
  | 'SERVICE_ATTITUDE'
  | 'BILLING'
  | 'FOOD_QUALITY'
  | 'BOOKING_PROBLEM'
  | 'FACILITY_PROBLEM'
  | 'OTHER';

export type VaiTroTinNhan = 'KHACH_HANG' | 'DOI_TAC' | 'SYSTEM';

export type ComplaintResolutionActionType =
  | 'FULL_REFUND'
  | 'PARTIAL_REFUND'
  | 'VOUCHER'
  | 'DISCOUNT_NEXT_BOOKING'
  | 'CHANGE_ROOM'
  | 'OTHER_SOLUTION'
  | 'REJECT_COMPLAINT';

export type ComplaintResolutionActionStatus =
  | 'PROPOSED'
  | 'CUSTOMER_ACCEPTED'
  | 'CUSTOMER_REJECTED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED';

// --- Shared response atoms ---
export interface AttachmentResponse {
  id: string;
  fileUrl: string;
  fileName: string;
  fileType: 'IMAGE' | 'DOCUMENT';
  mimeType: string;
  fileSize: number;
}

export interface ComplaintMessageResponse {
  id: string;
  senderRole: VaiTroTinNhan;
  senderName: string;
  content: string;
  createdAt: string;
  attachments: AttachmentResponse[];
}

export interface ResolutionActionResponse {
  id: string;
  complaintId: string;
  actionType: ComplaintResolutionActionType;
  tieuDe: string;
  moTa?: string;
  amount?: number;
  currency?: string;
  voucherCode?: string;
  discountPercent?: number;
  status: ComplaintResolutionActionStatus;
  proposedByPartnerName: string;
  customerResponseNote?: string;
  partnerCompletionNote?: string;
  proposedAt?: string;
  customerRespondedAt?: string;
  completedAt?: string;
  createdAt?: string;
  updatedAt?: string;
  attachments: AttachmentResponse[];
}

export interface ComplaintActivityResponse {
  id: string;
  complaintId: string;
  activityType: string;
  actorName: string;
  actorRole: 'KHACH_HANG' | 'DOI_TAC' | 'SYSTEM';
  summary: string;
  metadata?: string;
  createdAt: string;
}

// --- Main DTO ---
export interface ComplaintResponse {
  id: string;
  title: string;
  serviceType: LoaiDichVu;
  category: ComplaintCategory;
  severity: MucDoKhieuNai;
  status: TrangThaiKhieuNai;
  overdue: boolean;
  createdAt: string;
  updatedAt: string;
  attachments: AttachmentResponse[];
  messages: ComplaintMessageResponse[];
  resolutionActions: ResolutionActionResponse[];
  activities: ComplaintActivityResponse[];
}

// --- Requests ---
export interface CreateComplaintRequest {
  loaiDichVu: LoaiDichVu;
  bookingId?: string;
  reservationId?: string;
  tieuDe: string;
  noiDungTomTat: string;
  category: ComplaintCategory;
  mucDo: MucDoKhieuNai;
}

export interface ComplaintMessageCreateRequest {
  noiDung: string;
}

export interface ResolutionActionCreateRequest {
  actionType: ComplaintResolutionActionType;
  tieuDe: string;
  moTa?: string;
  amount?: number;
  currency?: string;
  voucherCode?: string;
  discountPercent?: number;
}

export interface ResolutionActionRejectRequest {
  customerResponseNote: string;
}

export interface ResolutionActionCompleteRequest {
  partnerCompletionNote?: string;
}
