package ru.shcherbatykh.vacationCalculator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private final ResponseStatus responseStatus;
    private String message;
    @JsonSerialize(using = CustomDoubleSerializer.class)
    private Double vacationPay;

    public Response(ResponseStatus responseStatus, Double vacationPay) {
        this.responseStatus = responseStatus;
        this.vacationPay = vacationPay;
    }

    public Response(ResponseStatus responseStatus, String message) {
        this.responseStatus = responseStatus;
        this.message = message;
    }
}
