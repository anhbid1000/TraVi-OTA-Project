// Trang thuộc module nhà hàng. File được đặt trong features/restaurants để gom UI, hook, service và type cùng miền nghiệp vụ.
import { useMemo, useState } from 'react'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { CalendarDays, Clock3, Users } from 'lucide-react'
import {
  DetailAmenities,
  DetailGallery,
  DetailHeader,
  DetailMapCard,
  DetailTopNav,
  SelectableItemCard,
  StickyBookingBar,
  WeatherAlertCard,
} from '../../detail/components'
import { useRestaurantDetail } from '../hooks/useRestaurantDetail'
import { formatVnd } from '../../../utils/display'
import { PublicReviewBlock } from '../../feedback-v2/components/public/PublicReviewBlock'

type MenuTag = 'Tất cả' | string

function toPositiveNumber(value: string | null, fallback: number) {
  const parsed = Number(value)
  if (Number.isNaN(parsed) || parsed < 1) {
    return fallback
  }
  return parsed
}


export function RestaurantDetailPage() {
  const navigate = useNavigate()
  const { id = '' } = useParams()
  const [searchParams] = useSearchParams()

  const date = searchParams.get('date') ?? ''
  const time = searchParams.get('time') ?? ''
  const guests = toPositiveNumber(searchParams.get('guests'), 4)

  const detailParams = useMemo(
    () => ({ date, time, guests }),
    [date, time, guests],
  )

  const {
    data: restaurant,
    loading,
    error,
    refetch,
  } = useRestaurantDetail(id, detailParams)

  const visibleMenuItems = useMemo(() => {
    if (!restaurant) {
      return []
    }

    const allItems = restaurant.menus.flatMap((menu) => menu.items)

    return allItems.filter(
      (item) => item.status !== 'TAM_HET' && item.status !== 'NGUNG_BAN' && !item.deleted,
    )
  }, [restaurant])

  const [selectedItems, setSelectedItems] = useState<Record<string, number>>({})
  const [activeMenuTag, setActiveMenuTag] = useState<MenuTag>('Tất cả')

  const menuTags = useMemo<MenuTag[]>(() => {
    const unique = new Set(visibleMenuItems.map((item) => item.category))
    return ['Tất cả', ...Array.from(unique)]
  }, [visibleMenuItems])

  const effectiveMenuTag = menuTags.includes(activeMenuTag) ? activeMenuTag : 'Tất cả'

  const filteredMenuItems = useMemo(() => {
    if (effectiveMenuTag === 'Tất cả') {
      return visibleMenuItems
    }
    return visibleMenuItems.filter((item) => item.category === effectiveMenuTag)
  }, [effectiveMenuTag, visibleMenuItems])

  const selectedPreOrderItems = useMemo(
    () => Object.values(selectedItems).reduce((sum, quantity) => sum + quantity, 0),
    [selectedItems],
  )

  const totalPrice = useMemo(() => {
    if (!restaurant) {
      return 0
    }

    return visibleMenuItems.reduce((sum, item) => {
      const quantity = selectedItems[item.id] || 0
      return sum + item.price * quantity
    }, 0)
  }, [restaurant, selectedItems, visibleMenuItems])

  if (loading) {
    return (
      <div className="min-h-screen bg-surface text-on-surface">
        <DetailTopNav active="restaurants" />
        <main className="mx-auto flex min-h-[60vh] w-full max-w-7xl items-center justify-center px-5 py-16 md:px-12">
          <div className="rounded-2xl border border-outline-variant/40 bg-white p-8 text-center shadow-sm">
            <p className="font-semibold text-on-surface">Đang tải chi tiết nhà hàng...</p>
          </div>
        </main>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-surface text-on-surface">
        <DetailTopNav active="restaurants" />
        <main className="mx-auto flex min-h-[60vh] w-full max-w-7xl items-center justify-center px-5 py-16 md:px-12">
          <div className="rounded-2xl border border-outline-variant/40 bg-white p-8 text-center shadow-sm">
            <h1 className="font-display text-2xl font-bold text-error">Không thể tải dữ liệu</h1>
            <p className="mt-2 text-on-surface-variant">{error}</p>
            <button
              type="button"
              onClick={() => {
                void refetch()
              }}
              className="mt-5 inline-flex cursor-pointer rounded-xl bg-primary px-5 py-2.5 text-sm font-semibold text-on-primary"
            >
              Thử lại
            </button>
          </div>
        </main>
      </div>
    )
  }

  if (!restaurant) {
    return (
      <div className="min-h-screen bg-surface text-on-surface">
        <DetailTopNav active="restaurants" />
        <main className="mx-auto flex min-h-[60vh] w-full max-w-7xl items-center justify-center px-5 py-16 md:px-12">
          <div className="rounded-2xl border border-outline-variant/40 bg-white p-8 text-center shadow-sm">
            <h1 className="font-display text-3xl font-bold text-primary">Không tìm thấy nhà hàng</h1>
            <p className="mt-2 text-on-surface-variant">Liên kết có thể không còn hợp lệ.</p>
            <Link
              to="/restaurants"
              className="mt-5 inline-flex cursor-pointer rounded-xl bg-primary px-5 py-2.5 text-sm font-semibold text-on-primary"
            >
              Quay lại danh sách nhà hàng
            </Link>
          </div>
        </main>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-surface text-on-surface antialiased">
      <DetailTopNav active="restaurants" />

      <main className="mx-auto w-full max-w-7xl px-5 py-8 pb-44 md:px-12">
        <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
          <nav className="text-sm text-on-surface-variant">
            <Link to="/" className="cursor-pointer hover:text-primary">
              Trang chủ
            </Link>
            <span className="mx-2">/</span>
            <Link to="/restaurants" className="cursor-pointer hover:text-primary">
              Nhà hàng
            </Link>
            <span className="mx-2">/</span>
            <span className="font-semibold text-on-surface">{restaurant.name}</span>
          </nav>
          <div className="rounded-xl bg-mint-green px-4 py-2 text-sm font-semibold text-on-secondary-container">
            {restaurant.tablesRemaining} bàn còn trống
          </div>
        </div>

        <div className="mt-6">
          <DetailGallery
            images={restaurant.images.map((image) => image.url)}
            title={restaurant.name}
          />
        </div>

        <DetailHeader
          title={restaurant.name}
          location={restaurant.location}
          rating={restaurant.rating}
          reviewCount={restaurant.reviewCount}
          category={restaurant.cuisine}

        />

        <div className="grid grid-cols-1 gap-8 lg:grid-cols-12">
          <section className="space-y-10 lg:col-span-8">
            <article>
              <h2 className="mb-4 font-display text-3xl font-bold text-primary">
                Trải nghiệm ẩm thực
              </h2>
              <p className="text-lg leading-relaxed text-on-surface-variant">
                {restaurant.description}
              </p>
            </article>

            <DetailAmenities title="Điểm nổi bật" amenities={restaurant.amenities} />

            <article className="grid gap-3 rounded-2xl border border-outline-variant/30 bg-white p-4 text-sm shadow-sm sm:grid-cols-3">
              <div className="flex items-center gap-2 text-on-surface-variant">
                <CalendarDays size={14} />
                <span>{date || 'Chưa chọn ngày'}</span>
              </div>
              <div className="flex items-center gap-2 text-on-surface-variant">
                <Clock3 size={14} />
                <span>{time || 'Chưa chọn giờ'}</span>
              </div>
              <div className="flex items-center gap-2 text-on-surface-variant">
                <Users size={14} />
                <span>{guests} khách</span>
              </div>
            </article>

            <section>
              <div className="mb-5 flex flex-wrap items-end justify-between gap-3">
                <h2 className="font-display text-3xl font-bold text-primary">Thực đơn đề xuất</h2>
                <div className="flex gap-2 overflow-x-auto border-b border-outline-variant/30 pb-1 text-sm">
                  {menuTags.map((tag) => (
                    <button
                      key={tag}
                      type="button"
                      onClick={() => setActiveMenuTag(tag)}
                      className={`cursor-pointer whitespace-nowrap border-b-2 px-1.5 pb-1 font-medium transition-colors ${
                        effectiveMenuTag === tag
                          ? 'border-primary text-primary'
                          : 'border-transparent text-on-surface-variant hover:text-primary'
                      }`}
                    >
                      {tag}
                    </button>
                  ))}
                </div>
              </div>

              {filteredMenuItems.length === 0 ? (
                <div className="rounded-2xl border border-outline-variant/30 bg-white p-8 text-center shadow-sm">
                  <p className="font-semibold text-on-surface">
                    Không có món trong nhóm {effectiveMenuTag}.
                  </p>
                  <p className="mt-2 text-sm text-on-surface-variant">
                    Thử chuyển sang nhóm khác để xem thêm món.
                  </p>
                </div>
              ) : (
                <div className="space-y-4">
                  {filteredMenuItems.map((menuItem) => {
                    const selectedQty = selectedItems[menuItem.id] || 0;
                    const canPreOrder = restaurant.allowPreOrder;

                    return (
                      <SelectableItemCard
                        key={menuItem.id}
                        image={menuItem.image}
                        title={menuItem.name}
                        meta={[menuItem.category]}
                        description={menuItem.description}
                        price={formatVnd(menuItem.price)}
                        statusLabel={selectedQty > 0 ? `Đã chọn ${selectedQty}` : 'Đang bán'}
                        statusTone={selectedQty > 0 ? 'success' : 'neutral'}
                        actionLabel={
                          canPreOrder ? (selectedQty > 0 ? 'Bỏ món' : 'Thêm món') : undefined
                        }
                        onAction={
                          canPreOrder
                            ? () => {
                                setSelectedItems((current) => {
                                  const next = { ...current };
                                  if (next[menuItem.id]) {
                                    delete next[menuItem.id];
                                  } else {
                                    next[menuItem.id] = 1;
                                  }
                                  return next;
                                });
                              }
                            : undefined
                        }
                        footer={
                          selectedQty > 0 ? (
                            <span className="inline-flex items-center gap-2 rounded-lg bg-surface-container-low px-2 py-1 text-xs">
                              <button
                                type="button"
                                onClick={() => {
                                  setSelectedItems((current) => {
                                    const next = { ...current };
                                    const nextQty = (next[menuItem.id] || 0) - 1;
                                    if (nextQty <= 0) {
                                      delete next[menuItem.id];
                                    } else {
                                      next[menuItem.id] = nextQty;
                                    }
                                    return next;
                                  });
                                }}
                                className="cursor-pointer rounded px-1 text-on-surface-variant hover:bg-surface-container"
                              >
                                -
                              </button>
                              <span className="min-w-4 text-center font-semibold">
                                {selectedQty}
                              </span>
                              <button
                                type="button"
                                onClick={() => {
                                  setSelectedItems((current) => ({
                                    ...current,
                                    [menuItem.id]: (current[menuItem.id] || 0) + 1,
                                  }));
                                }}
                                className="cursor-pointer rounded px-1 text-on-surface-variant hover:bg-surface-container"
                              >
                                +
                              </button>
                            </span>
                          ) : null
                        }
                      />
                    );
                  })}
                </div>
              )}
            </section>

            <PublicReviewBlock businessProfileId={restaurant.businessProfileId} />
          </section>

          <aside className="space-y-6 lg:col-span-4">
            <div className="lg:sticky lg:top-28 lg:space-y-6">
              <WeatherAlertCard
                latitude={restaurant.latitude}
                longitude={restaurant.longitude}
                date={date}
                locationName={restaurant.city}
              />

              <DetailMapCard
                address={restaurant.address}
                latitude={restaurant.latitude}
                longitude={restaurant.longitude}
              />

              <article className="rounded-2xl border border-outline-variant/30 bg-white p-6 shadow-sm">
                <h3 className="mb-4 font-display text-2xl font-semibold text-primary">
                  Thông tin thực tế
                </h3>
                <div className="space-y-3 text-sm text-on-surface-variant">
                  <p>
                    <span className="font-semibold text-on-surface">Giờ mở cửa:</span>{' '}
                    {restaurant.practicalInfo.openingHours}
                  </p>
                  <p>
                    <span className="font-semibold text-on-surface">Quy định trang phục:</span>{' '}
                    {restaurant.practicalInfo.dressCode}
                  </p>
                  <p>
                    <span className="font-semibold text-on-surface">Liên hệ:</span>{' '}
                    {restaurant.practicalInfo.contact}
                  </p>
                </div>
              </article>
            </div>
          </aside>
        </div>
      </main>

      <StickyBookingBar
        mode="restaurant"
        date={date}
        time={time}
        guests={guests}
        selectedPreOrderItems={selectedPreOrderItems}
        totalPrice={totalPrice}
        onAction={() => {
          navigate('/checkout', {
            state: {
              type: 'restaurant',
              restaurantId: restaurant.id,
              date,
              time,
              guests,
              selectedItems,
              totalPrice,
            },
          });
        }}
        actionLabel="Đặt bàn"
        actionDisabled={restaurant.tablesRemaining <= 0}
      />

      <footer className="border-t border-outline-variant/30 bg-surface-container-lowest">
        <div className="mx-auto grid w-full max-w-7xl gap-6 px-5 py-10 text-sm md:grid-cols-4 md:px-12">
          <div>
            <Link to="/" className="font-display text-xl font-bold text-primary">
              TraVi
            </Link>
            <p className="mt-2 text-on-surface-variant">© 2024 TraVi Vietnam. Bảo lưu mọi quyền.</p>
          </div>
          <div className="space-y-2 text-on-surface-variant">
            <p>Về chúng tôi</p>
            <p>Liên hệ</p>
          </div>
          <div className="space-y-2 text-on-surface-variant">
            <p>Chính sách bảo mật</p>
            <p>Điều khoản dịch vụ</p>
          </div>
          <div className="space-y-2 text-on-surface-variant">
            <p>VNPay</p>
            <p>MoMo</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
