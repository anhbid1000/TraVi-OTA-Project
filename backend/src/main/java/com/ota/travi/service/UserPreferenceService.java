package com.ota.travi.service;

import com.ota.travi.dto.request.UpdatePreferencesRequest;
import com.ota.travi.dto.response.DanhMucSoThichResponse;
import com.ota.travi.dto.response.SoThichResponse;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.SoThich;
import com.ota.travi.repository.DanhMucSoThichRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.SoThichRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserPreferenceService {

    private final DanhMucSoThichRepository danhMucRepository;
    private final SoThichRepository soThichRepository;
    private final KhachHangRepository khachHangRepository;
    private final JdbcTemplate jdbcTemplate;

    public UserPreferenceService(
            DanhMucSoThichRepository danhMucRepository,
            SoThichRepository soThichRepository,
            KhachHangRepository khachHangRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.danhMucRepository = danhMucRepository;
        this.soThichRepository = soThichRepository;
        this.khachHangRepository = khachHangRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<DanhMucSoThichResponse> getAllCategories() {
        return danhMucRepository.findAll().stream()
                .map(dm -> new DanhMucSoThichResponse(
                        dm.getId(),
                        dm.getTenDanhMuc(),
                        dm.getMoTa(),
                        dm.getDanhSachSoThich() != null
                                ? dm.getDanhSachSoThich().stream()
                                .map(st -> new SoThichResponse(st.getId(), st.getTenSoThich()))
                                .toList()
                                : List.of()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SoThichResponse> getUserPreferences(String username) {
        KhachHang khachHang = khachHangRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Chi khach hang moi co the xem so thich"));

        return soThichRepository.findByKhachHangId(khachHang.getId()).stream()
                .map(st -> new SoThichResponse(st.getId(), st.getTenSoThich()))
                .toList();
    }

    @Transactional
    public List<SoThichResponse> updateUserPreferences(String username, UpdatePreferencesRequest request) {
        KhachHang khachHang = khachHangRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Chi khach hang moi co the cap nhat so thich"));

        List<SoThich> requestedPreferences = soThichRepository.findByIdIn(request.soThichIds());
        if (requestedPreferences.size() != request.soThichIds().size()) {
            throw new RuntimeException("Mot so so thich khong ton tai");
        }

        jdbcTemplate.update("DELETE FROM khach_hang_so_thich WHERE khach_hang_id = ?", khachHang.getId());
        for (SoThich preference : requestedPreferences) {
            jdbcTemplate.update(
                    "INSERT INTO khach_hang_so_thich (khach_hang_id, so_thich_id) VALUES (?, ?)",
                    khachHang.getId(),
                    preference.getId()
            );
        }

        return requestedPreferences.stream()
                .map(st -> new SoThichResponse(st.getId(), st.getTenSoThich()))
                .toList();
    }
}
