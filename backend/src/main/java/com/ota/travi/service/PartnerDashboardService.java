package com.ota.travi.service;

import com.ota.travi.dto.response.PartnerDashboardAvailabilityResponse;
import com.ota.travi.dto.response.PartnerDashboardBookingItemResponse;
import com.ota.travi.dto.response.PartnerDashboardKpiResponse;
import com.ota.travi.dto.response.PartnerDashboardOverviewResponse;
import com.ota.travi.dto.response.PartnerDashboardRevenuePointResponse;
import com.ota.travi.dto.response.PartnerDashboardTopRoomResponse;
import com.ota.travi.entity.AnhPhong;
import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.entity.DonKhachSanChiTiet;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.Phong;
import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.enums.TrangThaiPhong;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.AnhPhongRepository;
import com.ota.travi.repository.DonKhachSanChiTietRepository;
import com.ota.travi.repository.DonKhachSanRepository;
import com.ota.travi.repository.HoSoKinhDoanhRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.PhongRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PartnerDashboardService {
    private static final int DEFAULT_RANGE_DAYS = 30;
    private static final int MIN_RANGE_DAYS = 7;
    private static final int MAX_RANGE_DAYS = 90;
    private static final int RECENT_BOOKINGS_LIMIT = 6;
    private static final int TOP_ROOMS_LIMIT = 3;

    private static final List<TrangThaiDon> DASHBOARD_BOOKING_STATUSES = List.of(
            TrangThaiDon.CHO_THANH_TOAN,
            TrangThaiDon.DA_THANH_TOAN,
            TrangThaiDon.DA_XAC_NHAN,
            TrangThaiDon.DANG_PHUC_VU,
            TrangThaiDon.DA_HOAN_THANH
    );

    private static final List<TrangThaiDon> ACTIVE_OCCUPANCY_STATUSES = List.of(
            TrangThaiDon.CHO_THANH_TOAN,
            TrangThaiDon.DA_THANH_TOAN,
            TrangThaiDon.DA_XAC_NHAN,
            TrangThaiDon.DANG_PHUC_VU
    );

    private final HoSoKinhDoanhRepository hoSoKinhDoanhRepository;
    private final KhachSanRepository khachSanRepository;
    private final PhongRepository phongRepository;
    private final DonKhachSanRepository donKhachSanRepository;
    private final DonKhachSanChiTietRepository donKhachSanChiTietRepository;
    private final AnhPhongRepository anhPhongRepository;

    public PartnerDashboardService(
            HoSoKinhDoanhRepository hoSoKinhDoanhRepository,
            KhachSanRepository khachSanRepository,
            PhongRepository phongRepository,
            DonKhachSanRepository donKhachSanRepository,
            DonKhachSanChiTietRepository donKhachSanChiTietRepository,
            AnhPhongRepository anhPhongRepository
    ) {
        this.hoSoKinhDoanhRepository = hoSoKinhDoanhRepository;
        this.khachSanRepository = khachSanRepository;
        this.phongRepository = phongRepository;
        this.donKhachSanRepository = donKhachSanRepository;
        this.donKhachSanChiTietRepository = donKhachSanChiTietRepository;
        this.anhPhongRepository = anhPhongRepository;
    }

    @Transactional(readOnly = true)
    public PartnerDashboardOverviewResponse getOverview(String partnerId, Integer days) {
        int rangeDays = normalizeRangeDays(days);

        HoSoKinhDoanh profile = hoSoKinhDoanhRepository.findByDoiTac_IdAndDeletedFalse(partnerId).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ban chua co ho so kinh doanh"));

        if (profile.getLoaiDichVu() != LoaiDichVu.KHACH_SAN) {
            throw new BusinessConflictException("Dashboard hien chi ho tro doi tac khach san");
        }

        KhachSan hotel = khachSanRepository.findByHoSoKinhDoanh_IdHoSo(profile.getIdHoSo()).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thong tin khach san"));

        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(rangeDays - 1L);
        LocalDate previousToDate = fromDate.minusDays(1);
        LocalDate previousFromDate = previousToDate.minusDays(rangeDays - 1L);

        LocalDateTime fetchFrom = previousFromDate.atStartOfDay();
        LocalDateTime fetchToExclusive = toDate.plusDays(1).atStartOfDay();

        List<DonKhachSan> dashboardOrders = donKhachSanRepository.findDashboardOrders(
                profile.getIdHoSo(),
                DASHBOARD_BOOKING_STATUSES,
                fetchFrom,
                fetchToExclusive
        );

        List<DonKhachSan> currentPeriodOrders = filterByDateRange(dashboardOrders, fromDate, toDate);
        List<DonKhachSan> previousPeriodOrders = filterByDateRange(dashboardOrders, previousFromDate, previousToDate);

        PartnerDashboardKpiResponse kpi = buildKpi(hotel.getIdTaiSan(), rangeDays, currentPeriodOrders, previousPeriodOrders);
        List<PartnerDashboardRevenuePointResponse> trend = buildRevenueTrend(fromDate, rangeDays, currentPeriodOrders);
        PartnerDashboardAvailabilityResponse availability = buildTodayAvailability(hotel.getIdTaiSan());
        List<PartnerDashboardBookingItemResponse> recentBookings = buildRecentBookings(profile.getIdHoSo());
        List<PartnerDashboardTopRoomResponse> topRooms = buildTopRooms(currentPeriodOrders);

        return new PartnerDashboardOverviewResponse(
                profile.getIdHoSo(),
                hotel.getIdTaiSan(),
                profile.getTenCoSo(),
                profile.getLoaiDichVu(),
                fromDate,
                toDate,
                kpi,
                trend,
                availability,
                recentBookings,
                topRooms
        );
    }

    private List<DonKhachSan> filterByDateRange(List<DonKhachSan> source, LocalDate fromDate, LocalDate toDate) {
        return source.stream()
                .filter(order -> order.getNgayTao() != null)
                .filter(order -> {
                    LocalDate bookingDate = order.getNgayTao().toLocalDate();
                    return !bookingDate.isBefore(fromDate) && !bookingDate.isAfter(toDate);
                })
                .toList();
    }

    private PartnerDashboardKpiResponse buildKpi(
            String hotelId,
            int rangeDays,
            List<DonKhachSan> currentPeriodOrders,
            List<DonKhachSan> previousPeriodOrders
    ) {
        double currentRevenue = currentPeriodOrders.stream()
                .mapToDouble(order -> safeDouble(order.getTongTienThanhToan()))
                .sum();
        double previousRevenue = previousPeriodOrders.stream()
                .mapToDouble(order -> safeDouble(order.getTongTienThanhToan()))
                .sum();

        int currentBookings = currentPeriodOrders.size();
        int previousBookings = previousPeriodOrders.size();

        int currentRoomNights = calculateSoldRoomNights(currentPeriodOrders);
        int previousRoomNights = calculateSoldRoomNights(previousPeriodOrders);
        int availableRoomNights = calculateAvailableRoomNights(hotelId, rangeDays);

        double currentOccupancy = percentage(currentRoomNights, availableRoomNights);
        double previousOccupancy = percentage(previousRoomNights, availableRoomNights);
        double currentAdr = currentRoomNights > 0 ? currentRevenue / currentRoomNights : 0.0;
        double previousAdr = previousRoomNights > 0 ? previousRevenue / previousRoomNights : 0.0;

        return new PartnerDashboardKpiResponse(
                roundTwo(currentRevenue),
                roundTwo(growthPercentage(currentRevenue, previousRevenue)),
                roundTwo(currentOccupancy),
                roundTwo(growthPercentage(currentOccupancy, previousOccupancy)),
                roundTwo(currentAdr),
                roundTwo(growthPercentage(currentAdr, previousAdr)),
                currentBookings,
                roundTwo(growthPercentage(currentBookings, previousBookings))
        );
    }

    private List<PartnerDashboardRevenuePointResponse> buildRevenueTrend(
            LocalDate fromDate,
            int rangeDays,
            List<DonKhachSan> currentPeriodOrders
    ) {
        Map<LocalDate, Double> revenueByDate = new LinkedHashMap<>();
        for (int i = 0; i < rangeDays; i++) {
            revenueByDate.put(fromDate.plusDays(i), 0.0);
        }

        currentPeriodOrders.forEach(order -> {
            if (order.getNgayTao() == null) {
                return;
            }
            LocalDate bookingDate = order.getNgayTao().toLocalDate();
            if (revenueByDate.containsKey(bookingDate)) {
                revenueByDate.put(
                        bookingDate,
                        revenueByDate.get(bookingDate) + safeDouble(order.getTongTienThanhToan())
                );
            }
        });

        return revenueByDate.entrySet().stream()
                .map(entry -> new PartnerDashboardRevenuePointResponse(entry.getKey(), roundTwo(entry.getValue())))
                .toList();
    }

    private PartnerDashboardAvailabilityResponse buildTodayAvailability(String hotelId) {
        LocalDate today = LocalDate.now();
        int totalRooms = calculateActiveRoomInventory(hotelId);
        int occupiedRooms = normalizeInt(donKhachSanChiTietRepository.sumBookedQuantityByHotelAndDateRange(
                hotelId,
                today,
                today.plusDays(1),
                ACTIVE_OCCUPANCY_STATUSES
        ));
        occupiedRooms = Math.min(occupiedRooms, totalRooms);
        int availableRooms = Math.max(totalRooms - occupiedRooms, 0);
        double occupiedRate = percentage(occupiedRooms, totalRooms);

        return new PartnerDashboardAvailabilityResponse(
                occupiedRooms,
                availableRooms,
                totalRooms,
                roundTwo(occupiedRate)
        );
    }

    private List<PartnerDashboardBookingItemResponse> buildRecentBookings(String businessProfileId) {
        return donKhachSanRepository
                .findByHoSoKinhDoanh_IdHoSoAndDeletedFalseAndTrangThaiInOrderByNgayTaoDesc(
                        businessProfileId,
                        DASHBOARD_BOOKING_STATUSES,
                        PageRequest.of(0, RECENT_BOOKINGS_LIMIT)
                )
                .getContent()
                .stream()
                .map(this::toRecentBookingItem)
                .toList();
    }

    private PartnerDashboardBookingItemResponse toRecentBookingItem(DonKhachSan order) {
        DonKhachSanChiTiet firstDetail = order.getChiTietDon().stream().findFirst().orElse(null);
        String roomType = firstDetail != null && firstDetail.getPhong() != null
                ? coalesce(firstDetail.getPhong().getLoaiPhong(), firstDetail.getTenPhongTaiThoiDiemDat(), "N/A")
                : "N/A";
        String guestName = coalesce(order.getTenNguoiDat(), order.getSdtNguoiDat(), "Khach le");

        return new PartnerDashboardBookingItemResponse(
                order.getId(),
                order.getMaDon(),
                guestName,
                order.getNgayCheckIn(),
                order.getNgayCheckOut(),
                roomType,
                roundTwo(safeDouble(order.getTongTienThanhToan())),
                order.getTrangThai()
        );
    }

    private List<PartnerDashboardTopRoomResponse> buildTopRooms(List<DonKhachSan> currentPeriodOrders) {
        Map<String, RoomDemandAccumulator> roomDemandMap = new LinkedHashMap<>();

        currentPeriodOrders.forEach(order -> order.getChiTietDon().forEach(detail -> {
            if (detail.getPhong() == null || detail.getPhong().getId() == null) {
                return;
            }
            String roomId = detail.getPhong().getId();
            roomDemandMap
                    .computeIfAbsent(roomId, id -> new RoomDemandAccumulator(detail.getPhong()))
                    .add(detail);
        }));

        return roomDemandMap.values().stream()
                .sorted(Comparator.comparingInt(RoomDemandAccumulator::getTotalBookings).reversed())
                .limit(TOP_ROOMS_LIMIT)
                .map(acc -> new PartnerDashboardTopRoomResponse(
                        acc.roomId,
                        acc.roomName,
                        acc.roomType,
                        acc.getTotalBookings(),
                        roundTwo(acc.getAveragePrice()),
                        resolveRoomThumbnail(acc.roomId)
                ))
                .toList();
    }

    private String resolveRoomThumbnail(String roomId) {
        return anhPhongRepository.findFirstByPhong_IdAndLaAnhDaiDienTrue(roomId)
                .map(AnhPhong::getDuongDanUrl)
                .orElseGet(() -> anhPhongRepository.findByPhong_Id(roomId).stream()
                        .map(AnhPhong::getDuongDanUrl)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null));
    }

    private int calculateSoldRoomNights(List<DonKhachSan> orders) {
        int soldRoomNights = 0;
        for (DonKhachSan order : orders) {
            for (DonKhachSanChiTiet detail : order.getChiTietDon()) {
                int quantity = normalizeInt(detail.getSoLuong());
                int nights = normalizeInt(detail.getSoDem());
                soldRoomNights += quantity * Math.max(nights, 1);
            }
        }
        return soldRoomNights;
    }

    private int calculateAvailableRoomNights(String hotelId, int rangeDays) {
        int totalInventory = calculateActiveRoomInventory(hotelId);
        return totalInventory * rangeDays;
    }

    private int calculateActiveRoomInventory(String hotelId) {
        List<Phong> activeRooms = phongRepository.findByKhachSan_IdTaiSanAndDeletedFalse(hotelId).stream()
                .filter(room -> EnumSet.of(TrangThaiPhong.SAN_SANG, TrangThaiPhong.DANG_BAN).contains(room.getTrangThai()))
                .toList();
        return activeRooms.stream()
                .map(Phong::getSoLuongPhong)
                .mapToInt(this::normalizeInt)
                .sum();
    }

    private int normalizeRangeDays(Integer days) {
        if (days == null) {
            return DEFAULT_RANGE_DAYS;
        }
        if (days < MIN_RANGE_DAYS) {
            return MIN_RANGE_DAYS;
        }
        if (days > MAX_RANGE_DAYS) {
            return MAX_RANGE_DAYS;
        }
        return days;
    }

    private int normalizeInt(Number value) {
        return value == null ? 0 : Math.max(value.intValue(), 0);
    }

    private double safeDouble(Double value) {
        return value == null ? 0.0 : value;
    }

    private double percentage(double part, double total) {
        if (total <= 0) {
            return 0.0;
        }
        return (part / total) * 100.0;
    }

    private double growthPercentage(double current, double previous) {
        if (previous <= 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((current - previous) / previous) * 100.0;
    }

    private String coalesce(String first, String second, String fallback) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return fallback;
    }

    private double roundTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class RoomDemandAccumulator {
        private final String roomId;
        private final String roomName;
        private final String roomType;
        private int totalBookings;
        private double weightedPriceSum;
        private int weightedUnits;

        private RoomDemandAccumulator(Phong room) {
            this.roomId = room.getId();
            this.roomName = room.getTenPhong();
            this.roomType = room.getLoaiPhong();
            this.totalBookings = 0;
            this.weightedPriceSum = 0.0;
            this.weightedUnits = 0;
        }

        private void add(DonKhachSanChiTiet detail) {
            int quantity = detail.getSoLuong() == null ? 0 : Math.max(detail.getSoLuong(), 0);
            double unitPrice = detail.getDonGiaTaiThoiDiemDat() == null
                    ? (detail.getPhong().getGiaCoBan() == null ? 0.0 : detail.getPhong().getGiaCoBan())
                    : detail.getDonGiaTaiThoiDiemDat();

            this.totalBookings += quantity;
            if (quantity > 0) {
                this.weightedPriceSum += unitPrice * quantity;
                this.weightedUnits += quantity;
            }
        }

        private int getTotalBookings() {
            return totalBookings;
        }

        private double getAveragePrice() {
            if (weightedUnits <= 0) {
                return 0.0;
            }
            return weightedPriceSum / weightedUnits;
        }
    }
}
