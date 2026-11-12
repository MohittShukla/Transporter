package com.freightfox.transporter.dto.response;

public class InputResponse {

    private String status;
    private String message;

    public InputResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

