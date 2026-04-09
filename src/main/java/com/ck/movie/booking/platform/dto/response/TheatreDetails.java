package com.ck.movie.booking.platform.dto.response;

import lombok.Builder;

@Builder
public record TheatreDetails(
        String name,
        String address
) {}
