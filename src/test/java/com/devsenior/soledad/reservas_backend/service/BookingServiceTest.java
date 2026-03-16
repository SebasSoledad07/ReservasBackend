package com.devsenior.soledad.reservas_backend.service;

import com.devsenior.soledad.reservas_backend.dto.BookingPublicRequestDTO;
import com.devsenior.soledad.reservas_backend.dto.BookingRequest;
import com.devsenior.soledad.reservas_backend.dto.BookingResponse;
import com.devsenior.soledad.reservas_backend.dto.BookingResponseDTO;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBookingShouldSaveActiveBookingWhenSlotIsAvailable() {
        Long companyId = 10L;
        BookingRequest request = new BookingRequest("John", LocalDate.now().plusDays(2), LocalTime.of(10, 0), "Haircut");

        Booking mapped = Booking.builder()
                .clientName(request.clientName())
                .date(request.date())
                .time(request.time())
                .service(request.serviceName())
                .status(BookingStatus.CANCELLED)
                .build();

        Booking saved = Booking.builder()
                .id(1L)
                .clientName(request.clientName())
                .date(request.date())
                .time(request.time())
                .service(request.serviceName())
                .status(BookingStatus.ACTIVE)
                .company(Company.builder().id(companyId).name("Acme").build())
                .build();

        BookingResponse expected = new BookingResponse(1L, "John", request.date(), request.time(), "Haircut", BookingStatus.ACTIVE, "Acme");

        when(bookingRepository.existsByCompanyIdAndDateAndTime(companyId, request.date(), request.time())).thenReturn(false);
        when(bookingMapper.toEntity(request)).thenReturn(mapped);
        when(bookingRepository.save(any(Booking.class))).thenReturn(saved);
        when(bookingMapper.toDto(saved)).thenReturn(expected);

        BookingResponse result = bookingService.createBooking(request, companyId);

        assertEquals(expected, result);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking persisted = captor.getValue();
        assertEquals(BookingStatus.ACTIVE, persisted.getStatus());
        assertNotNull(persisted.getCompany());
        assertEquals(companyId, persisted.getCompany().getId());
    }

    @Test
    void createBookingShouldThrowWhenSlotAlreadyExists() {
        Long companyId = 10L;
        BookingRequest request = new BookingRequest("John", LocalDate.now().plusDays(2), LocalTime.of(10, 0), "Haircut");

        when(bookingRepository.existsByCompanyIdAndDateAndTime(companyId, request.date(), request.time())).thenReturn(true);

        assertThrows(BookingAlreadyExistsException.class, () -> bookingService.createBooking(request, companyId));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void listAllBookingsShouldMapTenantBookingsOnly() {
        Long companyId = 2L;

        Booking first = Booking.builder()
                .id(1L)
                .clientName("John")
                .date(LocalDate.now().plusDays(1))
                .time(LocalTime.of(9, 0))
                .service("Cut")
                .status(BookingStatus.ACTIVE)
                .company(Company.builder().id(companyId).name("Tenant A").build())
                .build();

        Booking second = Booking.builder()
                .id(2L)
                .clientName("Jane")
                .date(LocalDate.now().plusDays(1))
                .time(LocalTime.of(10, 0))
                .service("Color")
                .status(BookingStatus.ACTIVE)
                .company(Company.builder().id(companyId).name("Tenant A").build())
                .build();

        BookingResponse dto1 = new BookingResponse(1L, "John", first.getDate(), first.getTime(), "Cut", BookingStatus.ACTIVE, "Tenant A");
        BookingResponse dto2 = new BookingResponse(2L, "Jane", second.getDate(), second.getTime(), "Color", BookingStatus.ACTIVE, "Tenant A");

        when(bookingRepository.findAllByCompanyIdWithCompany(companyId)).thenReturn(List.of(first, second));
        when(bookingMapper.toDto(first)).thenReturn(dto1);
        when(bookingMapper.toDto(second)).thenReturn(dto2);

        List<BookingResponse> result = bookingService.listAllBookings(companyId);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(bookingRepository).findAllByCompanyIdWithCompany(companyId);
    }

    @Test
    void findBookingByIdShouldThrowWhenBookingDoesNotExistInTenant() {
        Long companyId = 3L;
        Long bookingId = 99L;

        when(bookingRepository.findByIdAndCompanyIdWithCompany(bookingId, companyId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.findBookingById(bookingId, companyId));
    }

    @Test
    void cancelBookingByIdShouldSetCancelledAndReturnResponse() {
        Long companyId = 4L;
        Long bookingId = 5L;

        Booking booking = Booking.builder()
                .id(bookingId)
                .clientName("John")
                .date(LocalDate.now().plusDays(3))
                .time(LocalTime.of(11, 0))
                .service("Cut")
                .status(BookingStatus.ACTIVE)
                .company(Company.builder().id(companyId).name("Tenant B").build())
                .build();

        BookingResponse response = new BookingResponse(bookingId, "John", booking.getDate(), booking.getTime(), "Cut", BookingStatus.CANCELLED, "Tenant B");

        when(bookingRepository.findByIdAndCompanyIdWithCompany(bookingId, companyId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(response);

        BookingResponse result = bookingService.cancelBookingById(bookingId, companyId);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(response, result);
        verify(bookingRepository).save(booking);
    }

    @Test
    void createPublicBookingShouldThrowWhenCompanySlugDoesNotExist() {
        String slug = "missing-company";
        BookingPublicRequestDTO request = new BookingPublicRequestDTO(
                "Visitor",
                LocalDate.now().plusDays(1),
                LocalTime.of(15, 0),
                "Consulting"
        );

        when(companyRepository.findBySlug(slug)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createPublicBooking(slug, request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createPublicBookingShouldThrowWhenDateIsInPast() {
        String slug = "tenant-a";
        Company company = Company.builder().id(6L).name("Tenant C").slug(slug).build();

        BookingPublicRequestDTO request = new BookingPublicRequestDTO(
                "Visitor",
                LocalDate.now().minusDays(1),
                LocalTime.of(15, 0),
                "Consulting"
        );

        when(companyRepository.findBySlug(slug)).thenReturn(Optional.of(company));

        assertThrows(BadRequestException.class, () -> bookingService.createPublicBooking(slug, request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createPublicBookingShouldThrowWhenSlotIsTakenForCompany() {
        String slug = "tenant-a";
        Company company = Company.builder().id(7L).name("Tenant D").slug(slug).build();

        BookingPublicRequestDTO request = new BookingPublicRequestDTO(
                "Visitor",
                LocalDate.now().plusDays(1),
                LocalTime.of(16, 0),
                "Consulting"
        );

        when(companyRepository.findBySlug(slug)).thenReturn(Optional.of(company));
        when(bookingRepository.existsByCompanyIdAndDateAndTime(company.getId(), request.date(), request.time())).thenReturn(true);

        assertThrows(BookingAlreadyExistsException.class, () -> bookingService.createPublicBooking(slug, request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createPublicBookingShouldCreateActiveBookingForSlug() {
        String slug = "tenant-a";
        Company company = Company.builder().id(8L).name("Tenant E").slug(slug).build();

        BookingPublicRequestDTO request = new BookingPublicRequestDTO(
                "Visitor",
                LocalDate.now().plusDays(1),
                LocalTime.of(17, 0),
                "Consulting"
        );

        Booking saved = Booking.builder()
                .id(100L)
                .clientName(request.clientName())
                .date(request.date())
                .time(request.time())
                .service(request.serviceName())
                .status(BookingStatus.ACTIVE)
                .company(company)
                .build();

        when(companyRepository.findBySlug(slug)).thenReturn(Optional.of(company));
        when(bookingRepository.existsByCompanyIdAndDateAndTime(company.getId(), request.date(), request.time())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(saved);

        BookingResponseDTO result = bookingService.createPublicBooking(slug, request);

        assertEquals(100L, result.id());
        assertEquals(BookingStatus.ACTIVE, result.status());
        assertEquals("Tenant E", result.companyName());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(BookingStatus.ACTIVE, captor.getValue().getStatus());
        assertEquals(company.getId(), captor.getValue().getCompany().getId());
    }
}

