import { api } from './api'

export interface DanhMucSoThich {
  id: string
  tenDanhMuc: string
  moTa?: string
  danhSachSoThich: SoThich[]
}

export interface SoThich {
  id: string
  tenSoThich: string
}

export interface UpdatePreferencesRequest {
  soThichIds: string[]
}

export const preferenceService = {
  // Lấy danh sách danh mục + sở thích
  async getCategories(): Promise<DanhMucSoThich[]> {
    const response = await api.get<DanhMucSoThich[]>('/v1/user/preferences/categories')
    return response.data
  },

  // Lấy sở thích hiện tại của user
  async getUserPreferences(): Promise<SoThich[]> {
    const response = await api.get<SoThich[]>('/v1/user/preferences')
    return response.data
  },

  // Cập nhật sở thích (auto mark onboarding complete trên backend)
  async updateUserPreferences(soThichIds: string[]): Promise<SoThich[]> {
    const response = await api.put<SoThich[]>('/v1/user/preferences', {
      soThichIds,
    })
    return response.data
  },
}
