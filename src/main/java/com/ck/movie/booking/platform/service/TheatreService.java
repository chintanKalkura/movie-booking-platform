package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.response.TheatreDetails;
import com.ck.movie.booking.platform.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepository;

    public TheatreDetails findById(String id) {
        return theatreRepository.findById(id)
                .map(t -> TheatreDetails.builder()
                        .name(t.getName())
                        .address(t.getAddress())
                        .build())
                .orElseThrow(() -> new RuntimeException("Theatre not found: " + id));
    }
}
