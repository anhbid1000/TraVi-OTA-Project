package com.ota.travi.service;

import com.ota.travi.dto.request.ItineraryRequest;
import com.ota.travi.dto.response.AiRecommendationItemResponse;
import com.ota.travi.dto.response.ItineraryResponse;
import com.ota.travi.dto.response.UserAiRecommendationResponse;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.repository.KhachHangRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TravelPlannerService {

    private final KhachHangRepository khachHangRepository;
    private final AiRecommendationService aiRecommendationService;
    private final AiTravelPlannerLlmService aiTravelPlannerLlmService;

    public TravelPlannerService(
            KhachHangRepository khachHangRepository,
            AiRecommendationService aiRecommendationService,
            AiTravelPlannerLlmService aiTravelPlannerLlmService
    ) {
        this.khachHangRepository = khachHangRepository;
        this.aiRecommendationService = aiRecommendationService;
        this.aiTravelPlannerLlmService = aiTravelPlannerLlmService;
    }

    public ItineraryResponse generateItinerary(String username, ItineraryRequest request) {
        KhachHang khachHang = khachHangRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Chỉ khách hàng mới có thể tạo lịch trình"));

        UserAiRecommendationResponse recommendations = aiRecommendationService.recommendForUser(
                khachHang.getUsername(),
                "ALL",
                request.city(),
                Math.max(request.durationDays() * 2, 6),
                true
        );

        List<AiRecommendationItemResponse> hotels = recommendations.recommendations().stream()
                .filter(item -> "HOTEL".equals(item.type()))
                .toList();
        List<AiRecommendationItemResponse> restaurants = recommendations.recommendations().stream()
                .filter(item -> "RESTAURANT".equals(item.type()))
                .toList();

        return aiTravelPlannerLlmService.generateWithLlm(request, recommendations.recommendations())
                .orElseGet(() -> generateRuleBasedItinerary(request, hotels, restaurants));
    }

    private ItineraryResponse generateRuleBasedItinerary(
            ItineraryRequest request,
            List<AiRecommendationItemResponse> hotels,
            List<AiRecommendationItemResponse> restaurants
    ) {
        List<ItineraryResponse.DailyPlan> dailyPlans = new ArrayList<>();
        for (int day = 1; day <= request.durationDays(); day++) {
            dailyPlans.add(new ItineraryResponse.DailyPlan(day, buildDailyActivities(day, hotels, restaurants)));
        }

        return new ItineraryResponse(request.city(), request.durationDays(), request.budgetLevel(), dailyPlans);
    }

    private List<ItineraryResponse.Activity> buildDailyActivities(
            int day,
            List<AiRecommendationItemResponse> hotels,
            List<AiRecommendationItemResponse> restaurants
    ) {
        List<ItineraryResponse.Activity> activities = new ArrayList<>();
        AiRecommendationItemResponse hotel = pickByDay(hotels, day);
        AiRecommendationItemResponse lunch = pickByDay(restaurants, day);
        AiRecommendationItemResponse dinner = pickByDay(restaurants, day + 1);

        if (day == 1 && hotel != null) {
            activities.add(toActivity("08:00 - 09:30", "Nhận phòng / gửi hành lý tại khách sạn phù hợp hồ sơ AI", hotel));
        }
        if (lunch != null) {
            activities.add(toActivity("11:30 - 13:00", "Ăn trưa theo khẩu vị và thành phố đang quan tâm", lunch));
        }
        activities.add(new ItineraryResponse.Activity(
                "14:00 - 17:00",
                "Tham quan địa điểm nổi bật tại " + (lunch != null ? lunch.city() : "điểm đến"),
                null,
                "PLACE",
                "Gợi ý điểm tham quan địa phương"
        ));
        if (dinner != null) {
            activities.add(toActivity("18:30 - 20:00", "Ăn tối tại nhà hàng được AI đề xuất", dinner));
        }

        return activities;
    }

    private AiRecommendationItemResponse pickByDay(List<AiRecommendationItemResponse> items, int day) {
        if (items.isEmpty()) {
            return null;
        }
        return items.get((day - 1) % items.size());
    }

    private ItineraryResponse.Activity toActivity(String timeWindow, String description, AiRecommendationItemResponse item) {
        return new ItineraryResponse.Activity(
                timeWindow,
                description,
                item.id(),
                item.type(),
                item.name()
        );
    }
}
