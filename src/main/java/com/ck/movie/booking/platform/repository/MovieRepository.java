package com.ck.movie.booking.platform.repository;

import com.ck.movie.booking.platform.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, String> {
}
