package ru.gizka.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ExceptionResponse {
    private String exception;
    private String descr;
    private String stackTrace;
    private Date date;

    public ExceptionResponse(String exception, String descr, String stackTrace) {
        this.exception = exception;
        this.descr = descr;
        this.stackTrace = stackTrace;
        date = new Date();
    }
}
