package com.ota.travi.booking.service;

import com.ota.travi.booking.domain.dto.request.BookingRequest;
import com.ota.travi.booking.domain.entities.DonDatCho;
import com.ota.travi.booking.domain.entities.DonKhachSan;
import com.ota.travi.booking.domain.entities.DonNhaHang;
import com.ota.travi.booking.domain.enums.TrangThaiDon;
import com.ota.travi.booking.repository.BookingDonDatChoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingDonDatChoRepository donDatChoRepository;

    // Task 2.1 & Task 6.3 (ACID): Khởi tạo đơn hàng an toàn
    @Transactional(rollbackFor = Exception.class)
    public DonDatCho createBooking(BookingRequest request) {
        DonDatCho don;

        // Rẽ nhánh xử lý cho Khách sạn hoặc Nhà hàng
        if (request.getLoaiDon() == BookingRequest.LoaiDon.KHACH_SAN) {
            DonKhachSan donKhachSan = new DonKhachSan();
            donKhachSan.setIdPhong(request.getIdPhong());
            donKhachSan.setNgayCheckIn(request.getNgayCheckIn());
            donKhachSan.setNgayCheckOut(request.getNgayCheckOut());
            
            // Tính tổng tiền dựa trên số ngày (Giả lập 500k/ngày)
            long soNgay = ChronoUnit.DAYS.between(request.getNgayCheckIn(), request.getNgayCheckOut());
            donKhachSan.setTongTien(BigDecimal.valueOf(500000).multiply(BigDecimal.valueOf(soNgay)));
            don = donKhachSan;
        } else {
            DonNhaHang donNhaHang = new DonNhaHang();
            donNhaHang.setIdBan(request.getIdBan());
            donNhaHang.setNgayGioDatCho(request.getNgayGioDatCho());
            donNhaHang.setSoNguoi(request.getSoNguoi());
            
            // Tính tổng tiền đặt cọc bàn (Giả lập 200k)
            donNhaHang.setTongTien(BigDecimal.valueOf(200000));
            don = donNhaHang;
        }

        // Gán các thông tin chung của người đặt
        don.setMaDon(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        don.setTenNguoiDat(request.getTenNguoiDat());
        don.setSdtNguoiDat(request.getSdtNguoiDat());
        don.setEmailNguoiDat(request.getEmailNguoiDat());
        don.setTrangThai(TrangThaiDon.CHO_THANH_TOAN);
        
        // Lưu xuống Database
        return donDatChoRepository.save(don);
    }
    // === CODE BỔ SUNG CHO GIAI ĐOẠN 3: PARTNER APIs ===

    // Task 3.1: Lấy danh sách đơn hàng cho Đối tác quản lý (phân loại theo trạng thái)
    public java.util.List<DonDatCho> getPartnerBookings(String filterStatus) {
        // Thực tế sẽ dùng câu Query trong Repository, ở đây mình lọc nhanh bằng Stream
        return donDatChoRepository.findAll().stream()
                .filter(don -> filterStatus == null || don.getTrangThai().name().equalsIgnoreCase(filterStatus))
                .collect(java.util.stream.Collectors.toList());
    }

    // Task 3.2a: API Check-in khi khách đến nhận phòng/bàn
    @Transactional(rollbackFor = Exception.class)
    public DonDatCho checkIn(Long id) {
        DonDatCho don = donDatChoRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        
        // Đổi trạng thái đơn thành ĐANG PHỤC VỤ
        don.setTrangThai(com.ota.travi.booking.domain.enums.TrangThaiDon.DANG_PHUC_VU);
        // TODO: Đổi trạng thái Phòng/Bàn thực tế trong DB thành "Đang sử dụng"
        
        return donDatChoRepository.save(don);
    }

    // Task 3.2b: API Check-out khi khách dùng xong và rời đi
    @Transactional(rollbackFor = Exception.class)
    public DonDatCho checkOut(Long id) {
        DonDatCho don = donDatChoRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        
        // Đổi trạng thái đơn thành ĐÃ HOÀN THANH
        don.setTrangThai(com.ota.travi.booking.domain.enums.TrangThaiDon.DA_HOAN_THANH);
        // TODO: Đổi trạng thái Phòng/Bàn thực tế thành "Trống / Cần dọn dẹp" và cộng doanh thu
        
        return donDatChoRepository.save(don);
    }

    // Task 3.2c: API No-show xử lý khi khách đặt nhưng bùng kèo không đến
    @Transactional(rollbackFor = Exception.class)
    public DonDatCho noShow(Long id) {
        DonDatCho don = donDatChoRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        
        // Khách không đến thì hủy đơn
        don.setTrangThai(com.ota.travi.booking.domain.enums.TrangThaiDon.DA_HUY);
        // Phạt 100% tiền nếu đơn này đã được thanh toán trước đó (giữ nguyên tiền, không hoàn)
        
        return donDatChoRepository.save(don);
    }
}
