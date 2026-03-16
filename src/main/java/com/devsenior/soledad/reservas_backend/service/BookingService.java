package com.devsenior.soledad.reservas_backend.service;

import com.devsenior.soledad.reservas_backend.dto.BookingPublicRequestDTO;
import com.devsenior.soledad.reservas_backend.dto.BookingResponseDTO;
import com.devsenior.soledad.reservas_backend.dto.BookingRequest;
import com.devsenior.soledad.reservas_backend.dto.BookingResponse;
import com.devsenior.soledad.reservas_backend.entity.Booking;
import com.devsenior.soledad.reservas_backend.entity.BookingStatus;
import com.devsenior.soledad.reservas_backend.entity.Company;
import com.devsenior.soledad.reservas_backend.exception.BadRequestException;
import com.devsenior.soledad.reservas_backend.exception.BookingAlreadyExistsException;
import com.devsenior.soledad.reservas_backend.exception.BookingNotFoundException;
import com.devsenior.soledad.reservas_backend.exception.NotFoundException;
import com.devsenior.soledad.reservas_backend.mapper.BookingMapper;
import com.devsenior.soledad.reservas_backend.repository.BookingRepository;
import com.devsenior.soledad.reservas_backend.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final CompanyRepository companyRepository;

    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper, CompanyRepository companyRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.companyRepository = companyRepository;
    }

    /**
     * Creates a new booking if the slot (date + time) is not already taken for the tenant.
     *
     * @param bookingRequest the booking request DTO (must not be null)
     * @param companyId      tenant identifier extracted from JWT
     * @return the persisted booking as a response DTO with generated id
     * @throws BookingAlreadyExistsException if a booking already exists for the same date and time in the company
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, Long companyId) {
        if (bookingRepository.existsByCompanyIdAndDateAndTime(companyId, bookingRequest.date(), bookingRequest.time())) {
            throw new BookingAlreadyExistsException("A booking already exists for the specified date and time." +
                    " Date: " + bookingRequest.date() + ", Time: " + bookingRequest.time());
        }

        Booking booking = bookingMapper.toEntity(bookingRequest);
        // Ensure newly created bookings are ACTIVE regardless of incoming data or mapper behavior
        booking.setStatus(BookingStatus.ACTIVE);
        // associate company by id (avoid fetching full entity)
        Company company = Company.builder().id(companyId).build();
        booking.setCompany(company);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    /**
     * Returns all bookings for a given company as response DTOs.
     *
     * @param companyId tenant identifier
     * @return list of bookings (may be empty)
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> listAllBookings(Long companyId) {
        return bookingRepository.findAllByCompanyIdWithCompany(companyId)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    /**
     * Finds a booking by its identifier within the tenant scope and returns a response DTO.
     *
     * @param id        the booking identifier (must not be null)
     * @param companyId tenant identifier
     * @return the found booking as response DTO
     * @throws BookingNotFoundException if the booking does not exist or is not owned by the tenant
     */
    @Transactional(readOnly = true)
    public BookingResponse findBookingById(Long id, Long companyId) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Booking booking = bookingRepository.findByIdAndCompanyIdWithCompany(id, companyId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + id + " not found."));
        return bookingMapper.toDto(booking);
    }

    /**
     * Cancels an existing booking by id within the tenant scope. The booking status will be set to CANCELLED.
     *
     * @param id        the booking identifier to cancel (must not be null)
     * @param companyId tenant identifier
     * @return the cancelled booking as response DTO
     * @throws BookingNotFoundException if the booking does not exist or does not belong to the tenant
     */
    @Transactional
    public BookingResponse cancelBookingById(Long id, Long companyId) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Booking booking = bookingRepository.findByIdAndCompanyIdWithCompany(id, companyId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + id + " not found."));

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    /**
     * Public creation of a booking for a company identified by slug.
     *
     * @param slug    company slug
     * @param request public booking request
     * @return created booking response DTO
     */
    @Transactional
    public BookingResponseDTO createPublicBooking(String slug, BookingPublicRequestDTO request) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Company with slug '" + slug + "' not found."));

        // Validate date not in the past
        LocalDate today = LocalDate.now();
        if (request.date().isBefore(today)) {
            throw new BadRequestException("Booking date cannot be in the past.");
        }

        // Check for existing booking conflict for that company
        if (bookingRepository.existsByCompanyIdAndDateAndTime(company.getId(), request.date(), request.time())) {
            throw new BookingAlreadyExistsException("A booking already exists for the specified date and time.");
        }

        Booking booking = Booking.builder()
                .clientName(request.clientName())
                .date(request.date())
                .time(request.time())
                .service(request.serviceName())
                .status(BookingStatus.ACTIVE)
                .company(company)
                .build();

        Booking saved = bookingRepository.save(booking);

        return new BookingResponseDTO(saved.getId(), saved.getClientName(), saved.getDate(), saved.getTime(), saved.getService(), saved.getStatus(), saved.getCompany().getName());
    }
}
