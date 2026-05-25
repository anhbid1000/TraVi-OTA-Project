import { useState } from 'react'
import { BrowserRouter, Link, Route, Routes } from 'react-router-dom'
import {
  AdminLogin,
  CustomerLogin,
  CustomerRegister,
  ForgotPasswordPage,
  PartnerLogin,
  PartnerRegister,
  VerifyEmailPage,
} from './pages/auth'
import { useAuth } from './hooks/useAuth'
import { ForbiddenPage } from './pages/ForbiddenPage'
import { AdminRoute, ProtectedRoute, StaffRoute } from './routes'
import './App.css'

function HomePage() {
  const { isAuthenticated, isLoading, logout, user } = useAuth()
  const [logoutError, setLogoutError] = useState('')

  const handleLogout = async () => {
    setLogoutError('')

    try {
      await logout()
    } catch {
      setLogoutError('Không thể đăng xuất lúc này. Vui lòng thử lại.')
    }
  }

  return (
    <main style={{ padding: '32px', maxWidth: '960px', margin: '0 auto' }}>
      <h1>TraVi-OTA</h1>
      <p>Nền tảng quản lý khách sạn và nhà hàng thông minh.</p>

      {isAuthenticated ? (
        <section
          style={{
            margin: '20px 0',
            padding: '16px 20px',
            border: '1px solid #dbeafe',
            borderRadius: '16px',
            background: '#f8fbff',
          }}
        >
          <p style={{ margin: '0 0 8px', fontWeight: 700 }}>Bạn đang đăng nhập</p>
          <p style={{ margin: '0 0 6px' }}>
            <strong>Email:</strong> {String(user?.email ?? 'Không xác định')}
          </p>
          <p style={{ margin: '0 0 16px' }}>
            <strong>Vai trò:</strong> {String(user?.role ?? 'Không xác định')}
          </p>

          <button
            type="button"
            onClick={handleLogout}
            disabled={isLoading}
            style={{
              border: 'none',
              borderRadius: '999px',
              padding: '10px 18px',
              background: '#dc2626',
              color: '#fff',
              fontWeight: 700,
              cursor: isLoading ? 'not-allowed' : 'pointer',
              opacity: isLoading ? 0.7 : 1,
            }}
          >
            {isLoading ? 'Đang đăng xuất...' : 'Đăng xuất'}
          </button>

          {logoutError && (
            <p style={{ margin: '12px 0 0', color: '#dc2626', fontWeight: 600 }}>{logoutError}</p>
          )}
        </section>
      ) : (
        <p style={{ margin: '20px 0', color: '#4b5563' }}>
          Hiện bạn chưa đăng nhập. Hãy dùng các liên kết bên dưới để đăng nhập hoặc đăng ký.
        </p>
      )}

      <nav>
        <Link to="/">Trang chủ</Link> | <Link to="/login">Đăng nhập</Link> |{' '}
        <Link to="/register">Đăng ký</Link> | <Link to="/partner/login">Đối tác</Link> |{' '}
        <Link to="/admin/login">Admin</Link> | <Link to="/payment">Thanh toán</Link>
      </nav>
    </main>
  )
}

function PaymentPage() {
  return (
    <main>
      <h1>Thanh toán</h1>
      <p>Trang này chỉ dành cho người dùng đã đăng nhập.</p>
    </main>
  )
}

function AdminDashboardPage() {
  return (
    <main>
      <h1>Admin Dashboard</h1>
      <p>Trang này chỉ dành cho quản trị viên.</p>
    </main>
  )
}

function PartnerDashboardPage() {
  return (
    <main>
      <h1>Đối tác Dashboard</h1>
      <p>Trang này chỉ dành cho đối tác.</p>
    </main>
  )
}

function NotFoundPage() {
  return (
    <main>
      <h1>404 - Không tìm thấy trang</h1>
      <Link to="/">Quay về trang chủ</Link>
    </main>
  )
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />

        <Route path="/login" element={<CustomerLogin />} />
        <Route path="/register" element={<CustomerRegister />} />
        <Route
          path="/forgot-password"
          element={
            <ForgotPasswordPage
              loginPath="/login"
              tone="blue"
              title="Quên mật khẩu"
              subtitle="Nhập email, xác thực OTP và đặt lại mật khẩu khách hàng của bạn"
            />
          }
        />
        <Route path="/verify-email" element={<VerifyEmailPage />} />

        <Route path="/partner/login" element={<PartnerLogin />} />
        <Route path="/partner/register" element={<PartnerRegister />} />
        <Route
          path="/partner/forgot-password"
          element={
            <ForgotPasswordPage
              loginPath="/partner/login"
              tone="emerald"
              title="Khôi phục mật khẩu đối tác"
              subtitle="Xác thực email đối tác bằng OTP để tạo mật khẩu mới"
            />
          }
        />

        <Route path="/admin/login" element={<AdminLogin />} />

        <Route path="/403" element={<ForbiddenPage />} />

        <Route element={<ProtectedRoute />}>
          <Route path="/payment" element={<PaymentPage />} />
        </Route>

        <Route element={<AdminRoute />}>
          <Route path="/admin" element={<AdminDashboardPage />} />
        </Route>

        <Route element={<StaffRoute />}>
          <Route path="/partner" element={<PartnerDashboardPage />} />
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
