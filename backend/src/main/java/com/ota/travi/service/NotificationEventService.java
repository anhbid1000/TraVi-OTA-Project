package com.ota.travi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ota.travi.entity.NotificationEvent;
import com.ota.travi.exception.BusinessConflictException;
import com.ota.travi.exception.ResourceNotFoundException;
import com.ota.travi.repository.NotificationEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventService {

    private final NotificationEventRepository notificationEventRepository;
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @Value("${notification.async.enabled:false}")
    private Boolean asyncEnabled;

    @Value("${notification.queue.enabled:false}")
    private Boolean queueEnabled;

    public enum EventType {
        POINT_EARNED,
        TIER_UPGRADED,
        VOUCHER_RECEIVED,
        VOUCHER_EXPIRING_SOON,
        MILESTONE_REWARD_GRANTED,
        VOUCHER_APPLIED,
        VOUCHER_CONSUMED,
        VOUCHER_RELEASED
    }

    @Transactional
    public NotificationEvent emitEvent(EventType eventType, String customerId, Map<String, Object> metadata) {
        log.info("Emitting notification event: {} for customer: {}", eventType, customerId);

        try {
            NotificationEvent event = new NotificationEvent();
            event.setEventType(eventType.toString());
            event.setCustomerId(customerId);
            event.setIsProcessed(false);
            event.setCreatedAt(LocalDateTime.now());

            // Serialize metadata to JSON
            if (metadata != null) {
                event.setMetadata(objectMapper.writeValueAsString(metadata));
            }

            NotificationEvent savedEvent = notificationEventRepository.save(event);
            log.debug("Notification event saved with ID: {}", savedEvent.getId());

            // Publish to message queue if enabled
            if (asyncEnabled && queueEnabled) {
                publishToQueue(savedEvent);
            }

            return savedEvent;
        } catch (Exception e) {
            log.error("Error emitting notification event: {}", e.getMessage(), e);
            throw new BusinessConflictException("Failed to emit notification event: " + e.getMessage());
        }
    }

    @Transactional
    public NotificationEvent emitPointEarned(String customerId, Integer points, String bookingId) {
        log.debug("Emitting POINT_EARNED event for customer: {}", customerId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("points", points);
        metadata.put("bookingId", bookingId);
        metadata.put("earnedAt", LocalDateTime.now());

        return emitEvent(EventType.POINT_EARNED, customerId, metadata);
    }

    @Transactional
    public NotificationEvent emitTierUpgraded(String customerId, String newTier, Double totalSpending) {
        log.debug("Emitting TIER_UPGRADED event for customer: {}", customerId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("newTier", newTier);
        metadata.put("totalSpending", totalSpending);
        metadata.put("upgradedAt", LocalDateTime.now());

        return emitEvent(EventType.TIER_UPGRADED, customerId, metadata);
    }

    @Transactional
    public NotificationEvent emitVoucherReceived(String customerId, Long voucherId, String sourceType) {
        log.debug("Emitting VOUCHER_RECEIVED event for customer: {}", customerId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("voucherId", voucherId);
        metadata.put("sourceType", sourceType);
        metadata.put("receivedAt", LocalDateTime.now());

        return emitEvent(EventType.VOUCHER_RECEIVED, customerId, metadata);
    }

    @Transactional
    public NotificationEvent emitMilestoneRewardGranted(String customerId, Integer milestone, Integer reward) {
        log.debug("Emitting MILESTONE_REWARD_GRANTED event for customer: {}", customerId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("milestone", milestone);
        metadata.put("reward", reward);
        metadata.put("grantedAt", LocalDateTime.now());

        return emitEvent(EventType.MILESTONE_REWARD_GRANTED, customerId, metadata);
    }

    @Transactional
    public NotificationEvent emitVoucherApplied(String customerId, Long voucherId, Double discountAmount) {
        log.debug("Emitting VOUCHER_APPLIED event for customer: {}", customerId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("voucherId", voucherId);
        metadata.put("discountAmount", discountAmount);
        metadata.put("appliedAt", LocalDateTime.now());

        return emitEvent(EventType.VOUCHER_APPLIED, customerId, metadata);
    }

    @Transactional
    public NotificationEvent emitVoucherConsumed(String customerId, Long voucherId, Long bookingId) {
        log.debug("Emitting VOUCHER_CONSUMED event for customer: {}", customerId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("voucherId", voucherId);
        metadata.put("bookingId", bookingId);
        metadata.put("consumedAt", LocalDateTime.now());

        return emitEvent(EventType.VOUCHER_CONSUMED, customerId, metadata);
    }

    @Transactional
    public NotificationEvent emitVoucherReleased(String customerId, Long voucherId) {
        log.debug("Emitting VOUCHER_RELEASED event for customer: {}", customerId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("voucherId", voucherId);
        metadata.put("releasedAt", LocalDateTime.now());

        return emitEvent(EventType.VOUCHER_RELEASED, customerId, metadata);
    }

    @Transactional(readOnly = true)
    public List<NotificationEvent> getCustomerUnreadEvents(String customerId) {
        log.debug("Fetching unread events for customer: {}", customerId);

        return notificationEventRepository.findUnreadEvents(customerId);
    }

    @Transactional
    public void markEventProcessed(Long eventId) {
        log.debug("Marking event {} as processed", eventId);

        NotificationEvent event = notificationEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification event not found with id: " + eventId));

        event.setIsProcessed(true);
        event.setProcessedAt(LocalDateTime.now());
        notificationEventRepository.save(event);

        log.debug("Event {} marked as processed", eventId);
    }

    @Transactional(readOnly = true)
    public List<NotificationEvent> getCustomerEvents(String customerId) {
        log.debug("Fetching all events for customer: {}", customerId);

        return notificationEventRepository.findByCustomerId(customerId);
    }

    @Transactional
    public void markEventProcessedByCustomer(String customerId, Long eventId) {
        log.debug("Marking event {} as processed for customer: {}", eventId, customerId);

        NotificationEvent event = notificationEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification event not found with id: " + eventId));

        if (!event.getCustomerId().equals(customerId)) {
            throw new BusinessConflictException("Event does not belong to this customer");
        }

        markEventProcessed(eventId);
    }

    private void publishToQueue(NotificationEvent event) {
        log.debug("Publishing notification event to message queue: {}", event.getId());
        // Implementation would depend on message queue system (RabbitMQ, Kafka, etc.)
        // For now, log the action
        log.info("Event published to queue (async processing enabled): {}", event.getId());
    }
}
