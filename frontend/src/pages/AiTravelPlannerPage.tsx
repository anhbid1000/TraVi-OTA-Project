import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { Bot, CalendarDays, MapPin, Users } from 'lucide-react'
import { Navbar } from '../components/layout/Navbar'
import { useItineraryPlanner, usePreferences } from '../features/ai'

export function AiTravelPlannerPage() {
  const { categories, userPrefs, loading: prefsLoading, error: prefsError, updatePrefs } = usePreferences()
  const { itinerary, loading, error, plan } = useItineraryPlanner()
  const [selectedPrefs, setSelectedPrefs] = useState<string[]>([])
  const [city, setCity] = useState('')
  const [durationDays, setDurationDays] = useState(3)
  const [groupSize, setGroupSize] = useState(2)
  const [budgetLevel, setBudgetLevel] = useState('Trung bình')
  const [message, setMessage] = useState('')

  const selectedIds = selectedPrefs.length ? selectedPrefs : userPrefs.map((pref) => pref.id)

  const togglePref = (id: string) => {
    setSelectedPrefs((current) => current.includes(id) ? current.filter((item) => item !== id) : [...current, id])
  }

  const savePrefs = async () => {
    if (!selectedIds.length) return true
    const ok = await updatePrefs(selectedIds)
    setMessage(ok ? 'Đã cập nhật sở thích để AI gợi ý chính xác hơn.' : '')
    return ok
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setMessage('')
    await savePrefs()
    await plan({ city, durationDays, groupSize, budgetLevel })
  }

  return (
    <div className="min-h-screen bg-surface text-on-surface">
      <Navbar />
      <main className="mx-auto max-w-7xl px-5 py-10 md:px-10">
        <div className="mb-8">
          <p className="flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-secondary">
            <Bot size={16} /> AI cá nhân hóa
          </p>
          <h1 className="mt-2 font-display text-4xl font-bold">Lập lịch trình thông minh</h1>
          <p className="mt-3 max-w-3xl text-on-surface-variant">
            Chọn sở thích và thông tin chuyến đi, TraVi sẽ dùng AI Profile + Recommendation Engine để gợi ý lịch trình phù hợp.
          </p>
        </div>

        <div className="grid gap-8 lg:grid-cols-[0.95fr_1.05fr]">
          <form onSubmit={handleSubmit} className="space-y-6 rounded-2xl border border-outline-variant/40 bg-white p-6 shadow-sm">
            <section>
              <h2 className="font-display text-xl font-bold">1. Sở thích của bạn</h2>
              {prefsLoading && <p className="mt-3 text-sm text-on-surface-variant">Đang tải danh mục sở thích...</p>}
              {prefsError && <p className="mt-3 rounded-lg bg-error-container/30 p-3 text-sm text-error">{prefsError}</p>}
              <div className="mt-4 space-y-4">
                {categories.map((category) => (
                  <div key={category.id}>
                    <p className="mb-2 text-sm font-semibold text-on-surface-variant">{category.tenDanhMuc}</p>
                    <div className="flex flex-wrap gap-2">
                      {category.danhSachSoThich.map((pref) => {
                        const active = selectedIds.includes(pref.id)
                        return (
                          <button
                            key={pref.id}
                            type="button"
                            onClick={() => togglePref(pref.id)}
                            className={`rounded-full border px-3 py-1.5 text-sm font-medium transition ${active ? 'border-primary bg-primary text-white' : 'border-outline-variant bg-white text-on-surface hover:bg-surface-container-low'}`}
                          >
                            {pref.tenSoThich}
                          </button>
                        )
                      })}
                    </div>
                  </div>
                ))}
              </div>
            </section>

            <section className="space-y-4 border-t border-outline-variant/40 pt-6">
              <h2 className="font-display text-xl font-bold">2. Thông tin chuyến đi</h2>
              <label className="block text-sm font-semibold">
                Điểm đến
                <input value={city} onChange={(e) => setCity(e.target.value)} required className="mt-2 w-full rounded-lg border border-outline-variant px-3 py-2" placeholder="VD: Đà Lạt" />
              </label>
              <div className="grid gap-4 md:grid-cols-3">
                <label className="block text-sm font-semibold">
                  Số ngày
                  <input type="number" min={1} max={14} value={durationDays} onChange={(e) => setDurationDays(Number(e.target.value))} className="mt-2 w-full rounded-lg border border-outline-variant px-3 py-2" />
                </label>
                <label className="block text-sm font-semibold">
                  Số người
                  <input type="number" min={1} value={groupSize} onChange={(e) => setGroupSize(Number(e.target.value))} className="mt-2 w-full rounded-lg border border-outline-variant px-3 py-2" />
                </label>
                <label className="block text-sm font-semibold">
                  Ngân sách
                  <select value={budgetLevel} onChange={(e) => setBudgetLevel(e.target.value)} className="mt-2 w-full rounded-lg border border-outline-variant px-3 py-2">
                    <option>Tiết kiệm</option>
                    <option>Trung bình</option>
                    <option>Cao cấp</option>
                  </select>
                </label>
              </div>
              <button disabled={loading} className="w-full rounded-lg bg-primary px-4 py-3 font-semibold text-white transition hover:bg-primary/90 disabled:opacity-60">
                {loading ? 'Đang tạo lịch trình...' : 'Tạo lịch trình AI'}
              </button>
              {message && <p className="text-sm font-medium text-primary">{message}</p>}
              {error && <p className="rounded-lg bg-error-container/30 p-3 text-sm text-error">{error}</p>}
            </section>
          </form>

          <section className="rounded-2xl border border-outline-variant/40 bg-white p-6 shadow-sm">
            <h2 className="font-display text-xl font-bold">Kết quả lịch trình</h2>
            {!itinerary && !loading && (
              <p className="mt-4 text-sm text-on-surface-variant">Nhập thông tin chuyến đi để AI tạo lịch trình cá nhân hóa.</p>
            )}
            {itinerary && (
              <div className="mt-6 space-y-5">
                <div className="rounded-xl bg-surface-container-low p-4">
                  <p className="flex items-center gap-2 font-semibold"><MapPin size={16} /> {itinerary.city}</p>
                  <p className="mt-2 flex items-center gap-2 text-sm text-on-surface-variant"><CalendarDays size={15} /> {itinerary.durationDays} ngày · {itinerary.budgetLevel}</p>
                  <p className="mt-1 flex items-center gap-2 text-sm text-on-surface-variant"><Users size={15} /> {groupSize} người</p>
                </div>
                {itinerary.dailyPlans.map((day) => (
                  <article key={day.day} className="rounded-xl border border-outline-variant/40 p-4">
                    <h3 className="font-display text-lg font-bold">Ngày {day.day}</h3>
                    <div className="mt-4 space-y-3">
                      {day.activities.map((activity) => (
                        <div key={`${day.day}-${activity.timeWindow}-${activity.description}`} className="rounded-lg bg-surface-container-lowest p-3">
                          <p className="text-xs font-bold uppercase tracking-wide text-secondary">{activity.timeWindow}</p>
                          <p className="mt-1 text-sm font-semibold">{activity.description}</p>
                          {activity.recommendedAssetId ? (
                            <Link className="mt-1 inline-block text-sm font-semibold text-primary" to={activity.assetType === 'HOTEL' ? `/hotels/${activity.recommendedAssetId}` : `/restaurants/${activity.recommendedAssetId}`}>
                              {activity.assetName}
                            </Link>
                          ) : (
                            <p className="mt-1 text-sm text-on-surface-variant">{activity.assetName}</p>
                          )}
                        </div>
                      ))}
                    </div>
                  </article>
                ))}
              </div>
            )}
          </section>
        </div>
      </main>
    </div>
  )
}
