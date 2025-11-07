package com.clc.levelup.validation;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for {@link PasswordMatches} annotation.
 * <p>
 * Reads two fields from a target object (typically a DTO)
 * and verifies that their values match. If they differ,
 * a validation message is attached to the confirm field.
 * </p>
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

  /** Field name for the password. */
  private String passField;

  /** Field name for the confirm password. */
  private String confField;

  /**
   * Initialize the validator with the field names
   * defined in the {@link PasswordMatches} annotation.
   * @param annotation the annotation instance
   */
  @Override
  public void initialize(PasswordMatches annotation) {
    passField = annotation.password();
    confField = annotation.confirmPassword();
  }

  /**
   * Validate that the two password fields match.
   * If either field is null, validation passes and the
   * {@code @NotBlank} constraint handles emptiness separately.
   * @param bean the object being validated
   * @param ctx validation context for building constraint messages
   * @return true if both values match or are null, false otherwise
   */
  @Override
  public boolean isValid(Object bean, ConstraintValidatorContext ctx) {
    try {
      String p = (String) read(bean, passField);
      String c = (String) read(bean, confField);

      // Allow @NotBlank to handle null or empty fields
      if (p == null || c == null) return true;

      boolean ok = p.equals(c);

      // If not equal, attach custom message to confirmPassword field
      if (!ok) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate("{validation.passwords.mismatch}")
           .addPropertyNode(confField)
           .addConstraintViolation();
      }

      return ok;
    } catch (Exception e) {
      // In case of reflection or access errors, fail validation gracefully
      return false;
    }
  }

  /**
   * Utility method that reads a property from the given object by name.
   * Uses Java Bean introspection to find the getter method.
   * @param bean target object
   * @param name property name
   * @return property value, or null if not found
   * @throws Exception if introspection or reflection fails
   */
  private Object read(Object bean, String name) throws Exception {
    for (PropertyDescriptor pd :
         Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
      if (pd.getName().equals(name)) {
        return pd.getReadMethod().invoke(bean);
      }
    }
    return null;
  }
}
