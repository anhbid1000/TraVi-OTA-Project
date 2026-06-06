import { useEffect, useState } from 'react'

export function useDebounce<T>(value: T, delay = 500): T {
  const [debounced, setDebounced] = useState(value)

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      setDebounced(value)
    }, delay)

    return () => {
      window.clearTimeout(timeout)
    }
  }, [value, delay])

  return debounced
}

