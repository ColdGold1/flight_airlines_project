package com.example.petinnoflightandairlines.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//icao = 4
//iata = 3

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

    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    @Column(name = "max_count_of_sync_flights")
    @Positive
    private int maxCountOfSyncFlights;

    @NotNull
    @Pattern(regexp = "\\w{3}")
    @Column(name = "airport_iata")
    private String airportIata;

    @NotNull
    @Pattern(regexp = "\\w{4}")
    @Column(name = "airport_icao")
    private String airportIcao;

    @NotNull
    @Size(min = 1, max = 50)
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
