package com.ck.movie.booking.platform.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BookingResponse(
        String bookingId,
        List<String> seats,
        BookedShowDetails show,
        BigDecimal totalCost,
        String userEmail,
        String userPhoneNumber
) {}
