package com.ota.travi.service;

import com.ota.travi.entity.IdempotencyKey;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.repository.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Value("${idempotency.key.ttl-hours:24}")
    private int keyTtlHours;

    public void storeIdempotencyKey(String key, String operationType, String customerId, String responseJson) {
        log.info("Storing idempotency key: {} for customer: {} and operation: {}", key, customerId, operationType);

        Optional<IdempotencyKey> existingKey = idempotencyKeyRepository.findByIdempotencyKey(key);
        if (existingKey.isPresent()) {
            log.warn("Idempotency key already exists: {}. Throwing conflict exception.", key);
            throw new BusinessConflictException("Idempotency key already processed: " + key);
        }

        IdempotencyKey idempotencyKey = new IdempotencyKey();
        idempotencyKey.setIdempotencyKey(key);
        idempotencyKey.setOperationType(operationType);
        idempotencyKey.setCustomerId(customerId);
        idempotencyKey.setResponseJson(responseJson);
        idempotencyKey.setStatus("COMPLETED");
        idempotencyKey.setExpiresAt(LocalDateTime.now().plusHours(keyTtlHours));

        idempotencyKeyRepository.save(idempotencyKey);
        log.info("Successfully stored idempotency key: {}", key);
    }

    @Transactional(readOnly = true)
    public Optional<String> getIdempotentResponse(String key) {
        log.debug("Fetching idempotency key response for: {}", key);

        Optional<IdempotencyKey> idempotencyKey = idempotencyKeyRepository.findByIdempotencyKey(key);
        if (idempotencyKey.isPresent()) {
            IdempotencyKey entity = idempotencyKey.get();
            if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.debug("Idempotency key has expired: {}", key);
                return Optional.empty();
            }
            log.debug("Found cached response for idempotency key: {}", key);
            return Optional.ofNullable(entity.getResponseJson());
        }

        return Optional.empty();
    }

    public void cleanupExpiredIdempotencyKeys() {
        log.info("Cleaning up expired idempotency keys");
        idempotencyKeyRepository.deleteExpiredKeys(LocalDateTime.now());
        log.info("Idempotency key cleanup completed");
    }
}
