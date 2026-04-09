package com.ck.movie.booking.platform.dto.response;

import com.ck.movie.booking.platform.constants.enums.ScreenType;
import com.ck.movie.booking.platform.constants.enums.ShowStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ShowDetails(
        MovieDetails movie,
        LocalTime showTime,
        LocalDate showDate,
        ScreenType screenType,
        TheatreDetails theatre,
        PriceDetails price,
        ShowStatus showStatus
) {}
