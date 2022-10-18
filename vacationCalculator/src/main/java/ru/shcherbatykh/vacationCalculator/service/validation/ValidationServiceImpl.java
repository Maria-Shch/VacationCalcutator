package ru.shcherbatykh.vacationCalculator.service.validation;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.vacationCalculator.model.Request;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Override
    public void validateRequest(Request request) {
        if (request.getAverageSalary() < 0) {
            throw new IllegalArgumentException("Average salary cannot be less than 0");
        }
        if (request.getCountOfVacationDays() <= 0) {
            throw new IllegalArgumentException("Count of vacation days cannot be less or equal 0");
        }
        if (request.getVacationStartDate() != null && request.getVacationEndDate() == null
                || request.getVacationStartDate() == null && request.getVacationEndDate() != null) {
            throw new IllegalArgumentException("Both start date and end date must be not empty if one of them is not empty");
        }
        if (request.getVacationStartDate() != null && request.getVacationStartDate().isAfter(request.getVacationEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }
}
