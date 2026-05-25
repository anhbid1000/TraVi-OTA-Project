package com.ota.travi.service;

import com.ota.travi.dto.request.BanRequest;
import com.ota.travi.dto.request.ComboRequest;
import com.ota.travi.dto.request.MonAnRequest;
import com.ota.travi.dto.response.BanResponse;
import com.ota.travi.dto.response.ComboResponse;
import com.ota.travi.dto.response.MonAnResponse;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.Combo;
import com.ota.travi.entity.MonAn;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.ThucDon;
import com.ota.travi.enums.TrangThaiMonAn;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.BanRepository;
import com.ota.travi.repository.ComboRepository;
import com.ota.travi.repository.MonAnRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.ThucDonRepository;

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
        ban.setTrangThai(request.trangThai() == null ? 1 : request.trangThai());
        return partnerAssetMapper.toBanResponse(banRepository.save(ban));
    }

    @Transactional(readOnly = true)
    public List<BanResponse> getTables(String partnerId, String restaurantId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        return banRepository.findByNhaHang_IdTaiSan(restaurantId).stream()
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
        ban.setTrangThai(request.trangThai() == null ? 1 : request.trangThai());
        return partnerAssetMapper.toBanResponse(banRepository.save(ban));
    }

    @Transactional
    public void deleteTable(String partnerId, String restaurantId, String tableId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        Ban ban = requireTableInRestaurant(restaurantId, tableId);
        ban.setDeleted(true);
        ban.setTrangThai(0);
        banRepository.save(ban);
    }

    @Transactional
    public MonAnResponse createMenuItem(String partnerId, String restaurantId, MonAnRequest request) {
        NhaHang nhaHang = requireOwnedRestaurant(partnerId, restaurantId);
        ThucDon thucDon = findOrCreateDefaultMenu(nhaHang);
        MonAn monAn = new MonAn();
        monAn.setThucDon(thucDon);
        applyMonAn(monAn, request);
        return partnerAssetMapper.toMonAnResponse(monAnRepository.save(monAn));
    }

    @Transactional(readOnly = true)
    public List<MonAnResponse> getMenuItems(String partnerId, String restaurantId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        return monAnRepository.findByThucDon_NhaHang_IdTaiSan(restaurantId).stream()
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
        return comboRepository.findByThucDon_NhaHang_IdTaiSan(restaurantId).stream()
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
        comboRepository.delete(combo);
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
                    thucDon.setPhanLoai(DEFAULT_MENU_CATEGORY);
                    return thucDonRepository.save(thucDon);
                });
    }

    private void applyCombo(Combo combo, String restaurantId, ComboRequest request) {
        combo.setTenCombo(request.tenCombo());
        combo.setMoTa(request.moTa());
        combo.setGiaCombo(request.giaCombo());
        combo.setTrangThai(request.trangThai());
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
        // TODO: Noi voi repository dat mon truoc khi module Booking/Order duoc tao.
        return false;
    }
}
