package com.ota.travi.service;

import com.ota.travi.dto.request.BanRequest;
import com.ota.travi.dto.request.MonAnRequest;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.DoiTac;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.MonAn;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.ThucDon;
import com.ota.travi.enums.TrangThaiBan;
import com.ota.travi.enums.TrangThaiDonDatCho;
import com.ota.travi.enums.TrangThaiMonAn;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.repository.BanRepository;
import com.ota.travi.repository.ChiTietDonDatMonRepository;
import com.ota.travi.repository.DonDatMonRepository;
import com.ota.travi.repository.MonAnRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.ThucDonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerRestaurantServiceTest {

    @Mock
    private NhaHangRepository nhaHangRepository;

    @Mock
    private BanRepository banRepository;

    @Mock
    private MonAnRepository monAnRepository;

    @Mock
    private ThucDonRepository thucDonRepository;

    @Mock
    private DonDatMonRepository donDatMonRepository;

    @Mock
    private ChiTietDonDatMonRepository chiTietDonDatMonRepository;

    @Mock
    private PartnerAssetMapper partnerAssetMapper;

    @InjectMocks
    private PartnerRestaurantService partnerRestaurantService;

    private DoiTac partner;
    private HoSoKinhDoanh hoSo;
    private NhaHang nhaHang;
    private Ban ban;
    private ThucDon thucDon;
    private MonAn monAn;

    @BeforeEach
    void setUp() {
        partner = new DoiTac();
        partner.setId("partner-123");

        hoSo = new HoSoKinhDoanh();
        hoSo.setIdHoSo("profile-456");
        hoSo.setDoiTac(partner);

        nhaHang = new NhaHang();
        nhaHang.setIdTaiSan("restaurant-789");
        nhaHang.setHoSoKinhDoanh(hoSo);

        ban = new Ban();
        ban.setId("table-001");
        ban.setNhaHang(nhaHang);
        ban.setTenBan("Ban 1");
        ban.setSoChoNgoi(4);
        ban.setTrangThai(TrangThaiBan.SAN_SANG);
        ban.setDeleted(false);

        thucDon = new ThucDon();
        thucDon.setId("menu-001");
        thucDon.setNhaHang(nhaHang);

        monAn = new MonAn();
        monAn.setId("item-001");
        monAn.setThucDon(thucDon);
        monAn.setTenMon("Pho Bo");
        monAn.setGiaBan(50000.0);
        monAn.setTrangThai(TrangThaiMonAn.DANG_BAN);
        monAn.setDeleted(false);
    }

    @Test
    void deleteTable_ShouldThrowException_WhenTableHasFutureReservation() {
        when(nhaHangRepository.findById("restaurant-789")).thenReturn(Optional.of(nhaHang));
        when(banRepository.findById("table-001")).thenReturn(Optional.of(ban));
        when(donDatMonRepository.existsByBan_IdAndThoiGianDatAfterAndDonDatCho_TrangThaiNotIn(
                eq("table-001"),
                any(LocalDateTime.class),
                eq(List.of(TrangThaiDonDatCho.DA_HUY_BO, TrangThaiDonDatCho.DA_HOAN_TIEN))
        )).thenReturn(true);

        BusinessConflictException exception = assertThrows(
                BusinessConflictException.class,
                () -> partnerRestaurantService.deleteTable("partner-123", "restaurant-789", "table-001")
        );

        assertEquals("Khong the xoa ban vi dang co don dat trong tuong lai", exception.getMessage());
        verify(banRepository, never()).save(any());
    }

    @Test
    void deleteTable_ShouldSoftDelete_WhenNoFutureReservation() {
        when(nhaHangRepository.findById("restaurant-789")).thenReturn(Optional.of(nhaHang));
        when(banRepository.findById("table-001")).thenReturn(Optional.of(ban));
        when(donDatMonRepository.existsByBan_IdAndThoiGianDatAfterAndDonDatCho_TrangThaiNotIn(
                eq("table-001"),
                any(LocalDateTime.class),
                eq(List.of(TrangThaiDonDatCho.DA_HUY_BO, TrangThaiDonDatCho.DA_HOAN_TIEN))
        )).thenReturn(false);

        partnerRestaurantService.deleteTable("partner-123", "restaurant-789", "table-001");

        assertTrue(ban.getDeleted());
        assertEquals(TrangThaiBan.NGUNG_SU_DUNG, ban.getTrangThai());
        verify(banRepository).save(ban);
    }

    @Test
    void deleteMenuItem_ShouldThrowException_WhenItemHasPreorder() {
        when(nhaHangRepository.findById("restaurant-789")).thenReturn(Optional.of(nhaHang));
        when(monAnRepository.findById("item-001")).thenReturn(Optional.of(monAn));
        when(chiTietDonDatMonRepository.existsByMonAn_IdAndDonDatMon_ThoiGianDatAfterAndDonDatMon_DonDatCho_TrangThaiNotIn(
                eq("item-001"),
                any(LocalDateTime.class),
                eq(List.of(TrangThaiDonDatCho.DA_HUY_BO, TrangThaiDonDatCho.DA_HOAN_TIEN))
        )).thenReturn(true);

        BusinessConflictException exception = assertThrows(
                BusinessConflictException.class,
                () -> partnerRestaurantService.deleteMenuItem("partner-123", "restaurant-789", "item-001")
        );

        assertEquals("Khong the xoa vi dang co don khach dat", exception.getMessage());
        verify(monAnRepository, never()).save(any());
    }

    @Test
    void deleteMenuItem_ShouldSoftDelete_WhenNoPreorder() {
        when(nhaHangRepository.findById("restaurant-789")).thenReturn(Optional.of(nhaHang));
        when(monAnRepository.findById("item-001")).thenReturn(Optional.of(monAn));
        when(chiTietDonDatMonRepository.existsByMonAn_IdAndDonDatMon_ThoiGianDatAfterAndDonDatMon_DonDatCho_TrangThaiNotIn(
                eq("item-001"),
                any(LocalDateTime.class),
                eq(List.of(TrangThaiDonDatCho.DA_HUY_BO, TrangThaiDonDatCho.DA_HOAN_TIEN))
        )).thenReturn(false);

        partnerRestaurantService.deleteMenuItem("partner-123", "restaurant-789", "item-001");

        assertTrue(monAn.getDeleted());
        assertEquals(TrangThaiMonAn.NGUNG_BAN, monAn.getTrangThai());
        verify(monAnRepository).save(monAn);
    }

    @Test
    void createTable_ShouldThrowException_WhenPartnerDoesNotOwnRestaurant() {
        DoiTac anotherPartner = new DoiTac();
        anotherPartner.setId("partner-999");
        hoSo.setDoiTac(anotherPartner);

        when(nhaHangRepository.findById("restaurant-789")).thenReturn(Optional.of(nhaHang));

        BanRequest request = new BanRequest("Ban 2", "Main Hall", "4-seat table", 4, TrangThaiBan.SAN_SANG);

        ForbiddenOperationException exception = assertThrows(
                ForbiddenOperationException.class,
                () -> partnerRestaurantService.createTable("partner-123", "restaurant-789", request)
        );

        assertEquals("Ban khong co quyen thao tac voi nha hang nay", exception.getMessage());
    }

    @Test
    void getTables_ShouldReturnOnlyNonDeletedTables() {
        Ban deletedTable = new Ban();
        deletedTable.setId("table-002");
        deletedTable.setDeleted(true);

        when(nhaHangRepository.findById("restaurant-789")).thenReturn(Optional.of(nhaHang));
        when(banRepository.findByNhaHang_IdTaiSanAndDeletedFalse("restaurant-789"))
                .thenReturn(List.of(ban));

        partnerRestaurantService.getTables("partner-123", "restaurant-789");

        verify(banRepository).findByNhaHang_IdTaiSanAndDeletedFalse("restaurant-789");
    }

    @Test
    void getMenuItems_ShouldReturnOnlyNonDeletedItems() {
        MonAn deletedItem = new MonAn();
        deletedItem.setId("item-002");
        deletedItem.setDeleted(true);

        when(nhaHangRepository.findById("restaurant-789")).thenReturn(Optional.of(nhaHang));
        when(monAnRepository.findByThucDon_NhaHang_IdTaiSanAndDeletedFalse("restaurant-789"))
                .thenReturn(List.of(monAn));

        partnerRestaurantService.getMenuItems("partner-123", "restaurant-789");

        verify(monAnRepository).findByThucDon_NhaHang_IdTaiSanAndDeletedFalse("restaurant-789");
    }
}
