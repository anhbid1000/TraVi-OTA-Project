package com.ota.travi.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TwoTowerRecommendationModelTest {
    private final TwoTowerRecommendationModel model = new TwoTowerRecommendationModel();

    @Test
    void dotProductScoreRanksMatchingContextHigher() {
        TwoTowerRecommendationModel.UserContext user = new TwoTowerRecommendationModel.UserContext(
                "user-1",
                "VANG",
                4_000_000.0,
                List.of("nghi duong", "spa", "da lat"),
                "Da Lat",
                "HOTEL",
                Set.of(),
                Set.of(),
                Map.of()
        );
        TwoTowerRecommendationModel.ItemContext matchingHotel = new TwoTowerRecommendationModel.ItemContext(
                "hotel-1",
                "HOTEL",
                "Da Lat Spa Resort",
                "Resort Spa",
                "Nghi duong yen tinh gan trung tam Da Lat",
                "Da Lat",
                "Phuong 3",
                900_000.0,
                4.0,
                List.of("Spa", "Ho boi")
        );
        TwoTowerRecommendationModel.ItemContext unrelatedRestaurant = new TwoTowerRecommendationModel.ItemContext(
                "restaurant-1",
                "RESTAURANT",
                "Quan hai san bien",
                "Hai san",
                "Nha hang gan bien",
                "Phu Quoc",
                "Duong Dong",
                250_000.0,
                2.0,
                List.of("Dat ban")
        );

        TwoTowerRecommendationModel.TwoTowerScore matchingScore = model.score(user, matchingHotel);
        TwoTowerRecommendationModel.TwoTowerScore unrelatedScore = model.score(user, unrelatedRestaurant);

        assertThat(matchingScore.score()).isGreaterThan(unrelatedScore.score());
        assertThat(matchingScore.dotProduct()).isGreaterThan(unrelatedScore.dotProduct());
        assertThat(matchingScore.reasons()).isNotEmpty();
    }

    @Test
    void collaborativeSignalBoostsKnownAsset() {
        TwoTowerRecommendationModel.UserContext user = new TwoTowerRecommendationModel.UserContext(
                "user-1",
                "DONG",
                0.0,
                List.of("am thuc dia phuong"),
                "Hoi An",
                "RESTAURANT",
                Set.of(),
                Set.of(),
                Map.of("restaurant-boosted", 3)
        );
        TwoTowerRecommendationModel.ItemContext boostedRestaurant = new TwoTowerRecommendationModel.ItemContext(
                "restaurant-boosted",
                "RESTAURANT",
                "Bep Hoi An",
                "Dac san dia phuong",
                "Am thuc dia phuong",
                "Hoi An",
                "Minh An",
                180_000.0,
                3.0,
                List.of("Dat ban")
        );

        TwoTowerRecommendationModel.TwoTowerScore score = model.score(user, boostedRestaurant);

        assertThat(score.reasons()).contains("Được nhiều người có hành vi tương tự quan tâm");
        assertThat(score.score()).isGreaterThanOrEqualTo(55);
    }
}
