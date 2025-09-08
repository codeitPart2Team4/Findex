package com.codeit.findex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;

@Configuration
public class WebConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new Converter<String, LocalDate>() {
      @Override public LocalDate convert(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        return LocalDate.parse(s);
      }
    });
  }
}