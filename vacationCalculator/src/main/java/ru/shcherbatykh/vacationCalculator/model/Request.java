package ru.shcherbatykh.vacationCalculator.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
public class Request {
    private double averageSalary;
    private int countOfVacationDays;
    private LocalDate vacationStartDate;
    private LocalDate vacationEndDate;
}
