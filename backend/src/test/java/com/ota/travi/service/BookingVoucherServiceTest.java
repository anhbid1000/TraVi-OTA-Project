package com.ota.travi.service;

import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.LoaiGiamGia;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.enums.TrangThaiUuDai;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.DonDatChoRepository;
import com.ota.travi.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingVoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private CustomerVoucherRepository customerVoucherRepository;

    @Mock
    private DonDatChoRepository donDatChoRepository;

    @Mock
    private VoucherReserveService voucherReserveService;

    @Mock
    private NotificationEventService notificationEventService;

    @Mock
    private PromotionAnalyticsService promotionAnalyticsService;

    @InjectMocks
    private BookingVoucherService bookingVoucherService;

    private DonDatCho booking;
    private KhachHang khachHang;
    private Voucher voucher;
    private CustomerVoucher customerVoucher;

    private final String BOOKING_ID = "booking123";
    private final String CUSTOMER_ID = "cust123";
    private final String MA_VOUCHER = "SALE50";
    private final Long VOUCHER_ID = 1L;

    @BeforeEach
    void setUp() {
        khachHang = new KhachHang();
        khachHang.setId(CUSTOMER_ID);

        booking = new DonDatCho() {};
        booking.setId(BOOKING_ID);
        booking.setKhachHang(khachHang);
        booking.setTrangThai(TrangThaiDon.CHO_THANH_TOAN);
        booking.setTongTienGoc(1000000.0);

        voucher = new Voucher();
        voucher.setId(VOUCHER_ID);
        voucher.setMaVoucher(MA_VOUCHER);
        voucher.setTrangThaiUuDai(TrangThaiUuDai.DANG_CO_HIEU_LUC);
        voucher.setNgayBatDau(LocalDate.now().minusDays(1));
        voucher.setNgayKetThuc(LocalDate.now().plusDays(10));
        voucher.setDonHangToiThieu(500000.0);
        voucher.setLoaiGiamGia(LoaiGiamGia.PHAN_TRAM);
        voucher.setMucGiam(10.0); // 10%
        voucher.setGiaTriGiamToiDa(200000.0);
        voucher.setSoLuongPhatHanh(100);
        voucher.setSoLuongDaDung(10);
        voucher.setUsageLimitPerUser(1);

        customerVoucher = new CustomerVoucher();
        customerVoucher.setId(10L);
        customerVoucher.setVoucherId(VOUCHER_ID);
        customerVoucher.setCustomerId(CUSTOMER_ID);
        customerVoucher.setTrangThai(TrangThaiCustomerVoucher.CHUA_DUNG);
    }

    @Test
    void testApplyVoucher_Success() {
        // Arrange
        when(donDatChoRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(voucherRepository.findByMaVoucher(MA_VOUCHER)).thenReturn(Optional.of(voucher));
        when(customerVoucherRepository.findByCustomerIdOrderByIssuedAtDesc(CUSTOMER_ID)).thenReturn(List.of(customerVoucher));
        when(donDatChoRepository.save(any(DonDatCho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingVoucherService.VoucherCheckoutResponse response = bookingVoucherService.applyVoucherToBooking(BOOKING_ID, CUSTOMER_ID, MA_VOUCHER);

        // Assert
        assertNotNull(response);
        assertTrue(response.getIsApplied());
        assertEquals(0, Double.compare(100000.0, response.getDiscountAmount().doubleValue())); // 10% of 1.000.000 = 100.000
        assertEquals(0, Double.compare(900000.0, response.getFinalAmount().doubleValue()));

        verify(voucherReserveService, times(1)).reserveVoucher(CUSTOMER_ID, customerVoucher.getId(), 1800);
        verify(notificationEventService, times(1)).emitVoucherApplied(eq(CUSTOMER_ID), eq(VOUCHER_ID), anyDouble());
        verify(promotionAnalyticsService, times(1)).trackVoucherUsage(VOUCHER_ID, CUSTOMER_ID);
    }

    @Test
    void testApplyVoucher_FailsIfExpired() {
        // Arrange
        voucher.setNgayKetThuc(LocalDate.now().minusDays(1)); // Đã hết hạn
        when(donDatChoRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(voucherRepository.findByMaVoucher(MA_VOUCHER)).thenReturn(Optional.of(voucher));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingVoucherService.applyVoucherToBooking(BOOKING_ID, CUSTOMER_ID, MA_VOUCHER);
        });

        assertEquals("Voucher has expired", exception.getMessage());
        verify(voucherReserveService, never()).reserveVoucher(anyString(), anyLong(), anyInt());
    }

    @Test
    void testPreviewVoucher_ReturnsIneligibleIfExpired() {
        // Arrange
        voucher.setNgayKetThuc(LocalDate.now().minusDays(1));
        when(donDatChoRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(voucherRepository.findByMaVoucher(MA_VOUCHER)).thenReturn(Optional.of(voucher));

        // Act
        BookingVoucherService.VoucherPreviewResponse response =
                bookingVoucherService.previewVoucherDiscount(BOOKING_ID, CUSTOMER_ID, MA_VOUCHER);

        // Assert
        assertFalse(response.getIsEligible());
        assertEquals("Voucher has expired", response.getIneligibilityReason());
    }

    @Test
    void testApplyVoucher_FailsIfUnderMinAmount() {
        // Arrange
        booking.setTongTienGoc(400000.0); // Không đủ min amount (500.000)
        when(donDatChoRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(voucherRepository.findByMaVoucher(MA_VOUCHER)).thenReturn(Optional.of(voucher));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingVoucherService.applyVoucherToBooking(BOOKING_ID, CUSTOMER_ID, MA_VOUCHER);
        });

        assertTrue(exception.getMessage().contains("Order amount does not meet minimum requirement"));
    }

    @Test
    void testApplyVoucher_FailsIfUsageLimitExceeded() {
        // Arrange
        CustomerVoucher usedVoucher = new CustomerVoucher();
        usedVoucher.setVoucherId(VOUCHER_ID);
        usedVoucher.setCustomerId(CUSTOMER_ID);
        usedVoucher.setTrangThai(TrangThaiCustomerVoucher.DA_DUNG);

        when(donDatChoRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(voucherRepository.findByMaVoucher(MA_VOUCHER)).thenReturn(Optional.of(voucher));
        // Khách hàng đã dùng 1 lần (đạt max = 1)
        when(customerVoucherRepository.findByCustomerIdOrderByIssuedAtDesc(CUSTOMER_ID)).thenReturn(List.of(usedVoucher));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingVoucherService.applyVoucherToBooking(BOOKING_ID, CUSTOMER_ID, MA_VOUCHER);
        });

        assertEquals("Customer usage limit reached for this voucher", exception.getMessage());
    }

    @Test
    void testRemoveVoucher_Success() {
        // Arrange
        booking.setVoucherId(customerVoucher.getId());
        booking.setTongTienGoc(1000000.0);
        booking.setTienKhuyenMai(100000.0);
        booking.setTongTienThanhToan(900000.0);
        
        customerVoucher.setTrangThai(TrangThaiCustomerVoucher.RESERVED);

        when(donDatChoRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(customerVoucherRepository.findById(customerVoucher.getId())).thenReturn(Optional.of(customerVoucher));
        when(donDatChoRepository.save(any(DonDatCho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        bookingVoucherService.removeVoucherFromBooking(BOOKING_ID, CUSTOMER_ID);

        // Assert
        assertNull(booking.getVoucherId());
        assertEquals(0.0, booking.getTienKhuyenMai());
        assertEquals(1000000.0, booking.getTongTienThanhToan());

        verify(voucherReserveService, times(1)).releaseVoucher(customerVoucher.getId());
        verify(notificationEventService, times(1)).emitVoucherReleased(CUSTOMER_ID, VOUCHER_ID);
    }
    
    @Test
    void testRemoveVoucher_FailsIfNotOwner() {
        // Arrange
        booking.getKhachHang().setId("OTHER_USER");

        when(donDatChoRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        // Act & Assert
        BusinessConflictException exception = assertThrows(BusinessConflictException.class, () -> {
            bookingVoucherService.removeVoucherFromBooking(BOOKING_ID, CUSTOMER_ID);
        });

        assertEquals("Customer does not own this booking", exception.getMessage());
    }
}
