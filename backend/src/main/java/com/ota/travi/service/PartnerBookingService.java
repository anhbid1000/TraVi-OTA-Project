package com.ota.travi.service;

import com.ota.travi.dto.request.PartnerUpdateBookingStatusRequest;
import com.ota.travi.dto.response.PartnerBookingDetailResponse;
import com.ota.travi.dto.response.PartnerBookingListItemResponse;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.entity.DonKhachSanChiTiet;
import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.entity.DonNhaHangBan;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.DonDatChoRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.NhaHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class PartnerBookingService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "ngayTao",
            "tongTienThanhToan",
            "maDon",
            "trangThai"
    );

    private static final Map<TrangThaiDon, Set<TrangThaiDon>> ALLOWED_TRANSITIONS = Map.of(
            TrangThaiDon.DA_THANH_TOAN, Set.of(TrangThaiDon.DA_XAC_NHAN, TrangThaiDon.DA_HUY),
            TrangThaiDon.DA_XAC_NHAN, Set.of(TrangThaiDon.DANG_PHUC_VU, TrangThaiDon.DA_HUY),
            TrangThaiDon.DANG_PHUC_VU, Set.of(TrangThaiDon.DA_HOAN_THANH)
    );

    private final DonDatChoRepository donDatChoRepository;
    private final KhachSanRepository khachSanRepository;
    private final NhaHangRepository nhaHangRepository;

    public PartnerBookingService(
            DonDatChoRepository donDatChoRepository,
            KhachSanRepository khachSanRepository,
            NhaHangRepository nhaHangRepository
    ) {
        this.donDatChoRepository = donDatChoRepository;
        this.khachSanRepository = khachSanRepository;
        this.nhaHangRepository = nhaHangRepository;
    }

    @Transactional(readOnly = true)
    public Page<PartnerBookingListItemResponse> getPartnerBookings(
            String partnerId,
            List<TrangThaiDon> statuses,
            LocalDate fromDate,
            LocalDate toDate,
            String keyword,
            Integer page,
            Integer size,
            String sort
    ) {
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new ValidationException("toDate phai lon hon hoac bang fromDate");
        }

        Pageable pageable = buildPageable(page, size, sort);

        Specification<DonDatCho> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleted"), false));
            predicates.add(cb.equal(root.join("hoSoKinhDoanh").join("doiTac").get("id"), partnerId));

            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("trangThai").in(statuses));
            }

            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayTao"), fromDate.atStartOfDay()));
            }

            if (toDate != null) {
                predicates.add(cb.lessThan(root.get("ngayTao"), toDate.plusDays(1).atStartOfDay()));
            }

            if (keyword != null && !keyword.isBlank()) {
                String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maDon")), normalizedKeyword),
                        cb.like(cb.lower(root.get("tenNguoiDat")), normalizedKeyword),
                        cb.like(cb.lower(root.get("sdtNguoiDat")), normalizedKeyword),
                        cb.like(cb.lower(root.get("emailNguoiDat")), normalizedKeyword)
                ));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        return donDatChoRepository.findAll(specification, pageable)
                .map(this::toListItemResponse);
    }

    @Transactional(readOnly = true)
    public PartnerBookingDetailResponse getPartnerBookingDetail(String partnerId, String bookingId) {
        DonDatCho booking = requireOwnedBooking(partnerId, bookingId);
        return toDetailResponse(booking);
    }

    @Transactional
    public PartnerBookingDetailResponse updateBookingStatus(
            String partnerId,
            String bookingId,
            PartnerUpdateBookingStatusRequest request
    ) {
        DonDatCho booking = requireOwnedBooking(partnerId, bookingId);
        validateStatusTransition(booking, request);

        if (request.targetStatus() == TrangThaiDon.DA_HUY) {
            booking.setTrangThai(TrangThaiDon.DA_HUY);
            booking.setCancelledAt(LocalDateTime.now());
            booking.setCancelReason(request.reason().trim());
        } else {
            booking.setTrangThai(request.targetStatus());
        }

        DonDatCho saved = donDatChoRepository.save(booking);
        return toDetailResponse(saved);
    }

    private DonDatCho requireOwnedBooking(String partnerId, String bookingId) {
        DonDatCho booking = donDatChoRepository.findByIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay don dat cho"));

        String ownerPartnerId = booking.getHoSoKinhDoanh() != null && booking.getHoSoKinhDoanh().getDoiTac() != null
                ? booking.getHoSoKinhDoanh().getDoiTac().getId()
                : null;

        if (ownerPartnerId == null || !ownerPartnerId.equals(partnerId)) {
            throw new ForbiddenOperationException("Ban khong co quyen thao tac voi don dat cho nay");
        }

        return booking;
    }

    private void validateStatusTransition(DonDatCho booking, PartnerUpdateBookingStatusRequest request) {
        TrangThaiDon currentStatus = booking.getTrangThai();
        TrangThaiDon targetStatus = request.targetStatus();

        if (currentStatus == TrangThaiDon.DA_HUY || currentStatus == TrangThaiDon.DA_HOAN_THANH) {
            throw new ValidationException("Khong the cap nhat don da ket thuc");
        }

        if (currentStatus == targetStatus) {
            throw new ValidationException("Don dang o trang thai nay");
        }

        if (targetStatus == TrangThaiDon.DA_HUY && (request.reason() == null || request.reason().isBlank())) {
            throw new ValidationException("Ly do tu choi don la bat buoc");
        }

        Set<TrangThaiDon> nextStatuses = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!nextStatuses.contains(targetStatus)) {
            throw new ValidationException("Khong the chuyen tu " + currentStatus + " sang " + targetStatus);
        }
    }

    private Pageable buildPageable(Integer page, Integer size, String sort) {
        int pageNumber = page == null ? DEFAULT_PAGE : Math.max(page, 0);
        int pageSize = size == null ? DEFAULT_SIZE : Math.min(Math.max(size, 1), MAX_SIZE);

        String normalizedSort = (sort == null || sort.isBlank()) ? "ngayTao,desc" : sort.trim();
        String[] sortParts = normalizedSort.split(",");

        String sortField = sortParts[0].trim();
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "ngayTao";
        }

        Sort.Direction direction = Sort.Direction.DESC;
        if (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1].trim())) {
            direction = Sort.Direction.ASC;
        }

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortField));
    }

    private PartnerBookingListItemResponse toListItemResponse(DonDatCho booking) {
        if (booking instanceof DonKhachSan hotelBooking) {
            KhachSan khachSan = resolveKhachSan(hotelBooking);
            return new PartnerBookingListItemResponse(
                    booking.getId(),
                    booking.getMaDon(),
                    "KHACH_SAN",
                    khachSan != null ? khachSan.getTen() : null,
                    booking.getTenNguoiDat(),
                    booking.getSdtNguoiDat(),
                    booking.getNgayTao(),
                    hotelBooking.getNgayCheckIn() != null ? hotelBooking.getNgayCheckIn().atStartOfDay() : null,
                    hotelBooking.getNgayCheckOut() != null ? hotelBooking.getNgayCheckOut().atStartOfDay() : null,
                    hotelBooking.getNgayCheckIn(),
                    hotelBooking.getNgayCheckOut(),
                    hotelBooking.getSoKhach(),
                    booking.getTongTienThanhToan(),
                    null,
                    booking.getTrangThai()
            );
        }

        DonNhaHang restaurantBooking = (DonNhaHang) booking;
        NhaHang nhaHang = resolveNhaHang(restaurantBooking);

        return new PartnerBookingListItemResponse(
                booking.getId(),
                booking.getMaDon(),
                "NHA_HANG",
                nhaHang != null ? nhaHang.getTen() : null,
                booking.getTenNguoiDat(),
                booking.getSdtNguoiDat(),
                booking.getNgayTao(),
                restaurantBooking.getNgayGioBatDau(),
                restaurantBooking.getNgayGioKetThuc(),
                null,
                null,
                restaurantBooking.getSoNguoi(),
                booking.getTongTienThanhToan(),
                restaurantBooking.getTienCoc(),
                booking.getTrangThai()
        );
    }

    private PartnerBookingDetailResponse toDetailResponse(DonDatCho booking) {
        String tenTaiSan = null;
        String diaChiTaiSan = booking.getHoSoKinhDoanh() != null ? booking.getHoSoKinhDoanh().getDiaChi() : null;
        List<PartnerBookingDetailResponse.RoomInfo> rooms = List.of();
        List<PartnerBookingDetailResponse.TableInfo> tables = List.of();

        LocalDate ngayCheckIn = null;
        LocalDate ngayCheckOut = null;
        LocalDateTime ngayGioBatDau = null;
        LocalDateTime ngayGioKetThuc = null;
        Integer soKhach = null;
        Double tienCoc = null;
        java.time.LocalTime gioNhanPhongDuKien = null;
        String serviceType;

        if (booking instanceof DonKhachSan hotelBooking) {
            serviceType = "KHACH_SAN";
            KhachSan khachSan = resolveKhachSan(hotelBooking);
            tenTaiSan = khachSan != null ? khachSan.getTen() : null;

            ngayCheckIn = hotelBooking.getNgayCheckIn();
            ngayCheckOut = hotelBooking.getNgayCheckOut();
            gioNhanPhongDuKien = hotelBooking.getGioNhanPhongDuKien();
            soKhach = hotelBooking.getSoKhach();

            List<DonKhachSanChiTiet> bookingDetails = hotelBooking.getChiTietDon() == null
                    ? List.of()
                    : hotelBooking.getChiTietDon();

            rooms = bookingDetails.stream()
                    .map(detail -> new PartnerBookingDetailResponse.RoomInfo(
                            detail.getPhong() != null ? detail.getPhong().getId() : null,
                            detail.getTenPhongTaiThoiDiemDat(),
                            detail.getSoLuong(),
                            detail.getDonGiaTaiThoiDiemDat(),
                            detail.getSoDem(),
                            detail.getThanhTien()
                    ))
                    .toList();
        } else {
            serviceType = "NHA_HANG";
            DonNhaHang restaurantBooking = (DonNhaHang) booking;
            NhaHang nhaHang = resolveNhaHang(restaurantBooking);
            tenTaiSan = nhaHang != null ? nhaHang.getTen() : null;

            ngayGioBatDau = restaurantBooking.getNgayGioBatDau();
            ngayGioKetThuc = restaurantBooking.getNgayGioKetThuc();
            soKhach = restaurantBooking.getSoNguoi();
            tienCoc = restaurantBooking.getTienCoc();

            List<DonNhaHangBan> linkedTables = restaurantBooking.getBanDaGan() == null
                    ? List.of()
                    : restaurantBooking.getBanDaGan();

            tables = linkedTables.stream()
                    .map(link -> new PartnerBookingDetailResponse.TableInfo(
                            link.getBan() != null ? link.getBan().getId() : null,
                            link.getBan() != null ? link.getBan().getTenBan() : null,
                            link.getBan() != null ? link.getBan().getSoChoNgoi() : null,
                            link.getBan() != null ? link.getBan().getViTriSanh() : null
                    ))
                    .toList();
        }

        return new PartnerBookingDetailResponse(
                booking.getId(),
                booking.getMaDon(),
                serviceType,
                booking.getTrangThai(),
                booking.getNgayTao(),
                booking.getPaymentExpiredAt(),
                booking.getCancelledAt(),
                booking.getCancelReason(),
                tenTaiSan,
                diaChiTaiSan,
                booking.getTenNguoiDat(),
                booking.getSdtNguoiDat(),
                booking.getEmailNguoiDat(),
                booking.getGhiChu(),
                soKhach,
                booking.getTongTienGoc(),
                booking.getTienKhuyenMai(),
                booking.getTongTienThanhToan(),
                tienCoc,
                ngayCheckIn,
                ngayCheckOut,
                gioNhanPhongDuKien,
                ngayGioBatDau,
                ngayGioKetThuc,
                rooms,
                tables,
                getAllowedActions(booking.getTrangThai())
        );
    }

    private List<String> getAllowedActions(TrangThaiDon status) {
        return switch (status) {
            case DA_THANH_TOAN -> List.of("CONFIRM", "REJECT");
            case DA_XAC_NHAN -> List.of("START_SERVICE", "REJECT");
            case DANG_PHUC_VU -> List.of("COMPLETE");
            default -> List.of();
        };
    }

    private KhachSan resolveKhachSan(DonKhachSan booking) {
        List<DonKhachSanChiTiet> bookingDetails = booking.getChiTietDon() == null
                ? List.of()
                : booking.getChiTietDon();
        for (DonKhachSanChiTiet detail : bookingDetails) {
            if (detail.getPhong() != null && detail.getPhong().getKhachSan() != null) {
                return detail.getPhong().getKhachSan();
            }
        }

        if (booking.getHoSoKinhDoanh() != null && booking.getHoSoKinhDoanh().getTaiSan() instanceof KhachSan hotel) {
            return hotel;
        }

        if (booking.getHoSoKinhDoanh() == null || booking.getHoSoKinhDoanh().getIdHoSo() == null) {
            return null;
        }

        List<KhachSan> hotels = khachSanRepository.findByHoSoKinhDoanh_IdHoSo(booking.getHoSoKinhDoanh().getIdHoSo());
        return hotels.isEmpty() ? null : hotels.get(0);
    }

    private NhaHang resolveNhaHang(DonNhaHang booking) {
        List<DonNhaHangBan> linkedTables = booking.getBanDaGan() == null
                ? List.of()
                : booking.getBanDaGan();
        for (DonNhaHangBan link : linkedTables) {
            Ban table = link.getBan();
            if (table != null && table.getNhaHang() != null) {
                return table.getNhaHang();
            }
        }

        if (booking.getHoSoKinhDoanh() != null && booking.getHoSoKinhDoanh().getTaiSan() instanceof NhaHang restaurant) {
            return restaurant;
        }

        if (booking.getHoSoKinhDoanh() == null || booking.getHoSoKinhDoanh().getIdHoSo() == null) {
            return null;
        }

        List<NhaHang> restaurants = nhaHangRepository.findByHoSoKinhDoanh_IdHoSo(booking.getHoSoKinhDoanh().getIdHoSo());
        return restaurants.isEmpty() ? null : restaurants.get(0);
    }
}
