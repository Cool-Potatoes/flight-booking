package com.flight_booking.user_service.application.service;

import com.flight_booking.common.application.dto.ProcessPaymentRequestDto;
import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.common.domain.model.PaymentStatusEnum;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.presentation.response.UserResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final KafkaTemplate<String, ApiResponse<?>> kafkaTemplate;

  // 전체 회원 목록 조회
  @Transactional(readOnly = true)
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  // 마일리지 차감
  @Transactional
  public UserResponse updateUserMileage(UserRequestDto userRequestDto) {

    User user = userRepository.findByEmail(userRequestDto.email())
        .orElseThrow();

    // 마일리지 있으면 마일리지 차감, 요금 할인
    user.updateMile(userRequestDto.Mileage());
    Long fair = userRequestDto.fare();
    fair -= userRequestDto.Mileage();

    kafkaTemplate.send("payment-process-topic", user.getId().toString(),
        ApiResponse.ok(new ProcessPaymentRequestDto(userRequestDto.bookingId(), fair,
                PaymentStatusEnum.PAYED), // TODO mileage 몇으로?
            "message from updateUserMileage"));

    return UserResponse.fromEntity(user);
  }
}