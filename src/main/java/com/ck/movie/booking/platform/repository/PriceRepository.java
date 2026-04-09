package com.ck.movie.booking.platform.repository;

import com.ck.movie.booking.platform.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, String> {
}
