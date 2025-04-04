package com.example.petinnoflightandairlines.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AirportDTO {

    @NotNull
    @Size(min = 1, max = 50, message = "Airport name must be in 50 symbols")
    private String name;

    @NotNull
    @Positive(message = "count of sync flights should be positive")
    private Integer maxCountOfSyncFlights;

    @NotNull
    @Pattern(regexp = "\\w{3}", message = "iata should contain 3 exactly numbers")
    private String airportIata;

    @NotNull
    @Pattern(regexp = "\\w{4}", message = "iata should contain 4 exactly numbers")
    private String airportIcao;

    @NotNull
    @Size(min = 1, max = 50, message = "location must be in 50 symbols")
    private String location;
}
