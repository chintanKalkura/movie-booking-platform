package com.ck.movie.booking.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theatres")
@Getter
@Setter
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "theatre_screens", joinColumns = @JoinColumn(name = "theatre_id"))
    private List<Screen> screens = new ArrayList<>();
}
