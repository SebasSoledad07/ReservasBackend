package com.devsenior.soledad.reservas_backend.service;

import com.devsenior.soledad.reservas_backend.dto.BookingRequest;
import com.devsenior.soledad.reservas_backend.dto.BookingResponse;
import com.devsenior.soledad.reservas_backend.entity.Booking;
import com.devsenior.soledad.reservas_backend.entity.BookingStatus;
import com.devsenior.soledad.reservas_backend.exception.BookingAlreadyExistsException;
import com.devsenior.soledad.reservas_backend.exception.BookingNotFoundException;
import com.devsenior.soledad.reservas_backend.mapper.BookingMapper;
import com.devsenior.soledad.reservas_backend.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service that manages booking operations.
 *
 * This is a single concrete service class (no separate interface + impl) as requested.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    /**
     * Creates a new booking if the slot (date + time) is not already taken.
     *
     * @param bookingRequest the booking request DTO (must not be null)
     * @return the persisted booking as a response DTO with generated id
     * @throws BookingAlreadyExistsException if a booking already exists for the same date and time
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest) {
        if (bookingRepository.existsByDateAndTime(bookingRequest.date(), bookingRequest.time())) {
            throw new BookingAlreadyExistsException("A booking already exists for the specified date and time." +
                    " Date: " + bookingRequest.date() + ", Time: " + bookingRequest.time());
        }

        Booking booking = bookingMapper.toEntity(bookingRequest);
        // Ensure newly created bookings are ACTIVE regardless of incoming data or mapper behavior
        booking.setStatus(BookingStatus.ACTIVE);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    /**
     * Returns all bookings as response DTOs.
     *
     * @return list of bookings (may be empty)
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> listAllBookings() {
        return bookingRepository.findAll().stream().map(bookingMapper::toDto).toList();
    }

    /**
     * Finds a booking by its identifier and returns a response DTO.
     *
     * @param id the booking identifier (must not be null)
     * @return the found booking as response DTO
     * @throws BookingNotFoundException if the booking does not exist
     */
    @Transactional(readOnly = true)
    public BookingResponse findBookingById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + id + " not found."));
        return bookingMapper.toDto(booking);
    }

    /**
     * Cancels an existing booking by id. The booking status will be set to CANCELLED.
     *
     * @param id the booking identifier to cancel (must not be null)
     * @return the cancelled booking as response DTO
     * @throws BookingNotFoundException if the booking does not exist
     */
    @Transactional
    public BookingResponse cancelBookingById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + id + " not found."));

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }
}
