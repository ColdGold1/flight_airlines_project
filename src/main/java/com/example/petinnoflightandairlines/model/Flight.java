package com.example.petinnoflightandairlines.model;

import com.example.petinnoflightandairlines.model.enumtype.FlightType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flight")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "[0-9]{1,4}")
    @Column(name = "flight_number")
    private String flightNumber;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "dep_airport_id")
    private Airport depAirport;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "arr_airport_id", nullable = false)
    private Airport arrAirport;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @NotNull
    @Column(name = "departure_time")
    private Instant departureTime;

    @NotNull
    @Column(name = "arrival_time")
    private Instant arrivalTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_flight")
    private FlightType flightType;

    @OneToMany(mappedBy = "flight",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true
    )
    private List<Booking> booking = new ArrayList<>();
}

