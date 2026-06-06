import { useEffect, useMemo, useState } from 'react'
import { getCitySuggestions } from '../services/catalogService'

type UseCitySuggestionsResult = {
  cities: string[]
  loading: boolean
  error: string | null
}

let cityCache: string[] | null = null

export function useCitySuggestions(): UseCitySuggestionsResult {
  const [cities, setCities] = useState<string[]>(() => cityCache ?? [])
  const [loading, setLoading] = useState(() => cityCache === null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (cityCache) {
      return
    }

    let isMounted = true

    void getCitySuggestions()
      .then((result) => {
        if (!isMounted) {
          return
        }
        cityCache = result
        setCities(result)
      })
      .catch((fetchError) => {
        if (!isMounted) {
          return
        }
        setError(fetchError instanceof Error ? fetchError.message : 'Không thể tải dữ liệu địa điểm.')
      })
      .finally(() => {
        if (isMounted) {
          setLoading(false)
        }
      })

    return () => {
      isMounted = false
    }
  }, [])

  const deduplicatedCities = useMemo(() => Array.from(new Set(cities)), [cities])

  return {
    cities: deduplicatedCities,
    loading,
    error,
  }
}
