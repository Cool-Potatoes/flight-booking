package com.flight_booking.gateway_service.infrastructure.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;

@Configuration
public class FeignConfig {

  @Bean
  public Decoder feignDecoder() {
    ObjectFactory<HttpMessageConverters> messageConverters = () -> {
      HttpMessageConverters converters = new HttpMessageConverters();
      return converters;
    };
    return new SpringDecoder(messageConverters);
  }
}
