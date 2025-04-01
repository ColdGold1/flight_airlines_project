package com.example.petinnoflightandairlines.model;

import com.example.petinnoflightandairlines.model.enumtype.FlightType;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
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
    private Airport departureAirport;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "arr_airport_id")
    private Airport arrivalAirport;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @NotNull
    @Column(name = "departure_time")
    private ZonedDateTime departureTime;

    @NotNull
    @Column(name = "arrival_time")
    private ZonedDateTime arrivalTime;

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

