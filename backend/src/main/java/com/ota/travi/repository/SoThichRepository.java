package com.ota.travi.repository;

import com.ota.travi.entity.SoThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoThichRepository extends JpaRepository<SoThich, String> {
    List<SoThich> findByIdIn(List<String> ids);

    @Query("select st from SoThich st join st.danhSachKhachHang kh where kh.id = :khachHangId")
    List<SoThich> findByKhachHangId(String khachHangId);
}
