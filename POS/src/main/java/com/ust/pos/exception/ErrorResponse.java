package com.ust.pos.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

    private final int status;

    private final String message;

    private final String path;

    private final LocalDateTime timestamp;

    public ErrorResponse(int status, String message, String path) {

        this.status = status;

        this.message = message;

        this.path = path;

        this.timestamp = LocalDateTime.now();

    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
