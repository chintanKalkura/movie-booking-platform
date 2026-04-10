package com.ck.movie.booking.platform.controller;

import com.ck.movie.booking.platform.constants.enums.MovieRating;
import com.ck.movie.booking.platform.constants.enums.ScreenType;
import com.ck.movie.booking.platform.dto.request.BookingRequest;
import com.ck.movie.booking.platform.dto.response.BookedShowDetails;
import com.ck.movie.booking.platform.dto.response.BookingResponse;
import com.ck.movie.booking.platform.dto.response.MovieDetails;
import com.ck.movie.booking.platform.dto.response.TheatreDetails;
import com.ck.movie.booking.platform.exception.BadRequestException;
import com.ck.movie.booking.platform.exception.ResourceNotFoundException;
import com.ck.movie.booking.platform.exception.ServiceException;
import com.ck.movie.booking.platform.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BookingService bookingService;

    private static final String SHOW_ID = "dddddddd-0000-0000-0000-000000000001";

    @Test
    void bookShow_validRequest_returns200WithBookingResponse() throws Exception {
        BookingRequest request = new BookingRequest(List.of("A1", "A2"), "user@example.com", "9876543210");
        when(bookingService.bookShow(eq(SHOW_ID), any(BookingRequest.class)))
                .thenReturn(buildBookingResponse());

        mockMvc.perform(post("/book")
                        .param("show", SHOW_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("booking-id-123"))
                .andExpect(jsonPath("$.seats", hasSize(2)))
                .andExpect(jsonPath("$.seats[0]").value("A1"))
                .andExpect(jsonPath("$.totalCost").value(1100))
                .andExpect(jsonPath("$.userEmail").value("user@example.com"))
                .andExpect(jsonPath("$.userPhoneNumber").value("9876543210"))
                .andExpect(jsonPath("$.show.movie.name").value("Inception"))
                .andExpect(jsonPath("$.show.screenType").value("IMAX"));
    }

    @Test
    void bookShow_missingShowParam_returns400() throws Exception {
        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookShow_missingBody_returns400() throws Exception {
        mockMvc.perform(post("/book")
                        .param("show", SHOW_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookShow_showNotFound_returns404() throws Exception {
        when(bookingService.bookShow(anyString(), any(BookingRequest.class)))
                .thenThrow(new ResourceNotFoundException("Show not found: " + SHOW_ID));

        mockMvc.perform(post("/book")
                        .param("show", SHOW_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookShow_notEnoughSeats_returns400() throws Exception {
        when(bookingService.bookShow(anyString(), any(BookingRequest.class)))
                .thenThrow(new BadRequestException("Not enough seats available"));

        mockMvc.perform(post("/book")
                        .param("show", SHOW_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookShow_serviceException_returns500() throws Exception {
        when(bookingService.bookShow(anyString(), any(BookingRequest.class)))
                .thenThrow(new ServiceException("Unexpected error booking show: " + SHOW_ID, new RuntimeException()));

        mockMvc.perform(post("/book")
                        .param("show", SHOW_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isInternalServerError());
    }

    private BookingRequest validRequest() {
        return new BookingRequest(List.of("A1"), "user@example.com", "9876543210");
    }

    private BookingResponse buildBookingResponse() {
        BookedShowDetails showDetails = BookedShowDetails.builder()
                .movie(MovieDetails.builder()
                        .name("Inception").category("Sci-Fi Thriller")
                        .language("English").rating(MovieRating.UA).build())
                .showTime(LocalTime.of(10, 30))
                .showDate(LocalDate.of(2026, 4, 10))
                .screenType(ScreenType.IMAX)
                .theatre(TheatreDetails.builder().name("PVR Cinemas").address("123 MG Road").build())
                .build();

        return BookingResponse.builder()
                .bookingId("booking-id-123")
                .seats(List.of("A1", "A2"))
                .show(showDetails)
                .totalCost(BigDecimal.valueOf(1100))
                .userEmail("user@example.com")
                .userPhoneNumber("9876543210")
                .build();
    }
}
