package com.ota.travi.service;

import com.ota.travi.entity.DonDatCho;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class HotelBookingPaymentHoldService {

    private static final Duration PAYMENT_HOLD_TTL = Duration.ofMinutes(20);
    // Dùng chung cho mọi loại đơn (khách sạn / nhà hàng)
    private static final String HOLD_KEY_PREFIX = "booking:payment:pending:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void putPending(DonDatCho booking) {
        String key = getHoldKey(booking.getId());
        String value = booking.getMaDon() + "|" + booking.getPaymentExpiredAt();
        redisTemplate.opsForValue().set(key, value, PAYMENT_HOLD_TTL);
    }

    public void removePending(String bookingId) {
        redisTemplate.delete(getHoldKey(bookingId));
    }

    public boolean isPendingInRedis(String bookingId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getHoldKey(bookingId)));
    }

    private String getHoldKey(String bookingId) {
        return HOLD_KEY_PREFIX + bookingId;
    }
}
