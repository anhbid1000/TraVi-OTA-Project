package com.ota.travi.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CollaborativeFilteringService {
    private final JdbcTemplate jdbcTemplate;

    public CollaborativeFilteringService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Integer> scoreCandidateAssets(String khachHangId) {
        String hoSoAIId = findAiProfileId(khachHangId);
        if (hoSoAIId == null) {
            return Map.of();
        }

        Set<String> currentAssetIds = findCurrentAssetIds(khachHangId, hoSoAIId);
        if (currentAssetIds.isEmpty()) {
            return Map.of();
        }

        List<String> similarProfileIds = findSimilarProfileIds(hoSoAIId, currentAssetIds);
        if (similarProfileIds.isEmpty()) {
            return Map.of();
        }

        Map<String, Integer> scores = new HashMap<>();
        for (String similarProfileId : similarProfileIds) {
            for (String assetId : findAssetsByProfileId(similarProfileId)) {
                if (!currentAssetIds.contains(assetId)) {
                    scores.merge(assetId, 1, Integer::sum);
                }
            }
        }
        return scores;
    }

    private String findAiProfileId(String khachHangId) {
        List<String> ids = jdbcTemplate.queryForList(
                "SELECT id_ho_so_ai FROM ho_so_ai WHERE khach_hang_id = ?",
                String.class,
                khachHangId
        );
        return ids.isEmpty() ? null : ids.get(0);
    }

    private Set<String> findCurrentAssetIds(String khachHangId, String hoSoAIId) {
        Set<String> ids = new LinkedHashSet<>(findAssetsByProfileId(hoSoAIId));
        ids.addAll(jdbcTemplate.queryForList(
                """
                SELECT DISTINCT ts.id_tai_san
                FROM don_dat_cho ddc
                JOIN tai_san ts ON ts.ho_so_kinh_doanh_id = ddc.ho_so_kinh_doanh_id
                WHERE ddc.khach_hang_id = ? AND ddc.deleted = false
                """,
                String.class,
                khachHangId
        ));
        ids.remove(null);
        return ids;
    }

    private List<String> findSimilarProfileIds(String hoSoAIId, Set<String> currentAssetIds) {
        return currentAssetIds.stream()
                .flatMap(assetId -> jdbcTemplate.queryForList(
                        """
                        SELECT DISTINCT ho_so_ai_id
                        FROM ho_so_ai_hanh_vi
                        WHERE id_tai_san = ? AND ho_so_ai_id <> ?
                        """,
                        String.class,
                        assetId,
                        hoSoAIId
                ).stream())
                .distinct()
                .limit(50)
                .toList();
    }

    private List<String> findAssetsByProfileId(String hoSoAIId) {
        return jdbcTemplate.queryForList(
                """
                SELECT DISTINCT id_tai_san
                FROM ho_so_ai_hanh_vi
                WHERE ho_so_ai_id = ? AND id_tai_san IS NOT NULL
                  AND loai_su_kien IN ('VIEW', 'CLICK', 'BOOK', 'REVIEW', 'PROMOTION_VIEW')
                """,
                String.class,
                hoSoAIId
        );
    }
}
