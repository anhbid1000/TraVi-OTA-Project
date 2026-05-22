package com.ota.travi.service;

import com.ota.travi.entity.DanhGia;

import java.util.Map;

public interface AdminFeedbackService {
    void goiApiKhoaTaiKhoanPartner(String idPartner);
    void taoVoucherDenBu(String idUser);
    DanhGia moderateReview(String id, String action);
    Map<String, Object> changeReportStatus(String id, String targetStatus);
    Map<String, Object> postVerdict(String id, String mucDoViPham);
}
