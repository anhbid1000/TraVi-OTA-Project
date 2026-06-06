import { api } from './api';

// Điểm chuẩn đón đầu API từ Backend quy định cho User và Partner
const USER_PREFIX = '/api/v1/user';
const PARTNER_PREFIX = '/api/v1/partner';

export interface CreateReviewRequest {
  bookingId: string;
  soSao: number;
  noiDung: string;
}

export const feedbackService = {
  // Khách hàng tạo đánh giá mới
  async createReview(payload: CreateReviewRequest) {
    const { data } = await api.post(`${USER_PREFIX}/reviews`, payload);
    return data;
  },

  // Lấy danh sách đặt đơn của khách
  async getCustomerBookings() {
    const { data } = await api.get(`${USER_PREFIX}/bookings`);
    return data;
  },

  // ĐỐI TÁC LẤY DANH SÁCH ĐÁNH GIÁ
  async getPartnerReviews() {
    const { data } = await api.get(`${PARTNER_PREFIX}/reviews`);
    return data;
  },

  // ĐỐI TÁC PHẢN HỒI ĐÁNH GIÁ
  async replyToReview(reviewId: string, replyContent: string) {
    // Truyền thẳng text/plain sang body nếu Backend nhận String thuần túy
    const { data } = await api.post(`${PARTNER_PREFIX}/reviews/${reviewId}/reply`, replyContent, {
      headers: { 'Content-Type': 'text/plain' }
    });
    return data;
  }
};
