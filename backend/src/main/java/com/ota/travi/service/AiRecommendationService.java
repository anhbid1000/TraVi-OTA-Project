package com.ota.travi.service;

import com.ota.travi.dto.request.AiRecommendationFeedbackRequest;
import com.ota.travi.dto.request.AiUserEventRequest;
import com.ota.travi.dto.response.AiRecommendationItemResponse;
import com.ota.travi.dto.response.UserAiRecommendationResponse;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.SoThich;
import com.ota.travi.enums.TrangThaiTaiSan;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.SoThichRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class AiRecommendationService {

    private static final int DEFAULT_LIMIT = 6;
    private static final int MAX_LIMIT = 20;
    private static final String TYPE_ALL = "ALL";
    private static final String TYPE_HOTEL = "HOTEL";
    private static final String TYPE_RESTAURANT = "RESTAURANT";
    private static final String EVENT_SEARCH = "SEARCH";
    private static final String EVENT_VIEW = "VIEW";
    private static final String EVENT_CLICK = "CLICK";
    private static final String EVENT_BOOK = "BOOK";
    private static final String EVENT_REVIEW = "REVIEW";
    private static final String EVENT_PROMOTION_VIEW = "PROMOTION_VIEW";

    private final KhachHangRepository khachHangRepository;
    private final KhachSanRepository khachSanRepository;
    private final NhaHangRepository nhaHangRepository;
    private final SoThichRepository soThichRepository;
    private final JdbcTemplate jdbcTemplate;
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final AiScoreCalculator aiScoreCalculator;
    private final TwoTowerRecommendationModel twoTowerRecommendationModel;

    public AiRecommendationService(
            KhachHangRepository khachHangRepository,
            KhachSanRepository khachSanRepository,
            NhaHangRepository nhaHangRepository,
            SoThichRepository soThichRepository,
            JdbcTemplate jdbcTemplate,
            CollaborativeFilteringService collaborativeFilteringService,
            AiScoreCalculator aiScoreCalculator,
            TwoTowerRecommendationModel twoTowerRecommendationModel
    ) {
        this.khachHangRepository = khachHangRepository;
        this.khachSanRepository = khachSanRepository;
        this.nhaHangRepository = nhaHangRepository;
        this.soThichRepository = soThichRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.collaborativeFilteringService = collaborativeFilteringService;
        this.aiScoreCalculator = aiScoreCalculator;
        this.twoTowerRecommendationModel = twoTowerRecommendationModel;
    }

    @Transactional
    public UserAiRecommendationResponse recommendForUser(String username, String type, String city, Integer limit, boolean strictCity) {
        KhachHang khachHang = khachHangRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Chỉ khách hàng mới có thể xem gợi ý cá nhân hóa"));

        String normalizedType = normalizeType(type);
        int safeLimit = normalizeLimit(limit);
        String hoSoAIId = getOrCreateAiProfileId(khachHang, List.of(), city, normalizedType);
        List<String> profileSignals = buildProfileSignals(khachHang);
        List<String> searchTokens = buildSearchTokens(khachHang, hoSoAIId);
        Set<String> clickedAssetIds = findClickedAssetIds(hoSoAIId);
        Set<String> bookedAssetIds = findBookedAssetIds(khachHang.getId());
        Map<String, Integer> collaborativeScores = collaborativeFilteringService.scoreCandidateAssets(khachHang.getId());
        capNhatHoSoAI(khachHang, searchTokens, city, normalizedType);

        List<AiRecommendationItemResponse> recommendations = new ArrayList<>();
        if (TYPE_ALL.equals(normalizedType) || TYPE_HOTEL.equals(normalizedType)) {
            recommendations.addAll(recommendHotels(khachHang, searchTokens, city, clickedAssetIds, bookedAssetIds, collaborativeScores, strictCity));
        }
        if (TYPE_ALL.equals(normalizedType) || TYPE_RESTAURANT.equals(normalizedType)) {
            recommendations.addAll(recommendRestaurants(khachHang, searchTokens, city, clickedAssetIds, bookedAssetIds, collaborativeScores, strictCity));
        }

        List<AiRecommendationItemResponse> ranked = recommendations.stream()
                .sorted(Comparator.comparing(AiRecommendationItemResponse::score).reversed())
                .limit(safeLimit)
                .toList();

        return new UserAiRecommendationResponse(
                buildProfileSummary(khachHang, profileSignals),
                profileSignals,
                ranked
        );
    }
    
    @Transactional
    public UserAiRecommendationResponse recommendForUser(String username, String type, String city, Integer limit) {
        return recommendForUser(username, type, city, limit, false);
    }

    @Transactional
    public void captureFeedback(String username, AiRecommendationFeedbackRequest request) {
        KhachHang khachHang = khachHangRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Chỉ khách hàng mới có thể gửi phản hồi gợi ý AI"));

        if (Boolean.TRUE.equals(request.clicked()) && request.idTaiSan() != null && !request.idTaiSan().isBlank()) {
            String hoSoAIId = getOrCreateAiProfileId(khachHang, List.of(), null, TYPE_ALL);
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM ho_so_ai_lich_su_click WHERE ho_so_ai_id = ? AND id_tai_san = ?",
                    Integer.class,
                    hoSoAIId,
                    request.idTaiSan()
            );
            if (count == null || count == 0) {
                jdbcTemplate.update(
                        "INSERT INTO ho_so_ai_lich_su_click (ho_so_ai_id, id_tai_san) VALUES (?, ?)",
                        hoSoAIId,
                        request.idTaiSan()
                    );
            }
        }

        recordBehaviorEvent(
                khachHang,
                resolveFeedbackEventType(request),
                null,
                request.idTaiSan(),
                request.feedbackRating(),
                request.feedbackNote(),
                null
        );
    }

    @Transactional
    public void captureUserEvent(String username, AiUserEventRequest request) {
        KhachHang khachHang = khachHangRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Chỉ khách hàng mới có thể cập nhật tín hiệu AI"));

        String eventType = normalizeEventType(request.eventType());
        String hoSoAIId = getOrCreateAiProfileId(khachHang, List.of(), request.city(), request.assetType());
        if (EVENT_SEARCH.equals(eventType)) {
            String keyword = firstNonBlank(request.keyword(), request.city(), request.assetType());
            if (keyword != null) {
                jdbcTemplate.update(
                        """
                        INSERT INTO ho_so_ai_lich_su_tim_kiem (
                            id, ho_so_ai_id, tu_khoa, thanh_pho, loai_dich_vu, nguon, searched_at
                        ) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                        """,
                        UUID.randomUUID().toString(),
                        hoSoAIId,
                        keyword,
                        request.city(),
                        request.assetType(),
                        request.source()
                );
            }
        }

        recordBehaviorEvent(
                khachHang,
                eventType,
                request.assetType(),
                request.assetId(),
                null,
                request.keyword(),
                request.metadata()
        );
    }

    public AiRecommendationItemResponse findTopDiscountRecommendation(String username) {
        KhachHang khachHang = khachHangRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Chỉ khách hàng mới có thể xem ưu đãi AI"));

        String hoSoAIId = getOrCreateAiProfileId(khachHang, List.of(), null, TYPE_HOTEL);
        List<String> searchTokens = buildSearchTokens(khachHang, hoSoAIId);
        Set<String> clickedAssetIds = findClickedAssetIds(hoSoAIId);
        Set<String> bookedAssetIds = findBookedAssetIds(khachHang.getId());
        Map<String, Integer> collaborativeScores = collaborativeFilteringService.scoreCandidateAssets(khachHang.getId());

        return khachSanRepository.findByTrangThai(TrangThaiTaiSan.SAN_SANG).stream()
                .map(hotel -> scoreHotel(khachHang, hotel, searchTokens, null, clickedAssetIds, bookedAssetIds, collaborativeScores))
                .filter(item -> item.reasons().stream().anyMatch(reason -> reason.contains("ưu đãi")))
                .max(Comparator.comparing(AiRecommendationItemResponse::score))
                .orElse(null);
    }

    private List<AiRecommendationItemResponse> recommendHotels(
            KhachHang khachHang,
            List<String> searchTokens,
            String city,
            Set<String> clickedAssetIds,
            Set<String> bookedAssetIds,
            Map<String, Integer> collaborativeScores,
            boolean strictCity
    ) {
        return khachSanRepository.findByTrangThai(TrangThaiTaiSan.SAN_SANG).stream()
                .filter(hotel -> !strictCity || matchesCity(hotel.getHoSoKinhDoanh(), city))
                .map(hotel -> scoreHotel(khachHang, hotel, searchTokens, city, clickedAssetIds, bookedAssetIds, collaborativeScores))
                .filter(item -> item != null)
                .toList();
    }

    private List<AiRecommendationItemResponse> recommendRestaurants(
            KhachHang khachHang,
            List<String> searchTokens,
            String city,
            Set<String> clickedAssetIds,
            Set<String> bookedAssetIds,
            Map<String, Integer> collaborativeScores,
            boolean strictCity
    ) {
        return nhaHangRepository.findByTrangThai(TrangThaiTaiSan.SAN_SANG).stream()
                .filter(restaurant -> !strictCity || matchesCity(restaurant.getHoSoKinhDoanh(), city))
                .map(restaurant -> scoreRestaurant(khachHang, restaurant, searchTokens, city, clickedAssetIds, bookedAssetIds, collaborativeScores))
                .filter(item -> item != null)
                .toList();
    }

    private AiRecommendationItemResponse scoreHotel(
            KhachHang khachHang,
            KhachSan hotel,
            List<String> searchTokens,
            String city,
            Set<String> clickedAssetIds,
            Set<String> bookedAssetIds,
            Map<String, Integer> collaborativeScores
    ) {
        TwoTowerRecommendationModel.TwoTowerScore twoTowerScore = twoTowerRecommendationModel.scoreHotel(
                khachHang,
                hotel,
                searchTokens,
                city,
                clickedAssetIds,
                bookedAssetIds,
                collaborativeScores
        );
        List<String> reasons = new ArrayList<>(twoTowerScore.reasons());
        HoSoKinhDoanh profile = hotel.getHoSoKinhDoanh();
        int score = twoTowerScore.score();

        if (matchesCity(profile, city)) {
            score += 8;
            reasons.add("Ưu tiên vì trùng thành phố bạn đang tìm");
        }

        int keywordScore = scoreByKeywords(searchTokens, hotel.getTen(), hotel.getLoaiKhachSan(), hotel.getMoTa(), profile);
        if (keywordScore > 0) {
            score += Math.min(keywordScore / 3, 10);
            reasons.add("Khớp thêm với từ khóa và sở thích gần đây");
        }

        if (hotel.getHangSao() != null && hotel.getHangSao() >= 4) {
            score += 4;
            reasons.add("Chất lượng lưu trú cao");
        }

        if (hotel.getGiaCoBan() != null && hotel.getGiaCoBan() > 0) {
            score += 2;
            reasons.add("Có giá tham khảo rõ ràng");
        }

        score = aiScoreCalculator.addBehaviorScore(score, hotel.getIdTaiSan(), clickedAssetIds, bookedAssetIds, collaborativeScores, reasons);

        Float bestDiscount = findBestRoomDiscount(hotel);
        if (bestDiscount != null && bestDiscount > 0) {
            score += Math.min(bestDiscount.intValue(), 8);
            reasons.add("Có ưu đãi phòng đang áp dụng");
        }

        return new AiRecommendationItemResponse(
                hotel.getIdTaiSan(),
                TYPE_HOTEL,
                hotel.getTen(),
                profile != null ? profile.getThanhPho() : null,
                profile != null ? profile.getQuanHuyen() : null,
                hotel.getGiaCoBan(),
                clampScore(score),
                defaultReasons(reasons)
        );
    }

    private AiRecommendationItemResponse scoreRestaurant(
            KhachHang khachHang,
            NhaHang restaurant,
            List<String> searchTokens,
            String city,
            Set<String> clickedAssetIds,
            Set<String> bookedAssetIds,
            Map<String, Integer> collaborativeScores
    ) {
        TwoTowerRecommendationModel.TwoTowerScore twoTowerScore = twoTowerRecommendationModel.scoreRestaurant(
                khachHang,
                restaurant,
                searchTokens,
                city,
                clickedAssetIds,
                bookedAssetIds,
                collaborativeScores
        );
        List<String> reasons = new ArrayList<>(twoTowerScore.reasons());
        HoSoKinhDoanh profile = restaurant.getHoSoKinhDoanh();
        int score = twoTowerScore.score();

        if (matchesCity(profile, city)) {
            score += 8;
            reasons.add("Ưu tiên vì trùng thành phố bạn đang tìm");
        }

        int keywordScore = scoreByKeywords(searchTokens, restaurant.getTen(), restaurant.getLoaiAmThuc(), restaurant.getMoTa(), profile);
        if (keywordScore > 0) {
            score += Math.min(keywordScore / 3, 10);
            reasons.add("Khớp thêm với khẩu vị và sở thích gần đây");
        }

        if (Boolean.TRUE.equals(restaurant.getCoDatBanTruoc())) {
            score += 4;
            reasons.add("Có hỗ trợ đặt bàn trước");
        }

        if (restaurant.getSucChua() != null && restaurant.getSucChua() >= 30) {
            score += 2;
            reasons.add("Phù hợp với nhóm khách đông");
        }

        score = aiScoreCalculator.addBehaviorScore(score, restaurant.getIdTaiSan(), clickedAssetIds, bookedAssetIds, collaborativeScores, reasons);

        return new AiRecommendationItemResponse(
                restaurant.getIdTaiSan(),
                TYPE_RESTAURANT,
                restaurant.getTen(),
                profile != null ? profile.getThanhPho() : null,
                profile != null ? profile.getQuanHuyen() : null,
                restaurant.getGiaCoBan(),
                clampScore(score),
                defaultReasons(reasons)
        );
    }

    private List<String> buildProfileSignals(KhachHang khachHang) {
        Set<String> signals = new LinkedHashSet<>();

        if (khachHang.getHangThanhVien() != null) {
            signals.add("Hạng thành viên: " + khachHang.getHangThanhVien().name());
        }

        if (khachHang.getTuKhoaGanDay() != null) {
            khachHang.getTuKhoaGanDay().stream()
                    .filter(value -> value != null && !value.isBlank())
                    .limit(5)
                    .forEach(value -> signals.add("Từ khóa gần đây: " + value));
        }

        soThichRepository.findByKhachHangId(khachHang.getId()).stream()
                .map(SoThich::getTenSoThich)
                .filter(value -> value != null && !value.isBlank())
                .limit(5)
                .forEach(value -> signals.add("Sở thích: " + value));

        Integer bookingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM don_dat_cho WHERE khach_hang_id = ? AND deleted = false",
                Integer.class,
                khachHang.getId()
        );
        if (bookingCount != null && bookingCount > 0) {
            signals.add("Đã có " + bookingCount + " đơn đặt chỗ");
        }

        if (signals.isEmpty()) {
            signals.add("Người dùng mới, ưu tiên dịch vụ phổ biến và có dữ liệu rõ ràng");
        }

        return List.copyOf(signals);
    }

    private List<String> buildSearchTokens(KhachHang khachHang, String hoSoAIId) {
        Set<String> tokens = new LinkedHashSet<>();

        if (khachHang.getTuKhoaGanDay() != null) {
            khachHang.getTuKhoaGanDay().forEach(value -> addToken(tokens, value));
        }

        soThichRepository.findByKhachHangId(khachHang.getId()).stream()
                .map(SoThich::getTenSoThich)
                .forEach(value -> addToken(tokens, value));

        findRecentSearchTokens(hoSoAIId).forEach(value -> addToken(tokens, value));
        findBookingTokens(khachHang.getId()).forEach(value -> addToken(tokens, value));

        return List.copyOf(tokens);
    }

    private int scoreByKeywords(List<String> tokens, String name, String type, String description, HoSoKinhDoanh profile) {
        if (tokens.isEmpty()) {
            return 0;
        }

        String haystack = normalizeText(String.join(" ",
                safeText(name),
                safeText(type),
                safeText(description),
                profile == null ? "" : safeText(profile.getTenCoSo()),
                profile == null ? "" : safeText(profile.getDiaChi()),
                profile == null ? "" : safeText(profile.getThanhPho()),
                profile == null ? "" : safeText(profile.getQuanHuyen())
        ));

        int matches = 0;
        for (String token : tokens) {
            if (!token.isBlank() && haystack.contains(token)) {
                matches++;
            }
        }

        return Math.min(matches * 12, 30);
    }

    private String buildProfileSummary(KhachHang khachHang, List<String> signals) {
        return "AI đang gợi ý dựa trên " + signals.size()
                + " tín hiệu từ hồ sơ và hành vi của khách hàng " + khachHang.getHoTen() + ".";
    }

    private void capNhatHoSoAI(KhachHang khachHang, List<String> searchTokens, String city, String type) {
        String hoSoAIId = getOrCreateAiProfileId(khachHang, searchTokens, city, type);
        jdbcTemplate.update("DELETE FROM ho_so_ai_tu_khoa_tim_kiem WHERE ho_so_ai_id = ?", hoSoAIId);
        for (String token : searchTokens) {
            jdbcTemplate.update(
                    "INSERT INTO ho_so_ai_tu_khoa_tim_kiem (ho_so_ai_id, tu_khoa) VALUES (?, ?)",
                    hoSoAIId,
                    token
            );
        }
    }

    private String getOrCreateAiProfileId(KhachHang khachHang, List<String> searchTokens, String city, String type) {
        List<String> profileIds = jdbcTemplate.queryForList(
                "SELECT id_ho_so_ai FROM ho_so_ai WHERE khach_hang_id = ?",
                String.class,
                khachHang.getId()
        );

        String hoSoAIId = profileIds.isEmpty() ? UUID.randomUUID().toString() : profileIds.get(0);
        String status = searchTokens.isEmpty() ? "DANG_LAP_HO_SO" : "DA_CA_NHAN_HOA";
        String profileSummary = buildSoThichTongHop(khachHang, city, type);

        if (profileIds.isEmpty()) {
            jdbcTemplate.update(
                    """
                    INSERT INTO ho_so_ai (
                        id_ho_so_ai, khach_hang_id, so_thich_tong_hop, trang_thai, created_at, updated_at
                    ) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    """,
                    hoSoAIId,
                    khachHang.getId(),
                    profileSummary,
                    status
            );
        } else {
            jdbcTemplate.update(
                    """
                    UPDATE ho_so_ai
                    SET so_thich_tong_hop = ?, trang_thai = ?, updated_at = CURRENT_TIMESTAMP
                    WHERE id_ho_so_ai = ?
                    """,
                    profileSummary,
                    status,
                    hoSoAIId
            );
        }

        return hoSoAIId;
    }

    private String buildSoThichTongHop(KhachHang khachHang, String city, String type) {
        return "{"
                + "\"idKhachHang\":\"" + safeText(khachHang.getId()) + "\","
                + "\"thanhPho\":\"" + safeText(city) + "\","
                + "\"loaiDichVu\":\"" + safeText(type) + "\""
                + "}";
    }

    private String normalizeType(String type) {
        if (type == null || type.isBlank()) {
            return TYPE_ALL;
        }

        String normalized = type.trim().toUpperCase(Locale.ROOT);
        if (!TYPE_ALL.equals(normalized) && !TYPE_HOTEL.equals(normalized) && !TYPE_RESTAURANT.equals(normalized)) {
            throw new RuntimeException("Loại gợi ý không hợp lệ. Giá trị cho phép: ALL, HOTEL, RESTAURANT.");
        }

        return normalized;
    }

    private String normalizeEventType(String eventType) {
        if (eventType == null || eventType.isBlank()) {
            return EVENT_VIEW;
        }

        String normalized = eventType.trim().toUpperCase(Locale.ROOT);
        if (!List.of(EVENT_SEARCH, EVENT_VIEW, EVENT_CLICK, EVENT_BOOK, EVENT_REVIEW, EVENT_PROMOTION_VIEW).contains(normalized)) {
            throw new RuntimeException("Loại sự kiện AI không hợp lệ");
        }

        return normalized;
    }

    private String resolveFeedbackEventType(AiRecommendationFeedbackRequest request) {
        if (Boolean.TRUE.equals(request.booked())) {
            return EVENT_BOOK;
        }
        if (request.feedbackRating() != null || (request.feedbackNote() != null && !request.feedbackNote().isBlank())) {
            return EVENT_REVIEW;
        }
        if (Boolean.TRUE.equals(request.clicked())) {
            return EVENT_CLICK;
        }
        return EVENT_VIEW;
    }

    private void recordBehaviorEvent(
            KhachHang khachHang,
            String eventType,
            String assetType,
            String assetId,
            Integer feedbackRating,
            String feedbackNote,
            String metadata
    ) {
        String hoSoAIId = getOrCreateAiProfileId(khachHang, List.of(), null, assetType);
        jdbcTemplate.update(
                """
                INSERT INTO ho_so_ai_hanh_vi (
                    id, ho_so_ai_id, loai_su_kien, loai_tai_san, id_tai_san,
                    diem_phan_hoi, ghi_chu, metadata, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                UUID.randomUUID().toString(),
                hoSoAIId,
                eventType,
                assetType,
                assetId,
                feedbackRating,
                feedbackNote,
                metadata
        );
    }

    private List<String> findRecentSearchTokens(String hoSoAIId) {
        return jdbcTemplate.queryForList(
                """
                SELECT tu_khoa
                FROM ho_so_ai_lich_su_tim_kiem
                WHERE ho_so_ai_id = ?
                ORDER BY searched_at DESC
                LIMIT 10
                """,
                String.class,
                hoSoAIId
        );
    }

    private List<String> findBookingTokens(String khachHangId) {
        return jdbcTemplate.queryForList(
                """
                SELECT CONCAT_WS(' ', hskd.ten_co_so, hskd.thanh_pho, hskd.quan_huyen, hskd.loai_dich_vu)
                FROM don_dat_cho ddc
                JOIN ho_so_kinh_doanh hskd ON hskd.id_ho_so = ddc.ho_so_kinh_doanh_id
                WHERE ddc.khach_hang_id = ? AND ddc.deleted = false
                ORDER BY ddc.ngay_tao DESC
                LIMIT 10
                """,
                String.class,
                khachHangId
        );
    }

    private Set<String> findClickedAssetIds(String hoSoAIId) {
        Set<String> ids = new LinkedHashSet<>(jdbcTemplate.queryForList(
                "SELECT DISTINCT id_tai_san FROM ho_so_ai_lich_su_click WHERE ho_so_ai_id = ? AND id_tai_san IS NOT NULL",
                String.class,
                hoSoAIId
        ));
        ids.addAll(jdbcTemplate.queryForList(
                """
                SELECT DISTINCT id_tai_san
                FROM ho_so_ai_hanh_vi
                WHERE ho_so_ai_id = ? AND loai_su_kien IN ('CLICK', 'VIEW', 'PROMOTION_VIEW') AND id_tai_san IS NOT NULL
                """,
                String.class,
                hoSoAIId
        ));
        return ids;
    }

    private Set<String> findBookedAssetIds(String khachHangId) {
        return new LinkedHashSet<>(jdbcTemplate.queryForList(
                """
                SELECT DISTINCT ts.id_tai_san
                FROM don_dat_cho ddc
                JOIN tai_san ts ON ts.ho_so_kinh_doanh_id = ddc.ho_so_kinh_doanh_id
                WHERE ddc.khach_hang_id = ? AND ddc.deleted = false
                """,
                String.class,
                khachHangId
        ));
    }

    private Float findBestRoomDiscount(KhachSan hotel) {
        if (hotel.getDanhSachPhong() == null || hotel.getDanhSachPhong().isEmpty()) {
            return 0.0f;
        }

        return hotel.getDanhSachPhong().stream()
                .filter(room -> room.getPhanTramGiamGia() != null && room.getPhanTramGiamGia() > 0)
                .map(room -> room.getPhanTramGiamGia())
                .max(Float::compareTo)
                .orElse(0.0f);
    }

    private String firstNonBlank(String first, String second, String third) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        if (third != null && !third.isBlank()) {
            return third;
        }
        return null;
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }

        if (limit < 1 || limit > MAX_LIMIT) {
            throw new RuntimeException("Số lượng gợi ý phải từ 1 đến " + MAX_LIMIT + ".");
        }

        return limit;
    }

    private boolean matchesCity(HoSoKinhDoanh profile, String city) {
        if (profile == null || city == null || city.isBlank()) {
            return false;
        }

        return normalizeText(profile.getThanhPho()).contains(normalizeText(city));
    }

    private List<String> defaultReasons(List<String> reasons) {
        if (!reasons.isEmpty()) {
            return List.copyOf(reasons);
        }

        return List.of("Dịch vụ đang sẵn sàng và phù hợp để khám phá");
    }

    private void addToken(Set<String> tokens, String value) {
        String token = normalizeText(value);
        if (!token.isBlank()) {
            tokens.add(token);
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        return normalized.trim();
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private int clampScore(int score) {
        return Math.min(Math.max(score, 0), 100);
    }
}
