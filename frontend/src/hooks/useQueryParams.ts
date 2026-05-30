import { useMemo } from 'react'
import { useSearchParams } from 'react-router-dom'

type QueryValue = string | number | null | undefined

type QueryUpdates = Record<string, QueryValue>

function parseCsv(value: string | null): string[] {
  if (!value) {
    return []
  }
  return value
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
}

export function useQueryParams() {
  const [searchParams, setSearchParams] = useSearchParams()

  const query = useMemo(() => searchParams, [searchParams])

  const setQuery = (updates: QueryUpdates, options?: { resetPage?: boolean; replace?: boolean }) => {
    const next = new URLSearchParams(searchParams)

    Object.entries(updates).forEach(([key, value]) => {
      if (value === null || value === undefined || value === '') {
        next.delete(key)
        return
      }
      next.set(key, String(value))
    })

    if (options?.resetPage) {
      next.set('page', '0')
    }

    setSearchParams(next, { replace: options?.replace })
  }

  const getString = (key: string, fallback = '') => searchParams.get(key) ?? fallback

  const getNumber = (key: string, fallback: number) => {
    const raw = searchParams.get(key)
    if (!raw) {
      return fallback
    }

    const parsed = Number(raw)
    return Number.isNaN(parsed) ? fallback : parsed
  }

  const getCsvArray = (key: string) => parseCsv(searchParams.get(key))

  return {
    query,
    setQuery,
    getString,
    getNumber,
    getCsvArray,
  }
}
