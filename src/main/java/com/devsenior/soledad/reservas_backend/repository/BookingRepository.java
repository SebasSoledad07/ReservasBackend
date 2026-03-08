package com.devsenior.soledad.reservas_backend.repository;

import com.devsenior.soledad.reservas_backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


/**
 * Repository interface for Booking entity.
 */
public interface BookingRepository extends JpaRepository<Booking,Long> {

    /**
     * Checks if a booking exists for the given date and time.
     *
     * @param date the booking date (must not be null)
     * @param time the booking time (must not be null)
     * @return true if a booking exists at the given date and time, false otherwise
     */
    boolean existsByDateAndTime(LocalDate date, LocalTime time);

    // Multi-tenant methods
    List<Booking> findByCompany_Id(Long companyId);

    boolean existsByCompany_IdAndDateAndTime(Long companyId, LocalDate date, LocalTime time);

    java.util.Optional<Booking> findByIdAndCompany_Id(Long id, Long companyId);
}
