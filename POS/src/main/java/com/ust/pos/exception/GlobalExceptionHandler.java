package com.ust.pos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("No endpoint mapped for request: {}", request.getRequestURI());

        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "The requested endpoint does not exist",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAuthorizationDenied(AuthorizationDeniedException ex, HttpServletRequest request) {
        log.warn("Authorization denied: {} for request {}", ex.getMessage(), request.getRequestURI());

        return new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "You do not have permission to perform this action",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred", ex);

        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong",
                request.getRequestURI()
        );
    }
}