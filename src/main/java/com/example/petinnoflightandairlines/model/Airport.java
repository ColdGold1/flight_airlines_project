package com.example.petinnoflightandairlines.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.*;
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

    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    @Column(name = "max_count_of_sync_flights")
    @Positive
    private Integer maxCountOfSyncFlights;

    @NotNull
    @Pattern(regexp = "\\w{3}")
    @Column(name = "airport_iata", unique = true)
    private String airportIata;

    @NotNull
    @Pattern(regexp = "\\w{4}")
    @Column(name = "airport_icao", unique = true)
    private String airportIcao;

    @NotNull
    @Size(min = 1, max = 50)
    private String location;

    @Builder.Default
    @OneToMany(
            mappedBy = "arrivalAirport",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true
    )
    private List<Flight> arrivalFlights = new ArrayList<>();


    @Builder.Default
    @OneToMany(mappedBy = "departureAirport",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true
    )
    private List<Flight> departureFlights = new ArrayList<>();

}
