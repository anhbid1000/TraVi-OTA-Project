package com.ota.travi.service;

import com.ota.travi.dto.request.ChinhSachRequest;
import com.ota.travi.dto.request.HoSoKinhDoanhRequest;
import com.ota.travi.dto.request.KhachSanRequest;
import com.ota.travi.dto.request.PartnerBusinessProfileRequest;
import com.ota.travi.entity.DoiTac;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiHoatDong;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.repository.DoiTacRepository;
import com.ota.travi.repository.HoSoKinhDoanhRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerBusinessProfileServiceTest {

    @Mock
    private DoiTacRepository doiTacRepository;

    @Mock
    private HoSoKinhDoanhRepository hoSoKinhDoanhRepository;

    @Mock
    private PartnerAssetMapper partnerAssetMapper;

    @InjectMocks
    private PartnerBusinessProfileService partnerBusinessProfileService;

    private DoiTac partner;
    private PartnerBusinessProfileRequest validRequest;

    @BeforeEach
    void setUp() {
        partner = new DoiTac();
        partner.setId("partner-123");
        partner.setEmail("partner@example.com");

        HoSoKinhDoanhRequest hoSoRequest = new HoSoKinhDoanhRequest(
                "Khach San ABC",
                "0901234567",
                "contact@abc.com",
                "123 Nguyen Hue",
                "Ho Chi Minh",
                "Quan 1",
                "Phuong Ben Nghe",
                106.7009,
                10.7769,
                LoaiDichVu.KHACH_SAN,
                "0123456789",
                "https://example.com/license.pdf",
                "10.7769,106.7009",
                new ChinhSachRequest(
                        "CHINH_SACH_CHUNG",
                        "Chinh sach chung",
                        java.time.LocalDate.parse("2024-01-01"),
                        java.time.LocalTime.parse("14:00"),
                        java.time.LocalTime.parse("12:00"),
                        java.time.LocalTime.parse("08:00"),
                        java.time.LocalTime.parse("22:00"),
                        "Huy truoc 24h",
                        "Hoan 100% neu huy truoc 48h",
                        "Tre em duoi 5 tuoi mien phi",
                        "Khong cho phep",
                        "Khong co"
                )
        );

        KhachSanRequest khachSanRequest = new KhachSanRequest(
                "Khach San ABC",
                4,
                "KHACH_SAN_RESORT",
                "Luxury hotel",
                500000.0,
                false,
                java.time.LocalTime.parse("14:00"),
                java.time.LocalTime.parse("12:00"),
                java.time.LocalTime.parse("14:00"),
                java.time.LocalTime.parse("12:00"),
                5,
                50,
                new java.util.HashSet<>()
        );

        validRequest = new PartnerBusinessProfileRequest(
                hoSoRequest,
                khachSanRequest,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Test
    void createBusinessProfile_ShouldThrowException_WhenMSTAlreadyExists() {
        when(hoSoKinhDoanhRepository.existsByMaSoThue("0123456789")).thenReturn(true);

        BusinessConflictException exception = assertThrows(
                BusinessConflictException.class,
                () -> partnerBusinessProfileService.createBusinessProfile("partner-123", validRequest)
        );

        assertEquals("Ma so thue da ton tai", exception.getMessage());
        verify(hoSoKinhDoanhRepository, never()).save(any());
    }

    @Test
    void createBusinessProfile_ShouldSucceed_WhenMSTIsUnique() {
        when(hoSoKinhDoanhRepository.existsByMaSoThue("0123456789")).thenReturn(false);
        when(doiTacRepository.findById("partner-123")).thenReturn(Optional.of(partner));
        when(hoSoKinhDoanhRepository.save(any(HoSoKinhDoanh.class))).thenAnswer(invocation -> {
            HoSoKinhDoanh saved = invocation.getArgument(0);
            saved.setIdHoSo("profile-456");
            return saved;
        });

        partnerBusinessProfileService.createBusinessProfile("partner-123", validRequest);

        verify(hoSoKinhDoanhRepository).save(any(HoSoKinhDoanh.class));
    }

    @Test
    void createBusinessProfile_ShouldThrowException_WhenPartnerNotFound() {
        when(hoSoKinhDoanhRepository.existsByMaSoThue("0123456789")).thenReturn(false);
        when(doiTacRepository.findById("partner-123")).thenReturn(Optional.empty());

        ForbiddenOperationException exception = assertThrows(
                ForbiddenOperationException.class,
                () -> partnerBusinessProfileService.createBusinessProfile("partner-123", validRequest)
        );

        assertEquals("Tai khoan khong phai doi tac", exception.getMessage());
    }

    @Test
    void createBusinessProfile_ShouldSetDefaultStatus_WhenCreated() {
        when(hoSoKinhDoanhRepository.existsByMaSoThue("0123456789")).thenReturn(false);
        when(doiTacRepository.findById("partner-123")).thenReturn(Optional.of(partner));
        when(hoSoKinhDoanhRepository.save(any(HoSoKinhDoanh.class))).thenAnswer(invocation -> {
            HoSoKinhDoanh saved = invocation.getArgument(0);
            assertEquals(TrangThaiHoatDong.DANG_HOAT_DONG, saved.getTrangThaiHoatDong());
            assertEquals(false, saved.getDeleted());
            return saved;
        });

        partnerBusinessProfileService.createBusinessProfile("partner-123", validRequest);

        verify(hoSoKinhDoanhRepository).save(any(HoSoKinhDoanh.class));
    }

    @Test
    void updateBusinessProfile_ShouldThrowException_WhenMSTDuplicatesAnotherProfile() {
        HoSoKinhDoanh existingProfile = new HoSoKinhDoanh();
        existingProfile.setIdHoSo("profile-456");
        existingProfile.setDoiTac(partner);
        existingProfile.setMaSoThue("0123456789");

        when(hoSoKinhDoanhRepository.findById("profile-456")).thenReturn(Optional.of(existingProfile));
        when(hoSoKinhDoanhRepository.existsByMaSoThueAndIdHoSoNot("9999999999", "profile-456")).thenReturn(true);

        HoSoKinhDoanhRequest updatedHoSo = new HoSoKinhDoanhRequest(
                validRequest.hoSo().tenCoSo(),
                validRequest.hoSo().sdtLienHe(),
                validRequest.hoSo().emailLienHe(),
                validRequest.hoSo().diaChi(),
                validRequest.hoSo().thanhPho(),
                validRequest.hoSo().quanHuyen(),
                validRequest.hoSo().phuongXa(),
                validRequest.hoSo().kinhDo(),
                validRequest.hoSo().viDo(),
                validRequest.hoSo().loaiDichVu(),
                "9999999999",
                validRequest.hoSo().giayPhepKinhDoanh(),
                validRequest.hoSo().toaDoGPS(),
                validRequest.hoSo().chinhSach()
        );

        PartnerBusinessProfileRequest updatedRequest = new PartnerBusinessProfileRequest(
                updatedHoSo,
                validRequest.khachSan(),
                validRequest.nhaHang(),
                validRequest.danhSachAnh(),
                validRequest.tienIchKhachSan(),
                validRequest.tienIchNhaHang()
        );

        BusinessConflictException exception = assertThrows(
                BusinessConflictException.class,
                () -> partnerBusinessProfileService.updateBusinessProfile("partner-123", "profile-456", updatedRequest)
        );

        assertEquals("Ma so thue da ton tai", exception.getMessage());
    }
}
