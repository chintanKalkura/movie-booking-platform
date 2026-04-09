package com.ck.movie.booking.platform.dto.response;

import com.ck.movie.booking.platform.entity.Screen;
import lombok.Builder;

import java.util.List;

@Builder
public record TheatreDetails(
        String name,
        String address,
        List<Screen> screens
) {}
