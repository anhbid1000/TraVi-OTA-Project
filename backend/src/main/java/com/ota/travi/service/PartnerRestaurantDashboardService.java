package com.ota.travi.service;

import com.ota.travi.dto.response.PartnerRestaurantDashboardKpiResponse;
import com.ota.travi.dto.response.PartnerRestaurantDashboardOverviewResponse;
import com.ota.travi.dto.response.PartnerRestaurantDashboardPeakHourResponse;
import com.ota.travi.dto.response.PartnerRestaurantDashboardRecentOrderResponse;
import com.ota.travi.dto.response.PartnerRestaurantDashboardRevenuePointResponse;
import com.ota.travi.dto.response.PartnerRestaurantDashboardTopDishResponse;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.MonAn;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiBan;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.BanRepository;
import com.ota.travi.repository.ChiTietDonDatMonRepository;
import com.ota.travi.repository.DonNhaHangRepository;
import com.ota.travi.repository.HoSoKinhDoanhRepository;
import com.ota.travi.repository.MonAnRepository;
import com.ota.travi.repository.NhaHangRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PartnerRestaurantDashboardService {
    private static final int RECENT_ORDERS_LIMIT = 8;
    private static final int TOP_DISH_LIMIT = 3;
    private static final List<Integer> REVENUE_HOURS = List.of(8, 10, 12, 14, 16, 18, 20, 22);

    private static final List<TrangThaiDon> DASHBOARD_STATUSES = List.of(
            TrangThaiDon.CHO_THANH_TOAN,
            TrangThaiDon.DA_THANH_TOAN,
            TrangThaiDon.DA_XAC_NHAN,
            TrangThaiDon.DANG_PHUC_VU,
            TrangThaiDon.DA_HOAN_THANH
    );

    private static final List<TrangThaiDon> LIVE_TABLE_STATUSES = List.of(
            TrangThaiDon.CHO_THANH_TOAN,
            TrangThaiDon.DA_THANH_TOAN,
            TrangThaiDon.DA_XAC_NHAN,
            TrangThaiDon.DANG_PHUC_VU
    );

    private final HoSoKinhDoanhRepository hoSoKinhDoanhRepository;
    private final NhaHangRepository nhaHangRepository;
    private final DonNhaHangRepository donNhaHangRepository;
    private final BanRepository banRepository;
    private final ChiTietDonDatMonRepository chiTietDonDatMonRepository;
    private final MonAnRepository monAnRepository;

    public PartnerRestaurantDashboardService(
            HoSoKinhDoanhRepository hoSoKinhDoanhRepository,
            NhaHangRepository nhaHangRepository,
            DonNhaHangRepository donNhaHangRepository,
            BanRepository banRepository,
            ChiTietDonDatMonRepository chiTietDonDatMonRepository,
            MonAnRepository monAnRepository
    ) {
        this.hoSoKinhDoanhRepository = hoSoKinhDoanhRepository;
        this.nhaHangRepository = nhaHangRepository;
        this.donNhaHangRepository = donNhaHangRepository;
        this.banRepository = banRepository;
        this.chiTietDonDatMonRepository = chiTietDonDatMonRepository;
        this.monAnRepository = monAnRepository;
    }

    @Transactional(readOnly = true)
    public PartnerRestaurantDashboardOverviewResponse getOverview(String partnerId, String periodText) {
        DashboardPeriod period = DashboardPeriod.from(periodText);

        HoSoKinhDoanh profile = hoSoKinhDoanhRepository.findByDoiTac_IdAndDeletedFalse(partnerId).stream()
                .filter(item -> item.getLoaiDichVu() == LoaiDichVu.NHA_HANG)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ban chua co ho so nha hang"));

        NhaHang restaurant = nhaHangRepository.findByHoSoKinhDoanh_IdHoSo(profile.getIdHoSo()).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay du lieu nha hang"));

        LocalDateTime now = LocalDateTime.now();
        TimeRange currentRange = buildCurrentRange(period, now);
        TimeRange previousRange = currentRange.previousRange();

        List<DonNhaHang> currentOrders = donNhaHangRepository.findDashboardOrdersByRestaurantAndDateRange(
                restaurant.getIdTaiSan(),
                DASHBOARD_STATUSES,
                currentRange.from(),
                currentRange.to()
        );

        List<DonNhaHang> previousOrders = donNhaHangRepository.findDashboardOrdersByRestaurantAndDateRange(
                restaurant.getIdTaiSan(),
                DASHBOARD_STATUSES,
                previousRange.from(),
                previousRange.to()
        );

        PartnerRestaurantDashboardKpiResponse kpi = buildKpi(restaurant.getIdTaiSan(), now, currentOrders, previousOrders);

        List<PartnerRestaurantDashboardRevenuePointResponse> revenueByHour = buildRevenueByHour(currentOrders);
        List<PartnerRestaurantDashboardPeakHourResponse> peakHours = buildPeakHours(currentOrders);

        List<PartnerRestaurantDashboardRecentOrderResponse> recentOrders = donNhaHangRepository
                .findDistinctByBanDaGan_Ban_NhaHang_IdTaiSanAndDeletedFalseAndTrangThaiInOrderByNgayGioBatDauDesc(
                        restaurant.getIdTaiSan(),
                        DASHBOARD_STATUSES,
                        PageRequest.of(0, RECENT_ORDERS_LIMIT)
                )
                .getContent()
                .stream()
                .map(this::toRecentOrderResponse)
                .toList();

        List<PartnerRestaurantDashboardTopDishResponse> topDishes = buildTopDishes(
                restaurant.getIdTaiSan(),
                currentRange
        );

        return new PartnerRestaurantDashboardOverviewResponse(
                profile.getIdHoSo(),
                restaurant.getIdTaiSan(),
                restaurant.getTen(),
                period.name(),
                currentRange.from(),
                currentRange.to(),
                kpi,
                revenueByHour,
                peakHours,
                recentOrders,
                topDishes
        );
    }

    private PartnerRestaurantDashboardKpiResponse buildKpi(
            String restaurantId,
            LocalDateTime now,
            List<DonNhaHang> currentOrders,
            List<DonNhaHang> previousOrders
    ) {
        double currentRevenue = currentOrders.stream().mapToDouble(order -> safeDouble(order.getTongTienThanhToan())).sum();
        double previousRevenue = previousOrders.stream().mapToDouble(order -> safeDouble(order.getTongTienThanhToan())).sum();

        int currentGuests = currentOrders.stream().mapToInt(order -> normalizeInt(order.getSoNguoi(), 1)).sum();
        int previousGuests = previousOrders.stream().mapToInt(order -> normalizeInt(order.getSoNguoi(), 1)).sum();
        int guestDelta = currentGuests - previousGuests;

        double currentAov = currentOrders.isEmpty() ? 0.0 : currentRevenue / currentOrders.size();
        double previousAov = previousOrders.isEmpty() ? 0.0 : previousRevenue / previousOrders.size();

        int totalTables = banRepository.findByNhaHang_IdTaiSanAndTrangThaiAndDeletedFalse(
                restaurantId,
                TrangThaiBan.SAN_SANG
        ).size();
        int occupiedTables = normalizeInt(
                donNhaHangRepository.countOccupiedTablesAtTime(restaurantId, LIVE_TABLE_STATUSES, now),
                0
        );
        double occupancyRate = percentage(occupiedTables, totalTables);

        return new PartnerRestaurantDashboardKpiResponse(
                roundTwo(currentRevenue),
                roundTwo(growthPercentage(currentRevenue, previousRevenue)),
                roundTwo(occupancyRate),
                occupiedTables,
                totalTables,
                roundTwo(currentAov),
                roundTwo(growthPercentage(currentAov, previousAov)),
                currentGuests,
                guestDelta
        );
    }

    private List<PartnerRestaurantDashboardRevenuePointResponse> buildRevenueByHour(List<DonNhaHang> orders) {
        Map<Integer, Double> revenueByHour = new LinkedHashMap<>();
        REVENUE_HOURS.forEach(hour -> revenueByHour.put(hour, 0.0));

        orders.forEach(order -> {
            if (order.getNgayGioBatDau() == null) {
                return;
            }
            int slot = resolveHourSlot(order.getNgayGioBatDau().getHour());
            revenueByHour.put(slot, revenueByHour.get(slot) + safeDouble(order.getTongTienThanhToan()));
        });

        return revenueByHour.entrySet().stream()
                .map(item -> new PartnerRestaurantDashboardRevenuePointResponse(item.getKey(), roundTwo(item.getValue())))
                .toList();
    }

    private int resolveHourSlot(int hour) {
        if (hour <= 8) return 8;
        if (hour >= 22) return 22;
        int floor = hour - (hour % 2);
        return REVENUE_HOURS.contains(floor) ? floor : 20;
    }

    private List<PartnerRestaurantDashboardPeakHourResponse> buildPeakHours(List<DonNhaHang> orders) {
        int breakfast = 0;
        int lunch = 0;
        int dinner = 0;
        int lateNight = 0;

        for (DonNhaHang order : orders) {
            if (order.getNgayGioBatDau() == null) {
                continue;
            }
            int guests = normalizeInt(order.getSoNguoi(), 1);
            LocalTime time = order.getNgayGioBatDau().toLocalTime();
            if (!time.isBefore(LocalTime.of(18, 0)) && time.isBefore(LocalTime.of(21, 0))) {
                dinner += guests;
            } else if (!time.isBefore(LocalTime.of(11, 30)) && time.isBefore(LocalTime.of(13, 30))) {
                lunch += guests;
            } else if (!time.isBefore(LocalTime.of(21, 0))) {
                lateNight += guests;
            } else if (!time.isBefore(LocalTime.of(7, 0)) && time.isBefore(LocalTime.of(10, 0))) {
                breakfast += guests;
            } else {
                lunch += guests;
            }
        }

        int total = breakfast + lunch + dinner + lateNight;

        return List.of(
                new PartnerRestaurantDashboardPeakHourResponse("Bữa tối", "18:00 - 21:00", roundTwo(percentage(dinner, total))),
                new PartnerRestaurantDashboardPeakHourResponse("Bữa trưa", "11:30 - 13:30", roundTwo(percentage(lunch, total))),
                new PartnerRestaurantDashboardPeakHourResponse("Khuya", "21:00+", roundTwo(percentage(lateNight, total))),
                new PartnerRestaurantDashboardPeakHourResponse("Bữa sáng", "07:00 - 10:00", roundTwo(percentage(breakfast, total)))
        );
    }

    private PartnerRestaurantDashboardRecentOrderResponse toRecentOrderResponse(DonNhaHang order) {
        String tableCode = order.getBanDaGan().stream()
                .map(link -> link.getBan() == null ? null : link.getBan().getTenBan())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse("N/A");

        return new PartnerRestaurantDashboardRecentOrderResponse(
                order.getId(),
                order.getMaDon(),
                coalesce(order.getTenNguoiDat(), "Khach vang lai"),
                order.getNgayGioBatDau() == null ? order.getNgayTao() : order.getNgayGioBatDau(),
                tableCode,
                roundTwo(safeDouble(order.getTongTienThanhToan())),
                order.getTrangThai()
        );
    }

    private List<PartnerRestaurantDashboardTopDishResponse> buildTopDishes(String restaurantId, TimeRange range) {
        List<PartnerRestaurantDashboardTopDishResponse> aggregated = chiTietDonDatMonRepository.findTopDishesForDashboard(
                        restaurantId,
                        DASHBOARD_STATUSES,
                        range.from(),
                        range.to(),
                        PageRequest.of(0, TOP_DISH_LIMIT)
                )
                .stream()
                .map(item -> new PartnerRestaurantDashboardTopDishResponse(
                        item.getMonAnId(),
                        item.getTenMon(),
                        item.getTongSoLuong() == null ? 0L : item.getTongSoLuong(),
                        roundTwo(item.getGiaTrungBinh() == null ? 0.0 : item.getGiaTrungBinh()),
                        item.getAnhMon()
                ))
                .toList();

        if (!aggregated.isEmpty()) {
            return aggregated;
        }

        return monAnRepository.findByThucDon_NhaHang_IdTaiSanAndDeletedFalse(restaurantId).stream()
                .limit(TOP_DISH_LIMIT)
                .map(item -> new PartnerRestaurantDashboardTopDishResponse(
                        item.getId(),
                        item.getTenMon(),
                        0L,
                        roundTwo(item.getGiaBan() == null ? 0.0 : item.getGiaBan()),
                        item.getDuongDanUrl()
                ))
                .toList();
    }

    private TimeRange buildCurrentRange(DashboardPeriod period, LocalDateTime now) {
        return switch (period) {
            case TODAY -> new TimeRange(now.toLocalDate().atStartOfDay(), now.toLocalDate().plusDays(1).atStartOfDay());
            case WEEK -> new TimeRange(now.toLocalDate().minusDays(6).atStartOfDay(), now.toLocalDate().plusDays(1).atStartOfDay());
            case MONTH -> new TimeRange(now.toLocalDate().withDayOfMonth(1).atStartOfDay(), now.toLocalDate().plusDays(1).atStartOfDay());
        };
    }

    private double growthPercentage(double current, double previous) {
        if (previous <= 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((current - previous) / previous) * 100.0;
    }

    private int normalizeInt(Number number, int defaultValue) {
        if (number == null) {
            return defaultValue;
        }
        return Math.max(number.intValue(), 0);
    }

    private double safeDouble(Double number) {
        return number == null ? 0.0 : number;
    }

    private double percentage(double part, double total) {
        if (total <= 0) {
            return 0.0;
        }
        return (part / total) * 100.0;
    }

    private String coalesce(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private double roundTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private enum DashboardPeriod {
        TODAY,
        WEEK,
        MONTH;

        private static DashboardPeriod from(String raw) {
            if (raw == null || raw.isBlank()) {
                return TODAY;
            }
            try {
                return DashboardPeriod.valueOf(raw.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ValidationException("Period khong hop le. Ho tro: TODAY, WEEK, MONTH");
            }
        }
    }

    private record TimeRange(LocalDateTime from, LocalDateTime to) {
        private TimeRange previousRange() {
            Duration duration = Duration.between(from, to);
            return new TimeRange(from.minus(duration), from);
        }
    }
}
