import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { Bell, LogIn, LogOut, UserRound, Bot } from 'lucide-react'
import { useAuth } from '../../hooks/useAuth'

export function AuthActions() {
  const { isAuthenticated, user, logout, isLoading } = useAuth()
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [isLoggingOut, setIsLoggingOut] = useState(false)
  const menuRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (!isMenuOpen) {
      return
    }

    const handleOutsideClick = (event: MouseEvent) => {
      if (!menuRef.current) {
        return
      }
      if (!menuRef.current.contains(event.target as Node)) {
        setIsMenuOpen(false)
      }
    }

    window.addEventListener('mousedown', handleOutsideClick)
    return () => {
      window.removeEventListener('mousedown', handleOutsideClick)
    }
  }, [isMenuOpen])

  const handleLogout = async () => {
    if (isLoggingOut || isLoading) {
      return
    }

    setIsLoggingOut(true)
    setIsMenuOpen(false)

    await new Promise((resolve) => setTimeout(resolve, 450))
    await logout()
    window.location.href = '/'
  }

  return (
    <div className="flex items-center gap-2">
      <button
        type="button"
        className="cursor-pointer rounded-full p-2 text-on-surface-variant transition-colors hover:bg-surface-container-low"
      >
        <Bell size={18} />
      </button>

      {isAuthenticated && user ? (
        <div className="relative" ref={menuRef}>
          <button
            type="button"
            onClick={() => setIsMenuOpen((value) => !value)}
            className="cursor-pointer rounded-full p-2 text-on-surface-variant transition-colors hover:bg-surface-container-low"
            title={`Đăng nhập với ${user.email}`}
          >
            <UserRound size={18} />
          </button>

          {isMenuOpen && (
            <div className="absolute right-0 top-11 z-50 w-56 overflow-hidden rounded-xl border border-outline-variant/50 bg-white shadow-lg">
              <div className="border-b border-outline-variant/30 px-3 py-2">
                <p className="text-xs text-on-surface-variant">Tài khoản</p>
                <p className="truncate text-sm font-semibold text-on-surface">{user.email}</p>
              </div>

              <div className="p-1">
                <Link
                  to="/ai/travel-planner"
                  onClick={() => setIsMenuOpen(false)}
                  className="flex w-full items-center gap-2 px-3 py-2.5 text-left text-sm font-medium text-on-surface transition-colors hover:bg-surface-container-low"
                >
                  <Bot size={16} /> Lập lịch trình AI
                </Link>

                <button
                  type="button"
                  onClick={() => {
                    void handleLogout()
                  }}
                  disabled={isLoading || isLoggingOut}
                  className="flex w-full cursor-pointer items-center gap-2 px-3 py-2.5 text-left text-sm font-medium text-error transition-colors hover:bg-surface-container-low hover:text-error disabled:cursor-not-allowed disabled:opacity-60"
                >
                  <LogOut size={16} /> {isLoggingOut ? 'Đang đăng xuất...' : 'Đăng xuất'}
                </button>
              </div>
            </div>
          )}
        </div>
      ) : (
        <Link
          to="/login"
          className="inline-flex cursor-pointer items-center gap-1.5 rounded-xl border border-primary/20 bg-primary/5 px-4 py-2 text-sm font-semibold text-primary transition-colors hover:bg-primary/10"
        >
          <LogIn size={16} /> Đăng nhập
        </Link>
      )}
    </div>
  )
}

