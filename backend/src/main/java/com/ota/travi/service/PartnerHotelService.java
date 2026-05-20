package com.ota.travi.service;

import com.ota.travi.dto.request.AnhRequest;
import com.ota.travi.dto.request.PhongRequest;
import com.ota.travi.dto.request.PhongUpsertRequest;
import com.ota.travi.dto.response.PhongResponse;
import com.ota.travi.entity.AnhPhong;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.Phong;
import com.ota.travi.enums.TrangThaiPhong;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ForbiddenOperationException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.KhachSanRepository;
import com.ota.travi.repository.PhongRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class PartnerHotelService {
    @Autowired
    private KhachSanRepository khachSanRepository;

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private PartnerAssetMapper partnerAssetMapper;

    @Transactional
    public PhongResponse createRoom(String partnerId, String hotelId, PhongUpsertRequest request) {
        KhachSan khachSan = requireOwnedHotel(partnerId, hotelId);
        if (phongRepository.existsByKhachSan_IdTaiSanAndSoPhong(hotelId, request.phong().soPhong())) {
            throw new BusinessConflictException("So phong da ton tai trong khach san nay");
        }

        Phong phong = toPhong(request.phong(), khachSan);
        replaceRoomImages(phong, request.danhSachAnh());
        return partnerAssetMapper.toPhongResponse(phongRepository.save(phong));
    }

    @Transactional
    public PhongResponse updateRoom(String partnerId, String hotelId, String roomId, PhongUpsertRequest request) {
        requireOwnedHotel(partnerId, hotelId);
        Phong phong = requireRoomInHotel(hotelId, roomId);
        applyPhong(phong, request.phong());
        replaceRoomImages(phong, request.danhSachAnh());
        return partnerAssetMapper.toPhongResponse(phongRepository.save(phong));
    }

    @Transactional
    public void deleteRoom(String partnerId, String hotelId, String roomId) {
        requireOwnedHotel(partnerId, hotelId);
        Phong phong = requireRoomInHotel(hotelId, roomId);
        if (hasFutureBookingForRoom(roomId)) {
            throw new BusinessConflictException("Khong the xoa phong vi dang co don dat cho trong tuong lai");
        }
        phongRepository.delete(phong);
    }

    private KhachSan requireOwnedHotel(String partnerId, String hotelId) {
        KhachSan khachSan = khachSanRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay khach san"));
        if (!Objects.equals(khachSan.getHoSoKinhDoanh().getDoiTac().getId(), partnerId)) {
            throw new ForbiddenOperationException("Ban khong co quyen thao tac voi khach san nay");
        }
        return khachSan;
    }

    private Phong requireRoomInHotel(String hotelId, String roomId) {
        Phong phong = phongRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phong"));
        if (!Objects.equals(phong.getKhachSan().getIdTaiSan(), hotelId)) {
            throw new ResourceNotFoundException("Phong khong thuoc khach san nay");
        }
        return phong;
    }

    private Phong toPhong(PhongRequest request, KhachSan khachSan) {
        Phong phong = new Phong();
        phong.setKhachSan(khachSan);
        applyPhong(phong, request);
        return phong;
    }

    private void applyPhong(Phong phong, PhongRequest request) {
        phong.setSoPhong(request.soPhong());
        phong.setLoaiPhong(request.loaiPhong());
        phong.setSucChuaToiDa(request.sucChuaToiDa());
        phong.setDienTich(request.dienTich());
        phong.setTrangThai(request.trangThai() == null ? TrangThaiPhong.SAN_SANG : request.trangThai());
        phong.setTienIch(request.tienIch() == null ? new HashSet<>() : new HashSet<>(request.tienIch()));
        phong.setPhanTramGiamGia(request.phanTramGiamGia() == null ? 0.0f : request.phanTramGiamGia());
    }

    private void replaceRoomImages(Phong phong, List<AnhRequest> requests) {
        phong.getDanhSachAnh().clear();
        if (requests == null) {
            return;
        }
        requests.forEach(request -> {
            AnhPhong anh = new AnhPhong();
            anh.setPhong(phong);
            applyAnh(anh, request);
            phong.getDanhSachAnh().add(anh);
        });
    }

    private void applyAnh(AnhPhong anh, AnhRequest request) {
        anh.setDuongDanUrl(request.duongDanUrl());
        anh.setMoTaAnh(request.moTaAnh());
        anh.setLaAnhDaiDien(Boolean.TRUE.equals(request.laAnhDaiDien()));
        anh.setNgayTaiLen(request.ngayTaiLen() == null ? LocalDate.now() : request.ngayTaiLen());
    }

    private boolean hasFutureBookingForRoom(String roomId) {
        // TODO: Noi voi repository DonDatCho khi module Booking duoc tao.
        return false;
    }
}
