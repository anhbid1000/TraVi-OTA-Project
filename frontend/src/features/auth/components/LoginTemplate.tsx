import type { ReactNode } from 'react'
import { Building2, Compass, ShieldCheck, Users } from 'lucide-react'
import { Link } from 'react-router-dom'
import heroImage from '../../../assets/hero.png'

type LoginRole = 'KHACH_HANG' | 'DOI_TAC' | 'QUAN_TRI_VIEN'

type LoginTemplateProps = {
  activeRole: LoginRole
  children: ReactNode
  title?: string
  subtitle?: string
}

const roleMeta: Record<
  LoginRole,
  {
    label: string
    route: string
    icon: ReactNode
    accent: string
    accentSoft: string
  }
> = {
  KHACH_HANG: {
    label: 'Khách hàng',
    route: '/login',
    icon: <Users size={17} aria-hidden="true" />,
    accent: '#487fff',
    accentSoft: '#eef6ff',
  },
  DOI_TAC: {
    label: 'Đối tác',
    route: '/partner/login',
    icon: <Building2 size={17} aria-hidden="true" />,
    accent: '#006c49',
    accentSoft: '#eefbf6',
  },
  QUAN_TRI_VIEN: {
    label: 'Quản trị',
    route: '/admin/login',
    icon: <ShieldCheck size={17} aria-hidden="true" />,
    accent: '#334155',
    accentSoft: '#f1f5f9',
  },
}



export function LoginTemplate({
  activeRole,
  children,
  title = 'Chào mừng trở lại',
  subtitle = 'Đăng nhập để truy cập hệ thống TraVi',
}: LoginTemplateProps) {
  const activeMeta = roleMeta[activeRole]

  return (
    <main className="h-screen w-screen overflow-hidden bg-[#f8f9fb]">
      <div className="flex h-full w-full overflow-hidden bg-white shadow-[0_24px_60px_rgba(15,23,42,0.12)]">
        <section className="relative hidden overflow-hidden md:flex md:w-1/2">
          <div
            className="absolute inset-0 bg-cover bg-center bg-no-repeat"
            style={{ backgroundImage: `url(${heroImage})` }}
            aria-hidden="true"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-[#13202a]/92 via-[#13202a]/30 to-transparent" />
          <div className="relative z-10 flex flex-1 flex-col justify-end p-12 text-white">
            <div className="mb-8 flex items-center gap-3">
              <div className="flex h-12 w-12 items-center justify-center rounded-full border border-white/20 bg-white/10 text-[#d1fae5] shadow-lg shadow-black/10">
                <Compass size={25} strokeWidth={2.2} aria-hidden="true" />
              </div>
              <div>
                <p className="text-xs font-semibold uppercase tracking-[0.32em] text-white/70">
                  TraVi
                </p>
                <h1 className="text-3xl font-bold tracking-tight text-white">TraVi OTA</h1>
              </div>
            </div>

            <h2 className="max-w-xl text-5xl font-bold leading-[1.03] tracking-tight text-white">
              Hành trình của bạn
              <br />
              bắt đầu tại đây.
            </h2>

            <p className="mt-6 max-w-xl text-lg leading-8 text-white/85">
              Khám phá những trải nghiệm du lịch chất lượng cao, được cá nhân hóa cho người hiện
              đại. TraVi kết nối du khách và đối tác dịch vụ trên cùng một nền tảng.
            </p>
          </div>
        </section>

        <section className="relative flex w-full items-start justify-center overflow-y-auto bg-[#f8f9fb] px-6 py-8 sm:px-10 md:w-1/2 md:px-16 md:py-10 lg:items-center">
          <div className="absolute top-0 right-0 h-72 w-72 -translate-y-1/2 translate-x-1/3 rounded-full bg-[#d1fae5]/30 blur-3xl" />
          <div className="absolute bottom-0 left-0 h-80 w-80 -translate-x-1/4 translate-y-1/3 rounded-full bg-[#b1f2be]/25 blur-3xl" />

          <div className="relative z-10 w-full max-w-[540px] lg:max-h-[calc(100vh-5rem)]">
            <div className="mb-6 flex items-center gap-2 md:hidden">
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-[#003b1b] text-white shadow-lg shadow-[#003b1b]/20">
                <Compass size={20} strokeWidth={2.4} aria-hidden="true" />
              </div>
              <span className="text-2xl font-bold tracking-tight text-[#003b1b]">TraVi</span>
            </div>

            <div className="rounded-3xl border border-white/70 bg-white/90 p-6 shadow-[0_20px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl sm:p-8 md:p-10 lg:max-h-[calc(100vh-5rem)] lg:overflow-y-auto">
              <div className="mb-7 text-center">
                <div
                  className="mx-auto mb-4 inline-flex items-center gap-2 rounded-full px-4 py-1.5 text-xs font-semibold uppercase tracking-[0.22em]"
                  style={{ backgroundColor: activeMeta.accentSoft, color: activeMeta.accent }}
                >
                  <span className="text-[10px] opacity-70">Cổng</span>
                  {activeMeta.label}
                </div>

                <h2 className="mb-2 text-[clamp(1.85rem,4vw,2.25rem)] font-bold tracking-tight text-[#191c1e]">
                  {title}
                </h2>
                <p className="text-sm text-[#5b6470] sm:text-base">{subtitle}</p>
              </div>

              <div className="mb-6 grid grid-cols-3 gap-2 rounded-2xl bg-[#e7e8ea] p-1">
                {(Object.entries(roleMeta) as Array<[LoginRole, (typeof roleMeta)[LoginRole]]>).map(
                  ([role, meta]) => {
                    const isActive = role === activeRole;

                    return isActive ? (
                      <div
                        key={role}
                        className="flex items-center justify-center gap-2 rounded-xl bg-white px-3 py-2.5 text-sm font-semibold shadow-sm ring-1 ring-black/5"
                        style={{ color: meta.accent }}
                        aria-current="page"
                      >
                        {meta.icon}
                        <span>{meta.label}</span>
                      </div>
                    ) : (
                      <Link
                        key={role}
                        to={meta.route}
                        className="flex items-center justify-center gap-2 rounded-xl px-3 py-2.5 text-sm font-medium text-[#404941] transition-all hover:bg-white/60 hover:text-[#191c1e]"
                      >
                        {meta.icon}
                        <span>{meta.label}</span>
                      </Link>
                    );
                  }
                )}
              </div>

              {children}
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}




