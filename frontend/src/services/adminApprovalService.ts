import { api } from './api'
import type { AdminApprovalResponse, ApprovalDecision } from '../types/asset'

export const adminApprovalService = {
  getPendingApprovals() {
    return api
      .get<AdminApprovalResponse[]>('/v1/admin/approvals/pending')
      .then((response) => response.data)
  },

  getApprovalDetail(id: string) {
    return api
      .get<AdminApprovalResponse>(`/v1/admin/approvals/${id}`)
      .then((response) => response.data)
  },

  updateApprovalStatus(id: string, status: ApprovalDecision, reason?: string) {
    return api
      .put<AdminApprovalResponse>(`/v1/admin/approvals/${id}/status`, { status, reason })
      .then((response) => response.data)
  },
}
