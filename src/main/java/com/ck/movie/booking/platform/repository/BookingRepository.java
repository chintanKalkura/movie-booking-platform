package com.ck.movie.booking.platform.repository;

import com.ck.movie.booking.platform.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, String> {
}
