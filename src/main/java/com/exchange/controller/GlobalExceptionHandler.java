package com.exchange.controller;

import com.exchange.dto.response.ErrorResponse;
import com.exchange.exception.BadRequestException;
import com.exchange.exception.CurrencyConversionException;
import com.exchange.exception.ExternalApiException;
import com.exchange.exception.NotFoundException;
import com.exchange.exception.UnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(CurrencyConversionException.class)
  public ResponseEntity<ErrorResponse> handleCurrencyConversion(CurrencyConversionException ex) {
    log.warn("Currency conversion error - from: {}, to: {}, message: {}",
        ex.getFromCurrency(), ex.getToCurrency(), ex.getMessage());
    return badRequest(ex.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
    log.warn("Bad request error - provider: {}, message: {}",
        ex.getApiName(), ex.getMessage());
    return badRequest(ex.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
    log.warn("Resource not found - provider: {}, message: {}",
        ex.getApiName(), ex.getMessage());
    return notFound(ex.getMessage());
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
    log.error("Authorization error - provider: {}, message: {}",
        ex.getApiName(), ex.getMessage());
    return unauthorized(ex.getMessage());
  }

  @ExceptionHandler(ExternalApiException.class)
  public ResponseEntity<ErrorResponse> handleExternalApi(ExternalApiException ex) {
    var status = Optional.ofNullable(HttpStatus.resolve(ex.getStatusCode()))
        .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

    if (status.is4xxClientError()) {
      log.warn("External API client error - provider: {}, statusCode: {}, message: {}",
          ex.getApiName(), ex.getStatusCode(), ex.getMessage());
    } else {
      log.error("External API server error - provider: {}, statusCode: {}, message: {}",
          ex.getApiName(), ex.getStatusCode(), ex.getMessage(), ex);
    }

    return ResponseEntity.status(status).body(errorResponse(ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    var message = ex.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));

    log.warn("Validation error - errors: {}", message);
    return badRequest(message);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
    var message = ex.getConstraintViolations().stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.joining(", "));

    log.warn("Constraint violation - violations: {}", message);
    return badRequest(message);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    log.warn("Type mismatch error - parameter: {}", ex.getName());
    return badRequest("Invalid parameter: " + ex.getName());
  }

  @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFound(
      org.springframework.web.servlet.resource.NoResourceFoundException ex) {

    log.debug("Static resource not found: {}", ex.getResourcePath());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse("Resource not found"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    log.error("Unexpected server error - type: {}, message: {}",
        ex.getClass().getSimpleName(), ex.getMessage(), ex);
    return internalError("An unexpected error occurred. Please try again later.");
  }

  private static ResponseEntity<ErrorResponse> badRequest(String message) {
    return ResponseEntity.badRequest().body(errorResponse(message));
  }

  private static ResponseEntity<ErrorResponse> notFound(String message) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(message));
  }

  private static ResponseEntity<ErrorResponse> unauthorized(String message) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse(message));
  }

  private static ResponseEntity<ErrorResponse> internalError(String message) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse(message));
  }

  private static ErrorResponse errorResponse(String message) {
    return ErrorResponse.builder().message(message).build();
  }
}
