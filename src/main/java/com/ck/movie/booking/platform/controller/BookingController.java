package com.ck.movie.booking.platform.controller;

import com.ck.movie.booking.platform.dto.request.BookingRequest;
import com.ck.movie.booking.platform.dto.response.BookingResponse;
import com.ck.movie.booking.platform.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> bookShow(
            @RequestParam String show,
            @RequestBody BookingRequest request
    ) {
        return ResponseEntity.ok(bookingService.bookShow(show, request));
    }
}
