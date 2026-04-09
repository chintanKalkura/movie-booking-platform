package com.ck.movie.booking.platform.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowCreateRequest(
        @NotBlank(message = "movieName is required")
        String movieName,

        @NotBlank(message = "movieId is required")
        String movieId,

        @NotBlank(message = "theatreId is required")
        String theatreId,

        @Positive(message = "screenNumber must be a positive number")
        int screenNumber,

        @NotBlank(message = "priceId is required")
        String priceId,

        @NotNull(message = "showTime is required")
        LocalTime showTime,

        @NotNull(message = "showDate is required")
        @FutureOrPresent(message = "showDate must be today or in the future")
        LocalDate showDate
) {}
