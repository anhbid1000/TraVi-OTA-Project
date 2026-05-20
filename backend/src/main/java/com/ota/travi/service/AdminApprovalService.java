package com.ota.travi.service;

import com.ota.travi.dto.request.ApprovalStatusRequest;
import com.ota.travi.dto.response.AdminApprovalResponse;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.enums.ApprovalDecisionStatus;
import com.ota.travi.enums.TrangThaiKiemDuyet;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.HoSoKinhDoanhRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminApprovalService {
    @Autowired
    private HoSoKinhDoanhRepository hoSoKinhDoanhRepository;

    @Autowired
    private PartnerAssetMapper partnerAssetMapper;

    @Autowired
    private BusinessApprovalMailService businessApprovalMailService;

    @Transactional(readOnly = true)
    public List<AdminApprovalResponse> getPendingApprovals() {
        return hoSoKinhDoanhRepository.findByTrangThaiKiemDuyet(TrangThaiKiemDuyet.CHO_DUYET)
                .stream()
                .map(partnerAssetMapper::toAdminApprovalResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminApprovalResponse getApprovalDetail(String id) {
        HoSoKinhDoanh hoSo = requireBusinessProfile(id);
        return partnerAssetMapper.toAdminApprovalResponse(hoSo);
    }

    @Transactional
    public AdminApprovalResponse updateApprovalStatus(String id, ApprovalStatusRequest request) {
        HoSoKinhDoanh hoSo = requireBusinessProfile(id);

        if (request.status() == ApprovalDecisionStatus.APPROVED) {
            approveBusinessProfile(hoSo);
        } else if (request.status() == ApprovalDecisionStatus.REJECTED) {
            rejectBusinessProfile(hoSo, request.reason());
        }

        return partnerAssetMapper.toAdminApprovalResponse(hoSoKinhDoanhRepository.save(hoSo));
    }

    private HoSoKinhDoanh requireBusinessProfile(String id) {
        return hoSoKinhDoanhRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ho so kinh doanh"));
    }

    private void approveBusinessProfile(HoSoKinhDoanh hoSo) {
        hoSo.setTrangThaiKiemDuyet(TrangThaiKiemDuyet.DANG_HOAT_DONG);
        hoSo.setThoiGianDuyet(LocalDateTime.now());
        businessApprovalMailService.sendApprovalEmail(
                hoSo.getDoiTac().getEmail(),
                hoSo.getDoiTac().getHoTen(),
                hoSo.getTenCoSo()
        );
    }

    private void rejectBusinessProfile(HoSoKinhDoanh hoSo, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new ValidationException("Ly do tu choi khong duoc de trong");
        }
        hoSo.setTrangThaiKiemDuyet(TrangThaiKiemDuyet.BI_TU_CHOI);
        hoSo.setThoiGianDuyet(LocalDateTime.now());
        businessApprovalMailService.sendRejectionEmail(
                hoSo.getDoiTac().getEmail(),
                hoSo.getDoiTac().getHoTen(),
                hoSo.getTenCoSo(),
                reason
        );
    }
}
