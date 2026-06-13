package com.ota.travi.service;

import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.DonDatChoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCallbackServiceTest {

    @Mock
    private CustomerVoucherRepository customerVoucherRepository;

    @Mock
    private DonDatChoRepository donDatChoRepository;

    @Mock
    private VoucherReserveService voucherReserveService;

    @Mock
    private PromotionAnalyticsService promotionAnalyticsService;

    @Mock
    private NotificationEventService notificationEventService;

    @InjectMocks
    private PaymentCallbackService service;

    private DonDatCho booking;

    @BeforeEach
    void setUp() {
        KhachHang customer = new KhachHang();
        customer.setId("cust-1");

        booking = new DonDatCho() {};
        booking.setId("booking-1");
        booking.setKhachHang(customer);
        booking.setTrangThai(TrangThaiDon.CHO_THANH_TOAN);
    }

    @Test
    void onPaymentSuccess_MarksPaidWithoutEmittingPointReward() {
        when(donDatChoRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(donDatChoRepository.save(any(DonDatCho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.onPaymentSuccess("booking-1", "cust-1");

        assertEquals(TrangThaiDon.DA_THANH_TOAN, booking.getTrangThai());
        verify(donDatChoRepository, times(1)).save(booking);
        verify(notificationEventService, never()).emitPointEarned(anyString(), anyInt(), anyString());
    }
}
