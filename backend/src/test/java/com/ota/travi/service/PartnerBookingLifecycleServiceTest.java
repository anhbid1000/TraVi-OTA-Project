package com.ota.travi.service;

import com.ota.travi.dto.response.PartnerBookingCompletionResponse;
import com.ota.travi.entity.DoiTac;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.repository.DonDatChoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerBookingLifecycleServiceTest {

    @Mock
    private DonDatChoRepository donDatChoRepository;

    @Mock
    private CustomerLoyaltyService customerLoyaltyService;

    @InjectMocks
    private PartnerBookingLifecycleService service;

    private DonDatCho booking;

    @BeforeEach
    void setUp() {
        DoiTac partner = new DoiTac();
        partner.setId("partner-1");

        HoSoKinhDoanh profile = new HoSoKinhDoanh();
        profile.setDoiTac(partner);

        booking = new DonDatCho() {};
        booking.setId("booking-1");
        booking.setMaDon("DKS001");
        booking.setHoSoKinhDoanh(profile);
        booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);
    }

    @Test
    void completeBooking_Success_ChangesStatusAndRewardsLoyalty() {
        when(donDatChoRepository.findByIdAndDeletedFalse("booking-1")).thenReturn(Optional.of(booking));
        when(donDatChoRepository.save(any(DonDatCho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartnerBookingCompletionResponse response = service.completeBooking("partner-1", "booking-1");

        assertEquals(TrangThaiDon.DA_HOAN_THANH, booking.getTrangThai());
        assertEquals(TrangThaiDon.DA_HOAN_THANH, response.trangThai());
        assertTrue(response.loyaltyRewardProcessed());
        verify(customerLoyaltyService, times(1)).processBookingCompletionReward(booking);
    }

    @Test
    void completeBooking_FailsIfPartnerDoesNotOwnBooking() {
        when(donDatChoRepository.findByIdAndDeletedFalse("booking-1")).thenReturn(Optional.of(booking));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.completeBooking("partner-2", "booking-1")
        );

        assertEquals(403, exception.getStatusCode().value());
        verify(donDatChoRepository, never()).save(any(DonDatCho.class));
        verifyNoInteractions(customerLoyaltyService);
    }

    @Test
    void completeBooking_FailsFromInvalidStatus() {
        booking.setTrangThai(TrangThaiDon.CHO_THANH_TOAN);
        when(donDatChoRepository.findByIdAndDeletedFalse("booking-1")).thenReturn(Optional.of(booking));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.completeBooking("partner-1", "booking-1")
        );

        assertEquals(400, exception.getStatusCode().value());
        verify(donDatChoRepository, never()).save(any(DonDatCho.class));
        verifyNoInteractions(customerLoyaltyService);
    }

    @Test
    void completeBooking_AlreadyCompleted_KeepsIdempotentRewardCall() {
        booking.setTrangThai(TrangThaiDon.DA_HOAN_THANH);
        when(donDatChoRepository.findByIdAndDeletedFalse("booking-1")).thenReturn(Optional.of(booking));

        PartnerBookingCompletionResponse response = service.completeBooking("partner-1", "booking-1");

        assertEquals(TrangThaiDon.DA_HOAN_THANH, response.trangThai());
        verify(donDatChoRepository, never()).save(any(DonDatCho.class));
        verify(customerLoyaltyService, times(1)).processBookingCompletionReward(booking);
    }
}
