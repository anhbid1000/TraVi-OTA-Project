package com.ota.travi.service;

import com.ota.travi.dto.request.AnhRequest;
import com.ota.travi.dto.request.ChinhSachRequest;
import com.ota.travi.dto.request.KhachSanRequest;
import com.ota.travi.dto.request.NhaHangRequest;
import com.ota.travi.dto.request.PartnerBusinessProfileRequest;
import com.ota.travi.dto.request.TienIchKhachSanRequest;
import com.ota.travi.dto.request.TienIchNhaHangRequest;
import com.ota.travi.dto.response.HoSoKinhDoanhResponse;
import com.ota.travi.entity.AnhKhachSan;
import com.ota.travi.entity.AnhNhaHang;
import com.ota.travi.entity.ChinhSach;
import com.ota.travi.entity.DoiTac;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.TienIchKhachSan;
import com.ota.travi.entity.TienIchNhaHang;
import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiHoatDong;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.DoiTacRepository;
import com.ota.travi.repository.HoSoKinhDoanhRepository;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.NhaHangRepository;
import com.ota.travi.repository.TienIchKhachSanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class PartnerBusinessProfileService {
    @Autowired
    private DoiTacRepository doiTacRepository;

    @Autowired
    private HoSoKinhDoanhRepository hoSoKinhDoanhRepository;

    @Autowired
    private KhachSanRepository khachSanRepository;

    @Autowired
    private NhaHangRepository nhaHangRepository;

    @Autowired
    private TienIchKhachSanRepository tienIchKhachSanRepository;

    @Autowired
    private PartnerAssetMapper partnerAssetMapper;

    @Transactional
    public HoSoKinhDoanhResponse createBusinessProfile(String partnerId, PartnerBusinessProfileRequest request) {
        if (hoSoKinhDoanhRepository.existsByMaSoThue(request.hoSo().maSoThue())) {
            throw new BusinessConflictException("Ma so thue da ton tai");
        }

        DoiTac doiTac = requirePartner(partnerId);
        HoSoKinhDoanh hoSo = new HoSoKinhDoanh();
        hoSo.setDoiTac(doiTac);
        applyBusinessProfileFields(hoSo, request);
        // Partner-only model: hồ sơ mới mặc định hoạt động ngay
        hoSo.setTrangThaiHoatDong(TrangThaiHoatDong.DANG_HOAT_DONG);
        hoSo.setDeleted(false);

        ChinhSach chinhSach = toChinhSach(request.hoSo().chinhSach(), hoSo);
        hoSo.setChinhSach(chinhSach);
        addPrimaryAsset(hoSo, request);

        return partnerAssetMapper.toHoSoResponse(hoSoKinhDoanhRepository.save(hoSo));
    }

    @Transactional(readOnly = true)
    public List<HoSoKinhDoanhResponse> getBusinessProfiles(String partnerId) {
        requirePartner(partnerId);
        return hoSoKinhDoanhRepository.findByDoiTac_IdAndDeletedFalse(partnerId).stream()
                .map(partnerAssetMapper::toHoSoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public HoSoKinhDoanhResponse getBusinessProfile(String partnerId, String profileId) {
        return partnerAssetMapper.toHoSoResponse(requireOwnedBusinessProfile(partnerId, profileId));
    }

    @Transactional
    public HoSoKinhDoanhResponse updateBusinessProfile(String partnerId, String profileId, PartnerBusinessProfileRequest request) {
        HoSoKinhDoanh hoSo = requireOwnedBusinessProfile(partnerId, profileId);
        boolean maSoThueChanged = !Objects.equals(hoSo.getMaSoThue(), request.hoSo().maSoThue());

        if (maSoThueChanged && hoSoKinhDoanhRepository.existsByMaSoThueAndIdHoSoNot(request.hoSo().maSoThue(), profileId)) {
            throw new BusinessConflictException("Ma so thue da ton tai");
        }

        applyBusinessProfileFields(hoSo, request);
        applyChinhSach(hoSo.getChinhSach(), request.hoSo().chinhSach(), hoSo);
        updatePrimaryAsset(hoSo, request);
        
        return partnerAssetMapper.toHoSoResponse(hoSoKinhDoanhRepository.save(hoSo));
    }

    private DoiTac requirePartner(String partnerId) {
        return doiTacRepository.findById(partnerId)
                .orElseThrow(() -> new ForbiddenOperationException("Tai khoan khong phai doi tac"));
    }

    private HoSoKinhDoanh requireOwnedBusinessProfile(String partnerId, String profileId) {
        HoSoKinhDoanh hoSo = hoSoKinhDoanhRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ho so kinh doanh"));
        if (!Objects.equals(hoSo.getDoiTac().getId(), partnerId)) {
            throw new ForbiddenOperationException("Ban khong co quyen thao tac voi ho so nay");
        }
        return hoSo;
    }

    private void applyBusinessProfileFields(HoSoKinhDoanh hoSo, PartnerBusinessProfileRequest request) {
        hoSo.setTenCoSo(request.hoSo().tenCoSo());
        hoSo.setSdtLienHe(request.hoSo().sdtLienHe());
        hoSo.setEmailLienHe(request.hoSo().emailLienHe());
        hoSo.setDiaChi(request.hoSo().diaChi());
        hoSo.setThanhPho(request.hoSo().thanhPho());
        hoSo.setQuanHuyen(request.hoSo().quanHuyen());
        hoSo.setPhuongXa(request.hoSo().phuongXa());
        hoSo.setKinhDo(request.hoSo().kinhDo());
        hoSo.setViDo(request.hoSo().viDo());
        hoSo.setLoaiDichVu(request.hoSo().loaiDichVu());
        hoSo.setMaSoThue(request.hoSo().maSoThue());
        hoSo.setGiayPhepKinhDoanh(request.hoSo().giayPhepKinhDoanh());
        hoSo.setToaDoGPS(request.hoSo().toaDoGPS());
    }

private ChinhSach toChinhSach(ChinhSachRequest request, HoSoKinhDoanh hoSo) {
        ChinhSach chinhSach = new ChinhSach();
        applyChinhSach(chinhSach, request, hoSo);
        return chinhSach;
    }

    private void applyChinhSach(ChinhSach chinhSach, ChinhSachRequest request, HoSoKinhDoanh hoSo) {
        chinhSach.setHoSoKinhDoanh(hoSo);
        chinhSach.setLoaiChinhSach(request.loaiChinhSach());
        chinhSach.setNoiDung(request.noiDung());
        chinhSach.setNgayApDung(request.ngayApDung());
        chinhSach.setGioNhanPhong(request.gioNhanPhong());
        chinhSach.setGioTraPhong(request.gioTraPhong());
        chinhSach.setGioMoCua(request.gioMoCua());
        chinhSach.setGioDongCua(request.gioDongCua());
        chinhSach.setChinhSachHuy(request.chinhSachHuy());
        chinhSach.setChinhSachHoanTien(request.chinhSachHoanTien());
        chinhSach.setQuyDinhTreEm(request.quyDinhTreEm());
        chinhSach.setQuyDinhVatNuoi(request.quyDinhVatNuoi());
        chinhSach.setGhiChuKhac(request.ghiChuKhac());
    }

    private void addPrimaryAsset(HoSoKinhDoanh hoSo, PartnerBusinessProfileRequest request) {
        if (request.hoSo().loaiDichVu() == LoaiDichVu.KHACH_SAN) {
            hoSo.getDanhSachTaiSan().add(toKhachSan(request.khachSan(), request.danhSachAnh(), request.tienIchKhachSan(), hoSo));
        } else if (request.hoSo().loaiDichVu() == LoaiDichVu.NHA_HANG) {
            hoSo.getDanhSachTaiSan().add(toNhaHang(request.nhaHang(), request.danhSachAnh(), request.tienIchNhaHang(), hoSo));
        }
    }

    private KhachSan toKhachSan(
            KhachSanRequest request,
            List<AnhRequest> anhRequests,
            List<TienIchKhachSanRequest> tienIchRequests,
            HoSoKinhDoanh hoSo
    ) {
        if (request == null) {
            throw new ValidationException("Thong tin khach san khong duoc de trong");
        }
        KhachSan khachSan = new KhachSan();
        khachSan.setHoSoKinhDoanh(hoSo);
        applyKhachSan(khachSan, request);
        replaceHotelImages(khachSan, anhRequests);
        replaceHotelAmenities(khachSan, tienIchRequests);
        return khachSan;
    }

    private NhaHang toNhaHang(
            NhaHangRequest request,
            List<AnhRequest> anhRequests,
            List<TienIchNhaHangRequest> tienIchRequests,
            HoSoKinhDoanh hoSo
    ) {
        if (request == null) {
            throw new ValidationException("Thong tin nha hang khong duoc de trong");
        }
        NhaHang nhaHang = new NhaHang();
        nhaHang.setHoSoKinhDoanh(hoSo);
        applyNhaHang(nhaHang, request);
        replaceRestaurantImages(nhaHang, anhRequests);
        replaceRestaurantAmenities(nhaHang, tienIchRequests);
        return nhaHang;
    }

    private void updatePrimaryAsset(HoSoKinhDoanh hoSo, PartnerBusinessProfileRequest request) {
        if (request.hoSo().loaiDichVu() == LoaiDichVu.KHACH_SAN && request.khachSan() != null) {
            KhachSan khachSan = khachSanRepository.findByHoSoKinhDoanh_IdHoSo(hoSo.getIdHoSo()).stream()
                    .findFirst()
                    .orElseGet(() -> {
                        KhachSan newHotel = new KhachSan();
                        newHotel.setHoSoKinhDoanh(hoSo);
                        hoSo.getDanhSachTaiSan().add(newHotel);
                        return newHotel;
                    });
            applyKhachSan(khachSan, request.khachSan());
            replaceHotelImages(khachSan, request.danhSachAnh());
            replaceHotelAmenities(khachSan, request.tienIchKhachSan());
        } else if (request.hoSo().loaiDichVu() == LoaiDichVu.NHA_HANG && request.nhaHang() != null) {
            NhaHang nhaHang = nhaHangRepository.findByHoSoKinhDoanh_IdHoSo(hoSo.getIdHoSo()).stream()
                    .findFirst()
                    .orElseGet(() -> {
                        NhaHang newRestaurant = new NhaHang();
                        newRestaurant.setHoSoKinhDoanh(hoSo);
                        hoSo.getDanhSachTaiSan().add(newRestaurant);
                        return newRestaurant;
                    });
            applyNhaHang(nhaHang, request.nhaHang());
            replaceRestaurantImages(nhaHang, request.danhSachAnh());
            replaceRestaurantAmenities(nhaHang, request.tienIchNhaHang());
        }
    }

    private void applyKhachSan(KhachSan khachSan, KhachSanRequest request) {
        khachSan.setTen(request.ten());
        khachSan.setHangSao(request.hangSao());
        khachSan.setLoaiKhachSan(request.loaiKhachSan());
        khachSan.setMoTa(request.moTa());
        khachSan.setGiaCoBan(request.giaCoBan());
        khachSan.setIsDynamicPricing(Boolean.TRUE.equals(request.isDynamicPricing()));
        khachSan.setGioNhanPhong(request.gioNhanPhong());
        khachSan.setGioTraPhong(request.gioTraPhong());
        khachSan.setGioNhanPhongMacDinh(request.gioNhanPhongMacDinh());
        khachSan.setGioTraPhongMacDinh(request.gioTraPhongMacDinh());
        khachSan.setSoTang(request.soTang());
        khachSan.setTongSoPhong(request.tongSoPhong());
    }

    private void applyNhaHang(NhaHang nhaHang, NhaHangRequest request) {
        nhaHang.setTen(request.ten());
        nhaHang.setLoaiAmThuc(request.loaiAmThuc());
        nhaHang.setMoTa(request.moTa());
        nhaHang.setGiaCoBan(request.giaCoBan());
        nhaHang.setIsDynamicPricing(Boolean.TRUE.equals(request.isDynamicPricing()));
        nhaHang.setSucChua(request.sucChua());
        nhaHang.setGioMoCua(request.gioMoCua());
        nhaHang.setGioDongCua(request.gioDongCua());
        nhaHang.setCoDatBanTruoc(request.coDatBanTruoc() == null || request.coDatBanTruoc());
        nhaHang.setCoDatMonTruoc(request.coDatMonTruoc() == null || request.coDatMonTruoc());
    }

    private void replaceHotelImages(KhachSan khachSan, List<AnhRequest> requests) {
        khachSan.getDanhSachAnh().clear();
        if (requests == null) {
            return;
        }
        requests.forEach(request -> {
            AnhKhachSan anh = new AnhKhachSan();
            anh.setKhachSan(khachSan);
            applyAnh(anh, request);
            khachSan.getDanhSachAnh().add(anh);
        });
    }

    private void replaceRestaurantImages(NhaHang nhaHang, List<AnhRequest> requests) {
        nhaHang.getDanhSachAnh().clear();
        if (requests == null) {
            return;
        }
        requests.forEach(request -> {
            AnhNhaHang anh = new AnhNhaHang();
            anh.setNhaHang(nhaHang);
            applyAnh(anh, request);
            nhaHang.getDanhSachAnh().add(anh);
        });
    }

    private void applyAnh(AnhKhachSan anh, AnhRequest request) {
        anh.setDuongDanUrl(request.duongDanUrl());
        anh.setMoTaAnh(request.moTaAnh());
        anh.setLaAnhDaiDien(Boolean.TRUE.equals(request.laAnhDaiDien()));
        anh.setNgayTaiLen(request.ngayTaiLen() == null ? LocalDate.now() : request.ngayTaiLen());
    }

    private void applyAnh(AnhNhaHang anh, AnhRequest request) {
        anh.setDuongDanUrl(request.duongDanUrl());
        anh.setMoTaAnh(request.moTaAnh());
        anh.setLaAnhDaiDien(Boolean.TRUE.equals(request.laAnhDaiDien()));
        anh.setNgayTaiLen(request.ngayTaiLen() == null ? LocalDate.now() : request.ngayTaiLen());
    }

    private void replaceHotelAmenities(KhachSan khachSan, List<TienIchKhachSanRequest> requests) {
        khachSan.getTienIch().clear();
        if (requests == null) {
            return;
        }
        requests.stream()
                .map(this::findOrCreateHotelAmenity)
                .forEach(khachSan.getTienIch()::add);
    }

    private TienIchKhachSan findOrCreateHotelAmenity(TienIchKhachSanRequest request) {
        return tienIchKhachSanRepository.findByTenTienIchIgnoreCase(request.tenTienIch())
                .orElseGet(() -> {
                    TienIchKhachSan tienIch = new TienIchKhachSan();
                    tienIch.setTenTienIch(request.tenTienIch());
                    tienIch.setLoaiTienIch(request.loaiTienIch());
                    tienIch.setMoTa(request.moTa());
                    return tienIchKhachSanRepository.save(tienIch);
                });
    }

    private void replaceRestaurantAmenities(NhaHang nhaHang, List<TienIchNhaHangRequest> requests) {
        nhaHang.getTienIch().clear();
        if (requests == null) {
            return;
        }
        requests.forEach(request -> {
            TienIchNhaHang tienIch = new TienIchNhaHang();
            tienIch.setNhaHang(nhaHang);
            tienIch.setTenTienIch(request.tenTienIch());
            tienIch.setLoaiTienIch(request.loaiTienIch());
            tienIch.setMoTa(request.moTa());
            tienIch.setCoThuPhi(Boolean.TRUE.equals(request.coThuPhi()));
            tienIch.setPhiSuDung(request.phiSuDung() == null ? 0.0f : request.phiSuDung());
            nhaHang.getTienIch().add(tienIch);
        });
    }

}

