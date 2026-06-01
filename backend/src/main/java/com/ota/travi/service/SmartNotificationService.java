package com.ota.travi.service;

import com.ota.travi.dto.response.ThongBaoNguCanhResponse;
import com.ota.travi.dto.response.WeatherForecastResponse;
import com.ota.travi.dto.response.AiRecommendationItemResponse;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.repository.DonKhachSanRepository;
import com.ota.travi.repository.DonNhaHangRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.ThongBaoNguCanhRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SmartNotificationService {

    private final KhachHangRepository khachHangRepository;
    private final DonKhachSanRepository donKhachSanRepository;
    private final DonNhaHangRepository donNhaHangRepository;
    private final WeatherService weatherService;
    private final ThongBaoNguCanhRepository thongBaoNguCanhRepository;
    private final AiRecommendationService aiRecommendationService;

    public SmartNotificationService(
            KhachHangRepository khachHangRepository,
            DonKhachSanRepository donKhachSanRepository,
            DonNhaHangRepository donNhaHangRepository,
            WeatherService weatherService,
            ThongBaoNguCanhRepository thongBaoNguCanhRepository,
            AiRecommendationService aiRecommendationService
    ) {
        this.khachHangRepository = khachHangRepository;
        this.donKhachSanRepository = donKhachSanRepository;
        this.donNhaHangRepository = donNhaHangRepository;
        this.weatherService = weatherService;
        this.thongBaoNguCanhRepository = thongBaoNguCanhRepository;
        this.aiRecommendationService = aiRecommendationService;
    }

    public List<ThongBaoNguCanhResponse> generateSmartNotifications(String username) {
        KhachHang khachHang = khachHangRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Chỉ khách hàng mới nhận được thông báo thông minh"));

        List<ThongBaoNguCanhResponse> notifications = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        List<DonDatCho> activeBookings = new ArrayList<>();
        activeBookings.addAll(donKhachSanRepository.findByKhachHang_UsernameAndDeletedFalse(khachHang.getUsername()));
        activeBookings.addAll(donNhaHangRepository.findByKhachHang_UsernameAndDeletedFalse(khachHang.getUsername()));

        activeBookings = activeBookings.stream()
                .filter(don -> don.getTrangThai() == TrangThaiDon.DA_XAC_NHAN
                        || don.getTrangThai() == TrangThaiDon.CHO_THANH_TOAN
                        || don.getTrangThai() == TrangThaiDon.DA_THANH_TOAN)
                .toList();

        for (DonDatCho booking : activeBookings) {
            LocalDateTime serviceTime = resolveServiceTime(booking);
            if (serviceTime != null && !serviceTime.isBefore(now) && !serviceTime.isAfter(now.plusDays(3))) {
                notifications.add(new ThongBaoNguCanhResponse(
                        "auto-" + booking.getId(),
                        "BOOKING_REMINDER",
                        "Bạn có đặt chỗ sắp tới tại " + booking.getHoSoKinhDoanh().getTenCoSo()
                                + " vào " + serviceTime + " - Mã: " + booking.getMaDon(),
                        "MEDIUM",
                        serviceTime,
                        "BOOKING"
                ));

                addWeatherWarning(notifications, booking, serviceTime);
            }
        }

        thongBaoNguCanhRepository.findTop10ByThoiGianHieuLucAfterOrderByThoiGianHieuLucAsc(now)
                .forEach(tb -> notifications.add(new ThongBaoNguCanhResponse(
                        tb.getIdThongBao(),
                        tb.getLoaiNguCanh(),
                        tb.getNoiDung(),
                        tb.getMucDo(),
                        tb.getThoiGianHieuLuc(),
                        "SYSTEM"
                )));

        addRecommendationNotification(notifications, khachHang.getUsername(), now);
        addPromotionNotification(notifications, khachHang.getUsername(), now);

        notifications.sort(Comparator.comparing(ThongBaoNguCanhResponse::thoiGianHieuLuc));
        return notifications;
    }

    private void addRecommendationNotification(
            List<ThongBaoNguCanhResponse> notifications,
            String username,
            LocalDateTime now
    ) {
        try {
            AiRecommendationItemResponse topRecommendation = aiRecommendationService
                    .recommendForUser(username, "ALL", null, 1)
                    .recommendations()
                    .stream()
                    .findFirst()
                    .orElse(null);
            if (topRecommendation != null && topRecommendation.score() >= 70) {
                notifications.add(new ThongBaoNguCanhResponse(
                        "recommendation-" + topRecommendation.id(),
                        "RECOMMENDATION",
                        "AI đề xuất " + topRecommendation.name()
                                + " vì phù hợp với sở thích và hành vi gần đây của bạn.",
                        "LOW",
                        now.plusHours(2),
                        "RECOMMENDATION"
                ));
            }
        } catch (Exception ignored) {
            // Lỗi gợi ý không được làm hỏng luồng thông báo booking/thời tiết.
        }
    }

    private void addPromotionNotification(
            List<ThongBaoNguCanhResponse> notifications,
            String username,
            LocalDateTime now
    ) {
        try {
            AiRecommendationItemResponse discountRecommendation = aiRecommendationService.findTopDiscountRecommendation(username);
            if (discountRecommendation != null) {
                notifications.add(new ThongBaoNguCanhResponse(
                        "promotion-" + discountRecommendation.id(),
                        "PROMOTION",
                        "Có ưu đãi lưu trú phù hợp với hồ sơ của bạn tại " + discountRecommendation.name() + ".",
                        "MEDIUM",
                        now.plusHours(1),
                        "PROMOTION"
                ));
            }
        } catch (Exception ignored) {
            // Lỗi ưu đãi không được làm hỏng luồng thông báo chính.
        }
    }

    private void addWeatherWarning(
            List<ThongBaoNguCanhResponse> notifications,
            DonDatCho booking,
            LocalDateTime serviceTime
    ) {
        if (booking.getHoSoKinhDoanh().getViDo() == null || booking.getHoSoKinhDoanh().getKinhDo() == null) {
            return;
        }

        try {
            WeatherForecastResponse weather = weatherService.getForecast(
                    booking.getHoSoKinhDoanh().getViDo(),
                    booking.getHoSoKinhDoanh().getKinhDo(),
                    serviceTime.toLocalDate()
            );
            if ("cloud-rain".equals(weather.icon()) || "cloud-lightning".equals(weather.icon())) {
                notifications.add(new ThongBaoNguCanhResponse(
                        "weather-" + booking.getId(),
                        "WEATHER_WARNING",
                        "Thời tiết tại điểm đến dự báo " + weather.condition()
                                + ". Hãy chuẩn bị " + weather.warningMessage(),
                        "HIGH",
                        serviceTime,
                        "WEATHER"
                ));
            }
        } catch (Exception ignored) {
            // Weather failures should not block booking reminders.
        }
    }

    private LocalDateTime resolveServiceTime(DonDatCho booking) {
        if (booking instanceof DonKhachSan hotelBooking && hotelBooking.getNgayCheckIn() != null) {
            return hotelBooking.getNgayCheckIn().atTime(
                    hotelBooking.getGioNhanPhongDuKien() != null
                            ? hotelBooking.getGioNhanPhongDuKien()
                            : LocalTime.of(14, 0)
            );
        }

        if (booking instanceof DonNhaHang restaurantBooking) {
            return restaurantBooking.getNgayGioBatDau();
        }

        return booking.getNgayTao();
    }
}
