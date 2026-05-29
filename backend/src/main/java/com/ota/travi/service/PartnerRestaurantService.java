package com.ota.travi.service;

import com.ota.travi.dto.request.BanRequest;
import com.ota.travi.dto.request.ComboRequest;
import com.ota.travi.dto.request.MonAnRequest;
import com.ota.travi.dto.request.ThucDonRequest;
import com.ota.travi.dto.response.BanResponse;
import com.ota.travi.dto.response.ComboResponse;
import com.ota.travi.dto.response.MonAnResponse;
import com.ota.travi.dto.response.ThucDonResponse;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.Combo;
import com.ota.travi.entity.MonAn;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.ThucDon;
import com.ota.travi.enums.TrangThaiMonAn;
import com.ota.travi.enums.TrangThaiDonDatCho;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.BanRepository;
import com.ota.travi.repository.ComboRepository;
import com.ota.travi.repository.MonAnRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.ThucDonRepository;
import com.ota.travi.repository.DonDatMonRepository;
import com.ota.travi.repository.ChiTietDonDatMonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class PartnerRestaurantService {
    private static final String DEFAULT_MENU_CATEGORY = "MAC_DINH";
    private static final int COMBO_ACTIVE = 1;
    private static final int COMBO_INACTIVE = 0;

    @Autowired
    private NhaHangRepository nhaHangRepository;

    @Autowired
    private BanRepository banRepository;

    @Autowired
    private MonAnRepository monAnRepository;

    @Autowired
    private ThucDonRepository thucDonRepository;

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private DonDatMonRepository donDatMonRepository;

    @Autowired
    private ChiTietDonDatMonRepository chiTietDonDatMonRepository;

    @Autowired
    private PartnerAssetMapper partnerAssetMapper;

    @Transactional
    public BanResponse createTable(String partnerId, String restaurantId, BanRequest request) {
        NhaHang nhaHang = requireOwnedRestaurant(partnerId, restaurantId);
        Ban ban = new Ban();
        ban.setNhaHang(nhaHang);
        ban.setTenBan(request.tenBan());
        ban.setViTriSanh(request.viTriSanh());
        ban.setMoTa(request.moTa());
        ban.setSoChoNgoi(request.soChoNgoi());
        ban.setTrangThai(com.ota.travi.enums.TrangThaiBan.SAN_SANG);
        ban.setDeleted(false);
        return partnerAssetMapper.toBanResponse(banRepository.save(ban));
    }

    @Transactional(readOnly = true)
    public List<BanResponse> getTables(String partnerId, String restaurantId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        return banRepository.findByNhaHang_IdTaiSanAndDeletedFalse(restaurantId).stream()
                .map(partnerAssetMapper::toBanResponse)
                .toList();
    }

    @Transactional
    public BanResponse updateTable(String partnerId, String restaurantId, String tableId, BanRequest request) {
        requireOwnedRestaurant(partnerId, restaurantId);
        Ban ban = requireTableInRestaurant(restaurantId, tableId);
        ban.setTenBan(request.tenBan());
        ban.setViTriSanh(request.viTriSanh());
        ban.setMoTa(request.moTa());
        ban.setSoChoNgoi(request.soChoNgoi());
        ban.setTrangThai(request.trangThai() == null ? com.ota.travi.enums.TrangThaiBan.SAN_SANG : request.trangThai());
        return partnerAssetMapper.toBanResponse(banRepository.save(ban));
    }

    @Transactional
    public void deleteTable(String partnerId, String restaurantId, String tableId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        Ban ban = requireTableInRestaurant(restaurantId, tableId);
        if (hasFutureReservationForTable(tableId)) {
            throw new BusinessConflictException("Khong the xoa ban vi dang co don dat trong tuong lai");
        }
        ban.setDeleted(true);
        ban.setTrangThai(com.ota.travi.enums.TrangThaiBan.NGUNG_SU_DUNG);
        banRepository.save(ban);
    }


    @Transactional
    public ThucDonResponse createMenu(String partnerId, String restaurantId, ThucDonRequest request) {
        NhaHang nhaHang = requireOwnedRestaurant(partnerId, restaurantId);
        ThucDon thucDon = new ThucDon();
        thucDon.setNhaHang(nhaHang);
        thucDon.setTenThucDon(request.tenThucDon() == null || request.tenThucDon().isBlank() ? "Thực đơn mới" : request.tenThucDon());
        thucDon.setPhanLoai(request.phanLoai());
        thucDon.setTrangThai(com.ota.travi.enums.TrangThaiThucDon.DANG_HIEN_THI);
        return partnerAssetMapper.toThucDonResponse(thucDonRepository.save(thucDon));
    }

    @Transactional(readOnly = true)
    public List<ThucDonResponse> getMenus(String partnerId, String restaurantId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        return thucDonRepository.findByNhaHang_IdTaiSan(restaurantId).stream()
                .map(partnerAssetMapper::toThucDonResponse)
                .toList();
    }

    @Transactional
    public ThucDonResponse updateMenu(String partnerId, String restaurantId, String menuId, ThucDonRequest request) {
        requireOwnedRestaurant(partnerId, restaurantId);
        ThucDon thucDon = thucDonRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thuc don"));
        if (!java.util.Objects.equals(thucDon.getNhaHang().getIdTaiSan(), restaurantId)) {
            throw new ResourceNotFoundException("Thuc don khong thuoc nha hang nay");
        }
        thucDon.setTenThucDon(request.tenThucDon());
        thucDon.setPhanLoai(request.phanLoai());
        return partnerAssetMapper.toThucDonResponse(thucDonRepository.save(thucDon));
    }

    @Transactional
    public void deleteMenu(String partnerId, String restaurantId, String menuId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        ThucDon thucDon = thucDonRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thuc don"));
        if (!java.util.Objects.equals(thucDon.getNhaHang().getIdTaiSan(), restaurantId)) {
            throw new ResourceNotFoundException("Thuc don khong thuoc nha hang nay");
        }
        if (!thucDon.getMonAn().isEmpty() || !thucDon.getCombo().isEmpty()) {
            thucDon.setTrangThai(com.ota.travi.enums.TrangThaiThucDon.TAM_AN);
            thucDonRepository.save(thucDon);
        } else {
            thucDonRepository.delete(thucDon);
        }
    }

    @Transactional
    public MonAnResponse createMenuItem(String partnerId, String restaurantId, MonAnRequest request) {
        NhaHang nhaHang = requireOwnedRestaurant(partnerId, restaurantId);
        ThucDon thucDon = resolveMenuForItem(nhaHang, request.thucDonId());
        MonAn monAn = new MonAn();
        monAn.setThucDon(thucDon);
        applyMonAn(monAn, request);
        return partnerAssetMapper.toMonAnResponse(monAnRepository.save(monAn));
    }

    @Transactional(readOnly = true)
    public List<MonAnResponse> getMenuItems(String partnerId, String restaurantId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        return monAnRepository.findByThucDon_NhaHang_IdTaiSanAndDeletedFalse(restaurantId).stream()
                .map(partnerAssetMapper::toMonAnResponse)
                .toList();
    }

    @Transactional
    public MonAnResponse updateMenuItem(String partnerId, String restaurantId, String itemId, MonAnRequest request) {
        requireOwnedRestaurant(partnerId, restaurantId);
        MonAn monAn = requireMenuItemInRestaurant(restaurantId, itemId);
        applyMonAn(monAn, request);
        return partnerAssetMapper.toMonAnResponse(monAnRepository.save(monAn));
    }

    @Transactional
    public void deleteMenuItem(String partnerId, String restaurantId, String itemId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        MonAn monAn = requireMenuItemInRestaurant(restaurantId, itemId);
        if (hasPreorderedMenuItem(itemId)) {
            throw new BusinessConflictException("Khong the xoa vi dang co don khach dat");
        }
        monAn.setDeleted(true);
        monAn.setTrangThai(TrangThaiMonAn.NGUNG_BAN);
        monAnRepository.save(monAn);
    }

    @Transactional
    public MonAnResponse updateMenuItemStatus(String partnerId, String restaurantId, String itemId, TrangThaiMonAn status) {
        requireOwnedRestaurant(partnerId, restaurantId);
        MonAn monAn = requireMenuItemInRestaurant(restaurantId, itemId);
        monAn.setTrangThai(status);
        return partnerAssetMapper.toMonAnResponse(monAnRepository.save(monAn));
    }

    @Transactional
    public ComboResponse createCombo(String partnerId, String restaurantId, ComboRequest request) {
        NhaHang nhaHang = requireOwnedRestaurant(partnerId, restaurantId);
        ThucDon thucDon = findOrCreateDefaultMenu(nhaHang);
        Combo combo = new Combo();
        combo.setThucDon(thucDon);
        applyCombo(combo, restaurantId, request);
        return partnerAssetMapper.toComboResponse(comboRepository.save(combo));
    }

    @Transactional(readOnly = true)
    public List<ComboResponse> getCombos(String partnerId, String restaurantId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        return comboRepository.findByThucDon_NhaHang_IdTaiSanAndTrangThaiNot(restaurantId, COMBO_INACTIVE).stream()
                .map(partnerAssetMapper::toComboResponse)
                .toList();
    }

    @Transactional
    public ComboResponse updateCombo(String partnerId, String restaurantId, String comboId, ComboRequest request) {
        requireOwnedRestaurant(partnerId, restaurantId);
        Combo combo = requireComboInRestaurant(restaurantId, comboId);
        applyCombo(combo, restaurantId, request);
        return partnerAssetMapper.toComboResponse(comboRepository.save(combo));
    }

    @Transactional
    public void deleteCombo(String partnerId, String restaurantId, String comboId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        Combo combo = requireComboInRestaurant(restaurantId, comboId);
        combo.setTrangThai(COMBO_INACTIVE);
        comboRepository.save(combo);
    }

    private NhaHang requireOwnedRestaurant(String partnerId, String restaurantId) {
        NhaHang nhaHang = nhaHangRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nha hang"));
        if (!Objects.equals(nhaHang.getHoSoKinhDoanh().getDoiTac().getId(), partnerId)) {
            throw new ForbiddenOperationException("Ban khong co quyen thao tac voi nha hang nay");
        }
        return nhaHang;
    }

    private Ban requireTableInRestaurant(String restaurantId, String tableId) {
        Ban ban = banRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ban"));
        if (!Objects.equals(ban.getNhaHang().getIdTaiSan(), restaurantId)) {
            throw new ResourceNotFoundException("Ban khong thuoc nha hang nay");
        }
        return ban;
    }

    private MonAn requireMenuItemInRestaurant(String restaurantId, String itemId) {
        MonAn monAn = monAnRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay mon an"));
        if (!Objects.equals(monAn.getThucDon().getNhaHang().getIdTaiSan(), restaurantId)) {
            throw new ResourceNotFoundException("Mon an khong thuoc nha hang nay");
        }
        return monAn;
    }

    private Combo requireComboInRestaurant(String restaurantId, String comboId) {
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay combo"));
        if (!Objects.equals(combo.getThucDon().getNhaHang().getIdTaiSan(), restaurantId)) {
            throw new ResourceNotFoundException("Combo khong thuoc nha hang nay");
        }
        return combo;
    }

    private void applyMonAn(MonAn monAn, MonAnRequest request) {
        monAn.setTenMon(request.tenMon());
        monAn.setMoTa(request.moTa());
        monAn.setGiaBan(request.giaBan());
        monAn.setDanhMucMon(request.danhMucMon());
        monAn.setDuongDanUrl(request.duongDanUrl());
        monAn.setTrangThai(request.trangThai() == null ? TrangThaiMonAn.DANG_BAN : request.trangThai());
        monAn.setTheNguCanh(request.theNguCanh() == null ? new ArrayList<>() : new ArrayList<>(request.theNguCanh()));
    }

    private ThucDon findOrCreateDefaultMenu(NhaHang nhaHang) {
        return thucDonRepository.findByNhaHang_IdTaiSanAndPhanLoai(nhaHang.getIdTaiSan(), DEFAULT_MENU_CATEGORY).stream()
                .findFirst()
                .orElseGet(() -> {
                    ThucDon thucDon = new ThucDon();
                    thucDon.setNhaHang(nhaHang);
                    thucDon.setTenThucDon("Thực đơn mặc định");
                    thucDon.setPhanLoai(DEFAULT_MENU_CATEGORY);
                    return thucDonRepository.save(thucDon);
                });
    }

    private ThucDon resolveMenuForItem(NhaHang nhaHang, String menuId) {
        if (menuId == null || menuId.isBlank()) {
            return findOrCreateDefaultMenu(nhaHang);
        }
        ThucDon thucDon = thucDonRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thuc don"));
        if (!Objects.equals(thucDon.getNhaHang().getIdTaiSan(), nhaHang.getIdTaiSan())) {
            throw new ResourceNotFoundException("Thuc don khong thuoc nha hang nay");
        }
        return thucDon;
    }

    private void applyCombo(Combo combo, String restaurantId, ComboRequest request) {
        combo.setTenCombo(request.tenCombo());
        combo.setMoTa(request.moTa());
        combo.setGiaCombo(request.giaCombo());
        combo.setTrangThai(request.trangThai() == null ? COMBO_ACTIVE : request.trangThai());
        combo.setNgayBatDau(request.ngayBatDau());
        combo.setNgayKetThuc(request.ngayKetThuc());
        combo.setMonAn(resolveComboMenuItems(restaurantId, request.monAnIds()));
    }

    private Set<MonAn> resolveComboMenuItems(String restaurantId, Set<String> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<MonAn> items = new HashSet<>();
        itemIds.forEach(itemId -> items.add(requireMenuItemInRestaurant(restaurantId, itemId)));
        return items;
    }

    private boolean hasPreorderedMenuItem(String itemId) {
        return chiTietDonDatMonRepository.existsByMonAn_IdAndDonDatMon_ThoiGianDatAfterAndDonDatMon_DonDatCho_TrangThaiNotIn(
                itemId,
                java.time.LocalDateTime.now(),
                java.util.List.of(TrangThaiDonDatCho.DA_HUY_BO, TrangThaiDonDatCho.DA_HOAN_TIEN)
        );
    }

    private boolean hasFutureReservationForTable(String tableId) {
        return donDatMonRepository.existsByBan_IdAndThoiGianDatAfterAndDonDatCho_TrangThaiNotIn(
                tableId,
                java.time.LocalDateTime.now(),
                java.util.List.of(TrangThaiDonDatCho.DA_HUY_BO, TrangThaiDonDatCho.DA_HOAN_TIEN)
        );
    }
}


