package com.devsenior.soledad.reservas_backend.repository;

import com.devsenior.soledad.reservas_backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
    Optional<Company> findBySlug(String slug);
}
