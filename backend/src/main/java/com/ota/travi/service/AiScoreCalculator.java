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
            reasons.add("Tuong dong voi dich vu ban da tung dat");
        } else if (clickedAssetIds.contains(assetId)) {
            nextScore += 12;
            reasons.add("Ban da tung quan tam dich vu nay");
        }

        int collaborativeBoost = calculateCollaborativeBoost(collaborativeScores.getOrDefault(assetId, 0));
        if (collaborativeBoost > 0) {
            nextScore += collaborativeBoost;
            reasons.add("Nguoi dung co hanh vi tuong tu cung quan tam");
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
