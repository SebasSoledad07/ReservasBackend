package com.devsenior.soledad.reservas_backend.repository;

import com.devsenior.soledad.reservas_backend.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    @Query("select u from AppUser u join fetch u.company where u.username = :username")
    Optional<AppUser> findByUsernameWithCompany(@Param("username") String username);

    boolean existsByUsername(String username);
}

