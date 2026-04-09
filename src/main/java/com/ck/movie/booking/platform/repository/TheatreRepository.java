package com.ck.movie.booking.platform.repository;

import com.ck.movie.booking.platform.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TheatreRepository extends JpaRepository<Theatre, String> {
}
