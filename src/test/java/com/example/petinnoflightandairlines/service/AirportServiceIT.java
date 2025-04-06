package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.mapper.AirportMapper;
import com.example.petinnoflightandairlines.model.Airport;
import com.example.petinnoflightandairlines.repository.AirportRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
@RequiredArgsConstructor
class AirportServiceIT {

    @Autowired
    private AirportService airportService;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirportMapper airportMapper;

    private static final Integer MAX_COUNT_OF_SYNC_FLIGHTS = 4;
    private static final String NAME = "some_name";
    private static final String AIRPORT_IATA = "333";
    private static final String AIRPORT_ICAO = "3334";
    private static final String LOCATION = "some location";

    @BeforeEach
    void setUp() {
        airportService = new AirportServiceImpl(airportRepository, airportMapper);
    }

    @Test
    void ifAllGood_thenAddAirport() {
        //given
        Airport newAirport = Airport.builder()
                .name(NAME)
                .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
                .airportIata(AIRPORT_IATA)
                .airportIcao(AIRPORT_ICAO)
                .location(LOCATION)
                .build();

        AirportDTO newAirportDTO = airportMapper.convertAirportToAirportDTO(newAirport);
        //when
        AirportDTO airportDTO = airportService.addAirport(newAirportDTO);
        //then
        assertNotNull(airportDTO);
        assertEquals(NAME, airportDTO.getName());
        assertEquals(AIRPORT_ICAO, airportDTO.getAirportIcao());
        assertEquals(AIRPORT_IATA, airportDTO.getAirportIata());
        assertEquals(LOCATION, airportDTO.getLocation());
    }

    @Test
    void ifIataIsBad_whenAddAirport_thenThrowException() {
        //given
        Airport newAirport = Airport.builder()
                .name(NAME)
                .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
                .airportIata("12")
                .airportIcao(AIRPORT_ICAO)
                .location(LOCATION)
                .build();

        AirportDTO newAirportDTO = airportMapper.convertAirportToAirportDTO(newAirport);
        //when
        //then
        ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, () -> airportService.addAirport(newAirportDTO));
        log.info("mes = {}", cve.getMessage());
    }

    @Test
    void ifAllGood_thenGetAirport() {
        //given
        Airport newAirport = Airport.builder()
                .name(NAME)
                .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
                .airportIata(AIRPORT_IATA)
                .airportIcao(AIRPORT_ICAO)
                .location(LOCATION)
                .build();

        Airport airport = airportRepository.save(newAirport);
        //when
        AirportDTO foundAirportDTO = airportService.getAirportDTO(airport.getId());
        //then
        assertNotNull(foundAirportDTO);
        assertEquals(NAME, foundAirportDTO.getName());
        assertEquals(AIRPORT_ICAO, foundAirportDTO.getAirportIcao());
        assertEquals(AIRPORT_IATA, foundAirportDTO.getAirportIata());
        assertEquals(LOCATION, foundAirportDTO.getLocation());
        assertEquals(MAX_COUNT_OF_SYNC_FLIGHTS, foundAirportDTO.getMaxCountOfSyncFlights());
    }

    @Test
    void ifAllGood_thenUpdateAirportDTO() {
        //given
        Airport newAirport = Airport.builder()
                .name(NAME)
                .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
                .airportIata(AIRPORT_IATA)
                .airportIcao(AIRPORT_ICAO)
                .location(LOCATION)
                .build();
        Airport airport = airportRepository.save(newAirport);

        AirportDTO airportDTO = AirportDTO.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("123")
                .airportIcao("1234")
                .location("random location")
                .build();
        //when
        AirportDTO foundAirportDTO = airportService.updateAirportDTO(airport.getId(), airportDTO);
        //then
        assertNotNull(foundAirportDTO);
        assertEquals(airportDTO.getName(), foundAirportDTO.getName());
        assertEquals(airportDTO.getAirportIcao(), foundAirportDTO.getAirportIcao());
        assertEquals(airportDTO.getAirportIata(), foundAirportDTO.getAirportIata());
        assertEquals(airportDTO.getLocation(), foundAirportDTO.getLocation());
        assertEquals(airportDTO.getMaxCountOfSyncFlights(), foundAirportDTO.getMaxCountOfSyncFlights());
    }

    @Test
    void ifAllGood_thenDeleteAirport() {
        //given
        Airport newAirport = Airport.builder()
                .name(NAME)
                .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
                .airportIata(AIRPORT_IATA)
                .airportIcao(AIRPORT_ICAO)
                .location(LOCATION)
                .build();
        Airport airport = airportRepository.save(newAirport);
        //when
        airportService.deleteAirport(airport.getId());
        //then
        Optional<Airport> airportOptional = airportRepository.findById(airport.getId());
        assertTrue(airportOptional.isEmpty());
    }

//    TODO cuz of I need aircraft
//    @Test
//    void getAllFlightsConnectedWithAirport() {
//        //given
//        //when
//        //then
//    }

    @Test
    void ifAllGood_thenGetAirportsByLocation() {
        //given
        Airport newAirport = Airport.builder()
                .name(NAME)
                .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
                .airportIata(AIRPORT_IATA)
                .airportIcao(AIRPORT_ICAO)
                .location(LOCATION)
                .build();

        Airport airport1 = Airport.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("123")
                .airportIcao("1234")
                .location(LOCATION)
                .build();

        Airport airport2 = Airport.builder()
                .name("random name1")
                .maxCountOfSyncFlights(7)
                .airportIata("124")
                .airportIcao("1235")
                .location("random location")
                .build();
        airportRepository.save(airport1);
        airportRepository.save(airport2);
        airportRepository.save(newAirport);
        //when
        List<AirportDTO> airportsByLocation = airportService.getAirportsByLocation(LOCATION);
        //then
        assertEquals(2, airportsByLocation.size());
    }

    @Test
    void getAllAirports() {
        //given
        Airport newAirport = Airport.builder()
                .name(NAME)
                .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
                .airportIata(AIRPORT_IATA)
                .airportIcao(AIRPORT_ICAO)
                .location(LOCATION)
                .build();

        Airport airport = Airport.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("123")
                .airportIcao("1234")
                .location("random location")
                .build();

        airportRepository.save(newAirport);
        airportRepository.save(airport);
        //when
        List<Airport> airportList = airportService.getAllAirports();
        //then
        assertEquals(2, airportList.size());
    }

}