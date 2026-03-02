package com.devsenior.soledad.reservas_backend.mapper;

import com.devsenior.soledad.reservas_backend.dto.BookingRequest;
import com.devsenior.soledad.reservas_backend.dto.BookingResponse;
import com.devsenior.soledad.reservas_backend.entity.Booking;
import org.springframework.stereotype.Component;

/**
 * Simple mapper between Booking entity and DTOs.
 */
@Component
public class BookingMapper {

    /**
     * Maps BookingRequest to Booking entity.
     *
     * @param request source DTO
     * @return mapped entity
     */
    public Booking toEntity(BookingRequest request) {
        if (request == null) return null;
        return Booking.builder()
                .clientName(request.clientName())
                .date(request.date())
                .time(request.time())
                .service(request.serviceName())
                .status(com.devsenior.soledad.reservas_backend.entity.BookingStatus.ACTIVE)
                .build();
    }

    /**
     * Maps Booking entity to BookingResponse.
     *
     * @param booking source entity
     * @return mapped DTO
     */
    public BookingResponse toDto(Booking booking) {
        if (booking == null) return null;
        return new BookingResponse(
                booking.getId(),
                booking.getClientName(),
                booking.getDate(),
                booking.getTime(),
                booking.getService(),
                booking.getStatus()
        );
    }
}
