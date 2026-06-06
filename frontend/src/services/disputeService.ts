import { api } from './api';

const PARTNER_PREFIX = '/api/v1/partner';
const ADMIN_PREFIX = '/api/v1/admin';

export const disputeService = {
  // Lấy danh sách các vụ tranh chấp của mình
  async getPartnerDisputes() {
    const { data } = await api.get(`${PARTNER_PREFIX}/reports`);
    return data;
  },

  // ĐỐI TÁC GỬI GIẢI TRÌNH CHỨNG CỨ
  async submitExplanation(reportId: string, message: string, files: File[]) {
    const formData = new FormData();
    const explanationData = { noiDung: message };
    
    formData.append("data", new Blob([JSON.stringify(explanationData)], { type: "application/json" }));
    files.forEach(file => {
      formData.append("files", file);
    });

    const { data } = await api.post(`${PARTNER_PREFIX}/reports/${reportId}/explanations`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return data;
  },

  // ADMIN ĐƯA RA PHÁN QUYẾT
  async submitAdminVerdict(caseId: string, verdictType: 'KHONG_VI_PHAM' | 'VI_PHAM_NHE' | 'VI_PHAM_NANG') {
    const { data } = await api.post(`${ADMIN_PREFIX}/reports/${caseId}/verdict`, null, {
      params: { mucDoViPham: verdictType }
    });
    return data;
  }
};
