package com.ck.movie.booking.platform.service;

import com.ck.movie.booking.platform.constants.enums.MovieRating;
import com.ck.movie.booking.platform.dto.response.MovieDetails;
import com.ck.movie.booking.platform.entity.Movie;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock private MovieRepository movieRepository;
    @InjectMocks private MovieService movieService;

    @Test
    void findById_mapsEntityToDetailsCorrectly() {
        Movie movie = new Movie();
        movie.setId("movie-1");
        movie.setName("Inception");
        movie.setCategory("Sci-Fi Thriller");
        movie.setLanguage("English");
        movie.setRating(MovieRating.UA);

        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(movie));

        MovieDetails result = movieService.findById("movie-1");

        assertThat(result.name()).isEqualTo("Inception");
        assertThat(result.category()).isEqualTo("Sci-Fi Thriller");
        assertThat(result.language()).isEqualTo("English");
        assertThat(result.rating()).isEqualTo(MovieRating.UA);
    }

    @Test
    void findById_notFound_throwsRuntimeException() {
        when(movieRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.findById("missing-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("missing-id");
    }
}
