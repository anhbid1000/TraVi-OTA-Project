import { BrowserRouter, Link, Route, Routes } from 'react-router-dom'
import {
  AdminLogin,
  CustomerLogin,
  CustomerRegister,
  PartnerLogin,
  PartnerRegister,
  VerifyEmailPage,
} from './pages/auth'
import { ForbiddenPage } from './pages/ForbiddenPage'
import { AdminRoute, ProtectedRoute, StaffRoute } from './routes'
import './App.css'

function HomePage() {
  return (
    <main>
      <h1>TraVi-OTA</h1>
      <p>Nền tảng quản lý khách sạn và nhà hàng thông minh.</p>

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
        <Route path="/verify-email" element={<VerifyEmailPage />} />

        <Route path="/partner/login" element={<PartnerLogin />} />
        <Route path="/partner/register" element={<PartnerRegister />} />

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
