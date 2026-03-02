package com.devsenior.soledad.reservas_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "booking_date", nullable = false)
    private LocalDate date;

    @Column(name = "booking_time", nullable = false)
    private LocalTime time;

    @Column(name = "service", nullable = false, length = 50)
    private String service;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;


}