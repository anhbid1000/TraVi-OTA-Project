// Types cho Module 5 - Review (Customer & Partner)

export interface ReviewResponse {
  id: string;
  customerName: string;
  businessProfileId: string;
  serviceType: LoaiDichVu;
  rating: number;
  content: string;
  status: TrangThaiDanhGia;
  createdAt: string;
  partnerReply?: ReviewReplyResponse;
}

export interface ReviewReplyResponse {
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateReviewRequest {
  loaiDichVu: LoaiDichVu;
  bookingId?: string;
  reservationId?: string;
  soSao: number;
  noiDung: string;
}

// String literal types (thay cho enum để pass 'erasableSyntaxOnly')
export type LoaiDichVu = 'KHACH_SAN' | 'NHA_HANG';
export type TrangThaiDanhGia = 'DA_HIEN_THI' | 'BI_AN';
