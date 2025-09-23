package com.clc.levelup.validation;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// Reads two properties by name and compares them.
// If they don't match, attach the message to the confirm field.
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
  private String passField;
  private String confField;

  @Override
  public void initialize(PasswordMatches a) {
    passField = a.password();
    confField = a.confirmPassword();
  }

  @Override
  public boolean isValid(Object bean, ConstraintValidatorContext ctx) {
    try {
      String p = (String) read(bean, passField);
      String c = (String) read(bean, confField);
      if (p == null || c == null) return true; // let @NotBlank handle empty cases

      boolean ok = p.equals(c);
      if (!ok) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate("{validation.passwords.mismatch}")
           .addPropertyNode(confField).addConstraintViolation();
      }
      return ok;
    } catch (Exception e) {
      return false;
    }
  }

  private Object read(Object bean, String name) throws Exception {
    for (PropertyDescriptor pd : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
      if (pd.getName().equals(name)) return pd.getReadMethod().invoke(bean);
    }
    return null;
  }
}
