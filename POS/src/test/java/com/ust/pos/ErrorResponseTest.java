package com.ust.pos.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class ErrorResponseTest {

    @Test
    @DisplayName("Constructor should map fields correctly and generate a recent timestamp")
    void constructor_ShouldInitializeFieldsCorrectly() {
        int expectedStatus = 404;
        String expectedMessage = "Resource not found";
        String expectedPath = "/api/v1/rack/123";
        LocalDateTime executionTime = LocalDateTime.now();

        ErrorResponse errorResponse = new ErrorResponse(expectedStatus, expectedMessage, expectedPath);

        Assertions.assertEquals(expectedStatus, errorResponse.getStatus());
        Assertions.assertEquals(expectedMessage, errorResponse.getMessage());
        Assertions.assertEquals(expectedPath, errorResponse.getPath());
        Assertions.assertNotNull(errorResponse.getTimestamp());

        long secondsDifference = ChronoUnit.SECONDS.between(executionTime, errorResponse.getTimestamp());
        Assertions.assertTrue(Math.abs(secondsDifference) < 2,
                "Timestamp should be close to the initialization time");
    }
}