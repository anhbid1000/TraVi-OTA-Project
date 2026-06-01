package com.ota.travi.dto.response;

public record ExchangeVoucherResponse(
        Long customerVoucherId,
        Long voucherId,
        String maVoucher,
        String maVoucherCaNhan,
        Integer diemCanDoi,
        Integer diemSauGiaoDich,
        String message
) {
}
