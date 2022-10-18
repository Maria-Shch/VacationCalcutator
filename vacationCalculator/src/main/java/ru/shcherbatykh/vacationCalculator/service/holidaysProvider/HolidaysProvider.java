package ru.shcherbatykh.vacationCalculator.service.holidaysProvider;

import java.time.LocalDate;
import java.util.List;

public interface HolidaysProvider {
    List<LocalDate> getHolidays(List<Integer> years);
}
