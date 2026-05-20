package com.ota.travi.service;

import com.ota.travi.dto.request.BanRequest;
import com.ota.travi.dto.request.MonAnRequest;
import com.ota.travi.dto.response.BanResponse;
import com.ota.travi.dto.response.MonAnResponse;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.MonAn;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.ThucDon;
import com.ota.travi.enums.TrangThaiMonAn;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.BanRepository;
import com.ota.travi.repository.MonAnRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.ThucDonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

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
    private PartnerAssetMapper partnerAssetMapper;

    @Transactional
    public BanResponse createTable(String partnerId, String restaurantId, BanRequest request) {
        NhaHang nhaHang = requireOwnedRestaurant(partnerId, restaurantId);
        Ban ban = new Ban();
        ban.setNhaHang(nhaHang);
        ban.setViTriSanh(request.viTriSanh());
        ban.setSoChoNgoi(request.soChoNgoi());
        ban.setTrangThai(request.trangThai() == null ? 1 : request.trangThai());
        return partnerAssetMapper.toBanResponse(banRepository.save(ban));
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

    @Transactional
    public void deleteMenuItem(String partnerId, String restaurantId, String itemId) {
        requireOwnedRestaurant(partnerId, restaurantId);
        MonAn monAn = requireMenuItemInRestaurant(restaurantId, itemId);
        if (hasPreorderedMenuItem(itemId)) {
            throw new BusinessConflictException("Khong the xoa vi dang co don khach dat");
        }
        monAnRepository.delete(monAn);
    }

    @Transactional
    public MonAnResponse updateMenuItemStatus(String partnerId, String restaurantId, String itemId, TrangThaiMonAn status) {
        requireOwnedRestaurant(partnerId, restaurantId);
        MonAn monAn = requireMenuItemInRestaurant(restaurantId, itemId);
        monAn.setTrangThai(status);
        return partnerAssetMapper.toMonAnResponse(monAnRepository.save(monAn));
    }

    private NhaHang requireOwnedRestaurant(String partnerId, String restaurantId) {
        NhaHang nhaHang = nhaHangRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nha hang"));
        if (!Objects.equals(nhaHang.getHoSoKinhDoanh().getDoiTac().getId(), partnerId)) {
            throw new ForbiddenOperationException("Ban khong co quyen thao tac voi nha hang nay");
        }
        return nhaHang;
    }

    private MonAn requireMenuItemInRestaurant(String restaurantId, String itemId) {
        MonAn monAn = monAnRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay mon an"));
        if (!Objects.equals(monAn.getThucDon().getNhaHang().getIdTaiSan(), restaurantId)) {
            throw new ResourceNotFoundException("Mon an khong thuoc nha hang nay");
        }
        return monAn;
    }

    private void applyMonAn(MonAn monAn, MonAnRequest request) {
        monAn.setTenMon(request.tenMon());
        monAn.setGiaBan(request.giaBan());
        monAn.setDuongDanUrl(request.duongDanUrl());
        monAn.setTrangThai(request.trangThai() == null ? TrangThaiMonAn.CO_SAN : request.trangThai());
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

    private boolean hasPreorderedMenuItem(String itemId) {
        // TODO: Noi voi repository dat mon truoc khi module Booking/Order duoc tao.
        return false;
    }
}
