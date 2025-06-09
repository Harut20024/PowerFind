package com.powerfind.configuration;

import com.powerfind.backoffice.model.BadRequestResponse;
import com.powerfind.exception.ResponseCodeException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionAdvice
{

    /**
     * Handles validation errors from @Valid annotations. Returns 400 Bad Request with detailed
     * error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequestResponse> handleBadRequest(MethodArgumentNotValidException ex)
    {
        String errorDetails = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation error: {}", errorDetails, ex);
        return ResponseEntity.badRequest()
                .body(new BadRequestResponse("Validation failed: " + errorDetails));
    }

    /**
     * Handles cases where no data is found for a given request. Returns 404 Not Found with a
     * meaningful error message.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BadRequestResponse> handleResponseStatusException(
            ResponseStatusException ex)
    {
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BadRequestResponse("Requested data not found: " + ex.getReason()));
        }
        return ResponseEntity.status(ex.getStatusCode())
                .body(new BadRequestResponse("Error: " + ex.getReason()));
    }

    /**
     * Handles constraint violations (e.g., @Size, @NotNull violations). Returns 400 Bad Request
     * with a generic error message.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BadRequestResponse> handleConstraintViolation(
            ConstraintViolationException ex)
    {
        log.warn("Constraint violation: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(new BadRequestResponse(
                        "Invalid request parameter(s). Please check the request data."));
    }

    /**
     * Handles application-specific exceptions. Returns the custom HTTP status code and a generic
     * error message.
     */
    @ExceptionHandler(ResponseCodeException.class)
    public ResponseEntity<BadRequestResponse> handleResponseCodeException(ResponseCodeException ex)
    {
        log.error("ResponseCodeException: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(ex.getHttpStatusCode())
                .body(new BadRequestResponse("An error occurred while processing your request."));
    }

    /**
     * Handles missing static resources. Returns 404 Not Found with a descriptive message.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BadRequestResponse> handleNoResourceFound(NoResourceFoundException ex)
    {
        log.warn("Static resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new BadRequestResponse("Static resource not found: " + ex.getResourcePath()));
    }

    /**
     * Handles invalid type conversion, such as malformed UUIDs. Returns 400 Bad Request with a
     * helpful message.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BadRequestResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex)
    {
        log.warn("Type mismatch: {}", ex.getMessage());
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType()
                .getSimpleName() : "unknown";
        return ResponseEntity.badRequest().body(new BadRequestResponse(
                "Invalid value for parameter '" + ex.getName() + "'. Expected type: " + expectedType + "."
        ));
    }


    /**
     * Handles missing or unreadable request bodies. Returns 400 Bad Request with a clear message.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BadRequestResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex)
    {
        log.warn("Request body is missing or malformed: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new BadRequestResponse("Request body is missing or malformed."));
    }

    /**
     * Handles all uncaught exceptions globally. Returns 500 Internal Server Error with a generic
     * error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BadRequestResponse> handleGlobalException(Exception ex)
    {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BadRequestResponse("An unexpected error occurred."));
    }
}
