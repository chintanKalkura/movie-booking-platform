package com.ck.movie.booking.platform.cache;

import com.ck.movie.booking.platform.constants.enums.ScreenType;
import com.ck.movie.booking.platform.dto.response.MovieDetails;
import com.ck.movie.booking.platform.dto.response.PriceDetails;
import com.ck.movie.booking.platform.dto.response.TheatreDetails;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Stable (non-volatile) subset of show data stored in Redis.
 * showStatus and seatsAvailable are intentionally excluded — they are fetched
 * live from the database on every request and merged at the service layer.
 */
public record CachedShowDetails(
        String showId,
        MovieDetails movie,
        LocalTime showTime,
        LocalDate showDate,
        ScreenType screenType,
        TheatreDetails theatre,
        PriceDetails price,
        int totalSeats
) {}
