package com.devsenior.soledad.reservas_backend.repository;

import com.devsenior.soledad.reservas_backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


/**
 * Repository interface for Booking entity.
 */
public interface BookingRepository extends JpaRepository<Booking,Long> {

    /**
     * Returns all bookings for a company including company relation to prevent lazy-loading N+1 issues.
     *
     * @param companyId company identifier
     * @return tenant bookings with company preloaded
     */
    @Query("select b from Booking b join fetch b.company where b.company.id = :companyId")
    List<Booking> findAllByCompanyIdWithCompany(@Param("companyId") Long companyId);

    /**
     * Returns one booking by id and company including company relation to prevent lazy-loading N+1 issues.
     *
     * @param id booking identifier
     * @param companyId company identifier
     * @return booking if found in tenant scope
     */
    @Query("select b from Booking b join fetch b.company where b.id = :id and b.company.id = :companyId")
    Optional<Booking> findByIdAndCompanyIdWithCompany(@Param("id") Long id, @Param("companyId") Long companyId);

    boolean existsByCompanyIdAndDateAndTime(Long companyId, LocalDate date, LocalTime time);

    Optional<Booking> findByIdAndCompany_Id(Long id, Long companyId);
}
