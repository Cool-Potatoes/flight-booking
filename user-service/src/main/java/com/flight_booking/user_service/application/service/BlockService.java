package com.flight_booking.user_service.application.service;

import com.flight_booking.user_service.domain.model.Role;
import com.flight_booking.user_service.domain.model.User;
import com.flight_booking.user_service.domain.repository.UserRepository;
import com.flight_booking.user_service.infrastructure.security.authentication.CustomUserDetails;
import com.flight_booking.user_service.presentation.global.exception.ErrorCode;
import com.flight_booking.user_service.presentation.global.exception.UserException;
import com.flight_booking.user_service.presentation.request.BlockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockService {

  private final UserRepository userRepository;

  // 관리자 - 회원 블락 처리
  public void blockUser(Long id, BlockRequest request, CustomUserDetails userDetails) {
    User user = getUser(id);
    if (user.getRole() != Role.ADMIN) {
      throw new UserException(ErrorCode.CANNOT_ADMIN_BLOCKED);
    }
    if (user.getIsBlocked()) {
      throw new UserException(ErrorCode.USER_BLOCKED);
    }
    user.blockUser(request.reason(), userDetails.getUsername());
  }

  // 관리자 - 회원 블락 해제
  public void unblockUser(Long id) {
    User user = getUser(id);
    if (!user.getIsBlocked()) {
      throw new UserException(ErrorCode.USER_NOT_BLOCKED);
    }
    user.unblockUser();
  }

  //-----------------------------------------------------------------------------------------

  // 존재하는 사용자 확인 및 삭제된 사용자 확인
  private User getUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserException(ErrorCode.ACCESS_ONLY_SELF));

    if (user.getIsDeleted()) {
      throw new UserException(ErrorCode.USER_DELETED);
    }
    return user;
  }

}
