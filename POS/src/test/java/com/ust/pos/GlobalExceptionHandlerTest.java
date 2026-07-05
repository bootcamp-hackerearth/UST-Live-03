package com.ust.pos.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/v1/test-resource");
        this.request = mockRequest;
    }

    @Test
    @DisplayName("Handle ResourceNotFoundException - Returns 404 Status and Message")
    void handleNotFound_ReturnsNotFoundErrorResponse() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Rack not found");

        ErrorResponse response = exceptionHandler.handleNotFound(exception, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        Assertions.assertEquals("Rack not found", response.getMessage());
        Assertions.assertEquals("/api/v1/test-resource", response.getPath());
    }

    @Test
    @DisplayName("Handle AccessDeniedException - Returns 403 Status and Fixed Message")
    void handleAccessDenied_ReturnsForbiddenErrorResponse() {
        AccessDeniedException exception = new AccessDeniedException("Unauthorized user access");

        ErrorResponse response = exceptionHandler.handleAccessDenied(exception, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        Assertions.assertEquals("Access Denied", response.getMessage());
        Assertions.assertEquals("/api/v1/test-resource", response.getPath());
    }

    @Test
    @DisplayName("Handle Generic Exception - Returns 500 Status and Sanitized Message")
    void handleGeneric_ReturnsInternalServerErrorResponse() {
        NullPointerException exception = new NullPointerException("Something crashed internally");

        ErrorResponse response = exceptionHandler.handleGeneric(exception, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        Assertions.assertEquals("Something went wrong", response.getMessage());
        Assertions.assertEquals("/api/v1/test-resource", response.getPath());
    }
}