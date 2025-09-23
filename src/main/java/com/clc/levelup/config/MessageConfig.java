package com.clc.levelup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

// Makes validation use messages.properties for error text.
@Configuration
public class MessageConfig {

  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource m = new ResourceBundleMessageSource();
    m.setBasename("messages");   // looks for src/main/resources/messages.properties
    m.setDefaultEncoding("UTF-8");
    return m;
  }

  @Bean
  public LocalValidatorFactoryBean getValidator(ResourceBundleMessageSource ms) {
    LocalValidatorFactoryBean v = new LocalValidatorFactoryBean();
    v.setValidationMessageSource(ms);
    return v;
  }
}
