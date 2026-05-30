import { api } from '../../../services/api';
import { getApiErrorMessage } from '../../../utils/apiError';
import type { PageResponse } from '../../../types/common';
import type { ComplaintResponse, CreateComplaintRequest, ComplaintMessageCreateRequest, ComplaintStatusUpdateRequest } from '../types/complaint';

const USER_PREFIX = '/v1/user';
const PARTNER_PREFIX = '/v1/partner';

export const complaintServiceV2 = {
  // --- CUSTOMER ---

  async createComplaint(payload: CreateComplaintRequest): Promise<ComplaintResponse> {
    try {
      const { data } = await api.post(`${USER_PREFIX}/complaints`, payload);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tạo khiếu nại lúc này.'));
    }
  },

  async getCustomerComplaints(
    page = 0, size = 10, status?: string, mucDo?: string, sort = 'updated_desc'
  ): Promise<PageResponse<ComplaintResponse>> {
    try {
      const { data } = await api.get(`${USER_PREFIX}/complaints`, {
        params: { page, size, status, mucDo, sort }
      });
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải danh sách khiếu nại.'));
    }
  },

  async getCustomerComplaintDetail(id: string): Promise<ComplaintResponse> {
    try {
      const { data } = await api.get(`${USER_PREFIX}/complaints/${id}`);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải chi tiết khiếu nại.'));
    }
  },

  async postCustomerMessage(id: string, payload: ComplaintMessageCreateRequest): Promise<ComplaintResponse> {
    try {
      const { data } = await api.post(`${USER_PREFIX}/complaints/${id}/messages`, payload);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể gửi tin nhắn.'));
    }
  },

  async closeComplaint(id: string): Promise<ComplaintResponse> {
    try {
      const { data } = await api.put(`${USER_PREFIX}/complaints/${id}/close`);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể đóng khiếu nại.'));
    }
  },

  // --- PARTNER ---

  async getPartnerComplaints(
    page = 0, size = 10, status?: string, mucDo?: string, sort = 'updated_desc'
  ): Promise<PageResponse<ComplaintResponse>> {
    try {
      const { data } = await api.get(`${PARTNER_PREFIX}/complaints`, {
        params: { page, size, status, mucDo, sort }
      });
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải danh sách khiếu nại.'));
    }
  },

  async getPartnerComplaintDetail(id: string): Promise<ComplaintResponse> {
    try {
      const { data } = await api.get(`${PARTNER_PREFIX}/complaints/${id}`);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải chi tiết khiếu nại.'));
    }
  },

  async postPartnerMessage(id: string, payload: ComplaintMessageCreateRequest): Promise<ComplaintResponse> {
    try {
      const { data } = await api.post(`${PARTNER_PREFIX}/complaints/${id}/messages`, payload);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể gửi tin nhắn.'));
    }
  },

  async updateComplaintStatus(id: string, payload: ComplaintStatusUpdateRequest): Promise<ComplaintResponse> {
    try {
      const { data } = await api.put(`${PARTNER_PREFIX}/complaints/${id}/status`, payload);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể cập nhật trạng thái khiếu nại.'));
    }
  }
};
