package com.freightfox.transporter.exception;

/**
 * Thrown when API 2 is called but no input data has been submitted via API 1.
 */
public class NoDataException extends RuntimeException {

    public NoDataException(String message) {
        super(message);
    }
}

