package com.ota.travi.repository.specification;

import com.ota.travi.dto.request.RestaurantSearchRequest;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.TienIchNhaHang;
import com.ota.travi.enums.TrangThaiHoatDong;
import com.ota.travi.enums.TrangThaiKiemDuyet;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class RestaurantSpecification {

    private RestaurantSpecification() {
    }

    public static Specification<NhaHang> build(RestaurantSearchRequest request) {
        return Specification.where(isPublicVisible())
                .and(hasCity(request.city()))
                .and(keywordContains(request.keyword()))
                .and(hasCuisineTypes(request.cuisines()))
                .and(hasAmenities(request.amenities()))
                .and(hasGuestCapacity(request.guests()))
                .and(priceRangeBetween(request.minPrice(), request.maxPrice()));
    }

    public static Specification<NhaHang> isPublicVisible() {
        return (root, query, cb) -> {
            Join<NhaHang, HoSoKinhDoanh> hoSoJoin = root.join("hoSoKinhDoanh", JoinType.INNER);
            return cb.and(
                    cb.equal(hoSoJoin.get("trangThaiKiemDuyet"), TrangThaiKiemDuyet.DA_DUYET),
                    cb.equal(hoSoJoin.get("trangThaiHoatDong"), TrangThaiHoatDong.DANG_HOAT_DONG),
                    cb.isFalse(hoSoJoin.get("deleted"))
            );
        };
    }

    public static Specification<NhaHang> hasCity(String city) {
        return (root, query, cb) -> {
            if (!hasText(city)) {
                return null;
            }
            Join<NhaHang, HoSoKinhDoanh> hoSoJoin = root.join("hoSoKinhDoanh", JoinType.INNER);
            return cb.equal(cb.lower(hoSoJoin.get("thanhPho")), city.trim().toLowerCase(Locale.ROOT));
        };
    }

    public static Specification<NhaHang> hasCuisineTypes(List<String> cuisineTypes) {
        return (root, query, cb) -> {
            if (cuisineTypes == null || cuisineTypes.isEmpty()) {
                return null;
            }
            List<String> normalizedCuisines = cuisineTypes.stream()
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .map(value -> value.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());
            if (normalizedCuisines.isEmpty()) {
                return null;
            }
            return cb.lower(root.get("loaiAmThuc")).in(normalizedCuisines);
        };
    }

    public static Specification<NhaHang> hasAmenities(List<String> amenityIds) {
        return (root, query, cb) -> {
            if (amenityIds == null || amenityIds.isEmpty()) {
                return null;
            }
            query.distinct(true);
            Join<NhaHang, TienIchNhaHang> amenityJoin = root.join("tienIch", JoinType.INNER);
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

    public static Specification<NhaHang> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (!hasText(keyword)) {
                return null;
            }
            String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            Join<NhaHang, HoSoKinhDoanh> hoSoJoin = root.join("hoSoKinhDoanh", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("ten")), normalizedKeyword),
                    cb.like(cb.lower(root.get("moTa")), normalizedKeyword),
                    cb.like(cb.lower(root.get("loaiAmThuc")), normalizedKeyword),
                    cb.like(cb.lower(hoSoJoin.get("tenCoSo")), normalizedKeyword),
                    cb.like(cb.lower(hoSoJoin.get("diaChi")), normalizedKeyword),
                    cb.like(cb.lower(hoSoJoin.get("quanHuyen")), normalizedKeyword),
                    cb.like(cb.lower(hoSoJoin.get("phuongXa")), normalizedKeyword)
            );
        };
    }

    public static Specification<NhaHang> hasGuestCapacity(Integer guests) {
        return (root, query, cb) -> {
            if (guests == null) {
                return null;
            }
            query.distinct(true);
            Join<NhaHang, Ban> tableJoin = root.join("danhSachBan", JoinType.LEFT);

            return cb.or(
                    cb.greaterThanOrEqualTo(root.get("sucChua"), guests),
                    cb.and(
                            cb.isFalse(tableJoin.get("deleted")),
                            cb.greaterThanOrEqualTo(tableJoin.get("soChoNgoi"), guests)
                    )
            );
        };
    }

    public static Specification<NhaHang> priceRangeBetween(BigDecimal minPrice, BigDecimal maxPrice) {
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

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

