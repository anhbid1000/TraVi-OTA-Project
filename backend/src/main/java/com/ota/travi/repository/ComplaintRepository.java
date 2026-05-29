package com.ota.travi.repository;

import com.ota.travi.entity.Complaint;
import com.ota.travi.enums.MucDoKhieuNai;
import com.ota.travi.enums.TrangThaiKhieuNai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, String> {
    Page<Complaint> findByKhachHang_Id(String khachHangId, Pageable pageable);
    Page<Complaint> findByKhachHang_IdAndTrangThai(String khachHangId, TrangThaiKhieuNai trangThai, Pageable pageable);
    Page<Complaint> findByKhachHang_IdAndMucDo(String khachHangId, MucDoKhieuNai mucDo, Pageable pageable);
    Page<Complaint> findByKhachHang_IdAndTrangThaiAndMucDo(String khachHangId, TrangThaiKhieuNai trangThai, MucDoKhieuNai mucDo, Pageable pageable);

    Page<Complaint> findByHoSoKinhDoanh_DoiTac_Id(String partnerId, Pageable pageable);
    Page<Complaint> findByHoSoKinhDoanh_DoiTac_IdAndTrangThai(String partnerId, TrangThaiKhieuNai trangThai, Pageable pageable);
    Page<Complaint> findByHoSoKinhDoanh_DoiTac_IdAndMucDo(String partnerId, MucDoKhieuNai mucDo, Pageable pageable);
    Page<Complaint> findByHoSoKinhDoanh_DoiTac_IdAndTrangThaiAndMucDo(String partnerId, TrangThaiKhieuNai trangThai, MucDoKhieuNai mucDo, Pageable pageable);
}
