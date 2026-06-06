type SearchCriteriaValue = string | number

type SearchCriteria = Record<string, SearchCriteriaValue>

export const HOTEL_SEARCH_CRITERIA_KEY = 'travi.catalog.hotels.search'
export const RESTAURANT_SEARCH_CRITERIA_KEY = 'travi.catalog.restaurants.search'

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value)
}

function getSessionStorage() {
  if (typeof window === 'undefined') {
    return null
  }

  return window.sessionStorage
}

export function readSearchCriteria(key: string): SearchCriteria | null {
  const storage = getSessionStorage()
  if (!storage) {
    return null
  }

  try {
    const raw = storage.getItem(key)
    if (!raw) {
      return null
    }

    const parsed = JSON.parse(raw) as unknown
    if (!isRecord(parsed)) {
      return null
    }

    return Object.entries(parsed).reduce<SearchCriteria>((criteria, [field, value]) => {
      if ((typeof value === 'string' && value.trim()) || typeof value === 'number') {
        criteria[field] = value
      }
      return criteria
    }, {})
  } catch {
    return null
  }
}

export function writeSearchCriteria(key: string, criteria: SearchCriteria) {
  const storage = getSessionStorage()
  if (!storage) {
    return
  }

  const meaningfulCriteria = Object.entries(criteria).reduce<SearchCriteria>((next, [field, value]) => {
    if ((typeof value === 'string' && value.trim()) || typeof value === 'number') {
      next[field] = value
    }
    return next
  }, {})

  storage.setItem(key, JSON.stringify(meaningfulCriteria))
}

export function clearSearchCriteria(key: string) {
  const storage = getSessionStorage()
  if (!storage) {
    return
  }

  storage.removeItem(key)
}
