package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.response.MovieDetails;
import com.ck.movie.booking.platform.dto.response.TheatreDetails;
import com.ck.movie.booking.platform.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieDetails findById(String id) {
        return movieRepository.findById(id)
                .map(m -> MovieDetails.builder()
                        .name(m.getName())
                        .rating(m.getRating())
                        .category(m.getCategory())
                        .language(m.getLanguage())
                        .build()
                )
                .orElseThrow(() -> new RuntimeException("Movie not found: " + id));
    }
}
