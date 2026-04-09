package com.ck.movie.booking.platform.dto.request;

import java.util.List;

public record BookingRequest(
        List<String> seats,
        String userEmail,
        String userPhoneNumber
) {}
