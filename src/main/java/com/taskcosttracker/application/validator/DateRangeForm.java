package com.taskcosttracker.application.validator;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DateRangeForm {

    private LocalDate startDate;
    private LocalDate endDate;

}