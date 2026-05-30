import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface SearchParams {
  city: string | null
  checkIn: string | null
  checkOut: string | null
  guests: number
}

interface SearchStore {
  params: SearchParams
  setParams: (params: Partial<SearchParams>) => void
}

export const useSearchStore = create<SearchStore>()(
  persist(
    (set) => ({
      params: {
        city: 'Hồ Chí Minh',
        checkIn: new Date().toISOString().split('T')[0],
        checkOut: new Date(Date.now() + 86400000).toISOString().split('T')[0],
        guests: 2,
      },
      setParams: (newParams) =>
        set((state) => ({ params: { ...state.params, ...newParams } })),
    }),
    {
      name: 'travi-search-storage',
    }
  )
)
