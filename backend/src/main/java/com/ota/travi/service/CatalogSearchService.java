package com.ota.travi.service;

import com.ota.travi.dto.request.HotelSearchRequest;
import com.ota.travi.dto.request.RestaurantSearchRequest;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.specification.HotelSpecification;
import com.ota.travi.repository.specification.RestaurantSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CatalogSearchService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    private final KhachSanRepository khachSanRepository;
    private final NhaHangRepository nhaHangRepository;

    public CatalogSearchService(KhachSanRepository khachSanRepository, NhaHangRepository nhaHangRepository) {
        this.khachSanRepository = khachSanRepository;
        this.nhaHangRepository = nhaHangRepository;
    }

    // --- 1. TÌM KIẾM KHÁCH SẠN (SEARCH HOTELS) ---
    // Tìm kiếm khách sạn với phân trang và sắp xếp
    public Page<KhachSan> searchHotels(HotelSearchRequest request) {
        // Xây dựng đối tượng Pageable với trang, kích thước và sắp xếp
        Pageable pageable = PageRequest.of(
                normalizePage(request.page()),
                normalizeSize(request.size()),
                resolveHotelSort(request.sort())
        );

        // Thực hiện tìm kiếm sử dụng Specification pattern
        return khachSanRepository.findAll(HotelSpecification.build(request), pageable);
    }

    // --- 2. TÌM KIẾM NHÀ HÀNG (SEARCH RESTAURANTS) ---
    // Tìm kiếm nhà hàng với phân trang và sắp xếp
    public Page<NhaHang> searchRestaurants(RestaurantSearchRequest request) {
        Pageable pageable = PageRequest.of(
                normalizePage(request.page()),
                normalizeSize(request.size()),
                resolveRestaurantSort(request.sort())
        );

        return nhaHangRepository.findAll(RestaurantSpecification.build(request), pageable);
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private Sort resolveHotelSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "ten");
        }

        return switch (sort.trim().toLowerCase()) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "giaCoBan");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "giaCoBan");
            // Current schema does not have rating/popularity fields yet, so use stable approximations.
            case "rating_desc" -> Sort.by(Sort.Direction.DESC, "hangSao");
            case "popular_desc" -> Sort.by(Sort.Direction.DESC, "tongSoPhong");
            default -> Sort.by(Sort.Direction.ASC, "ten");
        };
    }

    private Sort resolveRestaurantSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "ten");
        }

        return switch (sort.trim().toLowerCase()) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "giaCoBan");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "giaCoBan");
            // Fallbacks keep API contract stable until review metrics are introduced.
            case "rating_desc" -> Sort.by(Sort.Direction.DESC, "sucChua");
            case "popular_desc" -> Sort.by(Sort.Direction.DESC, "sucChua");
            default -> Sort.by(Sort.Direction.ASC, "ten");
        };
    }
}

