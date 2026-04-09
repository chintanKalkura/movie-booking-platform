package com.ck.movie.booking.platform.entity;

import com.ck.movie.booking.platform.constants.enums.ScreenType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Screen {

    @Column(name = "screen_number")
    private int number;

    private int totalSeats;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;
}
