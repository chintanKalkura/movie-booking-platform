package com.ck.movie.booking.platform.controller;

import com.ck.movie.booking.platform.dto.response.ShowDetails;
import com.ck.movie.booking.platform.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping
    public ResponseEntity<Page<ShowDetails>> getShows(
            @RequestParam String movie,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
            @PageableDefault(size = 10, sort = "showTime") Pageable pageable
    ) {
        return ResponseEntity.ok(showService.getShowsByMovieName(movie, date, pageable));
    }
}
