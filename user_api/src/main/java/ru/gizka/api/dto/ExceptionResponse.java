package ru.gizka.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ExceptionResponse {
    private String exception;
    private String descr;
    private Date date;

    public ExceptionResponse(String exception, String descr) {
        this.exception = exception;
        this.descr = descr;
        date = new Date();
    }
}
