package com.flight_booking.notification_service;

import com.flight_booking.notification_service.repository.NotificationRepository;
import com.flight_booking.notification_service.service.EmailService;
import com.flight_booking.notification_service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class NotificationServiceTests {

	@MockBean
	private NotificationRepository notificationRepository;

	@MockBean
	private EmailService emailService;

	@Autowired
	private NotificationService notificationService;

	@Test
	void testNotificationCreation() {
		// 테스트 코드 작성
	}
}
