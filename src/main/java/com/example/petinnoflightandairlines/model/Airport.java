package com.example.petinnoflightandairlines.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "airport")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "max_count_of_sync_flights")
    private int maxCountOfSyncFlights;

    @Column(name = "airport_iata")
    private String airportIata;

    @Column(name = "airport_icao")
    private String airportIcao;

    private String location;

    @OneToMany(
            mappedBy = "arrAirport",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true
    )
    private List<Flight> arrFlights = new ArrayList<>();

    @OneToMany(mappedBy = "depAirport",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true
    )
    private List<Flight> depFlights = new ArrayList<>();

}
