package com.flight_booking.user_service.application.service;

import com.flight_booking.common.application.dto.ProcessPaymentRequestDto;
import com.flight_booking.common.application.dto.UserRequestDto;
import com.flight_booking.common.presentation.global.ApiResponse;
import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetails;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.UpdateRequest;
import com.flight_booking.user_service.presentation.response.AdminUserDetailResponse;
import com.flight_booking.user_service.presentation.response.PageResponse;
import com.flight_booking.user_service.presentation.response.UserDetailResponse;
import com.flight_booking.user_service.presentation.response.UserListResponse;
import com.flight_booking.user_service.presentation.response.UserResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final KafkaTemplate<String, ApiResponse<?>> kafkaTemplate;

  // 이메일 기반으로 사용자 정보 조회
  @Transactional(readOnly = true)
  public UserResponse findUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    return UserResponse.fromEntity(user);
  }

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
  public void deleteUser(Long id, CustomUserDetails userDetails) {
    User user = getUser(id);
    checkUser(userDetails, user);
    user.setDeletedBy(userDetails.getUsername());
    user.setIsDeleted(true);
    user.setDeletedAt(LocalDateTime.now());
  }

  // -----------------------------------------------------------------------------------------------

  // 존재하는 사용자 확인 및 삭제된 사용자 확인
  private User getUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserException(ErrorCode.ACCESS_ONLY_SELF)); // 사용자 존재 여부 확인

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

  // 마일리지 차감
  @Transactional
  public void updateUserMileage(UserRequestDto userRequestDto) {

    // TODO lock 걸어서 유저 정보 변경 할 때 중복변경 되지 않게
    User user = userRepository.findByEmail(userRequestDto.email())
        .orElseThrow();

    // 마일리지가 충분한지 확인
    if (user.getMileage() < userRequestDto.fare()) {

      // payment fallback 로직
      kafkaTemplate.send("payment-fail-process-topic", userRequestDto.paymentId().toString(),
          ApiResponse.of(
              new ProcessPaymentRequestDto(userRequestDto.paymentId()),
              "message from updateUserMileage -Not enough mileage.",
              HttpStatus.BAD_REQUEST
          ));

      return;
    }

    // 마일리지 차감
    user.updateMile(userRequestDto.fare());

    // 결제 상태 업데이트
    kafkaTemplate.send("payment-success-process-topic", user.getId().toString(),
        ApiResponse.ok(new ProcessPaymentRequestDto(userRequestDto.paymentId()),
            "message from updateUserMileage"));

  }
}