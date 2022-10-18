package ru.shcherbatykh.vacationCalculator.service.calculation;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.shcherbatykh.vacationCalculator.model.Request;
import ru.shcherbatykh.vacationCalculator.service.holidaysProvider.HolidaysProvider;
import ru.shcherbatykh.vacationCalculator.service.holidaysProvider.StaticHolidaysProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class VacationPayServiceTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @TestConfiguration
    static class ValidationPayTestConfiguration {
        @Bean
        public HolidaysProvider holidaysProvider() {
            return new StaticHolidaysProvider();
        }

        @Bean
        public VacationPayService vacationPayService() {
            return new VacationPayService(holidaysProvider());
        }
    }

    @Autowired
    private VacationPayService vacationPayService;

    @Test
    public void whenOneDayVacationWithoutPeriod() {
        Request request = Request.builder()
                .averageSalary(20000)
                .countOfVacationDays(1)
                .build();
        double vacationPay = vacationPayService.calculate(request);
        Assertions.assertThat(vacationPay).isCloseTo(682.59, Offset.offset(0.005));
    }

    @Test
    public void whenOneDayVacationWithPeriodWithoutHolidays() {
        LocalDate date = LocalDate.parse("11.10.2022", DATE_TIME_FORMATTER);
        Request request = Request.builder()
                .averageSalary(20000)
                .vacationStartDate(date)
                .vacationEndDate(date)
                .build();
        double vacationPay = vacationPayService.calculate(request);
        Assertions.assertThat(vacationPay).isCloseTo(682.59, Offset.offset(0.005));
    }

    @Test
    public void whenOneDayVacationWithPeriodWithHolidays() {
        LocalDate date = LocalDate.parse("01.01.2022", DATE_TIME_FORMATTER);
        Request request = Request.builder()
                .averageSalary(20000)
                .vacationStartDate(date)
                .vacationEndDate(date)
                .build();
        double vacationPay = vacationPayService.calculate(request);
        Assertions.assertThat(vacationPay).isCloseTo(0.0, Offset.offset(0.005));
    }

    @Test
    public void whenTwoWeeksVacationWithoutPeriod() {
        Request request = Request.builder()
                .averageSalary(20000)
                .countOfVacationDays(14)
                .build();
        double vacationPay = vacationPayService.calculate(request);
        Assertions.assertThat(vacationPay).isCloseTo(9556.31, Offset.offset(0.005));
    }

    @Test
    public void whenTwoWeeksVacationWithPeriodWithoutHolidays() {
        LocalDate startDate = LocalDate.parse("03.10.2022", DATE_TIME_FORMATTER);
        LocalDate endDate = startDate.plusDays(13);
        Request request = Request.builder()
                .averageSalary(20000)
                .vacationStartDate(startDate)
                .vacationEndDate(endDate)
                .build();
        double vacationPay = vacationPayService.calculate(request);
        Assertions.assertThat(vacationPay).isCloseTo(9556.31, Offset.offset(0.005));
    }

    @Test
    public void whenSixteenVacationWithPeriodWithTwoHolidays() {
        // two holidays in the period 30.04.2022-15.05.2022 - 01.05, 09.05
        LocalDate startDate = LocalDate.parse("30.04.2022", DATE_TIME_FORMATTER);
        LocalDate endDate = startDate.plusDays(15);
        Request request = Request.builder()
                .averageSalary(20000)
                .vacationStartDate(startDate)
                .vacationEndDate(endDate)
                .build();
        double vacationPay = vacationPayService.calculate(request);
        Assertions.assertThat(vacationPay).isCloseTo(9556.31, Offset.offset(0.005));
    }

    @Test
    public void whenTwoWeeksVacationWithPeriodWithEightHolidays() {
        // 8 holidays in the period 26.12.2022-08.01.2023 - 01.01-08.01
        LocalDate startDate = LocalDate.parse("26.12.2022", DATE_TIME_FORMATTER);
        LocalDate endDate = startDate.plusDays(13);
        Request request = Request.builder()
                .averageSalary(20000)
                .vacationStartDate(startDate)
                .vacationEndDate(endDate)
                .build();
        double vacationPay = vacationPayService.calculate(request);
        Assertions.assertThat(vacationPay).isCloseTo(4095.56, Offset.offset(0.005));
    }
}
