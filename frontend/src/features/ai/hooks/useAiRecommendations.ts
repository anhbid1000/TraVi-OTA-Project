import { useCallback, useEffect, useMemo, useState } from 'react'
import { getApiErrorMessage } from '../../../utils/apiError'
import { getUserRecommendations, type RecommendationParams } from '../services/aiService'
import type { UserAiRecommendationResponse } from '../types'

type UseAiRecommendationsOptions = {
  enabled?: boolean
}

export function useAiRecommendations(
  params: RecommendationParams = {},
  options: UseAiRecommendationsOptions = {},
) {
  const enabled = options.enabled ?? true
  const paramsKey = useMemo(() => JSON.stringify(params), [params])
  const [data, setData] = useState<UserAiRecommendationResponse | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const refetch = useCallback(async () => {
    if (!enabled) {
      return
    }

    setLoading(true)
    setError(null)
    try {
      const result = await getUserRecommendations(params)
      setData(result)
    } catch (fetchError) {
      setData(null)
      setError(getApiErrorMessage(fetchError, 'Không thể tải gợi ý AI lúc này.'))
    } finally {
      setLoading(false)
    }
  }, [enabled, paramsKey])

  useEffect(() => {
    void refetch()
  }, [refetch])

  return { data, loading, error, refetch }
}
