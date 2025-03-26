package com.example.petinnoflightandairlines.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "aircraft")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;

    @Column(name = "aircraft_icao")
    private String aircraftIcao;

    @Column(name = "aircraft_iata")
    private String aircraftIata;

    @Column(name = "count_of_seats")
    private int countOfSeats;

    @OneToMany(mappedBy = "aircraft",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true)
    private List<Flight> flights = new ArrayList<>();
}
