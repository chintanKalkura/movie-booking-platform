package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.response.PriceDetails;
import com.ck.movie.booking.platform.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;

    public PriceDetails findById(String id) {
        return priceRepository.findById(id)
                .map(p -> PriceDetails.builder()
                        .cost(p.getCost())
                        .offers(parseOffers(p.getOffers()))
                        .build())
                .orElseThrow(() -> new RuntimeException("Price not found for id: " + id));
    }

    private List<String> parseOffers(String offers) {
        if (offers == null || offers.isBlank()) return List.of();
        return Arrays.stream(offers.split(","))
                .map(String::trim)
                .toList();
    }
}
