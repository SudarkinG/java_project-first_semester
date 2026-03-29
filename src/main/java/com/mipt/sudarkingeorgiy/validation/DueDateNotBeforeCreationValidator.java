package com.mipt.sudarkingeorgiy.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DueDateNotBeforeCreationValidator
        implements ConstraintValidator<DueDateNotBeforeCreation, LocalDate> {

    @Override
    public boolean isValid(LocalDate dueDate, ConstraintValidatorContext context) {
        if (dueDate == null) {
            return true;
        }
        return !dueDate.isBefore(LocalDate.now());
    }
}
