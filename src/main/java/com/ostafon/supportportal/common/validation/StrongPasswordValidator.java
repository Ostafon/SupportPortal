package com.ostafon.supportportal.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for strong passwords
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return true; // Use @NotBlank for null/empty validation
        }

        if (requireUppercase && !password.matches(".*[A-Z].*")) {
            return false;
        }

        if (requireLowercase && !password.matches(".*[a-z].*")) {
            return false;
        }

        if (requireDigit && !password.matches(".*\\d.*")) {
            return false;
        }

        if (requireSpecialChar && !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }

        return true;
    }
}

