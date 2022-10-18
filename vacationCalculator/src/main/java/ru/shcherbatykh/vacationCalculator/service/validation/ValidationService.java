package ru.shcherbatykh.vacationCalculator.service.validation;

import ru.shcherbatykh.vacationCalculator.model.Request;

public interface ValidationService {
    void validateRequest(Request request);
}
