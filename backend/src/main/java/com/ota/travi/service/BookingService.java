package com.ota.travi.service;

import com.ota.travi.dto.response.BookingSearchResponse;
import com.ota.travi.entity.*;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.repository.DonDatChoRepository;
import com.ota.travi.repository.DonKhachSanRepository;
import com.ota.travi.repository.DonNhaHangRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.NhaHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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

    @Transactional(readOnly = true)
    public List<BookingSearchResponse> getBookingHistory(String username, String type) {
        String queryType = (type != null) ? type.trim().toLowerCase() : "all";
        
        List<? extends DonDatCho> bookings;
        if ("hotel".equals(queryType)) {
            bookings = donKhachSanRepository.findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(username);
        } else if ("restaurant".equals(queryType)) {
            bookings = donNhaHangRepository.findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(username);
        } else {
            bookings = donDatChoRepository.findByKhachHang_UsernameAndDeletedFalseOrderByNgayTaoDesc(username);
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

        DonDatCho booking = donDatChoRepository.findByMaDonAndDeletedFalse(maDon)
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
        String loaiTaiSan = "";
        LocalDateTime ngayBatDau = null;
        LocalDateTime ngayKetThuc = null;
        Integer soKhach = 0;
        List<BookingSearchResponse.RoomInfo> rooms = new ArrayList<>();
        List<BookingSearchResponse.TableInfo> tables = new ArrayList<>();

        if (booking instanceof DonKhachSan hotelBooking) {
            loaiTaiSan = "HOTEL";
            ngayBatDau = hotelBooking.getNgayCheckIn() != null ? hotelBooking.getNgayCheckIn().atStartOfDay() : null;
            ngayKetThuc = hotelBooking.getNgayCheckOut() != null ? hotelBooking.getNgayCheckOut().atStartOfDay() : null;
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
        }

        return new BookingSearchResponse(
                id,
                maDon,
                tenTaiSan,
                anhTaiSan,
                ngayTao,
                tongTienThanhToan,
                trangThai,
                loaiTaiSan,
                ngayBatDau,
                ngayKetThuc,
                tenNguoiDat,
                sdtNguoiDat,
                emailNguoiDat,
                soKhach,
                rooms,
                tables
        );
    }
}
