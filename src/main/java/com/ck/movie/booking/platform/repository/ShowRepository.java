package com.ck.movie.booking.platform.repository;

import com.ck.movie.booking.platform.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ShowRepository extends JpaRepository<Show, String> {

    Page<Show> findByMovieNameAndShowDate(String movieName, LocalDate showDate, Pageable pageable);
}
