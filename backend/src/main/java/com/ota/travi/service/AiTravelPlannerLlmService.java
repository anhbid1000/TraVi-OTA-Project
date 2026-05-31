package com.ota.travi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ota.travi.dto.request.ItineraryRequest;
import com.ota.travi.dto.response.AiRecommendationItemResponse;
import com.ota.travi.dto.response.ItineraryResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AiTravelPlannerLlmService {
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public AiTravelPlannerLlmService(
            ObjectProvider<ChatClient.Builder> chatClientBuilder,
            @Value("${spring.ai.openai.api-key:}") String apiKey
    ) {
        ChatClient.Builder builder = chatClientBuilder.getIfAvailable();
        this.chatClient = builder == null ? null : builder.build();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
    }

    public Optional<ItineraryResponse> generateWithLlm(
            ItineraryRequest request,
            List<AiRecommendationItemResponse> recommendations
    ) {
        if (chatClient == null || apiKey == null || apiKey.isBlank() || apiKey.contains("dummy")) {
            return Optional.empty();
        }

        try {
            String content = chatClient.prompt()
                    .system("""
                            Ban la AI lap lich trinh du lich cho TraVi.
                            Chi tra ve JSON hop le theo schema:
                            {"city":"...","durationDays":3,"budgetLevel":"...","dailyPlans":[{"day":1,"activities":[{"timeWindow":"08:00 - 09:30","description":"...","recommendedAssetId":"...","assetType":"HOTEL|RESTAURANT|PLACE","assetName":"..."}]}]}
                            Khong boc JSON trong markdown.
                            """)
                    .user(buildPrompt(request, recommendations))
                    .call()
                    .content();

            String json = stripCodeFence(content);
            return Optional.of(objectMapper.readValue(json, ItineraryResponse.class));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private String buildPrompt(ItineraryRequest request, List<AiRecommendationItemResponse> recommendations) {
        StringBuilder builder = new StringBuilder();
        builder.append("Diem den: ").append(request.city()).append('\n');
        builder.append("So ngay: ").append(request.durationDays()).append('\n');
        builder.append("So nguoi: ").append(request.groupSize()).append('\n');
        builder.append("Ngan sach: ").append(request.budgetLevel()).append('\n');
        builder.append("Goi y da duoc collaborative filtering/scoring cua he thong xep hang:\n");
        recommendations.stream().limit(10).forEach(item -> builder
                .append("- ")
                .append(item.type()).append(" | ")
                .append(item.id()).append(" | ")
                .append(item.name()).append(" | ")
                .append(item.city()).append(" | diem ")
                .append(item.score())
                .append(" | ly do ")
                .append(String.join(", ", item.reasons()))
                .append('\n'));
        builder.append("Lap lich trinh co buoi sang, trua, chieu, toi; uu tien goi y co id neu phu hop.");
        return builder.toString();
    }

    private String stripCodeFence(String content) {
        if (content == null) {
            return "";
        }
        return content.replace("```json", "").replace("```", "").trim();
    }
}
