package com.clc.levelup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Configures message resolution for validation and form feedback.
 * Connects Bean Validation annotations to custom messages
 * stored in {@code src/main/resources/messages.properties}.
 */
@Configuration
public class MessageConfig {

  /**
   * Define the message source used by the application.
   * This bean loads messages from the messages.properties file.
   * @return configured ResourceBundleMessageSource
   */
  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource m = new ResourceBundleMessageSource();
    m.setBasename("messages"); // Searches for messages.properties in resources
    m.setDefaultEncoding("UTF-8");
    return m;
  }

  /**
   * Configure validation to use the same message source.
   * Links constraint annotations (e.g., @NotBlank) to custom messages.
   * @param ms the ResourceBundleMessageSource
   * @return LocalValidatorFactoryBean configured with message source
   */
  @Bean
  public LocalValidatorFactoryBean getValidator(ResourceBundleMessageSource ms) {
    LocalValidatorFactoryBean v = new LocalValidatorFactoryBean();
    v.setValidationMessageSource(ms);
    return v;
  }
}
