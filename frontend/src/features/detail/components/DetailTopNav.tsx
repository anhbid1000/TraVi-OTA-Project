import { Link } from 'react-router-dom'
import { AuthActions } from '../../../components/layout/AuthActions'

type DetailTopNavProps = {
  active: 'hotels' | 'restaurants'
}

export function DetailTopNav({ active }: DetailTopNavProps) {
  return (
    <header className="sticky top-0 z-50 border-b border-outline-variant/30 bg-white/80 shadow-sm backdrop-blur-xl">
      <div className="mx-auto flex h-20 w-full max-w-7xl items-center justify-between px-5 md:px-12">
        <div className="flex items-center gap-8">
          <Link to="/" className="cursor-pointer font-display text-2xl font-bold text-primary">
            TraVi
          </Link>
          <nav className="hidden gap-1 md:flex">
            <Link
              to="/hotels"
              className={`cursor-pointer px-3 py-2 text-sm font-semibold transition-colors ${
                active === 'hotels'
                  ? 'border-b-2 border-primary text-primary'
                  : 'rounded-lg text-on-surface-variant hover:bg-surface-container-low hover:text-primary'
              }`}
            >
              Khách sạn
            </Link>
            <Link
              to="/restaurants"
              className={`cursor-pointer px-3 py-2 text-sm font-semibold transition-colors ${
                active === 'restaurants'
                  ? 'border-b-2 border-primary text-primary'
                  : 'rounded-lg text-on-surface-variant hover:bg-surface-container-low hover:text-primary'
              }`}
            >
              Nhà hàng
            </Link>
            <Link
              to="/search"
              className={`cursor-pointer px-3 py-2 text-sm font-semibold transition-colors ${
                active === 'search'
                  ? 'border-b-2 border-primary text-primary'
                  : 'rounded-lg text-on-surface-variant hover:bg-surface-container-low hover:text-primary'
              }`}
            >
              Tra cứu
            </Link>
          </nav>
        </div>
        <AuthActions />
      </div>
    </header>
  )
}

