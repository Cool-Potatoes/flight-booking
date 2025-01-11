package com.flight_booking.user_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false, nullable = false, length = 100)
  protected String createdBy;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @LastModifiedBy
  @Column(name = "updated_by", length = 100)
  protected String updatedBy;

  @Setter
  @Column(name = "deleted_at")
  protected LocalDateTime deletedAt;

  @Setter
  @Column(name = "deleted_by", length = 100)
  protected String deletedBy;

  @Setter
  @Column(name = "is_deleted", nullable = false)
  protected Boolean isDeleted = false;
}
