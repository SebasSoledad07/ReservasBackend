package com.devsenior.soledad.reservas_backend.controller;

import com.devsenior.soledad.reservas_backend.dto.BookingRequest;
import com.devsenior.soledad.reservas_backend.dto.BookingResponse;
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

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private Long extractCompanyId(HttpServletRequest request) {
        // First try attribute (set by JWT filter previously)
        Object attr = request.getAttribute("companyId");
        if (attr instanceof Long) return (Long) attr;
        if (attr instanceof Integer) return ((Integer) attr).longValue();

        // Fallback: accept X-Company-Id header (frontend can send this when JWT is not used)
        String header = request.getHeader("X-Company-Id");
        if (header != null && !header.isBlank()) {
            try {
                return Long.valueOf(header.trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid X-Company-Id header value");
            }
        }

        throw new IllegalArgumentException("Company id missing in token or header 'X-Company-Id'");
    }

    /**
     * Lists all bookings for the authenticated user's company.
     *
     * @return list of booking responses
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> listAll(HttpServletRequest request) {
        Long companyId = extractCompanyId(request);
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
        Long companyId = extractCompanyId(request);
        BookingResponse response = bookingService.createBooking(requestDto, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a booking by id within the authenticated user's company.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(HttpServletRequest request, @PathVariable Long id) {
        Long companyId = extractCompanyId(request);
        BookingResponse response = bookingService.findBookingById(id, companyId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels a booking by id within the authenticated user's company.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(HttpServletRequest request, @PathVariable Long id) {
        Long companyId = extractCompanyId(request);
        bookingService.cancelBookingById(id, companyId);
        return ResponseEntity.noContent().build();
    }
}
