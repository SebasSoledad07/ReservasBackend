package com.devsenior.soledad.reservas_backend.controller;

import com.devsenior.soledad.reservas_backend.dto.BookingRequest;
import com.devsenior.soledad.reservas_backend.dto.BookingResponse;
import com.devsenior.soledad.reservas_backend.security.TenantContextResolver;
import com.devsenior.soledad.reservas_backend.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for bookings.
 */
@RestController
@RequestMapping("/reservas")
public class BookingController {

    private final BookingService bookingService;
    private final TenantContextResolver tenantContextResolver;

    public BookingController(BookingService bookingService, TenantContextResolver tenantContextResolver) {
        this.bookingService = bookingService;
        this.tenantContextResolver = tenantContextResolver;
    }

    /**
     * Lists all bookings for the authenticated user's company.
     *
     * @return list of booking responses
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> listAll(HttpServletRequest request) {
        Long companyId = tenantContextResolver.resolveCompanyId(request);
        List<BookingResponse> list = bookingService.listAllBookings(companyId);
        return ResponseEntity.ok(list);
    }

    /**
     * Creates a new booking for the authenticated user's company.
     *
     * @param requestDto the booking request payload
     * @return created booking response with HTTP 201
     */
    @PostMapping
    public ResponseEntity<BookingResponse> create(HttpServletRequest request, @Valid @RequestBody BookingRequest requestDto) {
        Long companyId = tenantContextResolver.resolveCompanyId(request);
        BookingResponse response = bookingService.createBooking(requestDto, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a booking by id within the authenticated user's company.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(HttpServletRequest request, @PathVariable Long id) {
        Long companyId = tenantContextResolver.resolveCompanyId(request);
        BookingResponse response = bookingService.findBookingById(id, companyId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels a booking by id within the authenticated user's company.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(HttpServletRequest request, @PathVariable Long id) {
        Long companyId = tenantContextResolver.resolveCompanyId(request);
        bookingService.cancelBookingById(id, companyId);
        return ResponseEntity.noContent().build();
    }
}
