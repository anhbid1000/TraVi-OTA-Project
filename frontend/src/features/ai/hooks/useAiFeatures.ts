import { useCallback, useEffect, useState } from 'react'
import { getApiErrorMessage } from '../../../utils/apiError'
import {
  getCategories,
  getUserPreferences,
  updateUserPreferences,
  generateItinerary,
  getSmartNotifications,
} from '../services/aiService'
import type {
  DanhMucSoThichResponse,
  ItineraryRequest,
  ItineraryResponse,
  SoThichResponse,
  ThongBaoNguCanhResponse,
} from '../types'

export function usePreferences() {
  const [categories, setCategories] = useState<DanhMucSoThichResponse[]>([])
  const [userPrefs, setUserPrefs] = useState<SoThichResponse[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchPrefs = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const [cats, prefs] = await Promise.all([getCategories(), getUserPreferences()])
      setCategories(cats)
      setUserPrefs(prefs)
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tải sở thích.'))
    } finally {
      setLoading(false)
    }
  }, [])

  const updatePrefs = async (ids: string[]) => {
    try {
      const updated = await updateUserPreferences({ soThichIds: ids })
      setUserPrefs(updated)
      return true
    } catch (err) {
      setError(getApiErrorMessage(err, 'Lỗi cập nhật sở thích.'))
      return false
    }
  }

  useEffect(() => {
    void fetchPrefs()
  }, [fetchPrefs])

  return { categories, userPrefs, loading, error, updatePrefs, refetch: fetchPrefs }
}

export function useItineraryPlanner() {
  const [itinerary, setItinerary] = useState<ItineraryResponse | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const plan = async (request: ItineraryRequest) => {
    setLoading(true)
    setError(null)
    try {
      const res = await generateItinerary(request)
      setItinerary(res)
      return res
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tạo lịch trình.'))
      return null
    } finally {
      setLoading(false)
    }
  }

  return { itinerary, loading, error, plan }
}

export function useSmartNotifications(enabled = true) {
  const [notifications, setNotifications] = useState<ThongBaoNguCanhResponse[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchNotifs = useCallback(async () => {
    if (!enabled) return
    setLoading(true)
    setError(null)
    try {
      const res = await getSmartNotifications()
      setNotifications(res)
    } catch (err) {
      setError(getApiErrorMessage(err, 'Không thể tải thông báo.'))
    } finally {
      setLoading(false)
    }
  }, [enabled])

  useEffect(() => {
    void fetchNotifs()
  }, [fetchNotifs])

  return { notifications, loading, error, refetch: fetchNotifs }
}
