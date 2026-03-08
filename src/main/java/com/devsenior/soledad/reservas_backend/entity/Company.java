package com.devsenior.soledad.reservas_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Company entity representing a tenant.
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, length = 100)
    private String slug;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<AppUser> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Booking> bookings;
}
