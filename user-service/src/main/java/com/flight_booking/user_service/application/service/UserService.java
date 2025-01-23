package com.flight_booking.user_service.application.service;

import com.flight_booking.common.application.dto.PaymentRefundProcessRequestDto;
import com.flight_booking.common.application.dto.PaymentRetryRequestDto;
import com.flight_booking.common.application.dto.ProcessTicketPaymentRequestDto;
import com.flight_booking.common.application.dto.UserRefundTicketRequestDto;
import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.common.infrastructure.util.StackTraceUtils;
import com.flight_booking.common.presentation.dto.NotificationRequest;
import com.flight_booking.user_service.application.dto.UserStatusDto;
import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.messaging.UserKafkaSender;
import com.flight_booking.user_service.infrastructure.security.CustomUserDetails;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.UpdateRequest;
import com.flight_booking.user_service.presentation.response.AdminUserDetailResponse;
import com.flight_booking.user_service.presentation.response.PageResponse;
import com.flight_booking.user_service.presentation.response.UserDetailResponse;
import com.flight_booking.user_service.presentation.response.UserListResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final UserKafkaSender userKafkaSender;
  private final RedisTemplate<String, String> redisTemplate;

  // 전체 회원 목록 조회
  @Transactional(readOnly = true)
  public PageResponse<UserListResponse> getUserList(
      String email, String name, String role, Boolean isBlocked, Pageable pageable) {
    Page<UserListResponse> users = userRepository.findAll(email, name, role, isBlocked, pageable);
    return PageResponse.from(users);
  }

  // 회원 상세 정보 조회
  @Transactional(readOnly = true)
  public Object getUserDetails(Long id, CustomUserDetails userDetails) {
    User user = getUser(id);
    boolean isAdmin = isAdmin(userDetails);

    // 관리자의 경우
    if (isAdmin) {
      return AdminUserDetailResponse.fromEntity(user);
    }

    // 사용자의 경우 본인 정보만 조회 가능
    checkUser(userDetails, user);
    return UserDetailResponse.fromEntity(user);
  }

  // 회원 정보 수정
  @Transactional
  public void updateUser(Long id, CustomUserDetails userDetails, UpdateRequest updateRequest) {
    User user = getUser(id);
    boolean isAdmin = isAdmin(userDetails);

    // 사용자: 본인 정보만 수정, 추가 항목 수정 불가
    if (!isAdmin) {
      checkUser(userDetails, user);
      validateUser(updateRequest);
    }

    // 공통 수정 사항
    updateBasic(user, updateRequest);

    // 관리자: role 수정 가능
    if (isAdmin) {
      Role role = updateRequest.role();
      if (role != null) {
        user.setRole(role);
      }
    }
  }

  // 사용자 - 회원 탈퇴
  @Transactional
  public void deleteUser(Long id, CustomUserDetails userDetails) {
    User user = getUser(id);
    checkUser(userDetails, user);
    user.setDeletedBy(userDetails.getUsername());
    user.setIsDeleted(true);
    user.setDeletedAt(LocalDateTime.now());
  }

  // 상태 조회 (WebClient용)
  public UserStatusDto getUserStatus(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    return new UserStatusDto(user.getIsBlocked(), user.getIsDeleted());
  }


  /**
   * Kafka methods
   */

  // 마일리지 차감
  @Transactional
  public boolean updateUserMileage(UserRequestDto userRequestDto) {

    User user = userRepository.findByEmail(userRequestDto.email())
        .orElseThrow();

    if (user.getMileage() < userRequestDto.fare()) {

      if (isRetrying(userRequestDto.paymentId())) {
        return false;
      }

      sendPaymentRetryQueue(userRequestDto, user);
      redisTemplate.opsForValue()
          .set("Retry:paymentId:" + userRequestDto.paymentId().toString(), "In Retry Queue", 15,
              TimeUnit.MINUTES);

      // TODO 알림 발송. 잠시 후 자동으로 결제가 재시도 됩니다. 마일리지를 충전해주세요.
      sendInsufficientMileageMessage(user);

      return false;
    }

    user.updateMile(userRequestDto.fare());

    userKafkaSender.sendMessage(
        "payment-success-process-topic",
        user.getId().toString(),
        new PaymentRefundProcessRequestDto(userRequestDto.ticketId(), userRequestDto.paymentId(),
            userRequestDto.email()),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName()
    );

    return true;
  }

  // 동기, 환불
  @Transactional
  public Boolean checkAndRefundMileage(String email, Long difference, Long paymentFair) {

    User user = getUserByEmailAndIsDeletedFalse(email);

    if (user.getMileage() < difference) {
      return false;
    }

    // 환불해줌 ( 마일리지가 여유가 있으니 재 결제 )
    user.refundMile(paymentFair);

    return true;
  }

  // 동기, 취소 환불
  @Transactional
  public Boolean RefundMileage(String email, Long paymentFair) {

    User user = getUserByEmailAndIsDeletedFalse(email);

    // 환불해줌 ( 마일리지가 여유가 있으니 재 결제 )
    user.refundMile(paymentFair);

    return true;
  }

  private User getUserByEmailAndIsDeletedFalse(String email) {

    return userRepository.findByEmailAndIsDeletedFalse(email)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
  }

  public void createNotificationByEmail(NotificationRequest request) {
    String email = request.receiverEmail();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("일치하는 email을 찾을 수 없습니다."));
    sendNotificationCreationMessage(user, request.title(), request.content());
  }

  /**
   * private methods
   */

  // 존재하는 사용자 확인 및 삭제된 사용자 확인
  private User getUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    // 삭제된 사용자 확인
    if (user.getIsDeleted()) {
      throw new UserException(ErrorCode.USER_DELETED);
    }
    return user;
  }

  // 관리자 권한 확인
  private boolean isAdmin(CustomUserDetails userDetails) {
    return userDetails.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
  }

  // 사용자 본인 확인
  private void checkUser(CustomUserDetails userDetails, User user) {
    if (!userDetails.getUsername().equals(user.getEmail())) {
      throw new UserException(ErrorCode.ACCESS_ONLY_SELF);
    }
  }

  // 기본 수정 항목
  private void updateBasic(User user, UpdateRequest updateRequest) {
    String name = updateRequest.name();
    if (name != null) {
      user.setName(name);
    }
    String phone = updateRequest.phone();
    if (phone != null) {
      user.setPhone(phone);
    }
  }

  // 사용자 수정 불가 항목 처리
  private void validateUser(UpdateRequest updateRequest) {
    if (updateRequest.role() != null) {
      throw new UserException(ErrorCode.CANNOT_MODIFY_FIELD);
    }
  }

  private void sendPaymentRetryQueue(UserRequestDto userRequestDto, User user) {
    userKafkaSender.sendMessage("payment-retry-topic", user.getId().toString(),
        PaymentRetryRequestDto.from(user.getEmail(), userRequestDto),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName());
  }

  private boolean isRetrying(UUID paymentId) {
    return redisTemplate.hasKey("Retry:paymentId:" + paymentId.toString());
  }

  private void sendInsufficientMileageMessage(User user) {
    sendNotificationCreationMessage(
        user,
        "마일리지 부족으로 인한 결제 오류 안내",
        "마일리지 부족으로 인하여 결제가 이루어지지 않았습니다.\n"
            + "잠시 후 자동적으로 재결제가 이루어질 예정이니, 마일리지를 충전해주세요.");
  }

  private void sendNotificationCreationMessage(User user, String title, String message) {
    userKafkaSender.sendMessage(
        "notification-create-notification-topic",
        user.getId().toString(),
        new NotificationRequest(
            user.getId(), "From System", user.getEmail(), title, message
        ),
        StackTraceUtils.getCurrentMethodName(),
        StackTraceUtils.getCurrentClassName());
  }

}