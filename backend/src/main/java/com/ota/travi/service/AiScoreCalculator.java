package com.ota.travi.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AiScoreCalculator {
    public int addBehaviorScore(
            int score,
            String assetId,
            Set<String> clickedAssetIds,
            Set<String> bookedAssetIds,
            Map<String, Integer> collaborativeScores,
            List<String> reasons
    ) {
        int nextScore = score;
        if (bookedAssetIds.contains(assetId)) {
            nextScore += 18;
            reasons.add("Tương đồng với dịch vụ bạn đã từng đặt");
        } else if (clickedAssetIds.contains(assetId)) {
            nextScore += 12;
            reasons.add("Bạn đã từng quan tâm dịch vụ này");
        }

        int collaborativeBoost = calculateCollaborativeBoost(collaborativeScores.getOrDefault(assetId, 0));
        if (collaborativeBoost > 0) {
            nextScore += collaborativeBoost;
            reasons.add("Người dùng có hành vi tương tự cũng quan tâm");
        }

        return clampScore(nextScore);
    }

    public int calculateCollaborativeBoost(int rawScore) {
        return Math.min(Math.max(rawScore, 0) * 8, 24);
    }

    public int clampScore(int score) {
        return Math.min(Math.max(score, 0), 100);
    }
}
