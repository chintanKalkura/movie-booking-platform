package com.ck.movie.booking.platform.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PriceDetails(
        BigDecimal cost,
        List<String> offers
) {}
