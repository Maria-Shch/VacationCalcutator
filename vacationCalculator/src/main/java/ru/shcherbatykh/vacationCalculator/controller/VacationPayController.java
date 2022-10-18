package ru.shcherbatykh.vacationCalculator.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.vacationCalculator.model.Request;
import ru.shcherbatykh.vacationCalculator.model.Response;
import ru.shcherbatykh.vacationCalculator.model.ResponseStatus;
import ru.shcherbatykh.vacationCalculator.service.calculation.VacationPayService;
import ru.shcherbatykh.vacationCalculator.service.validation.ValidationService;

import java.time.LocalDate;

@Slf4j
@RestController
public class VacationPayController {

    private final VacationPayService vacationPayService;
    private final ValidationService validationService;

    public VacationPayController(VacationPayService vacationPayService, ValidationService validationService) {
        this.vacationPayService = vacationPayService;
        this.validationService = validationService;
    }

    @GetMapping("/calculate")
    public Response getVacationPay(@RequestParam double averageSalary,
                                   @RequestParam int countOfVacationDays,
                                   @RequestParam(required = false) @Nullable @DateTimeFormat(pattern = "dd-MM-yyyy")
                                           LocalDate vacationStartDate,
                                   @RequestParam(required = false) @Nullable @DateTimeFormat(pattern = "dd-MM-yyyy")
                                           LocalDate vacationEndDate) {

        Request request = Request.builder()
                .averageSalary(averageSalary)
                .countOfVacationDays(countOfVacationDays)
                .vacationStartDate(vacationStartDate)
                .vacationEndDate(vacationEndDate)
                .build();
        log.trace("request: {}", request);

        try {
            validationService.validateRequest(request);
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            return new Response(ResponseStatus.FAILED, ex.getMessage());
        }

        double vacationPay = vacationPayService.calculate(request);
        log.trace("vacationPay: {}", vacationPay);

        return new Response(ResponseStatus.OK, vacationPay);
    }
}
