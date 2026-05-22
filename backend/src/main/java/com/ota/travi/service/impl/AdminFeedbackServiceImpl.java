package com.ota.travi.service.impl;

import com.ota.travi.Enum.TrangThaiPhanHoi;
import com.ota.travi.entity.DanhGia;
import com.ota.travi.entity.PhanHoiUser;
import com.ota.travi.entity.ToCao;
import com.ota.travi.repository.DanhGiaRepository;
import com.ota.travi.repository.PhanHoiBaseRepository;
import com.ota.travi.service.AdminFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class AdminFeedbackServiceImpl implements AdminFeedbackService {
    @Autowired private PhanHoiBaseRepository phanHoiBaseRepository;
    @Autowired private DanhGiaRepository danhGiaRepository;

    @Override
    public void goiApiKhoaTaiKhoanPartner(String idPartner) {
        System.out.println("Đang thực thi khóa tài khoản khẩn cấp cho Partner ID: " + idPartner);
    }

    @Override
    public void taoVoucherDenBu(String idUser) {
        System.out.println("Đang tự động sinh mã 'DENBU'...");
        System.out.println("Đã nạp thành công Voucher đền bù vào ví của Khách hàng ID: " + idUser);
    }

    // DUYỆT REVIEW RÁC
    @Override
    public DanhGia moderateReview(String id, String action) {
        DanhGia dg = danhGiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá"));
        if ("AN".equalsIgnoreCase(action)) {
            dg.setTrangThai(TrangThaiPhanHoi.DA_AN);
        } else if ("XOA".equalsIgnoreCase(action)) {
            dg.setTrangThai(TrangThaiPhanHoi.DA_XOA);
        }
        return danhGiaRepository.save(dg);
    }

    // ĐIỀU TRA & ĐỔI TRẠNG THÁI TỐ CÁO
    @Override
    public Map<String, Object> changeReportStatus(String id, String targetStatus) {
        PhanHoiUser ph = phanHoiBaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bản ghi tố cáo"));

        if (!(ph instanceof ToCao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ: Bản ghi này không phải là Tố Cáo!");
        }

        ToCao tc = (ToCao) ph;
        TrangThaiPhanHoi newStatus;
        try {
            newStatus = TrangThaiPhanHoi.valueOf(targetStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái chuyển đổi không hợp lệ!");
        }

        // Kiểm soát luồng chuyển trạng thái
        if (newStatus == TrangThaiPhanHoi.DANG_DIEU_TRA) {
            if (tc.getTrangThai() != TrangThaiPhanHoi.DA_TIEP_NHAN) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ đơn ở trạng thái 'DA_TIEP_NHAN' mới có thể chuyển sang 'DANG_DIEU_TRA'!");
            }
        } else if (newStatus == TrangThaiPhanHoi.CHO_PARTNER_GIAI_TRINH) {
            if (tc.getTrangThai() != TrangThaiPhanHoi.DANG_DIEU_TRA) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ đơn ở trạng thái 'DANG_DIEU_TRA' mới có thể yêu cầu Partner giải trình!");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "API này chỉ dùng để chuyển sang 'DANG_DIEU_TRA' hoặc 'CHO_PARTNER_GIAI_TRINH'!");
        }

        tc.setTrangThai(newStatus);
        phanHoiBaseRepository.save(tc);
        return Map.of("message", "Cập nhật trạng thái điều tra thành công", "status", newStatus);
    }

    // 🌟 LOGIC PHÁN QUYẾT TỐ CÁO
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> postVerdict(String id, String mucDoViPham) {
        ToCao tc = (ToCao) phanHoiBaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn tố cáo với ID: " + id));

        switch (mucDoViPham.toUpperCase()) {
            case "KHONG_VI_PHAM":
                tc.setTrangThai(TrangThaiPhanHoi.DA_GIAI_QUYET);
                phanHoiBaseRepository.save(tc);
                return Map.of("verdict", "KHONG_VI_PHAM", "message", "Đóng đơn tố cáo. Không phát hiện sai phạm.");

            case "VI_PHAM_NHE":
                System.out.println("Cảnh cáo đối tác ID: " + tc.getIdNguoiGiaiQuyet() + " vì hành vi vi phạm nhẹ.");
                tc.setTrangThai(TrangThaiPhanHoi.DA_GIAI_QUYET);
                phanHoiBaseRepository.save(tc);
                return Map.of("verdict", "VI_PHAM_NHE", "message", "Hệ thống đã ghi log cảnh cáo đối tác và đóng đơn.");

            case "VI_PHAM_NANG":
                tc.setTrangThai(TrangThaiPhanHoi.DA_GIAI_QUYET);
                phanHoiBaseRepository.save(tc);
                goiApiKhoaTaiKhoanPartner(tc.getIdNguoiGiaiQuyet());
                taoVoucherDenBu(tc.getIdUser());

                return Map.of(
                        "verdict", "VI_PHAM_NANG",
                        "message", "Đã khóa đối tác và tự động phát voucher đền bù vào ví khách hàng!"
                );

            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mức độ vi phạm không hợp lệ! Chọn: KHONG_VI_PHAM, VI_PHAM_NHE, VI_PHAM_NANG.");
        }
    }
}
