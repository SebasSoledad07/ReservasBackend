package com.devsenior.soledad.reservas_backend.dto;

import com.devsenior.soledad.reservas_backend.entity.BookingStatus;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Booking response DTO used for outgoing responses.
 *
 * @param id booking identifier
 * @param clientName client name
 * @param date booking date
 * @param time booking time
 * @param serviceName service name
 * @param status booking status
 */
public record BookingResponse(Long id, String clientName, LocalDate date, LocalTime time, String serviceName, BookingStatus status) {
}

