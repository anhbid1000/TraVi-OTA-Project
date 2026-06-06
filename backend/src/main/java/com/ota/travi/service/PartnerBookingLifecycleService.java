package com.ota.travi.service;

import com.ota.travi.dto.response.PartnerBookingCompletionResponse;
import com.ota.travi.entity.DonDatCho;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.DonDatChoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PartnerBookingLifecycleService {

    private static final Set<TrangThaiDon> COMPLETABLE_STATUSES = EnumSet.of(
            TrangThaiDon.DA_THANH_TOAN,
            TrangThaiDon.DA_XAC_NHAN,
            TrangThaiDon.DANG_PHUC_VU
    );

    private final DonDatChoRepository donDatChoRepository;
    private final CustomerLoyaltyService customerLoyaltyService;

    @Transactional
    public PartnerBookingCompletionResponse completeBooking(String partnerId, String bookingId) {
        DonDatCho booking = donDatChoRepository.findByIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        requirePartnerOwnsBooking(partnerId, booking);

        if (booking.getTrangThai() == TrangThaiDon.DA_HOAN_THANH) {
            customerLoyaltyService.processBookingCompletionReward(booking);
            return toResponse(booking, true, "Booking was already completed. Loyalty reward is up to date.");
        }

        if (!COMPLETABLE_STATUSES.contains(booking.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking cannot be completed from status " + booking.getTrangThai()
            );
        }

        booking.setTrangThai(TrangThaiDon.DA_HOAN_THANH);
        DonDatCho saved = donDatChoRepository.save(booking);
        customerLoyaltyService.processBookingCompletionReward(saved);

        return toResponse(saved, true, "Booking completed successfully.");
    }

    private void requirePartnerOwnsBooking(String partnerId, DonDatCho booking) {
        if (booking.getHoSoKinhDoanh() == null
                || booking.getHoSoKinhDoanh().getDoiTac() == null
                || !partnerId.equals(booking.getHoSoKinhDoanh().getDoiTac().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Partner does not have access to this booking");
        }
    }

    private PartnerBookingCompletionResponse toResponse(
            DonDatCho booking,
            boolean loyaltyRewardProcessed,
            String message
    ) {
        return new PartnerBookingCompletionResponse(
                booking.getId(),
                booking.getMaDon(),
                booking.getTrangThai(),
                loyaltyRewardProcessed,
                message
        );
    }
}
