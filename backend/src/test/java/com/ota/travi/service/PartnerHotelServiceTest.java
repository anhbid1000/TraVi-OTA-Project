package com.ota.travi.service;

import com.ota.travi.dto.request.PhongRequest;
import com.ota.travi.dto.request.PhongUpsertRequest;
import com.ota.travi.entity.DoiTac;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.Phong;
import com.ota.travi.enums.TrangThaiDonDatCho;
import com.ota.travi.enums.TrangThaiPhong;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.repository.DatPhongRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.PhongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerHotelServiceTest {

    @Mock
    private KhachSanRepository khachSanRepository;

    @Mock
    private PhongRepository phongRepository;

    @Mock
    private DatPhongRepository datPhongRepository;

    @Mock
    private PartnerAssetMapper partnerAssetMapper;

    @InjectMocks
    private PartnerHotelService partnerHotelService;

    private DoiTac partner;
    private HoSoKinhDoanh hoSo;
    private KhachSan khachSan;
    private Phong phong;
    private PhongUpsertRequest roomRequest;

    @BeforeEach
    void setUp() {
        partner = new DoiTac();
        partner.setId("partner-123");

        hoSo = new HoSoKinhDoanh();
        hoSo.setIdHoSo("profile-456");
        hoSo.setDoiTac(partner);

        khachSan = new KhachSan();
        khachSan.setIdTaiSan("hotel-789");
        khachSan.setHoSoKinhDoanh(hoSo);

        phong = new Phong();
        phong.setId("room-001");
        phong.setKhachSan(khachSan);
        phong.setSoPhong("101");
        phong.setTrangThai(TrangThaiPhong.DANG_BAN);
        phong.setDeleted(false);

        PhongRequest phongReq = new PhongRequest(
                "101",
                "101",
                "Deluxe",
                "Luxury room",
                2,
                2,
                45.0f,
                500000.0,
                1,
                TrangThaiPhong.DANG_BAN,
                new HashSet<>(),
                0.0f
        );

        roomRequest = new PhongUpsertRequest(phongReq, new ArrayList<>());
    }

    @Test
    void deleteRoom_ShouldThrowException_WhenRoomHasFutureBooking() {
        when(khachSanRepository.findById("hotel-789")).thenReturn(Optional.of(khachSan));
        when(phongRepository.findById("room-001")).thenReturn(Optional.of(phong));
        when(datPhongRepository.existsByPhong_IdAndNgayCheckInAfterAndDonDatCho_TrangThaiNotIn(
                eq("room-001"),
                any(LocalDate.class),
                eq(List.of(TrangThaiDonDatCho.DA_HUY_BO, TrangThaiDonDatCho.DA_HOAN_TIEN))
        )).thenReturn(true);

        BusinessConflictException exception = assertThrows(
                BusinessConflictException.class,
                () -> partnerHotelService.deleteRoom("partner-123", "hotel-789", "room-001")
        );

        assertEquals("Khong the xoa phong vi dang co don dat cho trong tuong lai", exception.getMessage());
        verify(phongRepository, never()).save(any());
    }

    @Test
    void deleteRoom_ShouldSoftDelete_WhenNoFutureBooking() {
        when(khachSanRepository.findById("hotel-789")).thenReturn(Optional.of(khachSan));
        when(phongRepository.findById("room-001")).thenReturn(Optional.of(phong));
        when(datPhongRepository.existsByPhong_IdAndNgayCheckInAfterAndDonDatCho_TrangThaiNotIn(
                eq("room-001"),
                any(LocalDate.class),
                eq(List.of(TrangThaiDonDatCho.DA_HUY_BO, TrangThaiDonDatCho.DA_HOAN_TIEN))
        )).thenReturn(false);

        partnerHotelService.deleteRoom("partner-123", "hotel-789", "room-001");

        assertTrue(phong.getDeleted());
        assertEquals(TrangThaiPhong.NGUNG_KINH_DOANH, phong.getTrangThai());
        verify(phongRepository).save(phong);
    }

    @Test
    void createRoom_ShouldThrowException_WhenPartnerDoesNotOwnHotel() {
        DoiTac anotherPartner = new DoiTac();
        anotherPartner.setId("partner-999");
        hoSo.setDoiTac(anotherPartner);

        when(khachSanRepository.findById("hotel-789")).thenReturn(Optional.of(khachSan));

        ForbiddenOperationException exception = assertThrows(
                ForbiddenOperationException.class,
                () -> partnerHotelService.createRoom("partner-123", "hotel-789", roomRequest)
        );

        assertEquals("Ban khong co quyen thao tac voi khach san nay", exception.getMessage());
    }

    @Test
    void createRoom_ShouldThrowException_WhenRoomNumberDuplicate() {
        when(khachSanRepository.findById("hotel-789")).thenReturn(Optional.of(khachSan));
        when(phongRepository.existsByKhachSan_IdTaiSanAndSoPhongAndDeletedFalse("hotel-789", "101")).thenReturn(true);

        BusinessConflictException exception = assertThrows(
                BusinessConflictException.class,
                () -> partnerHotelService.createRoom("partner-123", "hotel-789", roomRequest)
        );

        assertEquals("So phong da ton tai trong khach san nay", exception.getMessage());
    }

    @Test
    void getRooms_ShouldReturnOnlyNonDeletedRooms() {
        Phong deletedRoom = new Phong();
        deletedRoom.setId("room-002");
        deletedRoom.setDeleted(true);

        when(khachSanRepository.findById("hotel-789")).thenReturn(Optional.of(khachSan));
        when(phongRepository.findByKhachSan_IdTaiSanAndDeletedFalse("hotel-789"))
                .thenReturn(List.of(phong));

        partnerHotelService.getRooms("partner-123", "hotel-789");

        verify(phongRepository).findByKhachSan_IdTaiSanAndDeletedFalse("hotel-789");
    }
}
