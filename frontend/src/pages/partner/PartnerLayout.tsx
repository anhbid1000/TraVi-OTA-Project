import { NavLink, Outlet } from 'react-router-dom'
import '../dashboard.css'

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  `partner-nav-link${isActive ? ' partner-nav-link--active' : ''}`

export function PartnerLayout() {
  return (
    <div className="partner-dashboard">
      <aside className="partner-sidebar">
        <div className="partner-sidebar__header">
          <span className="partner-sidebar__brand">TraVi Partner</span>
        </div>

        <nav className="partner-sidebar__nav">
          <NavLink className={navLinkClass} to="business-profile">
            Hồ sơ kinh doanh
          </NavLink>
          <NavLink className={navLinkClass} to="hotel-setup">
            Khách sạn
          </NavLink>
          <NavLink className={navLinkClass} to="rooms">
            Quản lý phòng
          </NavLink>
          <NavLink className={navLinkClass} to="restaurant-setup">
            Nhà hàng
          </NavLink>
          <NavLink className={navLinkClass} to="tables">
            Bố trí bàn
          </NavLink>
          <NavLink className={navLinkClass} to="menu">
            Thực đơn
          </NavLink>
        </nav>
      </aside>

      <main className="partner-content">
        <Outlet />
      </main>
    </div>
  )
}
