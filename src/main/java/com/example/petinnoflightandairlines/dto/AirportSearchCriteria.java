package com.example.petinnoflightandairlines.dto;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportSearchCriteria {

    private Long id;

    private String name;

    private Integer maxCountOfSyncFlights;

    private String airportIata;

    private String airportIcao;

    private String location;

    public boolean isEmpty() {

        return (id == null || id == 0)
                && StringUtils.isBlank(airportIata)
                && StringUtils.isBlank(airportIcao)
                && StringUtils.isBlank(name)
                && StringUtils.isBlank(location)
                && (maxCountOfSyncFlights == null
                || maxCountOfSyncFlights == 0);
    }

}
