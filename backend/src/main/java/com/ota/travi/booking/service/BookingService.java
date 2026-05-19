package com.traviota.booking.service;

import com.traviota.booking.domain.dto.request.BookingRequest;
import com.traviota.booking.domain.entities.DonDatCho;
import com.traviota.booking.domain.entities.DonKhachSan;
import com.traviota.booking.domain.entities.DonNhaHang;
import com.traviota.booking.domain.enums.TrangThaiDon;
import com.traviota.booking.repository.DonDatChoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final DonDatChoRepository donDatChoRepository;

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
}