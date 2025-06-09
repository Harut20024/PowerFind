package com.powerfind.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ResponseCodeException extends RuntimeException
{

    private final HttpStatusCode httpStatusCode;

    private final String statusText;

    public ResponseCodeException(HttpStatusCode httpStatusCode, String statusText)
    {
        this.httpStatusCode = httpStatusCode;
        this.statusText = statusText;
    }
}