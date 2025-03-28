package com.example.petinnoflightandairlines.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotNull
    @Size(min = 1, max = 50)
    private String model;

    @NotNull
    @Pattern(regexp = "\\w{4}")
    @Column(name = "aircraft_icao")
    private String aircraftIcao;

    @NotNull
    @Pattern(regexp = "\\w{3}")
    @Column(name = "aircraft_iata")
    private String aircraftIata;

    @NotNull
    @Column(name = "count_of_seats")
    @Positive
    private int countOfSeats;

    @OneToMany(mappedBy = "aircraft",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true)
    private List<Flight> flights = new ArrayList<>();
}
