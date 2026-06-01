import { Link } from 'react-router-dom';
import { Navbar } from '../../../components/layout/Navbar';

type CustomerFeedbackLayoutProps = {
  active: 'bookings' | 'complaints' | 'loyalty';
  title: string;
  subtitle?: string;
  children: React.ReactNode;
};

export function CustomerFeedbackLayout({ active, title, subtitle, children }: CustomerFeedbackLayoutProps) {
  return (
    <div className="flex min-h-screen flex-col bg-background font-sans text-on-background antialiased">
      <Navbar />
      
      <div className="flex flex-1">
        <aside className="sticky top-20 z-40 hidden h-[calc(100vh-5rem)] w-64 flex-col border-r border-outline-variant/30 bg-surface md:flex">
          <div className="flex h-full flex-col space-y-2 p-2">
            <div className="mb-4 px-4 py-8">
              <span className="font-display text-xl font-bold text-primary">TraVi Dashboard</span>
              <p className="text-sm text-on-surface-variant">Customer Portal</p>
            </div>
            <nav className="flex-1 space-y-1">
              <Link className="flex items-center gap-3 rounded-xl px-4 py-3 text-on-surface-variant transition hover:bg-surface-container-high" to="/">
                <span className="material-symbols-outlined">dashboard</span>
                <span className="text-sm font-semibold">Tổng quan</span>
              </Link>
              <Link className={`flex items-center gap-3 rounded-xl px-4 py-3 transition ${active === 'bookings' ? 'bg-secondary-container text-on-secondary-container font-bold' : 'text-on-surface-variant hover:bg-surface-container-high'}`} to="/user/bookings-v2">
                <span className="material-symbols-outlined">calendar_month</span>
                <span className="text-sm font-semibold">Chuyến đi</span>
              </Link>
              <Link className={`flex items-center gap-3 rounded-xl px-4 py-3 transition ${active === 'complaints' ? 'bg-secondary-container text-on-secondary-container font-bold' : 'text-on-surface-variant hover:bg-surface-container-high'}`} to="/user/complaints">
                <span className="material-symbols-outlined">report_problem</span>
                <span className="text-sm font-semibold">Khiếu nại của tôi</span>
              </Link>
              <Link className={`flex items-center gap-3 rounded-xl px-4 py-3 transition ${active === 'loyalty' ? 'bg-secondary-container text-on-secondary-container font-bold' : 'text-on-surface-variant hover:bg-surface-container-high'}`} to="/user/loyalty">
                <span className="material-symbols-outlined">workspace_premium</span>
                <span className="text-sm font-semibold">Loyalty & voucher</span>
              </Link>
              <Link className="flex items-center gap-3 rounded-xl px-4 py-3 text-on-surface-variant transition hover:bg-surface-container-high" to="#settings">
                <span className="material-symbols-outlined">settings</span>
                <span className="text-sm font-semibold">Cài đặt</span>
              </Link>
            </nav>
          </div>
        </aside>

        <main className="flex-1 min-h-screen">
          <header className="flex h-20 items-center justify-between border-b border-outline-variant/30 px-5 md:px-12 bg-white/60 backdrop-blur-xl">
            <div>
              <h1 className="font-display text-2xl font-bold text-primary">{title}</h1>
              {subtitle && <p className="mt-1 text-sm text-on-surface-variant">{subtitle}</p>}
            </div>
          </header>
          <section className="mx-auto max-w-7xl space-y-8 p-5 md:p-12">{children}</section>
        </main>
      </div>
    </div>
  );
}
