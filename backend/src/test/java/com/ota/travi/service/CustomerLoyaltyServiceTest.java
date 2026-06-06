package com.ota.travi.service;

import com.ota.travi.dto.response.ExchangeVoucherResponse;
import com.ota.travi.dto.response.LoyaltyProgressResponse;
import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.LichSuDiem;
import com.ota.travi.entity.LoyaltyRule;
import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.HangThanhVien;
import com.ota.travi.enums.LoaiGiaoDichDiem;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.LichSuDiemRepository;
import com.ota.travi.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerLoyaltyServiceTest {

    @Mock
    private KhachHangRepository khachHangRepository;

    @Mock
    private CustomerVoucherRepository customerVoucherRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private LichSuDiemRepository lichSuDiemRepository;

    @Mock
    private LoyaltyRuleService loyaltyRuleService;

    @Mock
    private MilestoneRewardService milestoneRewardService;

    @Mock
    private NotificationEventService notificationEventService;

    @InjectMocks
    private CustomerLoyaltyService customerLoyaltyService;

    private KhachHang khachHang;
    private DonDatCho booking;
    private LoyaltyRule loyaltyRule;
    private Voucher voucher;

    private final String CUSTOMER_ID = "cust123";
    private final Long VOUCHER_ID = 100L;

    @BeforeEach
    void setUp() {
        khachHang = new KhachHang();
        khachHang.setId(CUSTOMER_ID);
        khachHang.setDiemThanhVien(500);
        khachHang.setTongChiTieu(2000000.0);
        khachHang.setHangThanhVien(HangThanhVien.DONG);

        booking = new DonDatCho() {};
        booking.setId("booking_test");
        booking.setKhachHang(khachHang);
        booking.setTrangThai(TrangThaiDon.DA_HOAN_THANH);
        booking.setTongTienThanhToan(1000000.0);

        loyaltyRule = new LoyaltyRule();
        loyaltyRule.setMoneyPerPoint(10000.0); // 10.000 VNĐ = 1 điểm
        loyaltyRule.setSilverMultiplier(1.1);
        loyaltyRule.setSilverThreshold(5000000);
        loyaltyRule.setGoldThreshold(20000000);
        loyaltyRule.setDiamondThreshold(50000000);

        voucher = new Voucher();
        voucher.setId(VOUCHER_ID);
        voucher.setChoPhepDoiBangDiem(true);
        voucher.setDiemCanDoi(300);
        voucher.setTrangThaiUuDai(com.ota.travi.enums.TrangThaiUuDai.DANG_CO_HIEU_LUC);
        voucher.setNgayBatDau(java.time.LocalDate.now().minusDays(1));
        voucher.setNgayKetThuc(java.time.LocalDate.now().plusDays(10));
        voucher.setSoLuongPhatHanh(100);
        voucher.setSoLuongDaDung(0);
    }

    @Test
    void testProcessBookingCompletionReward_Success() {
        // Arrange
        when(loyaltyRuleService.requireActiveRule()).thenReturn(loyaltyRule);
        when(khachHangRepository.findByIdForUpdate(CUSTOMER_ID)).thenReturn(Optional.of(khachHang));
        
        // base points = 1.000.000 / 10.000 = 100
        // multiplier for DONG = 1.0 (default)
        // final earned = 100
        
        // Act
        customerLoyaltyService.processBookingCompletionReward(booking);

        // Assert
        assertEquals(600, khachHang.getDiemThanhVien()); // 500 + 100
        assertEquals(3000000.0, khachHang.getTongChiTieu()); // 2.000.000 + 1.000.000
        
        verify(khachHangRepository, times(1)).save(khachHang);
        verify(lichSuDiemRepository, times(1)).save(any(LichSuDiem.class));
        verify(milestoneRewardService, times(1)).incrementBookingCount(CUSTOMER_ID);
        verify(notificationEventService, times(1)).emitPointEarned(CUSTOMER_ID, 100, "booking_test");
    }

    @Test
    void testProcessBookingCompletionReward_UsesSilverMultiplier() {
        // Arrange
        khachHang.setHangThanhVien(HangThanhVien.BAC);
        when(loyaltyRuleService.requireActiveRule()).thenReturn(loyaltyRule);
        when(khachHangRepository.findByIdForUpdate(CUSTOMER_ID)).thenReturn(Optional.of(khachHang));

        // Act
        customerLoyaltyService.processBookingCompletionReward(booking);

        // Assert
        assertEquals(610, khachHang.getDiemThanhVien()); // 500 + floor(100 * 1.1)
        verify(notificationEventService, times(1)).emitPointEarned(CUSTOMER_ID, 110, "booking_test");
    }

    @Test
    void testProcessBookingCompletionReward_DoesNothingIfBookingNotCompleted() {
        // Arrange
        booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);

        // Act
        customerLoyaltyService.processBookingCompletionReward(booking);

        // Assert
        verifyNoInteractions(loyaltyRuleService, khachHangRepository, lichSuDiemRepository, milestoneRewardService, notificationEventService);
    }

    @Test
    void testProcessBookingCompletionReward_DoesNotRewardTwiceForSameBooking() {
        // Arrange
        when(lichSuDiemRepository.existsByCustomerIdAndLoaiGiaoDichDiemAndGhiChu(
                CUSTOMER_ID,
                LoaiGiaoDichDiem.TICH_DIEM,
                "Tich diem cho don booking_test"
        )).thenReturn(true);

        // Act
        customerLoyaltyService.processBookingCompletionReward(booking);

        // Assert
        verifyNoInteractions(loyaltyRuleService, khachHangRepository, milestoneRewardService, notificationEventService);
        verify(lichSuDiemRepository, never()).save(any(LichSuDiem.class));
    }

    @Test
    void testProcessBookingCompletionReward_EmitsTierUpgrade() {
        // Arrange
        khachHang.setTongChiTieu(4900000.0);
        when(loyaltyRuleService.requireActiveRule()).thenReturn(loyaltyRule);
        when(khachHangRepository.findByIdForUpdate(CUSTOMER_ID)).thenReturn(Optional.of(khachHang));

        // Act
        customerLoyaltyService.processBookingCompletionReward(booking);

        // Assert
        assertEquals(HangThanhVien.BAC, khachHang.getHangThanhVien());
        verify(notificationEventService, times(1)).emitTierUpgraded(CUSTOMER_ID, "BAC", 5900000.0);
    }

    @Test
    void testExchangeVoucher_Success() {
        // Arrange
        when(khachHangRepository.findByIdForUpdate(CUSTOMER_ID)).thenReturn(Optional.of(khachHang));
        when(voucherRepository.findById(VOUCHER_ID)).thenReturn(Optional.of(voucher));
        when(loyaltyRuleService.requireActiveRule()).thenReturn(loyaltyRule);
        when(customerVoucherRepository.save(any(CustomerVoucher.class))).thenAnswer(invocation -> {
            CustomerVoucher cv = invocation.getArgument(0);
            cv.setId(99L);
            return cv;
        });

        // Act
        ExchangeVoucherResponse response = customerLoyaltyService.exchangeVoucher(CUSTOMER_ID, VOUCHER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(99L, response.customerVoucherId());
        assertEquals(200, khachHang.getDiemThanhVien()); // 500 - 300 = 200

        verify(khachHangRepository, times(1)).save(khachHang);
        verify(customerVoucherRepository, times(1)).save(any(CustomerVoucher.class));
        verify(lichSuDiemRepository, times(1)).save(argThat(lichSu -> 
            lichSu.getLoaiGiaoDichDiem() == LoaiGiaoDichDiem.DOI_VOUCHER &&
            lichSu.getSoDiemThayDoi() == -300
        ));
    }

    @Test
    void testExchangeVoucher_InsufficientPoints() {
        // Arrange
        voucher.setDiemCanDoi(1000); // Khách hàng chỉ có 500
        when(khachHangRepository.findByIdForUpdate(CUSTOMER_ID)).thenReturn(Optional.of(khachHang));
        when(voucherRepository.findById(VOUCHER_ID)).thenReturn(Optional.of(voucher));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            customerLoyaltyService.exchangeVoucher(CUSTOMER_ID, VOUCHER_ID);
        });

        assertEquals("Khong du diem de doi voucher", exception.getMessage());
        verify(customerVoucherRepository, never()).save(any(CustomerVoucher.class));
    }

    @Test
    void testGetLoyaltyProgress_CorrectCalculation() {
        // Arrange
        when(khachHangRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(khachHang));
        when(loyaltyRuleService.requireActiveRule()).thenReturn(loyaltyRule);

        // Khách hàng đang có chi tiêu 2.000.000, hạng Bạc cần 5.000.000
        // Progress = 2.000.000 / 5.000.000 = 40%
        
        // Act
        LoyaltyProgressResponse progress = customerLoyaltyService.getLoyaltyProgress(CUSTOMER_ID);

        // Assert
        assertNotNull(progress);
        assertEquals("DONG", progress.currentTier());
        assertEquals("BAC", progress.nextTier());
        assertEquals(2000000.0, progress.totalSpending());
        assertEquals(5000000.0, progress.requiredSpending());
        assertEquals(3000000.0, progress.remainingSpending());
        assertEquals(40.0, progress.progressPercent());
    }
}
