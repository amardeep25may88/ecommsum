package com.ecommsum.config;



import com.ecommsum.exceptions.ApiErrorResponse;
import com.ecommsum.exceptions.BusinessException;
import com.ecommsum.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.ConnectException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =====================================================
       BUSINESS
       ===================================================== */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST, ex.getMessage(),
                "BUSINESS_ERROR", ex.getErrorCode(), request, ex);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        return build(HttpStatus.NOT_FOUND, ex.getMessage(),
                "RESOURCE_NOT_FOUND", null, request, ex);
    }

    /* =====================================================
       VALIDATION
       ===================================================== */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        ApiErrorResponse response = base(HttpStatus.BAD_REQUEST,
                "Validation failed", request)
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            Exception ex, HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST,
                "Invalid request parameters",
                "VALIDATION_ERROR",
                null,
                request,
                ex);
    }

    /* =====================================================
       REQUEST / HTTP
       ===================================================== */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST,
                "Malformed JSON request",
                "INVALID_JSON",
                null,
                request,
                ex);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParams(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST,
                ex.getParameterName() + " parameter is missing",
                "MISSING_PARAMETER",
                null,
                request,
                ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST,
                "Invalid value for parameter: " + ex.getName(),
                "TYPE_MISMATCH",
                null,
                request,
                ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        return build(HttpStatus.METHOD_NOT_ALLOWED,
                ex.getMessage(),
                "METHOD_NOT_ALLOWED",
                null,
                request,
                ex);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ex.getMessage(),
                "UNSUPPORTED_MEDIA_TYPE",
                null,
                request,
                ex);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {

        return build(HttpStatus.NOT_FOUND,
                "Endpoint not found",
                "ENDPOINT_NOT_FOUND",
                null,
                request,
                ex);
    }

    /* =====================================================
       SECURITY
       ===================================================== */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {

        return build(HttpStatus.UNAUTHORIZED,
                "Authentication failed",
                "AUTH_FAILED",
                null,
                request,
                ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        return build(HttpStatus.FORBIDDEN,
                "Access denied",
                "ACCESS_DENIED",
                null,
                request,
                ex);
    }

    /* =====================================================
       DATA / DB
       ===================================================== */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        return build(HttpStatus.CONFLICT,
                "Database constraint violation",
                "DB_CONSTRAINT_ERROR",
                null,
                request,
                ex);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleEmptyResult(
            EmptyResultDataAccessException ex, HttpServletRequest request) {

        return build(HttpStatus.NOT_FOUND,
                "Record not found",
                "DB_RECORD_NOT_FOUND",
                null,
                request,
                ex);
    }

    /* =====================================================
       EXTERNAL SERVICES
       ===================================================== */
    /*@ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiErrorResponse> handleFeign(
            FeignException ex, HttpServletRequest request) {

        return build(HttpStatus.BAD_GATEWAY,
                "Downstream service error",
                "DOWNSTREAM_ERROR",
                null,
                request,
                ex);
    }*/

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiErrorResponse> handleRestClient(
            RestClientException ex, HttpServletRequest request) {

        return build(HttpStatus.BAD_GATEWAY,
                "External service communication failure",
                "EXTERNAL_SERVICE_ERROR",
                null,
                request,
                ex);
    }

    @ExceptionHandler({TimeoutException.class, ConnectException.class})
    public ResponseEntity<ApiErrorResponse> handleTimeouts(
            Exception ex, HttpServletRequest request) {

        return build(HttpStatus.GATEWAY_TIMEOUT,
                "External service timeout",
                "TIMEOUT",
                null,
                request,
                ex);
    }

    /* =====================================================
       FALLBACK
       ===================================================== */
    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<ApiErrorResponse> handleUnhandled(
            Exception ex, HttpServletRequest request) {

        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                "INTERNAL_ERROR",
                null,
                request,
                ex);
    }

    /* =====================================================
       HELPERS
       ===================================================== */
    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String message,
            String error,
            String errorCode,
            HttpServletRequest request,
            Exception ex) {

        String traceId = UUID.randomUUID().toString();
        log.error("[{}] {}", traceId, ex.getMessage(), ex);

        ApiErrorResponse response = base(status, message, request)
                .error(error)
                .errorCode(errorCode)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    private ApiErrorResponse.ApiErrorResponseBuilder base(
            HttpStatus status, String message, HttpServletRequest request) {

        return ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .message(message)
                .path(request.getRequestURI())
                .traceId(UUID.randomUUID().toString());
    }

    private ApiErrorResponse.FieldError mapFieldError(FieldError error) {
        return ApiErrorResponse.FieldError.builder()
                .field(error.getField())
                .message(error.getDefaultMessage())
                .build();
    }
}
