package com.flight_booking.flight_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight_booking.common.application.dto.SeatCalculateDifferenceAndRefundRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SeatDLTHandler {

  private final ObjectMapper objectMapper;
  private final DltService dltService; // 문제 복구/저장/알림 처리용 서비스

  public SeatDLTHandler(ObjectMapper objectMapper, DltService dltService) {
    this.objectMapper = objectMapper;
    this.dltService = dltService;
  }

  @KafkaListener(groupId = "seat-calculate-difference-and-refund-group-dlt", //  topics = {"seat-calculate-difference-and-refund-topic.dlt", "another-topic.dlt"}
      topics = "seat-calculate-difference-and-refund-topic.dlt")
  public void handleDLTMessage(@Payload String message) {
    log.error("DLT message received: {}", message);

    try {
      // 1. 메시지 파싱
      SeatCalculateDifferenceAndRefundRequestDto dto = objectMapper.readValue(
          message, SeatCalculateDifferenceAndRefundRequestDto.class);

      // 2. 유효성 검증
      if (dto == null || dto.seatId() == null) {
        log.error("Invalid message format: {}", message);
        return;
      }

      // 3. 복구 시도
      boolean isRecovered = dltService.tryRecover(dto);
      if (isRecovered) {
        log.info("Message successfully recovered: {}", dto);
        return;
      }

      // 4. 복구 실패 -> 알림 전송 및 데이터베이스 저장
      log.error("Failed to recover message: {}", dto);
      dltService.notifyFailure(dto);
      dltService.saveToDatabase(dto);

    } catch (Exception e) {
      log.error("Error processing DLT message: {}", message, e);
    }
  }
}
