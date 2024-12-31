package com.flight_booking.notification_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailSenderTest {

  @Autowired
  private JavaMailSender mailSender;

  @Test
  public void testMailSender() {
    assert mailSender != null;
  }
}
