package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.dto.response.MovieDetails;
import com.ck.movie.booking.platform.entity.Movie;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.exception.ServiceException;
import com.ck.movie.booking.platform.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieDetails findById(String id) {
        try {
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));
            return MovieDetails.builder()
                    .name(movie.getName())
                    .category(movie.getCategory())
                    .language(movie.getLanguage())
                    .rating(movie.getRating())
                    .build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Unexpected error retrieving movie with id: " + id, e);
        }
    }
}
