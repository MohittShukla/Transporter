package com.freightfox.transporter.exception;

/**
 * Thrown when no subset of transporters (within the limit) can cover all lanes.
 */
public class NoFeasibleSolutionException extends RuntimeException {

    public NoFeasibleSolutionException(String message) {
        super(message);
    }
}

