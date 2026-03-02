package com.devsenior.soledad.reservas_backend.exception;

/**
 * Exception thrown when attempting to create a booking that conflicts with an existing one.
 */
public class BookingAlreadyExistsException extends RuntimeException {

    public BookingAlreadyExistsException() {
        super();
    }

    public BookingAlreadyExistsException(String message) {
        super(message);
    }

    public BookingAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

