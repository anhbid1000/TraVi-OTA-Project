import { BrowserRouter, Link, Navigate, Route, Routes } from 'react-router-dom';
import {
  CustomerLogin,
  CustomerRegister,
  ForgotPasswordPage,
  PartnerLogin,
  PartnerRegister,
  VerifyEmailPage,
} from './pages/auth';
import { ForbiddenPage } from './pages/ForbiddenPage';
import { HomePage } from './pages/HomePage';
import { CheckoutPage } from './pages/CheckoutPage';
import { PaymentPage } from './pages/PaymentPage'
import { ProtectedRoute, StaffRoute } from './routes';
import {
  PartnerBusinessProfilePage,
  PartnerDashboardOverviewPage,
  PartnerHotelProfilePage,
  PartnerLayout,
  PartnerMenuManagementPage,
  PartnerRestaurantProfilePage,
  PartnerRoomManagementPage,
  PartnerTableLayoutPage,
} from './pages/partner';
import { SearchPage } from './pages/search';
// import { useAuth } from './hooks/useAuth';
import { HotelCatalogPage } from './features/hotels/pages/HotelCatalogPage';
import { RestaurantCatalogPage } from './features/restaurants/pages/RestaurantCatalogPage';
import { HotelDetailPage } from './features/hotels/pages/HotelDetailPage';
import { RestaurantDetailPage } from './features/restaurants/pages/RestaurantDetailPage';
import MyBookings from './pages/user/dashboard/MyBookings';
import PartnerReviewsPage from './pages/partner/PartnerReviewsPage';
import PartnerComplaintsPage from './pages/partner/complaints/PartnerComplaintsPage.tsx';
import PartnerComplaintDetailPage from './pages/partner/complaints/PartnerComplaintDetailPage.tsx';
import './App.css';
import MyBookingsV2 from './pages/user/dashboard/MyBookingsV2.tsx';
import MyComplaintsPage from './pages/user/complaints/MyComplaintsPage.tsx';
import MyComplaintDetailPage from './pages/user/complaints/MyComplaintDetailPage.tsx';
import LoyaltyDashboardPage from './pages/user/loyalty/LoyaltyDashboardPage.tsx';
import PartnerPromotionsPage from './pages/partner/promotions/PartnerPromotionsPage.tsx';


function NotFoundPage() {
  return (
    <main>
      <h1>404 - Không tìm thấy trang</h1>
      <Link to="/">Quay về trang chủ</Link>
    </main>
  );
}

/**
 * Component App - Routing chính của ứng dụng TraVi OTA
 * Cấu trúc route:
 * /                             - Trang chủ
 * /hotels                       - Danh sách khách sạn (catalog)
 * /hotels/:id                   - Chi tiết khách sạn
 * /restaurants                  - Danh sách nhà hàng (catalog)
 * /restaurants/:id              - Chi tiết nhà hàng
 * /search                       - Trang tra cứu đơn đặt chỗ
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
        <Route path="/search" element={<SearchPage />} />

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
          <Route path="/user/bookings-v2" element={<MyBookingsV2 />} />
          <Route path="/user/complaints" element={<MyComplaintsPage />} />
          <Route path="/user/complaints/:complaintId" element={<MyComplaintDetailPage />} />
          <Route path="/user/loyalty" element={<LoyaltyDashboardPage />} />
        </Route>
        <Route element={<StaffRoute redirectTo="/partner/login" />}>
          <Route path="/partner" element={<PartnerLayout />}>
            <Route index element={<PartnerDashboardOverviewPage />} />
            <Route path="dashboard" element={<PartnerDashboardOverviewPage />} />
            <Route path="business-profile" element={<PartnerBusinessProfilePage />} />
            <Route path="hotels" element={<Navigate to="/partner/hotel" replace />} />
            <Route path="hotel" element={<PartnerHotelProfilePage />} />
            <Route path="restaurant" element={<PartnerRestaurantProfilePage />} />
            <Route path="room" element={<PartnerRoomManagementPage />} />
            <Route path="tables" element={<PartnerTableLayoutPage />} />
            <Route path="menus" element={<PartnerMenuManagementPage />} />
            <Route path="reviews" element={<PartnerReviewsPage />} />
            <Route path="complaints" element={<PartnerComplaintsPage />} />
            <Route path="complaints/:complaintId" element={<PartnerComplaintDetailPage />} />
            <Route path="promotions" element={<PartnerPromotionsPage />} />
          </Route>
        </Route>

        <Route path="/user/bookings" element={<MyBookings />} />

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
