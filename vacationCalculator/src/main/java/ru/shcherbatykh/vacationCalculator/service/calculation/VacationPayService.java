package ru.shcherbatykh.vacationCalculator.service.calculation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.vacationCalculator.model.Request;
import ru.shcherbatykh.vacationCalculator.service.holidaysProvider.HolidaysProvider;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VacationPayService {

    private final HolidaysProvider holidaysProvider;

    @Value(value = "${application.average_count_days_in_month}")
    private double averageCountOfDaysInMonth;

    public VacationPayService(HolidaysProvider holidaysProvider) {
        this.holidaysProvider = holidaysProvider;
    }

    public double calculate(Request request) {
        long countOfVacationDaysExcludingHolidays = getCountOfVacationDays(request);
        return request.getAverageSalary() / averageCountOfDaysInMonth * countOfVacationDaysExcludingHolidays;
    }

    private long getCountOfVacationDays(Request request) {
        if (request.getVacationStartDate() == null) {
            return request.getCountOfVacationDays();
        }

        long vacationPeriod = ChronoUnit.DAYS.between(request.getVacationStartDate(), request.getVacationEndDate().plusDays(1));
        long countOfHolidays = getCountOfHolidays(request);
        return vacationPeriod - countOfHolidays;
    }

    private int getCountOfHolidays(final Request request) {
        int startYear = request.getVacationStartDate().getYear();
        int endYear = request.getVacationEndDate().getYear();
        List<Integer> years = new ArrayList<>();

        int tmp = startYear;
        while (tmp <= endYear) {
            years.add(tmp++);
        }

        List<LocalDate> holidays = holidaysProvider.getHolidays(years);
        log.trace("holidays: {}", holidays);

        int count = 0;
        for (LocalDate holiday : holidays) {
            if ((holiday.isEqual(request.getVacationStartDate()) || holiday.isAfter(request.getVacationStartDate())) &&
                    (holiday.isEqual(request.getVacationEndDate()) || holiday.isBefore(request.getVacationEndDate()))) {
                count++;
            }
        }
        return count;
    }
}
