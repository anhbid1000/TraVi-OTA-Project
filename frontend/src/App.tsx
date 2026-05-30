import { BrowserRouter, Link, Route, Routes } from 'react-router-dom'
import {
  CustomerLogin,
  CustomerRegister,
  ForgotPasswordPage,
  PartnerLogin,
  PartnerRegister,
  VerifyEmailPage,
} from './pages/auth'
import { ForbiddenPage } from './pages/ForbiddenPage'
import { HomePage } from './pages/HomePage'
import { CheckoutPage } from './pages/CheckoutPage'
import { PaymentPage } from './pages/PaymentPage'
import { ProtectedRoute, StaffRoute } from './routes'
import { HotelCatalogPage } from './features/hotels/pages/HotelCatalogPage'
import { RestaurantCatalogPage } from './features/restaurants/pages/RestaurantCatalogPage'
import { HotelDetailPage } from './features/hotels/pages/HotelDetailPage'
import { RestaurantDetailPage } from './features/restaurants/pages/RestaurantDetailPage'
import './App.css'



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

/**
 * Component App - Routing chính của ứng dụng TraVi OTA
 * Cấu trúc route:
 * /                             - Trang chủ
 * /hotels                       - Danh sách khách sạn (catalog)
 * /hotels/:id                   - Chi tiết khách sạn
 * /restaurants                  - Danh sách nhà hàng (catalog)
 * /restaurants/:id              - Chi tiết nhà hàng
 * /login, /register, ...        - Auth flow cho khách
 * /partner/login, ...           - Auth flow cho đối tác
 * /admin/login                  - Auth flow cho admin
 * /payment                      - Trang thanh toán (protected)
 * /admin                        - Dashboard admin (protected)
 * /partner                      - Dashboard đối tác (protected)
 * /403                          - Trang lỗi 403 Forbidden
 * *                             - Trang 404 Not Found
 */
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/hotels" element={<HotelCatalogPage />} />
        <Route path="/hotels/:id" element={<HotelDetailPage />} />
        <Route path="/restaurants" element={<RestaurantCatalogPage />} />
        <Route path="/restaurants/:id" element={<RestaurantDetailPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />

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
        <Route path="/403" element={<ForbiddenPage />} />

        <Route element={<ProtectedRoute />}>
          <Route path="/payment" element={<PaymentPage />} />
        </Route>
        <Route element={<StaffRoute redirectTo="/partner/login" />}>
          <Route path="/partner" element={<PartnerDashboardPage />} />
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App

