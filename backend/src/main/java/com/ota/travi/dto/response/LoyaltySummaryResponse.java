package com.ota.travi.dto.response;

import com.ota.travi.enums.HangThanhVien;

import java.math.BigDecimal;
import java.util.List;

public record LoyaltySummaryResponse(
        String khachHangId,
        Integer diemHienTai,
        BigDecimal tongChiTieu,
        HangThanhVien hangHienTai,
        HangThanhVien hangTiepTheo,
        BigDecimal soTienCanChiThem,
        BigDecimal progressPercent,
        List<PointHistoryResponse> lichSuDiemGanDay,
        List<CustomerVoucherResponse> voucherDangSoHuu
) {}
