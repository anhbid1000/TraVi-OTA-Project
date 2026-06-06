package com.ota.travi.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AiScoreCalculatorTest {
    private final AiScoreCalculator calculator = new AiScoreCalculator();

    @Test
    void collaborativeBoostIsBasedOnSimilarUserSignalsAndCapped() {
        assertThat(calculator.calculateCollaborativeBoost(0)).isZero();
        assertThat(calculator.calculateCollaborativeBoost(2)).isEqualTo(16);
        assertThat(calculator.calculateCollaborativeBoost(9)).isEqualTo(24);
    }

    @Test
    void behaviorScorePrioritizesBookingThenCollaborativeFiltering() {
        List<String> reasons = new ArrayList<>();

        int score = calculator.addBehaviorScore(
                40,
                "asset-1",
                Set.of("asset-1"),
                Set.of(),
                Map.of("asset-1", 2),
                reasons
        );

        assertThat(score).isEqualTo(68);
        assertThat(reasons).contains(
                "Bạn đã từng quan tâm dịch vụ này",
                "Người dùng có hành vi tương tự cũng quan tâm"
        );
    }

}
