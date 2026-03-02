package com.devsenior.soledad.reservas_backend.exception;

/**
 * Exception thrown when a booking is not found.
 */
public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException() {
        super();
    }

    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

