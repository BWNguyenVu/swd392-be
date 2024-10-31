package com.swd.notification_service.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swd.notification_service.consts.Constants;
import com.swd.notification_service.dto.account.UserResponseDTO;
import com.swd.notification_service.dto.broadcast_notification.request.CreateBroadcastNotificationRequestDTO;
import com.swd.notification_service.dto.broadcast_notification.request.GetBroadcastNotificationListRequestDTO;
import com.swd.notification_service.dto.broadcast_notification.response.BroadcastNotificationResponseDTO;
import com.swd.notification_service.dto.notifications.PushNotificationEventDTO;
import com.swd.notification_service.dto.pagination.response.PaginationResponseDTO;
import com.swd.notification_service.entity.BroadcastNotification;
import com.swd.notification_service.mapper.BroadcastNotificationMapper;
import com.swd.notification_service.repository.BroadcastNotificationRepository;
import com.swd.notification_service.services.BroadcastNotificationService;
import com.swd.notification_service.services.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BroadcastNotificationServiceImpl implements BroadcastNotificationService {
    @NonNull
    private BroadcastNotificationRepository broadcastNotificationRepository;
    @NonNull
    private NotificationService notificationService;
    @NonNull
    private ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;
    @NonNull
    private ObjectMapper objectMapper;

    @Override
    public BroadcastNotificationResponseDTO createBroadcastNotification(CreateBroadcastNotificationRequestDTO requestDTO) {
        BroadcastNotification broadcastNotification = BroadcastNotification.builder()
                .title(requestDTO.getTitle())
                .type(requestDTO.getType())
                .scheduledTime(requestDTO.getScheduledTime())
                .message(requestDTO.getMessage())
                .destinationScreen(requestDTO.getDestinationScreen())
                .build();
        BroadcastNotification result = broadcastNotificationRepository.save(broadcastNotification);
        if (broadcastNotification.getScheduledTime() == null) {
            this.broadcastNotification(result);
        }

        return BroadcastNotificationMapper.toResponseDTO(result);
    }

    @Override
    public PaginationResponseDTO<BroadcastNotificationResponseDTO> getBroadcastNotifications(GetBroadcastNotificationListRequestDTO requestDTO) {
        if(requestDTO.getStartDate() == null ){
            requestDTO.setStartDate(LocalDateTime.of(1970, 1, 1, 0, 0));
        }
        if(requestDTO.getEndDate() == null ){
            requestDTO.setEndDate(LocalDateTime.of(9999, 12, 31, 0, 0));
        }

        Sort sort;
        switch (requestDTO.getSortBy()) {
            case Constants.SORT_BY_CREATE_DATE:
                sort = Sort.by(Constants.SORT_BY_CREATE_DATE);
                break;
            case Constants.SORT_BROADCAST_NOTIFICATION_BY_EXECUTE_TIME:
                sort = Sort.by(Constants.SORT_BROADCAST_NOTIFICATION_BY_EXECUTE_TIME);
                break;
            default:
                sort = Sort.by(Constants.SORT_BY_UPDATE_DATE);
                break;
        }
        //Construct sort order parameters
        if (Constants.SORT_ORDER_DESCENDING.equals(requestDTO.getOrder())) {
            sort = sort.descending();
        }
        //Construct pagination and sort parameters
        Pageable pageable = PageRequest.of(
                requestDTO.getPageNumber(),
                requestDTO.getPageSize(),
                sort
        );
        Page<BroadcastNotification> result = broadcastNotificationRepository.findByParameters(
                requestDTO.getSearch(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                requestDTO.getIsExecuted(), requestDTO.getIsDeleted(),
                pageable
        );
        return BroadcastNotificationMapper.toPaginationResponseDTO(result);
    }

    @Override
    public BroadcastNotificationResponseDTO getBroadcastNotificationById(Integer notificationId) {
        BroadcastNotification broadcastNotification = broadcastNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notificationId"));
        return BroadcastNotificationMapper.toResponseDTO(broadcastNotification);
    }

    @Override
    public void deleteBroadcastNotification(Integer notificationId) {
        BroadcastNotification broadcastNotification = broadcastNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notificationId"));
        broadcastNotification.setIsDeleted(Boolean.TRUE);
        broadcastNotificationRepository.save(broadcastNotification);
    }

    @Override
    public void restoreBroadcastNotification(Integer notificationId) {
        BroadcastNotification broadcastNotification = broadcastNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notificationId"));
        broadcastNotification.setIsDeleted(Boolean.FALSE);
        broadcastNotificationRepository.save(broadcastNotification);
    }

    @Override
    public void checkForBroadcastNotification() {
        LocalDateTime now = LocalDateTime.now();
        List<BroadcastNotification> pendingList = broadcastNotificationRepository.findByIsExecutedFalseAndScheduledTimeAfter(now);
        pendingList.forEach(this::broadcastNotification);
    }

    public void broadcastNotification(BroadcastNotification broadcastNotification) {
        try {
            broadcastNotification.setIsExecuted(true);
            broadcastNotification.setExecuteTime(LocalDateTime.now());
            broadcastNotificationRepository.save(broadcastNotification);
            ProducerRecord<String, Object> record = new ProducerRecord<>("user-request-topic", null);
            RequestReplyFuture<String, Object, Object> future = replyingKafkaTemplate.sendAndReceive(record);
            ConsumerRecord<String, Object> response = future.get();
            String innerJsonString = objectMapper.readValue(response.value().toString(), String.class);
            List<UserResponseDTO> userList = objectMapper.readValue(
                    innerJsonString, new TypeReference<List<UserResponseDTO>>() {}
            );
            List<PushNotificationEventDTO> eventDTOList = userList.stream()
                    .map(userResponseDTO ->
                            PushNotificationEventDTO.builder()
                                .userId(userResponseDTO.getId())
                                .title(broadcastNotification.getTitle())
                                .type(broadcastNotification.getType())
                                .destinationScreen(broadcastNotification.getDestinationScreen())
                                .message(broadcastNotification.getMessage())
                                .build()
                    )
                    .toList();
            notificationService.pushMultipleNotifications(eventDTOList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
