package com.ota.travi.repository.specification;

import com.ota.travi.dto.request.HotelSearchRequest;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.TienIchKhachSan;
import com.ota.travi.enums.TrangThaiHoatDong;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class HotelSpecification {

    private HotelSpecification() {
    }

    public static Specification<KhachSan> build(HotelSearchRequest request) {
        return Specification.where(isPublicVisible())
                .and(hasCity(request.city()))
                .and(keywordContains(request.keyword()))
                .and(priceBetween(request.minPrice(), request.maxPrice()))
                .and(hasStarRatings(request.stars()))
                .and(hasAmenities(request.amenities()));
    }

    public static Specification<KhachSan> isPublicVisible() {
        return (root, query, cb) -> {
            Join<KhachSan, HoSoKinhDoanh> hoSoJoin = root.join("hoSoKinhDoanh", JoinType.INNER);
            return cb.and(
                    cb.equal(hoSoJoin.get("trangThaiHoatDong"), TrangThaiHoatDong.DANG_HOAT_DONG),
                    cb.isFalse(hoSoJoin.get("deleted"))
            );
        };
    }

    public static Specification<KhachSan> hasCity(String city) {
        return (root, query, cb) -> {
            if (!hasText(city)) {
                return null;
            }
            Join<KhachSan, HoSoKinhDoanh> hoSoJoin = root.join("hoSoKinhDoanh", JoinType.INNER);
            String normalizedKeyword = "%" + city.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.or(
                    cb.like(cb.lower(hoSoJoin.get("thanhPho")), normalizedKeyword),
                    cb.like(cb.lower(hoSoJoin.get("diaChi")), normalizedKeyword)
            );
        };
    }

    public static Specification<KhachSan> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            }
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("giaCoBan"), minPrice.doubleValue(), maxPrice.doubleValue());
            }
            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("giaCoBan"), minPrice.doubleValue());
            }
            return cb.lessThanOrEqualTo(root.get("giaCoBan"), maxPrice.doubleValue());
        };
    }

    public static Specification<KhachSan> hasStarRatings(List<Integer> stars) {
        return (root, query, cb) -> {
            if (stars == null || stars.isEmpty()) {
                return null;
            }
            return root.get("hangSao").in(stars);
        };
    }

    public static Specification<KhachSan> hasAmenities(List<String> amenityIds) {
        return (root, query, cb) -> {
            if (amenityIds == null || amenityIds.isEmpty()) {
                return null;
            }
            query.distinct(true);
            Join<KhachSan, TienIchKhachSan> amenityJoin = root.join("tienIch", JoinType.INNER);
            List<String> normalizedAmenities = amenityIds.stream()
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .map(value -> value.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());
            if (normalizedAmenities.isEmpty()) {
                return null;
            }
            return cb.lower(amenityJoin.get("tenTienIch")).in(normalizedAmenities);
        };
    }

    public static Specification<KhachSan> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (!hasText(keyword)) {
                return null;
            }
            String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            Join<KhachSan, HoSoKinhDoanh> hoSoJoin = root.join("hoSoKinhDoanh", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("ten")), normalizedKeyword),
                    cb.like(cb.lower(root.get("moTa")), normalizedKeyword),
                    cb.like(cb.lower(hoSoJoin.get("tenCoSo")), normalizedKeyword),
                    cb.like(cb.lower(hoSoJoin.get("diaChi")), normalizedKeyword)
            );
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
