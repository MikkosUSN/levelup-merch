package com.clc.levelup.validation;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

// Put this on a class to say "these two fields must match".
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
  String message() default "{validation.passwords.mismatch}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
  String password();
  String confirmPassword();
}
