package com.ck.movie.booking.platform.entity;

import com.ck.movie.booking.platform.constants.enums.ScreenType;
import com.ck.movie.booking.platform.constants.enums.ShowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "shows")
@Getter
@Setter
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String movieName;
    private String movieId;
    private String theatreId;
    private String priceId;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    private LocalTime showTime;
    private LocalDate showDate;

    @Enumerated(EnumType.STRING)
    private ShowStatus showStatus;

    private int totalSeats;
    private int seatsAvailable;
}
