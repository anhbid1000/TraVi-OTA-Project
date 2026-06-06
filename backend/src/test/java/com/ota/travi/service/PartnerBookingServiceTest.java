package com.ota.travi.service;

import com.ota.travi.dto.request.PartnerUpdateBookingStatusRequest;
import com.ota.travi.dto.response.PartnerBookingDetailResponse;
import com.ota.travi.dto.response.PartnerBookingListItemResponse;
import com.ota.travi.entity.DoiTac;
import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.DonDatChoRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.NhaHangRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartnerBookingServiceTest {

    @Mock
    private DonDatChoRepository donDatChoRepository;

    @Mock
    private KhachSanRepository khachSanRepository;

    @Mock
    private NhaHangRepository nhaHangRepository;

    @InjectMocks
    private PartnerBookingService partnerBookingService;

    private HoSoKinhDoanh hoSo;

    @BeforeEach
    void setUp() {
        DoiTac doiTac = new DoiTac();
        doiTac.setId("partner-1");

        hoSo = new HoSoKinhDoanh();
        hoSo.setIdHoSo("hoso-1");
        hoSo.setDoiTac(doiTac);
    }

    @Test
    void updateBookingStatus_ShouldTransitionToDaXacNhan_WhenCurrentIsDaThanhToan() {
        DonKhachSan booking = new DonKhachSan();
        booking.setId("booking-1");
        booking.setHoSoKinhDoanh(hoSo);
        booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);

        when(donDatChoRepository.findByIdAndDeletedFalse("booking-1")).thenReturn(Optional.of(booking));
        when(donDatChoRepository.save(booking)).thenReturn(booking);

        PartnerBookingDetailResponse response = partnerBookingService.updateBookingStatus(
                "partner-1",
                "booking-1",
                new PartnerUpdateBookingStatusRequest(TrangThaiDon.DA_XAC_NHAN, null)
        );

        assertEquals(TrangThaiDon.DA_XAC_NHAN, response.trangThai());
        verify(donDatChoRepository).save(booking);
    }

    @Test
    void updateBookingStatus_ShouldRejectToDaHuy_WhenReasonProvided() {
        DonNhaHang booking = new DonNhaHang();
        booking.setId("booking-2");
        booking.setHoSoKinhDoanh(hoSo);
        booking.setTrangThai(TrangThaiDon.DA_XAC_NHAN);

        when(donDatChoRepository.findByIdAndDeletedFalse("booking-2")).thenReturn(Optional.of(booking));
        when(donDatChoRepository.save(booking)).thenReturn(booking);

        PartnerBookingDetailResponse response = partnerBookingService.updateBookingStatus(
                "partner-1",
                "booking-2",
                new PartnerUpdateBookingStatusRequest(TrangThaiDon.DA_HUY, "Khong du nhan su")
        );

        assertEquals(TrangThaiDon.DA_HUY, response.trangThai());
        assertEquals("Khong du nhan su", response.cancelReason());
        assertNotNull(response.cancelledAt());
    }

    @Test
    void updateBookingStatus_ShouldThrowValidation_WhenTransitionInvalid() {
        DonKhachSan booking = new DonKhachSan();
        booking.setId("booking-3");
        booking.setHoSoKinhDoanh(hoSo);
        booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);

        when(donDatChoRepository.findByIdAndDeletedFalse("booking-3")).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> partnerBookingService.updateBookingStatus(
                        "partner-1",
                        "booking-3",
                        new PartnerUpdateBookingStatusRequest(TrangThaiDon.DA_HOAN_THANH, null)
                )
        );

        assertEquals("Khong the chuyen tu DA_THANH_TOAN sang DA_HOAN_THANH", exception.getMessage());
        verify(donDatChoRepository, never()).save(any());
    }

    @Test
    void updateBookingStatus_ShouldThrowValidation_WhenRejectWithoutReason() {
        DonKhachSan booking = new DonKhachSan();
        booking.setId("booking-4");
        booking.setHoSoKinhDoanh(hoSo);
        booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);

        when(donDatChoRepository.findByIdAndDeletedFalse("booking-4")).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> partnerBookingService.updateBookingStatus(
                        "partner-1",
                        "booking-4",
                        new PartnerUpdateBookingStatusRequest(TrangThaiDon.DA_HUY, "")
                )
        );

        assertEquals("Ly do tu choi don la bat buoc", exception.getMessage());
    }

    @Test
    void updateBookingStatus_ShouldThrowForbidden_WhenPartnerDoesNotOwnBooking() {
        DoiTac anotherPartner = new DoiTac();
        anotherPartner.setId("partner-2");
        HoSoKinhDoanh otherProfile = new HoSoKinhDoanh();
        otherProfile.setDoiTac(anotherPartner);

        DonKhachSan booking = new DonKhachSan();
        booking.setId("booking-5");
        booking.setHoSoKinhDoanh(otherProfile);
        booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);

        when(donDatChoRepository.findByIdAndDeletedFalse("booking-5")).thenReturn(Optional.of(booking));

        assertThrows(
                ForbiddenOperationException.class,
                () -> partnerBookingService.updateBookingStatus(
                        "partner-1",
                        "booking-5",
                        new PartnerUpdateBookingStatusRequest(TrangThaiDon.DA_XAC_NHAN, null)
                )
        );

        verify(donDatChoRepository, never()).save(any());
    }

    @Test
    void updateBookingStatus_ShouldThrowValidation_WhenBookingAlreadyFinished() {
        DonNhaHang booking = new DonNhaHang();
        booking.setId("booking-6");
        booking.setHoSoKinhDoanh(hoSo);
        booking.setTrangThai(TrangThaiDon.DA_HOAN_THANH);

        when(donDatChoRepository.findByIdAndDeletedFalse("booking-6")).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> partnerBookingService.updateBookingStatus(
                        "partner-1",
                        "booking-6",
                        new PartnerUpdateBookingStatusRequest(TrangThaiDon.DA_HUY, "Late")
                )
        );

        assertEquals("Khong the cap nhat don da ket thuc", exception.getMessage());
    }

    @Test
    void getPartnerBookings_ShouldReturnPagedItems() {
        DonKhachSan booking = new DonKhachSan();
        booking.setId("booking-7");
        booking.setHoSoKinhDoanh(hoSo);
        booking.setTrangThai(TrangThaiDon.DA_THANH_TOAN);
        booking.setMaDon("DKS001");
        booking.setTenNguoiDat("Nguyen Van A");
        booking.setSdtNguoiDat("0900000000");
        booking.setNgayTao(LocalDateTime.now());
        booking.setNgayCheckIn(LocalDate.now().plusDays(1));
        booking.setNgayCheckOut(LocalDate.now().plusDays(2));
        booking.setSoKhach(2);
        booking.setTongTienThanhToan(1_000_000d);

        when(donDatChoRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        Page<PartnerBookingListItemResponse> result = partnerBookingService.getPartnerBookings(
                "partner-1",
                List.of(TrangThaiDon.DA_THANH_TOAN),
                null,
                null,
                "DKS",
                0,
                20,
                "ngayTao,desc"
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("booking-7", result.getContent().get(0).id());
        assertEquals("KHACH_SAN", result.getContent().get(0).serviceType());
    }
}
