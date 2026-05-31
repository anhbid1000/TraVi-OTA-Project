// Types cho Module 5 - Review (Customer & Partner)

export type LoaiDichVu = 'KHACH_SAN' | 'NHA_HANG';
export type TrangThaiDanhGia = 'DA_HIEN_THI' | 'BI_AN';
export type ReviewAspectType =
  | 'CLEANLINESS'
  | 'SERVICE'
  | 'LOCATION'
  | 'VALUE'
  | 'AMENITIES'
  | 'FOOD_QUALITY'
  | 'AMBIENCE';

export interface AttachmentResponse {
  id: string;
  fileUrl: string;
  fileName: string;
  fileType: 'IMAGE' | 'DOCUMENT';
  mimeType: string;
  fileSize: number;
}

export interface ReviewReplyResponse {
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface ReviewAspectScoreItem {
  aspect: ReviewAspectType;
  score: number;
}

export interface CreateReviewRequest {
  loaiDichVu: LoaiDichVu;
  bookingId?: string;
  reservationId?: string;
  soSao: number;
  noiDung: string;
  aspectScores?: ReviewAspectScoreItem[];
}

export interface ReviewResponse {
  id: string;
  customerName: string;
  businessProfileId: string;
  serviceType: LoaiDichVu;
  rating: number;
  content: string;
  status: TrangThaiDanhGia;
  createdAt: string;
  attachments: AttachmentResponse[];
  aspectScores: ReviewAspectScoreItem[];
  partnerReply?: ReviewReplyResponse;
}

export interface PartnerReviewReplyRequest {
  noiDung: string;
}
