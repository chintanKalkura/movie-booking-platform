package com.ck.movie.booking.platform.entity;

import com.ck.movie.booking.platform.constants.enums.MovieRating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movies")
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String category;
    private String language;

    @Enumerated(EnumType.STRING)
    private MovieRating rating;
}
