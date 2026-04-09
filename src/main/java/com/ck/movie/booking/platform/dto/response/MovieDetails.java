package com.ck.movie.booking.platform.dto.response;

import com.ck.movie.booking.platform.constants.enums.MovieRating;
import lombok.Builder;

@Builder
public record MovieDetails(
        String name,
        String category,
        String language,
        MovieRating rating
) {}
