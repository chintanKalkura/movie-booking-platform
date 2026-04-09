package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.response.PriceDetails;
import com.ck.movie.booking.platform.entity.Price;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.exception.ServiceException;
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
        try {
            Price price = priceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Price not found for id: " + id));
            return PriceDetails.builder()
                    .cost(price.getCost())
                    .offers(parseOffers(price.getOffers()))
                    .build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving price with id: " + id, e);
        }
    }

    private List<String> parseOffers(String offers) {
        if (offers == null || offers.isBlank()) return List.of();
        return Arrays.stream(offers.split(","))
                .map(String::trim)
                .toList();
    }
}
