package com.flight_booking.user_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "p_users")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "name", nullable = false, length = 30)
  private String name;

  @Column(name = "phone", nullable = false, length = 20)
  private String phone;

  @Column(name = "mileage", nullable = false)
  private Long mileage = 0L;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role = Role.USER;

  @Column(name = "is_blocked", nullable = false)
  private Boolean isBlocked = false;

  @PreRemove
  public void preRemove() {
    this.isDeleted = true;
    this.deletedAt = LocalDateTime.now();
    this.deletedBy = this.email;
  }

  @PrePersist
  protected void onCreate() {
    this.updatedBy = this.email;
    this.createdBy = this.email;
  }

}