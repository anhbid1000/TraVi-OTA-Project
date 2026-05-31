package com.ota.travi.service;

import com.ota.travi.dto.request.CreateHotelBookingRequest;
import com.ota.travi.dto.request.CreateRestaurantBookingRequest;
import com.ota.travi.dto.request.HotelBookingRoomRequest;
import com.ota.travi.dto.response.HotelBookingResponse;
import com.ota.travi.dto.response.HotelBookingRoomResponse;
import com.ota.travi.dto.response.RestaurantAssignedTableResponse;
import com.ota.travi.dto.response.RestaurantBookingResponse;
import com.ota.travi.dto.response.UserProfileResponse;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.DatPhong;
import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.entity.DonKhachSanChiTiet;
import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.entity.DonNhaHangBan;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.Phong;
import com.ota.travi.entity.User;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.enums.TrangThaiPhong;
import com.ota.travi.enums.TrangThaiUser;
import com.ota.travi.repository.BanRepository;
import com.ota.travi.repository.DonDatChoRepository;
import com.ota.travi.repository.DonKhachSanChiTietRepository;
import com.ota.travi.repository.DonKhachSanRepository;
import com.ota.travi.repository.DonNhaHangRepository;
import com.ota.travi.repository.DatPhongRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.PhongRepository;
import com.ota.travi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KhachSanRepository khachSanRepository;

    @Autowired
    private NhaHangRepository nhaHangRepository;

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private BanRepository banRepository;

    @Autowired
    private DonDatChoRepository donDatChoRepository;

    @Autowired
    private DonKhachSanRepository donKhachSanRepository;

    @Autowired
    private DonNhaHangRepository donNhaHangRepository;

    @Autowired
    private DonKhachSanChiTietRepository donKhachSanChiTietRepository;
    @Autowired
    private DatPhongRepository datPhongRepository;

    @Autowired
    private HotelBookingPaymentHoldService hotelBookingPaymentHoldService;

    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getTrangThai() != TrangThaiUser.HOAT_DONG) {
            throw new RuntimeException("Tài khoản không ở trạng thái hoạt động: " + user.getTrangThai().getDisplayName());
        }

        String roleName = (user.getVaiTro() != null) ? user.getVaiTro().getTen() : null;

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getHoTen(),
                user.getSoDienThoai(),
                roleName
        );
    }

    @Transactional
    public HotelBookingResponse createHotelBooking(String username, CreateHotelBookingRequest request) {
        if (request.ngayCheckOut() == null || !request.ngayCheckOut().isAfter(request.ngayCheckIn())) {
            throw new RuntimeException("ngayCheckOut phải sau ngayCheckIn");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!(user instanceof KhachHang khachHang)) {
            throw new RuntimeException("Chỉ khách hàng mới có thể đặt khách sạn");
        }

        KhachSan khachSan = khachSanRepository.findById(request.hotelId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));

        int soDem = (int) java.time.temporal.ChronoUnit.DAYS.between(request.ngayCheckIn(), request.ngayCheckOut());
        if (soDem <= 0) {
            throw new RuntimeException("Số đêm không hợp lệ");
        }

        DonKhachSan don = new DonKhachSan();
        don.setMaDon(generateBookingCode("DKS"));
        don.setKhachHang(khachHang);
        don.setHoSoKinhDoanh(khachSan.getHoSoKinhDoanh());
        don.setNgayTao(LocalDateTime.now());
        don.setTenNguoiDat(request.tenNguoiDat());
        don.setSdtNguoiDat(request.sdtNguoiDat());
        don.setEmailNguoiDat(request.emailNguoiDat());
        don.setGhiChu(request.ghiChu());
        don.setNgayCheckIn(request.ngayCheckIn());
        don.setNgayCheckOut(request.ngayCheckOut());
        don.setSoDem(soDem);
        don.setSoKhach(request.soKhach());
        don.setGioNhanPhongDuKien(request.gioNhanPhongDuKien());
        don.setTrangThai(TrangThaiDon.CHO_THANH_TOAN);
        don.setHoldExpiredAt(LocalDateTime.now().plusMinutes(20));
        don.setPaymentExpiredAt(LocalDateTime.now().plusMinutes(20));

        List<HotelBookingRoomResponse> roomResponses = new ArrayList<>();
        List<DonKhachSanChiTiet> chiTietList = new ArrayList<>();
        List<DatPhong> datPhongList = new ArrayList<>();

        double tongTienGoc = 0.0;
        for (HotelBookingRoomRequest roomRequest : request.rooms()) {
            Phong phong = phongRepository.findById(roomRequest.roomId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng: " + roomRequest.roomId()));

            if (!phong.getKhachSan().getIdTaiSan().equals(khachSan.getIdTaiSan())) {
                throw new RuntimeException("Phòng không thuộc khách sạn đã chọn: " + phong.getId());
            }

            if (phong.getTrangThai() != TrangThaiPhong.SAN_SANG) {
                throw new RuntimeException("Phòng không ở trạng thái SẴN SÀNG: " + phong.getTenPhong());
            }

            Integer daDat = donKhachSanChiTietRepository.sumBookedQuantityByRoomAndDateRange(
                    phong.getId(),
                    request.ngayCheckIn(),
                    request.ngayCheckOut(),
                    List.of(TrangThaiDon.CHO_THANH_TOAN, TrangThaiDon.DA_THANH_TOAN, TrangThaiDon.DA_XAC_NHAN, TrangThaiDon.DANG_PHUC_VU)
            );

            int tonKho = (phong.getSoLuongPhong() == null ? 0 : phong.getSoLuongPhong()) - (daDat == null ? 0 : daDat);
            if (roomRequest.soLuong() > tonKho) {
                throw new RuntimeException("Không đủ số lượng phòng cho " + phong.getTenPhong() + ". Còn lại: " + Math.max(tonKho, 0));
            }

            double donGia = (phong.getGiaCoBan() == null ? 0.0 : phong.getGiaCoBan());
            double thanhTien = donGia * roomRequest.soLuong() * soDem;
            tongTienGoc += thanhTien;

            DonKhachSanChiTiet chiTiet = new DonKhachSanChiTiet();
            chiTiet.setDonKhachSan(don);
            chiTiet.setPhong(phong);
            chiTiet.setTenPhongTaiThoiDiemDat(phong.getTenPhong());
            chiTiet.setSoLuong(roomRequest.soLuong());
            chiTiet.setDonGiaTaiThoiDiemDat(donGia);
            chiTiet.setSoDem(soDem);
            chiTiet.setThanhTien(thanhTien);
            chiTietList.add(chiTiet);

            DatPhong datPhong = new DatPhong();
            datPhong.setDonDatCho(don);
            datPhong.setPhong(phong);
            datPhong.setNgayCheckIn(request.ngayCheckIn());
            datPhong.setNgayCheckOut(request.ngayCheckOut());
            datPhong.setGhiChuKhachHangPhongDonDat(request.ghiChu());
            datPhongList.add(datPhong);

            roomResponses.add(new HotelBookingRoomResponse(
                    phong.getId(),
                    phong.getTenPhong(),
                    roomRequest.soLuong(),
                    donGia,
                    soDem,
                    thanhTien
            ));
        }

        if (request.expectedTotalAmount() != null && Math.abs(request.expectedTotalAmount() - tongTienGoc) > 1.0) {
            throw new RuntimeException("Tổng tiền không khớp với dữ liệu hệ thống. Vui lòng tải lại trang và thử lại.");
        }

        don.setTongTienGoc(tongTienGoc);
        don.setTienKhuyenMai(0.0);
        don.setTongTienThanhToan(tongTienGoc);
        DonKhachSan saved = donKhachSanRepository.save(don);
        for (DonKhachSanChiTiet chiTiet : chiTietList) {
            chiTiet.setDonKhachSan(saved);
        }
        List<DonKhachSanChiTiet> savedChiTiet = donKhachSanChiTietRepository.saveAll(chiTietList);
        saved.setChiTietDon(savedChiTiet);
        for (DatPhong datPhong : datPhongList) {
            datPhong.setDonDatCho(saved);
        }
        datPhongRepository.saveAll(datPhongList);
        hotelBookingPaymentHoldService.putPending(saved);

        return new HotelBookingResponse(
                saved.getId(),
                saved.getMaDon(),
                khachSan.getIdTaiSan(),
                khachSan.getTen(),
                saved.getNgayCheckIn(),
                saved.getNgayCheckOut(),
                saved.getSoDem(),
                saved.getSoKhach(),
                saved.getGioNhanPhongDuKien(),
                saved.getTongTienGoc(),
                saved.getTienKhuyenMai(),
                saved.getTongTienThanhToan(),
                saved.getTrangThai(),
                saved.getPaymentExpiredAt(),
                calculatePaymentExpiresInSeconds(saved.getPaymentExpiredAt()),
                roomResponses
        );
    }

    @Transactional
    public RestaurantBookingResponse createRestaurantBooking(String username, CreateRestaurantBookingRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!(user instanceof KhachHang khachHang)) {
            throw new RuntimeException("Chỉ khách hàng mới có thể đặt nhà hàng");
        }

        NhaHang nhaHang = nhaHangRepository.findById(request.restaurantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng"));

        LocalDateTime start = LocalDateTime.of(request.date(), request.time());
        if (!start.isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Thời gian đặt bàn phải ở tương lai");
        }
        LocalDateTime end = start.plusHours(2);

        List<Ban> candidateTables = banRepository.findByNhaHang_IdTaiSan(nhaHang.getIdTaiSan()).stream()
                .filter(table -> Boolean.FALSE.equals(table.getDeleted()))
                .filter(table -> table.getSoChoNgoi() != null && table.getSoChoNgoi() > 0)
                .filter(table -> !donNhaHangRepository.existsOverlappingReservationByTableAndTimeSlot(
                        table.getId(),
                        start,
                        end,
                        List.of(TrangThaiDon.CHO_THANH_TOAN, TrangThaiDon.DA_THANH_TOAN, TrangThaiDon.DA_XAC_NHAN, TrangThaiDon.DANG_PHUC_VU)
                ))
                .toList();

        if (candidateTables.isEmpty()) {
            throw new RuntimeException("Hiện không còn bàn trống cho khung giờ đã chọn");
        }

        List<Ban> assignedTables = assignTablesByPreference(candidateTables, request.soNguoi(), khachHang, request.ghiChu());
        if (assignedTables.isEmpty()) {
            throw new RuntimeException("Không thể điều phối bàn phù hợp với số lượng khách");
        }

        int totalSeats = assignedTables.stream().map(Ban::getSoChoNgoi).reduce(0, Integer::sum);
        double tienCoc = 0.0;
        double tongTien = tienCoc;

        DonNhaHang don = new DonNhaHang();
        don.setMaDon(generateBookingCode("DNH"));
        don.setKhachHang(khachHang);
        don.setHoSoKinhDoanh(nhaHang.getHoSoKinhDoanh());
        don.setNgayTao(LocalDateTime.now());
        don.setTenNguoiDat(request.tenNguoiDat());
        don.setSdtNguoiDat(request.sdtNguoiDat());
        don.setEmailNguoiDat(request.emailNguoiDat());
        don.setGhiChu(request.ghiChu());
        don.setTrangThai(TrangThaiDon.CHO_THANH_TOAN);
        don.setHoldExpiredAt(LocalDateTime.now().plusMinutes(20));
        don.setPaymentExpiredAt(LocalDateTime.now().plusMinutes(20));
        don.setNgayGioBatDau(start);
        don.setNgayGioKetThuc(end);
        don.setSoNguoi(request.soNguoi());
        don.setTienCoc(tienCoc);
        don.setCoDatMonTruoc(false);
        don.setTongTienGoc(tongTien);
        don.setTienKhuyenMai(0.0);
        don.setTongTienThanhToan(tongTien);

        List<DonNhaHangBan> links = new ArrayList<>();
        for (Ban table : assignedTables) {
            DonNhaHangBan link = new DonNhaHangBan();
            link.setDonNhaHang(don);
            link.setBan(table);
            links.add(link);
        }

        don.setBanDaGan(links);
        DonNhaHang saved = donNhaHangRepository.save(don);
        hotelBookingPaymentHoldService.putPending(saved);

        return toRestaurantBookingResponse(saved);
    }

    private List<Ban> assignTablesByPreference(List<Ban> candidates, int guests, KhachHang khachHang, String note) {
        List<Ban> sorted = new ArrayList<>(candidates);
        String preferredZone = resolvePreferredZone(khachHang, note);

        sorted.sort((a, b) -> {
            int seatsA = a.getSoChoNgoi() == null ? 0 : a.getSoChoNgoi();
            int seatsB = b.getSoChoNgoi() == null ? 0 : b.getSoChoNgoi();

            boolean matchA = preferredZone != null && a.getViTriSanh() != null
                    && a.getViTriSanh().toLowerCase().contains(preferredZone);
            boolean matchB = preferredZone != null && b.getViTriSanh() != null
                    && b.getViTriSanh().toLowerCase().contains(preferredZone);

            if (matchA != matchB) {
                return matchA ? -1 : 1;
            }

            int wasteA = Math.max(seatsA - guests, 0);
            int wasteB = Math.max(seatsB - guests, 0);
            if (wasteA != wasteB) {
                return Integer.compare(wasteA, wasteB);
            }

            return Integer.compare(seatsA, seatsB);
        });

        for (Ban table : sorted) {
            int seats = table.getSoChoNgoi() == null ? 0 : table.getSoChoNgoi();
            if (seats >= guests) {
                return List.of(table);
            }
        }

        List<Ban> picked = new ArrayList<>();
        int current = 0;
        String zone = null;
        for (Ban table : sorted) {
            if (zone == null) {
                zone = table.getViTriSanh();
            }
            if (zone != null && table.getViTriSanh() != null && !zone.equalsIgnoreCase(table.getViTriSanh())) {
                continue;
            }
            picked.add(table);
            current += table.getSoChoNgoi() == null ? 0 : table.getSoChoNgoi();
            if (current >= guests) {
                return picked;
            }
        }

        picked.clear();
        current = 0;
        for (Ban table : sorted) {
            picked.add(table);
            current += table.getSoChoNgoi() == null ? 0 : table.getSoChoNgoi();
            if (current >= guests) {
                return picked;
            }
        }

        return List.of();
    }

    private String resolvePreferredZone(KhachHang khachHang, String note) {
        if (note != null && !note.isBlank()) {
            String value = note.toLowerCase();
            if (value.contains("sân thượng") || value.contains("ngoài trời")) return "ngoài";
            if (value.contains("phòng riêng")) return "riêng";
            if (value.contains("cửa sổ")) return "cửa";
        }

        if (khachHang.getTuKhoaGanDay() != null) {
            for (String keyword : khachHang.getTuKhoaGanDay()) {
                if (keyword == null) continue;
                String value = keyword.toLowerCase();
                if (value.contains("ngoài") || value.contains("riêng") || value.contains("cửa")) {
                    return value;
                }
            }
        }

        return null;
    }

    @Transactional
    public RestaurantBookingResponse mockPayRestaurantBooking(String username, String bookingId) {
        DonNhaHang booking = donNhaHangRepository.findByIdAndKhachHang_Username(bookingId, username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt nhà hàng"));

        if (booking.getTrangThai() == TrangThaiDon.DA_HUY) {
            throw new RuntimeException("Đơn đã bị hủy, không thể thanh toán giả lập");
        }

        if (booking.getTrangThai() != TrangThaiDon.DA_THANH_TOAN) {
            booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);
            hotelBookingPaymentHoldService.removePending(booking.getId());
            booking = donNhaHangRepository.save(booking);
        }

        return toRestaurantBookingResponse(booking);
    }

    @Transactional
    public HotelBookingResponse mockPayHotelBooking(String username, String bookingId) {
        DonKhachSan booking = donKhachSanRepository.findByIdAndKhachHang_Username(bookingId, username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt khách sạn"));

        if (booking.getTrangThai() == TrangThaiDon.DA_HUY) {
            throw new RuntimeException("Đơn đã bị hủy, không thể thanh toán giả lập");
        }

        if (booking.getTrangThai() != TrangThaiDon.DA_THANH_TOAN) {
            booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);
            hotelBookingPaymentHoldService.removePending(booking.getId());
            booking = donKhachSanRepository.save(booking);
        }

        return toHotelBookingResponse(booking);
    }

    @Transactional
    public HotelBookingResponse confirmHotelBookingPayment(String username, String bookingId) {
        DonKhachSan booking = donKhachSanRepository.findByIdAndKhachHang_Username(bookingId, username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt khách sạn"));

        if (booking.getTrangThai() != TrangThaiDon.CHO_THANH_TOAN) {
            throw new RuntimeException("Đơn không còn ở trạng thái chờ thanh toán");
        }

        if (booking.getPaymentExpiredAt() != null && booking.getPaymentExpiredAt().isBefore(LocalDateTime.now())) {
            booking.setTrangThai(TrangThaiDon.DA_HUY);
            booking.setCancelledAt(LocalDateTime.now());
            booking.setCancelReason("Quá thời gian thanh toán");
            donKhachSanRepository.save(booking);
            hotelBookingPaymentHoldService.removePending(booking.getId());
            throw new RuntimeException("Đơn đã quá thời gian thanh toán và đã bị hủy");
        }

        if (!hotelBookingPaymentHoldService.isPendingInRedis(booking.getId())) {
            throw new RuntimeException("Phiên thanh toán đã hết hạn");
        }

        booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);
        hotelBookingPaymentHoldService.removePending(booking.getId());
        DonKhachSan saved = donKhachSanRepository.save(booking);

        return toHotelBookingResponse(saved);
    }

    private RestaurantBookingResponse toRestaurantBookingResponse(DonNhaHang booking) {
        NhaHang nhaHang = booking.getBanDaGan().isEmpty()
                ? null
                : booking.getBanDaGan().get(0).getBan().getNhaHang();

        List<RestaurantAssignedTableResponse> tables = booking.getBanDaGan().stream()
                .map(link -> new RestaurantAssignedTableResponse(
                        link.getBan().getId(),
                        link.getBan().getTenBan(),
                        link.getBan().getSoChoNgoi(),
                        link.getBan().getViTriSanh()
                ))
                .toList();

        return new RestaurantBookingResponse(
                booking.getId(),
                booking.getMaDon(),
                nhaHang != null ? nhaHang.getIdTaiSan() : null,
                nhaHang != null ? nhaHang.getTen() : null,
                booking.getNgayGioBatDau(),
                booking.getNgayGioKetThuc(),
                booking.getSoNguoi(),
                booking.getTienCoc(),
                booking.getTongTienThanhToan(),
                booking.getTrangThai(),
                booking.getPaymentExpiredAt(),
                calculatePaymentExpiresInSeconds(booking.getPaymentExpiredAt()),
                tables
        );
    }

    private HotelBookingResponse toHotelBookingResponse(DonKhachSan booking) {
        KhachSan khachSan = booking.getChiTietDon().isEmpty()
                ? null
                : booking.getChiTietDon().get(0).getPhong().getKhachSan();

        List<HotelBookingRoomResponse> rooms = booking.getChiTietDon().stream()
                .map(ct -> new HotelBookingRoomResponse(
                        ct.getPhong().getId(),
                        ct.getTenPhongTaiThoiDiemDat(),
                        ct.getSoLuong(),
                        ct.getDonGiaTaiThoiDiemDat(),
                        ct.getSoDem(),
                        ct.getThanhTien()
                ))
                .toList();

        return new HotelBookingResponse(
                booking.getId(),
                booking.getMaDon(),
                khachSan != null ? khachSan.getIdTaiSan() : null,
                khachSan != null ? khachSan.getTen() : null,
                booking.getNgayCheckIn(),
                booking.getNgayCheckOut(),
                booking.getSoDem(),
                booking.getSoKhach(),
                booking.getGioNhanPhongDuKien(),
                booking.getTongTienGoc(),
                booking.getTienKhuyenMai(),
                booking.getTongTienThanhToan(),
                booking.getTrangThai(),
                booking.getPaymentExpiredAt(),
                calculatePaymentExpiresInSeconds(booking.getPaymentExpiredAt()),
                rooms
        );
    }

    private long calculatePaymentExpiresInSeconds(LocalDateTime paymentExpiredAt) {
        if (paymentExpiredAt == null) {
            return 0L;
        }

        long seconds = java.time.Duration.between(LocalDateTime.now(), paymentExpiredAt).getSeconds();
        return Math.max(seconds, 0L);
    }

    private String generateBookingCode(String prefix) {
        String code;
        do {
            int random = ThreadLocalRandom.current().nextInt(100000, 1000000);
            code = prefix + random;
        } while (donDatChoRepository.existsByMaDon(code));
        return code;
    }
}
