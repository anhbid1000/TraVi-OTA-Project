import { Link } from 'react-router-dom';
import { useAuth } from '../../../../hooks/useAuth';

type PartnerSidebarProps = {
  active: 'complaints' | 'reviews' | 'dashboard';
};

export function PartnerSidebar({ active }: PartnerSidebarProps) {
  const { user, logout } = useAuth();

  // Đảm bảo lấy được tên đối tác thực tế hiển thị từ token/context
  const partnerName = String((user as { hoTen?: string; username?: string; sub?: string; email?: string } | null)?.hoTen || user?.username || user?.sub || user?.email || 'Đối tác');

  return (
    <aside className="hidden md:flex h-screen w-64 fixed left-0 top-0 flex-col bg-[#003b1b] py-6 shadow-md shadow-[#003b1b]/10 z-50">
      {/* Header Profile */}
      <div className="px-6 mb-8 flex items-center gap-3">
        <div className="w-10 h-10 rounded-full bg-white flex items-center justify-center overflow-hidden shrink-0">
          <span className="material-symbols-outlined text-primary text-xl">account_circle</span>
        </div>
        <div className="overflow-hidden">
          <h1 className="font-display text-sm font-bold text-white truncate" title={partnerName}>
            {partnerName}
          </h1>
          <p className="text-[10px] font-semibold tracking-wider text-emerald-300">Nhà điều hành</p>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 space-y-1 px-2">
        <Link
          to="/partner"
          className={`flex items-center gap-3 px-4 py-3 rounded-lg mx-2 transition-all duration-200 ease-in-out ${
            active === 'dashboard'
              ? 'bg-[#12512c] text-[#6ffbbe] font-semibold shadow-sm'
              : 'text-white hover:bg-[#12512c]/50 hover:text-[#6ffbbe]'
          }`}
        >
          <span 
            className="material-symbols-outlined" 
            style={{ fontVariationSettings: active === 'dashboard' ? "'FILL' 1" : "'FILL' 0" }}
          >
            dashboard
          </span>
          <span className="text-sm font-semibold">Tổng quan</span>
        </Link>

        <Link
          to="/partner/complaints"
          className={`flex items-center gap-3 px-4 py-3 rounded-lg mx-2 transition-all duration-200 ease-in-out ${
            active === 'complaints'
              ? 'bg-[#12512c] text-[#6ffbbe] font-semibold shadow-sm'
              : 'text-white hover:bg-[#12512c]/50 hover:text-[#6ffbbe]'
          }`}
        >
          <span 
            className="material-symbols-outlined" 
            style={{ fontVariationSettings: active === 'complaints' ? "'FILL' 1" : "'FILL' 0" }}
          >
            gavel
          </span>
          <span className="text-sm font-semibold">Khiếu nại</span>
        </Link>

        <Link
          to="/partner/reviews"
          className={`flex items-center gap-3 px-4 py-3 rounded-lg mx-2 transition-all duration-200 ease-in-out ${
            active === 'reviews'
              ? 'bg-[#12512c] text-[#6ffbbe] font-semibold shadow-sm'
              : 'text-white hover:bg-[#12512c]/50 hover:text-[#6ffbbe]'
          }`}
        >
          <span 
            className="material-symbols-outlined" 
            style={{ fontVariationSettings: active === 'reviews' ? "'FILL' 1" : "'FILL' 0" }}
          >
            star
          </span>
          <span className="text-sm font-semibold">Đánh giá</span>
        </Link>
      </nav>

      {/* Footer / Logout */}
      <div className="mt-auto px-2 space-y-1 pt-4 border-t border-emerald-800/30">
        <Link 
          to="#" 
          className="flex items-center gap-3 px-4 py-3 text-white hover:bg-[#12512c]/50 hover:text-[#6ffbbe] rounded-lg mx-2 transition-all"
        >
          <span className="material-symbols-outlined">help</span>
          <span className="text-sm font-semibold">Hỗ trợ</span>
        </Link>
        <button
          onClick={() => void logout()}
          className="w-[calc(100%-16px)] flex items-center gap-3 px-4 py-3 text-white hover:bg-red-900/30 hover:text-red-300 rounded-lg mx-2 transition-all text-left"
        >
          <span className="material-symbols-outlined text-red-400">logout</span>
          <span className="text-sm font-semibold text-red-200">Đăng xuất</span>
        </button>
      </div>
    </aside>
  );
}
