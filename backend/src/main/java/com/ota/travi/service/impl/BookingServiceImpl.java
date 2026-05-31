package com.ota.travi.service.impl;

import com.ota.travi.dto.response.UserBookingResponse;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.repository.DonDatChoRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.ReviewRepository;
import com.ota.travi.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final DonDatChoRepository donDatChoRepository;
    private final KhachSanRepository khachSanRepository;
    private final NhaHangRepository nhaHangRepository;
    private final ReviewRepository reviewRepository;

    public BookingServiceImpl(
            DonDatChoRepository donDatChoRepository,
            KhachSanRepository khachSanRepository,
            NhaHangRepository nhaHangRepository,
            ReviewRepository reviewRepository
    ) {
        this.donDatChoRepository = donDatChoRepository;
        this.khachSanRepository = khachSanRepository;
        this.nhaHangRepository = nhaHangRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBookingResponse> getCustomerBookings(String customerId) {
        List<DonDatCho> orders = donDatChoRepository.findByKhachHang_IdAndDeletedFalseOrderByNgayTaoDesc(customerId);
        return orders.stream().map(order -> mapToResponse(order, customerId)).toList();
    }

    private UserBookingResponse mapToResponse(DonDatCho order, String customerId) {
        LoaiDichVu type = order.getHoSoKinhDoanh().getLoaiDichVu();

        String bookingId = null;
        String reservationId = null;
        if (type == LoaiDichVu.KHACH_SAN) {
            bookingId = order.getId();
        } else if (type == LoaiDichVu.NHA_HANG) {
            reservationId = order.getId();
        }

        String dateLabel;
        if (order instanceof DonKhachSan dks) {
            dateLabel = dks.getNgayCheckIn() + " - " + dks.getNgayCheckOut();
        } else if (order instanceof DonNhaHang dnh) {
            dateLabel = dnh.getNgayGioBatDau().toString();
        } else {
            dateLabel = order.getNgayTao().toLocalDate().toString();
        }

        boolean reviewed = false;
        if (type == LoaiDichVu.KHACH_SAN) {
            reviewed = reviewRepository.existsByKhachHang_IdAndBookingId(customerId, order.getId());
        } else if (type == LoaiDichVu.NHA_HANG) {
            reviewed = reviewRepository.existsByKhachHang_IdAndReservationId(customerId, order.getId());
        }

        String thumbnailUrl = getThumbnailUrl(type, order.getHoSoKinhDoanh().getIdHoSo());

        return new UserBookingResponse(
                order.getMaDon(),
                order.getHoSoKinhDoanh().getTenCoSo(),
                type.name(),
                bookingId,
                reservationId,
                dateLabel,
                order.getTongTienThanhToan(),
                order.getTrangThai().name(),
                reviewed,
                thumbnailUrl
        );
    }

    private String getThumbnailUrl(LoaiDichVu type, String hoSoId) {
        try {
            if (type == LoaiDichVu.KHACH_SAN) {
                List<KhachSan> khachSans = khachSanRepository.findByHoSoKinhDoanh_IdHoSo(hoSoId);
                if (!khachSans.isEmpty()) {
                    KhachSan ks = khachSans.get(0);
                    if (ks.getDanhSachAnh() != null && !ks.getDanhSachAnh().isEmpty()) {
                        return ks.getDanhSachAnh().stream()
                                .filter(a -> a.getLaAnhDaiDien() != null && a.getLaAnhDaiDien())
                                .map(a -> a.getDuongDanUrl())
                                .findFirst()
                                .orElseGet(() -> ks.getDanhSachAnh().get(0).getDuongDanUrl());
                    }
                }
            } else if (type == LoaiDichVu.NHA_HANG) {
                List<NhaHang> nhaHangs = nhaHangRepository.findByHoSoKinhDoanh_IdHoSo(hoSoId);
                if (!nhaHangs.isEmpty()) {
                    NhaHang nh = nhaHangs.get(0);
                    if (nh.getDanhSachAnh() != null && !nh.getDanhSachAnh().isEmpty()) {
                        return nh.getDanhSachAnh().stream()
                                .filter(a -> a.getLaAnhDaiDien() != null && a.getLaAnhDaiDien())
                                .map(a -> a.getDuongDanUrl())
                                .findFirst()
                                .orElseGet(() -> nh.getDanhSachAnh().get(0).getDuongDanUrl());
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }
}
