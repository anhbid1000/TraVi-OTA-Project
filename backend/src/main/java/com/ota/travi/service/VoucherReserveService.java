package com.ota.travi.service;

import com.ota.travi.entity.CustomerVoucher;
import com.ota.travi.entity.Voucher;
import com.ota.travi.enums.TrangThaiCustomerVoucher;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.exception.ValidationException;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VoucherReserveService {

    private final CustomerVoucherRepository customerVoucherRepository;
    private final VoucherRepository voucherRepository;

    @Value("${voucher.reserve.timeout-seconds:1800}")
    private long reserveTimeoutSeconds;

    public CustomerVoucher reserveVoucher(String customerId, Long voucherId, long reserveTimeoutSeconds) {
        log.info("Attempting to reserve voucher {} for customer {}", voucherId, customerId);

        CustomerVoucher customerVoucher = customerVoucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer voucher not found with id: " + voucherId));

        if (!customerVoucher.getCustomerId().equals(customerId)) {
            throw new BusinessConflictException("Customer does not have this voucher");
        }

        if (customerVoucher.getTrangThai() == TrangThaiCustomerVoucher.DA_DUNG) {
            throw new BusinessConflictException("Voucher already used");
        }

        if (customerVoucher.getTrangThai() == TrangThaiCustomerVoucher.RESERVED) {
            if (customerVoucher.getReserveExpiresAt() != null && customerVoucher.getReserveExpiresAt().isAfter(LocalDateTime.now())) {
                throw new BusinessConflictException("Voucher already reserved");
            }
        }

        if (customerVoucher.getTrangThai() == TrangThaiCustomerVoucher.HET_HAN) {
            throw new BusinessConflictException("Voucher expired");
        }

        Voucher voucher = voucherRepository.findById(customerVoucher.getVoucherId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Voucher not found with id: " + customerVoucher.getVoucherId()
                ));

        if (voucher.getDonHangToiThieu() != null && voucher.getDonHangToiThieu() > 0) {
            log.debug("Voucher {} has minimum order amount: {}", voucherId, voucher.getDonHangToiThieu());
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(reserveTimeoutSeconds);

        customerVoucher.setTrangThai(TrangThaiCustomerVoucher.RESERVED);
        customerVoucher.setReservedAt(now);
        customerVoucher.setReserveExpiresAt(expiresAt);

        CustomerVoucher saved = customerVoucherRepository.save(customerVoucher);
        log.info("Successfully reserved voucher {} for customer {}. Expires at: {}", voucherId, customerId, expiresAt);
        return saved;
    }

    public CustomerVoucher releaseVoucher(Long customerVoucherId) {
        log.info("Attempting to release voucher {}", customerVoucherId);

        CustomerVoucher customerVoucher = customerVoucherRepository.findById(customerVoucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer voucher not found with id: " + customerVoucherId));

        if (customerVoucher.getTrangThai() != TrangThaiCustomerVoucher.RESERVED) {
            throw new BusinessConflictException("Voucher is not reserved. Current status: " + customerVoucher.getTrangThai());
        }

        customerVoucher.setTrangThai(TrangThaiCustomerVoucher.CHUA_DUNG);
        customerVoucher.setReservedAt(null);
        customerVoucher.setReserveExpiresAt(null);

        CustomerVoucher saved = customerVoucherRepository.save(customerVoucher);
        log.info("Successfully released voucher {}", customerVoucherId);
        return saved;
    }

    public CustomerVoucher consumeVoucher(Long customerVoucherId) {
        log.info("Attempting to consume voucher {}", customerVoucherId);

        CustomerVoucher customerVoucher = customerVoucherRepository.findById(customerVoucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer voucher not found with id: " + customerVoucherId));

        if (customerVoucher.getTrangThai() != TrangThaiCustomerVoucher.RESERVED) {
            throw new BusinessConflictException("Voucher is not reserved. Current status: " + customerVoucher.getTrangThai());
        }

        if (customerVoucher.getReserveExpiresAt() != null && customerVoucher.getReserveExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessConflictException("Voucher reserve has expired");
        }

        customerVoucher.setTrangThai(TrangThaiCustomerVoucher.DA_DUNG);
        customerVoucher.setUsedAt(LocalDateTime.now());

        Voucher voucher = voucherRepository.findById(customerVoucher.getVoucherId())
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + customerVoucher.getVoucherId()));

        voucher.setSoLuongDaDung((voucher.getSoLuongDaDung() != null ? voucher.getSoLuongDaDung() : 0) + 1);
        voucherRepository.save(voucher);

        CustomerVoucher saved = customerVoucherRepository.save(customerVoucher);
        log.info("Successfully consumed voucher {}. Voucher usage count updated to: {}", customerVoucherId, voucher.getSoLuongDaDung());
        return saved;
    }

    @Scheduled(fixedRateString = "${voucher.cleanup.interval-ms:300000}")
    public void cleanupExpiredReserves() {
        log.info("Running cleanup task for expired voucher reserves");

        LocalDateTime now = LocalDateTime.now();
        List<CustomerVoucher> expiredReserves = customerVoucherRepository.findReservedVouchersExpiredBefore(now);

        if (expiredReserves.isEmpty()) {
            log.debug("No expired reserves found");
            return;
        }

        log.info("Found {} expired reserved vouchers", expiredReserves.size());

        for (CustomerVoucher voucher : expiredReserves) {
            if (voucher.getTrangThai() == TrangThaiCustomerVoucher.RESERVED && 
                voucher.getReserveExpiresAt() != null && 
                voucher.getReserveExpiresAt().isBefore(now)) {
                
                voucher.setTrangThai(TrangThaiCustomerVoucher.CHUA_DUNG);
                voucher.setReservedAt(null);
                voucher.setReserveExpiresAt(null);
                customerVoucherRepository.save(voucher);
                
                log.debug("Released expired reserved voucher {}", voucher.getId());
            }
        }

        log.info("Cleanup completed. Released {} vouchers", expiredReserves.size());
    }

    @Transactional(readOnly = true)
    public List<CustomerVoucher> getCustomerVouchersWithStatus(String customerId, TrangThaiCustomerVoucher status) {
        log.debug("Fetching customer vouchers for customer {} with status {}", customerId, status);
        return customerVoucherRepository.findByCustomerIdOrderByIssuedAtDesc(customerId)
                .stream()
                .filter(voucher -> voucher.getTrangThai() == status)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isVoucherReserveValid(CustomerVoucher customerVoucher) {
        if (customerVoucher.getTrangThai() != TrangThaiCustomerVoucher.RESERVED) {
            return false;
        }

        if (customerVoucher.getReserveExpiresAt() == null) {
            return false;
        }

        return customerVoucher.getReserveExpiresAt().isAfter(LocalDateTime.now());
    }
}
