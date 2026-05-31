import { api } from '../../../services/api';
import { getApiErrorMessage } from '../../../utils/apiError';
import type { PageResponse } from '../../../types/common';
import type {
  ComplaintResponse,
  CreateComplaintRequest,
  ComplaintMessageCreateRequest,
  ResolutionActionCreateRequest,
  ResolutionActionRejectRequest,
  ResolutionActionCompleteRequest,
} from '../types/complaint';

const USER_PREFIX = '/v1/user';
const PARTNER_PREFIX = '/v1/partner';

export const complaintServiceV2 = {
  async createComplaint(payload: CreateComplaintRequest, files?: File[]): Promise<ComplaintResponse> {
    try {
      const formData = new FormData();
      formData.append('request', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
      if (files) files.forEach((file) => formData.append('files', file));

      const { data } = await api.post(`${USER_PREFIX}/complaints`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tạo khiếu nại lúc này.'));
    }
  },

  async getCustomerComplaints(page = 0, size = 10, status?: string, mucDo?: string, category?: string, sort = 'updated_desc'): Promise<PageResponse<ComplaintResponse>> {
    try {
      const { data } = await api.get(`${USER_PREFIX}/complaints`, {
        params: { page, size, status, mucDo, category, sort },
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

  async postCustomerMessage(id: string, payload: ComplaintMessageCreateRequest, files?: File[]): Promise<ComplaintResponse> {
    try {
      const formData = new FormData();
      formData.append('request', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
      if (files) files.forEach((file) => formData.append('files', file));

      const { data } = await api.post(`${USER_PREFIX}/complaints/${id}/messages`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
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

  async acceptResolutionAction(complaintId: string, actionId: string): Promise<ComplaintResponse> {
    try {
      const { data } = await api.put(`${USER_PREFIX}/complaints/${complaintId}/resolution-actions/${actionId}/accept`);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể chấp nhận phương án.'));
    }
  },

  async rejectResolutionAction(complaintId: string, actionId: string, payload: ResolutionActionRejectRequest): Promise<ComplaintResponse> {
    try {
      const { data } = await api.put(`${USER_PREFIX}/complaints/${complaintId}/resolution-actions/${actionId}/reject`, payload);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể từ chối phương án.'));
    }
  },

  async getPartnerComplaints(page = 0, size = 10, status?: string, mucDo?: string, category?: string, sort = 'updated_desc'): Promise<PageResponse<ComplaintResponse>> {
    try {
      const { data } = await api.get(`${PARTNER_PREFIX}/complaints`, {
        params: { page, size, status, mucDo, category, sort },
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

  async postPartnerMessage(id: string, payload: ComplaintMessageCreateRequest, files?: File[]): Promise<ComplaintResponse> {
    try {
      const formData = new FormData();
      formData.append('request', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
      if (files) files.forEach((file) => formData.append('files', file));

      const { data } = await api.post(`${PARTNER_PREFIX}/complaints/${id}/messages`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể gửi tin nhắn.'));
    }
  },

  async createResolutionAction(complaintId: string, payload: ResolutionActionCreateRequest): Promise<ComplaintResponse> {
    try {
      const { data } = await api.post(`${PARTNER_PREFIX}/complaints/${complaintId}/resolution-actions`, payload);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tạo phương án xử lý.'));
    }
  },

  async startResolutionAction(complaintId: string, actionId: string): Promise<ComplaintResponse> {
    try {
      const { data } = await api.put(`${PARTNER_PREFIX}/complaints/${complaintId}/resolution-actions/${actionId}/start`);
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể bắt đầu thực hiện phương án.'));
    }
  },

  async completeResolutionAction(complaintId: string, actionId: string, payload: ResolutionActionCompleteRequest, files?: File[]): Promise<ComplaintResponse> {
    try {
      const formData = new FormData();
      formData.append('request', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
      if (files) files.forEach((file) => formData.append('files', file));

      const { data } = await api.put(`${PARTNER_PREFIX}/complaints/${complaintId}/resolution-actions/${actionId}/complete`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      return data;
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể hoàn tất phương án.'));
    }
  },
};
