package com.ota.travi.service;

import com.ota.travi.dto.response.BookingSearchResponse;
import com.ota.travi.dto.response.ChinhSachResponse;
import com.ota.travi.dto.response.UserBookingResponse;
import com.ota.travi.entity.*;
import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.repository.DonDatChoRepository;
import com.ota.travi.repository.DonKhachSanRepository;
import com.ota.travi.repository.DonNhaHangRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("coreBookingService")
public class BookingService {

    @Autowired
    private DonDatChoRepository donDatChoRepository;

    @Autowired
    private DonKhachSanRepository donKhachSanRepository;

    @Autowired
    private DonNhaHangRepository donNhaHangRepository;

    @Autowired
    private KhachSanRepository khachSanRepository;

    @Autowired
    private NhaHangRepository nhaHangRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<BookingSearchResponse> getBookingHistory(String username, String type) {
        String queryType = (type != null) ? type.trim().toLowerCase() : "all";
        
        List<? extends DonDatCho> bookings;
        if ("hotel".equals(queryType)) {
            bookings = donKhachSanRepository.findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(username);
        } else if ("restaurant".equals(queryType)) {
            bookings = donNhaHangRepository.findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(username);
        } else {
            List<DonDatCho> allBookings = new ArrayList<>();
            allBookings.addAll(donKhachSanRepository.findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(username));
            allBookings.addAll(donNhaHangRepository.findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(username));
            allBookings.sort((left, right) -> safeCreatedAt(right).compareTo(safeCreatedAt(left)));
            bookings = allBookings;
        }

        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingSearchResponse lookupBookingPublicly(String maDon, String email, String phone) {
        if ((email == null || email.isBlank()) && (phone == null || phone.isBlank())) {
            throw new IllegalArgumentException("Vui lòng cung cấp email hoặc số điện thoại để xác minh đơn đặt chỗ.");
        }

        DonDatCho booking = findBookingByCode(maDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt chỗ với mã " + maDon));

        boolean verified = false;

        if (email != null && !email.isBlank()) {
            if (booking.getEmailNguoiDat() != null && booking.getEmailNguoiDat().equalsIgnoreCase(email.trim())) {
                verified = true;
            }
        }

        if (phone != null && !phone.isBlank()) {
            if (booking.getSdtNguoiDat() != null && booking.getSdtNguoiDat().trim().equals(phone.trim())) {
                verified = true;
            }
        }

        if (!verified) {
            throw new RuntimeException("Thông tin email hoặc số điện thoại xác thực không khớp với đơn đặt chỗ.");
        }

        return mapToResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<UserBookingResponse> getCustomerBookings(String customerId) {
        List<DonDatCho> orders = new ArrayList<>();
        orders.addAll(donKhachSanRepository.findByKhachHang_IdAndDeletedFalseOrderByNgayTaoDesc(customerId));
        orders.addAll(donNhaHangRepository.findByKhachHang_IdAndDeletedFalseOrderByNgayTaoDesc(customerId));
        orders.sort((left, right) -> safeCreatedAt(right).compareTo(safeCreatedAt(left)));
        return orders.stream().map(order -> mapToUserBookingResponse(order, customerId)).toList();
    }

    @Transactional(readOnly = true)
    public BookingSearchResponse getCustomerBookingDetail(String customerId, String bookingId) {
        DonDatCho booking = donDatChoRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt chỗ"));

        if (Boolean.TRUE.equals(booking.getDeleted())) {
            throw new RuntimeException("Không tìm thấy đơn đặt chỗ");
        }

        if (booking.getKhachHang() == null || !customerId.equals(booking.getKhachHang().getId())) {
            throw new RuntimeException("Bạn không có quyền xem đơn đặt chỗ này");
        }

        return mapToResponse(booking);
    }
    private Optional<DonDatCho> findBookingByCode(String maDon) {
        Optional<DonKhachSan> hotelBooking = donKhachSanRepository.findByMaDonAndDeletedFalse(maDon);
        if (hotelBooking.isPresent()) {
            return Optional.of(hotelBooking.get());
        }
        return donNhaHangRepository.findByMaDonAndDeletedFalse(maDon).map(order -> order);
    }

    private LocalDateTime safeCreatedAt(DonDatCho booking) {
        return booking.getNgayTao() == null ? LocalDateTime.MIN : booking.getNgayTao();
    }

    private BookingSearchResponse mapToResponse(DonDatCho booking) {
        String id = booking.getId();
        String maDon = booking.getMaDon();
        LocalDateTime ngayTao = booking.getNgayTao();
        Double tongTienThanhToan = booking.getTongTienThanhToan();
        TrangThaiDon trangThai = booking.getTrangThai();
        String tenNguoiDat = booking.getTenNguoiDat();
        String sdtNguoiDat = booking.getSdtNguoiDat();
        String emailNguoiDat = booking.getEmailNguoiDat();

        String tenTaiSan = "";
        String anhTaiSan = "";
        String diaChiTaiSan = "";
        String loaiTaiSan = "";
        LocalDateTime ngayBatDau = null;
        LocalDateTime ngayKetThuc = null;
        LocalDate ngayNhanPhong = null;
        LocalDate ngayTraPhong = null;
        Integer soKhach = 0;
        List<BookingSearchResponse.RoomInfo> rooms = new ArrayList<>();
        List<BookingSearchResponse.TableInfo> tables = new ArrayList<>();

        if (booking instanceof DonKhachSan hotelBooking) {
            loaiTaiSan = "HOTEL";
            ngayBatDau = hotelBooking.getNgayCheckIn() != null ? hotelBooking.getNgayCheckIn().atStartOfDay() : null;
            ngayKetThuc = hotelBooking.getNgayCheckOut() != null ? hotelBooking.getNgayCheckOut().atStartOfDay() : null;
            ngayNhanPhong = hotelBooking.getNgayCheckIn();
            ngayTraPhong = hotelBooking.getNgayCheckOut();
            soKhach = hotelBooking.getSoKhach();

            KhachSan khachSan = null;
            if (hotelBooking.getChiTietDon() != null && !hotelBooking.getChiTietDon().isEmpty()) {
                DonKhachSanChiTiet detail = hotelBooking.getChiTietDon().get(0);
                if (detail.getPhong() != null) {
                    khachSan = detail.getPhong().getKhachSan();
                }

                for (DonKhachSanChiTiet dt : hotelBooking.getChiTietDon()) {
                    rooms.add(new BookingSearchResponse.RoomInfo(
                            dt.getTenPhongTaiThoiDiemDat(),
                            dt.getSoLuong(),
                            dt.getDonGiaTaiThoiDiemDat(),
                            dt.getThanhTien()
                    ));
                }
            }

            if (khachSan == null && hotelBooking.getHoSoKinhDoanh() != null) {
                List<KhachSan> hotels = khachSanRepository.findByHoSoKinhDoanh_IdHoSo(hotelBooking.getHoSoKinhDoanh().getIdHoSo());
                if (!hotels.isEmpty()) {
                    khachSan = hotels.get(0);
                }
            }

            if (khachSan != null) {
                tenTaiSan = khachSan.getTen();
                if (khachSan.getDanhSachAnh() != null && !khachSan.getDanhSachAnh().isEmpty()) {
                    anhTaiSan = khachSan.getDanhSachAnh().stream()
                            .filter(AnhKhachSan::getLaAnhDaiDien)
                            .findFirst()
                            .map(AnhKhachSan::getDuongDanUrl)
                            .orElse(khachSan.getDanhSachAnh().get(0).getDuongDanUrl());
                }
            }

            if (booking.getHoSoKinhDoanh() != null && booking.getHoSoKinhDoanh().getDiaChi() != null) {
                diaChiTaiSan = booking.getHoSoKinhDoanh().getDiaChi();
            }

        } else if (booking instanceof DonNhaHang restaurantBooking) {
            loaiTaiSan = "RESTAURANT";
            ngayBatDau = restaurantBooking.getNgayGioBatDau();
            ngayKetThuc = restaurantBooking.getNgayGioKetThuc();
            soKhach = restaurantBooking.getSoNguoi();

            NhaHang nhaHang = null;
            if (restaurantBooking.getBanDaGan() != null && !restaurantBooking.getBanDaGan().isEmpty()) {
                DonNhaHangBan link = restaurantBooking.getBanDaGan().get(0);
                if (link.getBan() != null) {
                    nhaHang = link.getBan().getNhaHang();
                }

                for (DonNhaHangBan linkTable : restaurantBooking.getBanDaGan()) {
                    if (linkTable.getBan() != null) {
                        tables.add(new BookingSearchResponse.TableInfo(
                                linkTable.getBan().getTenBan(),
                                linkTable.getBan().getSoChoNgoi(),
                                linkTable.getBan().getViTriSanh()
                        ));
                    }
                }
            }

            if (nhaHang == null && restaurantBooking.getHoSoKinhDoanh() != null) {
                List<NhaHang> restaurants = nhaHangRepository.findByHoSoKinhDoanh_IdHoSo(restaurantBooking.getHoSoKinhDoanh().getIdHoSo());
                if (!restaurants.isEmpty()) {
                    nhaHang = restaurants.get(0);
                }
            }

            if (nhaHang != null) {
                tenTaiSan = nhaHang.getTen();
                if (nhaHang.getDanhSachAnh() != null && !nhaHang.getDanhSachAnh().isEmpty()) {
                    anhTaiSan = nhaHang.getDanhSachAnh().stream()
                            .filter(AnhNhaHang::getLaAnhDaiDien)
                            .findFirst()
                            .map(AnhNhaHang::getDuongDanUrl)
                            .orElse(nhaHang.getDanhSachAnh().get(0).getDuongDanUrl());
                }
            }

            if (booking.getHoSoKinhDoanh() != null && booking.getHoSoKinhDoanh().getDiaChi() != null) {
                diaChiTaiSan = booking.getHoSoKinhDoanh().getDiaChi();
            }
        }

        return new BookingSearchResponse(
                id,
                maDon,
                tenTaiSan,
                anhTaiSan,
                diaChiTaiSan,
                ngayTao,
                tongTienThanhToan,
                trangThai,
                loaiTaiSan,
                ngayBatDau,
                ngayKetThuc,
                ngayNhanPhong,
                ngayTraPhong,
                tenNguoiDat,
                sdtNguoiDat,
                emailNguoiDat,
                soKhach,
                booking instanceof DonNhaHang dnh ? dnh.getTienCoc() : null,
                booking.getGhiChu(),
                toChinhSachResponse(booking.getHoSoKinhDoanh() == null ? null : booking.getHoSoKinhDoanh().getChinhSach()),
                rooms,
                tables
        );
    }

    private UserBookingResponse mapToUserBookingResponse(DonDatCho order, String customerId) {
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
            dateLabel = String.valueOf(dnh.getNgayGioBatDau());
        } else {
            dateLabel = order.getNgayTao() != null ? order.getNgayTao().toLocalDate().toString() : "";
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

    private ChinhSachResponse toChinhSachResponse(ChinhSach policy) {
        if (policy == null) {
            return null;
        }

        return new ChinhSachResponse(
                policy.getId(),
                policy.getHoSoKinhDoanh() == null ? null : policy.getHoSoKinhDoanh().getIdHoSo(),
                policy.getLoaiChinhSach(),
                policy.getNoiDung(),
                policy.getNgayApDung(),
                policy.getGioNhanPhong(),
                policy.getGioTraPhong(),
                policy.getGioMoCua(),
                policy.getGioDongCua(),
                policy.getChinhSachHuy(),
                policy.getChinhSachHoanTien(),
                policy.getQuyDinhTreEm(),
                policy.getQuyDinhVatNuoi(),
                policy.getGhiChuKhac(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
        );
    }

    private String getThumbnailUrl(LoaiDichVu type, String hoSoId) {
        if (type == LoaiDichVu.KHACH_SAN) {
            List<KhachSan> khachSans = khachSanRepository.findByHoSoKinhDoanh_IdHoSo(hoSoId);
            if (!khachSans.isEmpty()) {
                KhachSan ks = khachSans.get(0);
                if (ks.getDanhSachAnh() != null && !ks.getDanhSachAnh().isEmpty()) {
                    return ks.getDanhSachAnh().stream()
                            .filter(a -> Boolean.TRUE.equals(a.getLaAnhDaiDien()))
                            .map(AnhKhachSan::getDuongDanUrl)
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
                            .filter(a -> Boolean.TRUE.equals(a.getLaAnhDaiDien()))
                            .map(AnhNhaHang::getDuongDanUrl)
                            .findFirst()
                            .orElseGet(() -> nh.getDanhSachAnh().get(0).getDuongDanUrl());
                }
            }
        }
        return "";
    }
}
