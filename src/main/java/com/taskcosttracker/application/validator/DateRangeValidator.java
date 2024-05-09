package com.taskcosttracker.application.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, DateRangeForm> {

    @Override
    public void initialize(DateRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(DateRangeForm form, ConstraintValidatorContext context) {
        LocalDate startDate = form.getStartDate();
        LocalDate endDate = form.getEndDate();

        if (startDate == null || endDate == null) {
            return false;
        }

        return !endDate.isBefore(startDate);
    }
}