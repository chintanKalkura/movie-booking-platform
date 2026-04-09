package com.ck.movie.booking.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String showId;

    @Column(name = "seats")
    private String seats; // comma-separated seat numbers

    private BigDecimal totalCost;
    private String userEmail;
    private String userPhoneNumber;
}
