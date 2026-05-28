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
import { HomePage } from './pages/HomePage'
import { AdminRoute, ProtectedRoute, StaffRoute } from './routes'
import { HotelCatalogPage } from './pages/hotels/HotelCatalogPage'
import { RestaurantCatalogPage } from './pages/restaurants/RestaurantCatalogPage'
import { HotelDetailPage } from './pages/hotels/HotelDetailPage'
import { RestaurantDetailPage } from './pages/restaurants/RestaurantDetailPage'
import './App.css'

function PaymentPage() {
  const { isLoading, logout, user } = useAuth()

  return (
    <main style={{ padding: '32px', maxWidth: '960px', margin: '0 auto' }}>
      <h1>Thanh toan</h1>
      <p>Trang nay chi danh cho nguoi dung da dang nhap.</p>
      <p>
        <strong>Email:</strong> {String(user?.email ?? 'Khong xac dinh')}
      </p>
      <button
        type="button"
        onClick={() => {
          void logout()
        }}
        disabled={isLoading}
      >
        {isLoading ? 'Dang dang xuat...' : 'Dang xuat'}
      </button>
      <p style={{ marginTop: '16px' }}>
        <Link to="/">Quay ve trang chu</Link>
      </p>
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
        <Route path="/hotels" element={<HotelCatalogPage />} />
        <Route path="/hotels/:id" element={<HotelDetailPage />} />
        <Route path="/restaurants" element={<RestaurantCatalogPage />} />
        <Route path="/restaurants/:id" element={<RestaurantDetailPage />} />

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
