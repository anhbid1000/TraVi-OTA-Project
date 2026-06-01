import { useEffect, useState } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { BriefcaseBusiness, Bed, DoorOpen, Utensils, Grid2X2, BookOpen, CircleHelp, LogOut, LayoutGrid, ChevronDown, ChevronUp, MessageSquare, ShieldAlert, BrainCircuit } from 'lucide-react'
import { useAuth } from '../../hooks/useAuth'
import { partnerAssetService } from '../../services/partnerAssetService'
import type { AuthUser } from '../../types/auth'

export type ServiceType = 'KHACH_SAN' | 'NHA_HANG' | null

export type PartnerView = 'business-profile' | 'hotel-setup' | 'room-management' | 'restaurant-setup' | 'table-layout' | 'menu-management'

export type PartnerLayoutContext = {
  user: AuthUser | null
  activeView: PartnerView
  setActiveView: React.Dispatch<React.SetStateAction<PartnerView>>
  partnerAvatarUrl: string
  partnerInitials: string
  serviceType: ServiceType
}

function getStringClaim(user: AuthUser | null, keys: string[]) {
  if (!user) return ''
  for (const key of keys) {
    if (typeof user[key] === 'string' && user[key]) return user[key]
  }
  return ''
}

function getPartnerInitials(user: AuthUser | null) {
  const displayName = getStringClaim(user, ['hoTen', 'fullName', 'name', 'username', 'email'])
  if (!displayName) return 'TV'
  return displayName.substring(0, 2).toUpperCase()
}

function CalendarIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect width="18" height="18" x="3" y="4" rx="2" ry="2"/><line x1="16" x2="16" y1="2" y2="6"/><line x1="8" x2="8" y1="2" y2="6"/><line x1="3" x2="21" y1="10" y2="10"/></svg>
  )
}
function AnalyticsIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M3 3v18h18"/><path d="M18 17V9"/><path d="M13 17V5"/><path d="M8 17v-3"/></svg>
  )
}
export function PartnerLayout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const isDashboardRoute = location.pathname === '/partner' || location.pathname === '/partner/dashboard'
  const isBookingRoute = location.pathname.startsWith('/partner/bookings')
  const isAiInsightRoute = location.pathname.startsWith('/partner/ai-insights')
  const isReviewRoute = location.pathname.startsWith('/partner/reviews')
  const isComplaintRoute = location.pathname.startsWith('/partner/complaints')
  const isPromotionRoute = location.pathname.startsWith('/partner/promotions')
  const isAssetManagementRoute = location.pathname.startsWith('/partner/asset-management') ||
    location.pathname.startsWith('/partner/business-profile') ||
    location.pathname.startsWith('/partner/hotels') ||
    location.pathname.startsWith('/partner/room') ||
    location.pathname.startsWith('/partner/restaurant') ||
    location.pathname.startsWith('/partner/tables') ||
    location.pathname.startsWith('/partner/menus')

  const [activeView, setActiveView] = useState<PartnerView>('business-profile')
  const [hasBusinessProfile, setHasBusinessProfile] = useState<boolean>(true)
  const [serviceType, setServiceType] = useState<ServiceType>(null)
  const [isManagementExpanded, setIsManagementExpanded] = useState<boolean>(false)

  useEffect(() => {
    if (!hasBusinessProfile) {
      setIsManagementExpanded(false)
      return
    }
    if (isAssetManagementRoute) {
      setIsManagementExpanded(true)
    }
  }, [hasBusinessProfile, isAssetManagementRoute])

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const profiles = await partnerAssetService.getBusinessProfiles()
        const currentProfile = profiles[0]
        if (currentProfile) {
          setHasBusinessProfile(true)
          setServiceType(currentProfile.loaiDichVu as ServiceType)
        } else {
          setHasBusinessProfile(false)
          setServiceType(null)
        }
      } catch {
        setHasBusinessProfile(false)
        setServiceType(null)
      }
    }

    const handleProfileCreated = () => {
      void loadProfile()
    }

    void loadProfile()
    window.addEventListener('partner:profile-created', handleProfileCreated)

    return () => {
      window.removeEventListener('partner:profile-created', handleProfileCreated)
    }
  }, [location.pathname])

  const partnerAvatarUrl = getStringClaim(user, ['avatarUrl', 'anhDaiDien', 'hinhDaiDien', 'photoUrl', 'picture'])
  const partnerInitials = getPartnerInitials(user)

  const handleLogout = async () => {
    await logout()
    navigate('/partner/login')
  }

  const navItems = [
    { path: '/partner/business-profile', label: 'Hồ sơ kinh doanh', icon: <BriefcaseBusiness size={20} /> },
  ]

  if (serviceType === 'KHACH_SAN') {
    navItems.push(
      { path: '/partner/hotel', label: 'Thiết lập khách sạn', icon: <Bed size={20} /> },
      { path: '/partner/room', label: 'Quản lý phòng', icon: <DoorOpen size={20} /> }
    )
  } else if (serviceType === 'NHA_HANG') {
    navItems.push(
      { path: '/partner/restaurant', label: 'Thiết lập nhà hàng', icon: <Utensils size={20} /> },
      { path: '/partner/tables', label: 'Bố trí bàn', icon: <Grid2X2 size={20} /> },
      { path: '/partner/menus', label: 'Quản lý thực đơn', icon: <BookOpen size={20} /> }
    )
  }

  const contextValue: PartnerLayoutContext = {
    user,
    activeView,
    setActiveView,
    partnerAvatarUrl,
    partnerInitials,
    serviceType
  }

  return (
    <div className="dashboard-shell partner-shell">
      <aside className="dashboard-sidebar partner-sidebar">
        <div className="partner-sidebar-inner">
          <div className="partner-brand-card">
            <div className="partner-user-avatar large">
              {partnerAvatarUrl ? <img src={partnerAvatarUrl} alt="Ảnh đại diện đối tác" /> : partnerInitials}
            </div>
            <div className="brand-title">
              <strong>Bảng điều khiển TraVi</strong>
              <span>Cổng đối tác</span>
            </div>
          </div>

          <nav className="side-nav partner-nav main-partner-nav">
            <button
              className={isDashboardRoute ? 'active' : ''}
              onClick={() => navigate('/partner/dashboard')}
            >
              <LayoutGrid size={20} />
              Tổng quan
            </button>

              {hasBusinessProfile && (
                <>
                  <button
                    className={isBookingRoute ? 'active' : ''}
                    onClick={() => navigate('/partner/bookings')}
                  >
                    <CalendarIcon />
                    Đặt dịch vụ
                  </button>
                  <button className={isPromotionRoute ? 'active' : ''} onClick={() => navigate('/partner/promotions')}>
                    <AnalyticsIcon />
                    Ưu đãi & phân tích
                  </button>
                  <button
                    className={isAiInsightRoute ? 'active' : ''}
                    onClick={() => navigate('/partner/ai-insights')}
                  >
                    <BrainCircuit size={20} />
                    AI insight
                  </button>
                  <button
                    className={isReviewRoute ? 'active' : ''}
                    onClick={() => navigate('/partner/reviews')}
                  >
                    <MessageSquare size={20} />
                    Đánh giá
                  </button>
                  <button
                    className={isComplaintRoute ? 'active' : ''}
                    onClick={() => navigate('/partner/complaints')}
                  >
                    <ShieldAlert size={20} />
                    Khiếu nại
                  </button>
                </>
              )}

            <button 
              className={isAssetManagementRoute ? 'active' : ''} 
              onClick={() => {
                if (hasBusinessProfile) {
                  navigate('/partner/business-profile')
                  setIsManagementExpanded(!isManagementExpanded)
                } else {
                  navigate('/partner/business-profile')
                }
              }}
            >
              <BriefcaseBusiness size={20} />
              {hasBusinessProfile ? 'Quản lý tài sản' : 'Tạo hồ sơ kinh doanh'}
              {hasBusinessProfile && (
                <>
                  <span className="nav-spacer" />
                  {isManagementExpanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
                </>
              )}
            </button>
          </nav>

          {hasBusinessProfile && isManagementExpanded && (
            <div className="management-subnav">
              <p>Quản lý tài sản</p>
              {navItems.map((item) => (
                <button
                  key={item.path}
                  className={location.pathname === item.path ? 'active' : ''}
                  onClick={() => navigate(item.path)}
                >
                  {item.icon}
                  {item.label}
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="sidebar-footer">
          <button><CircleHelp size={20} /> Trung tâm trợ giúp</button>
          <button onClick={handleLogout}><LogOut size={20} /> Đăng xuất</button>
        </div>
      </aside>

      <main className="dashboard-main partner-main">
        <Outlet context={contextValue} />
      </main>
    </div>
  )
}
