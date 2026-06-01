package com.ota.travi.dto.response;

import java.time.LocalDateTime;

public record ThongBaoNguCanhResponse(
    String idThongBao,
    String loaiNguCanh,
    String noiDung,
    String mucDo,
    LocalDateTime thoiGianHieuLuc,
    String type // WEATHER, BOOKING_REMINDER, RECOMMENDATION
) {}
