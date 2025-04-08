package com.example.petinnoflightandairlines.service;

import com.example.petinnoflightandairlines.dto.AirportDTO;
import com.example.petinnoflightandairlines.dto.AirportSearchCriteria;
import com.example.petinnoflightandairlines.mapper.AirportMapper;
import com.example.petinnoflightandairlines.model.Airport;
import com.example.petinnoflightandairlines.repository.AirportRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor
class AirportServiceIT {

    @Autowired
    private AirportService airportService;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirportMapper airportMapper;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    private static final Integer MAX_COUNT_OF_SYNC_FLIGHTS = 4;
    private static final String NAME = "some_name";
    private static final String AIRPORT_IATA = "333";
    private static final String AIRPORT_ICAO = "3334";
    private static final String LOCATION = "some location";

    @DynamicPropertySource
    static void dynamicPropertySource(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }

    @BeforeEach
    void setUp() {
        airportService = new AirportServiceImpl(airportRepository, airportMapper);
        airportRepository.deleteAll();
    }

    //TODO
    @Test
    void test_addAirport_shouldAddAirport() {
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
    void test_addAirport_shouldThrowException() {
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
        ConstraintViolationException cve = assertThrows(ConstraintViolationException.class,
                () -> airportService.addAirport(newAirportDTO));
        log.info("mes = {}", cve.getMessage());
    }

    @Test
    void test_getAirport_shouldGetAirport() {
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
        AirportSearchCriteria criteria = AirportSearchCriteria.builder()
                .id(airport.getId())
                .build();
        AirportDTO foundAirportDTO = airportService.getAirports(criteria).get(0);
        //then
        assertNotNull(foundAirportDTO);
        assertEquals(NAME, foundAirportDTO.getName());
        assertEquals(AIRPORT_ICAO, foundAirportDTO.getAirportIcao());
        assertEquals(AIRPORT_IATA, foundAirportDTO.getAirportIata());
        assertEquals(LOCATION, foundAirportDTO.getLocation());
        assertEquals(MAX_COUNT_OF_SYNC_FLIGHTS, foundAirportDTO.getMaxCountOfSyncFlights());
    }

    @Test
    void test_updateAirport_shouldUpdateAirportDTO() {
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
    void test_deleteAirport_shouldDeleteAirport() {
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

//    TODO cuz I need aircraft
//    @Test
//    void getAllFlightsConnectedWithAirport() {
//        //given
//        //when
//        //then
//    }

    @Test
    void test_getAirportByLocation_shouldGetAirportsByLocation() {
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
        AirportSearchCriteria criteria = AirportSearchCriteria.builder()
                .location(LOCATION)
                .build();

        List<AirportDTO> airportsByLocation = airportService.getAirports(criteria);
        //then
        assertEquals(2, airportsByLocation.size());
    }

    @Test
    void test_getAllAirports_shouldGetAllAirports() {
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
        AirportSearchCriteria criteria = new AirportSearchCriteria();
        //when
        List<AirportDTO> airportList = airportService.getAirports(criteria);
        //then
        assertEquals(2, airportList.size());
    }

}