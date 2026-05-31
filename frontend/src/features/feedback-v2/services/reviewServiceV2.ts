import { api } from '../../../services/api';
import { getApiErrorMessage } from '../../../utils/apiError';
import type { PageResponse } from '../../../types/common';
import type { CreateReviewRequest, ReviewResponse } from '../types/review';

const USER_PREFIX = '/v1/user';
const PARTNER_PREFIX = '/v1/partner';
const PUBLIC_PREFIX = '/v1/public';

export const reviewServiceV2 = {
  // --- CUSTOMER ---
  async createReview(payload: CreateReviewRequest, files?: File[]): Promise<ReviewResponse> {
    try {
      const formData = new FormData();
      // Spring Boot mong đợi part tên 'request' cho JSON
      formData.append('request', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
      if (files) {
        files.forEach((file) => formData.append('files', file));
      }

      const { data } = await api.post(`${USER_PREFIX}/reviews`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tạo đánh giá lúc này.'));
    }
  },

  async getCustomerReviews(page = 0, size = 10, sort = 'newest'): Promise<PageResponse<ReviewResponse>> {
    try {
      const { data } = await api.get(`${USER_PREFIX}/reviews`, {
        params: { page, size, sort },
      });
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải danh sách đánh giá.'));
    }
  },

  async getPublicReviews(businessProfileId: string, page = 0, size = 10, sort = 'newest'): Promise<PageResponse<ReviewResponse>> {
    try {
      const { data } = await api.get(`${PUBLIC_PREFIX}/business-profiles/${businessProfileId}/reviews`, {
        params: { page, size, sort },
      });
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải đánh giá.'));
    }
  },

  // --- PARTNER ---
  async getPartnerReviews(page = 0, size = 10, sort = 'newest'): Promise<PageResponse<ReviewResponse>> {
    try {
      const { data } = await api.get(`${PARTNER_PREFIX}/reviews`, {
        params: { page, size, sort },
      });
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải danh sách đánh giá.'));
    }
  },

  async upsertPartnerReply(reviewId: string, payload: { noiDung: string }): Promise<ReviewResponse> {
    try {
      const { data } = await api.post(`${PARTNER_PREFIX}/reviews/${reviewId}/reply`, payload);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể phản hồi đánh giá lúc này.'));
    }
  },
};
