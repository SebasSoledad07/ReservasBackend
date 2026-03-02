package com.devsenior.soledad.reservas_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Booking request DTO used for incoming requests.
 *
 * @param clientName client name
 * @param date booking date
 * @param time booking time
 * @param serviceName service name
 */
public record BookingRequest(
        @NotNull
        @Size(min = 1, max = 100)
        String clientName,
        @NotNull
        LocalDate date,
        @NotNull
        LocalTime time,
        @NotNull
        @Size(min = 1, max = 50)
        String serviceName
) {
}

