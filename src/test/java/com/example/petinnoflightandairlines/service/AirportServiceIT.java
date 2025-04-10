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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor
class AirportServiceIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest");

    private static final Integer MAX_COUNT_OF_SYNC_FLIGHTS = 4;
    private static final String NAME = "some_name";
    private static final String AIRPORT_IATA = "333";
    private static final String AIRPORT_ICAO = "3334";
    private static final String LOCATION = "some location";

    private final Airport newAirport = Airport.builder()
            .name(NAME)
            .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
            .airportIata(AIRPORT_IATA)
            .airportIcao(AIRPORT_ICAO)
            .location(LOCATION)
            .build();

    @Autowired
    private AirportService airportService;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirportMapper airportMapper;


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

    @Test
    void test_save_shouldSaveA() {
        //given
        AirportDTO newAirportDTO = AirportDTO.builder()
                .name(NAME)
                .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
                .airportIata(AIRPORT_IATA)
                .airportIcao(AIRPORT_ICAO)
                .location(LOCATION)
                .build();
        //when
        AirportDTO airportDTO = airportService.save(newAirportDTO);
        //then
        assertNotNull(airportDTO);
        assertEquals(NAME, airportDTO.getName());
        assertEquals(AIRPORT_ICAO, airportDTO.getAirportIcao());
        assertEquals(AIRPORT_IATA, airportDTO.getAirportIata());
        assertEquals(LOCATION, airportDTO.getLocation());
    }

    @Test
    void test_save_shouldThrowException() {
        //given
        AirportDTO airportDTO = AirportDTO.builder()
                .name(NAME)
                .maxCountOfSyncFlights(MAX_COUNT_OF_SYNC_FLIGHTS)
                .airportIata("12")
                .airportIcao(AIRPORT_ICAO)
                .location(LOCATION)
                .build();
        //when
        //then
        ConstraintViolationException cve = assertThrows(ConstraintViolationException.class,
                () -> airportService.save(airportDTO));
        log.info("mes = {}", cve.getMessage());
    }

    @Test
    void test_getAirports_shouldGetAirport() {
        //given
        Airport airport = airportRepository.save(newAirport);
        //when
        AirportSearchCriteria criteria = AirportSearchCriteria.builder()
                .id(airport.getId())
                .build();

        Pageable pageable = PageRequest.ofSize(1);
        Page<AirportDTO> foundAirportPage = airportService.getAirports(criteria, pageable);
        AirportDTO foundAirportDTO = foundAirportPage.getContent().get(0);
        //then
        assertNotNull(foundAirportDTO);
        assertEquals(NAME, foundAirportDTO.getName());
        assertEquals(AIRPORT_ICAO, foundAirportDTO.getAirportIcao());
        assertEquals(AIRPORT_IATA, foundAirportDTO.getAirportIata());
        assertEquals(LOCATION, foundAirportDTO.getLocation());
        assertEquals(MAX_COUNT_OF_SYNC_FLIGHTS, foundAirportDTO.getMaxCountOfSyncFlights());
    }

    @Test
    void test_updateAirport_shouldUpdateAirport() {
        //given
        Airport airport = airportRepository.save(newAirport);

        AirportDTO airportDTO = AirportDTO.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("123")
                .airportIcao("1234")
                .location("random location")
                .build();
        //when
        AirportDTO foundAirportDTO = airportService.updateAirport(airport.getId(), airportDTO);
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

        Airport airport3 = Airport.builder()
                .name("random name1")
                .maxCountOfSyncFlights(7)
                .airportIata("125")
                .airportIcao("1236")
                .location(LOCATION)
                .build();

        airportRepository.save(airport1);
        airportRepository.save(airport2);
        airportRepository.save(airport3);
        airportRepository.save(newAirport);
        //when
        AirportSearchCriteria criteria = AirportSearchCriteria.builder()
                .location(LOCATION)
                .build();
        //then
        assertEquals(3, getCountOfElementsInList(criteria, 3));
    }

    @Test
    void test_getAirports_shouldGetAllAirports() {
        //given
        Airport airport = Airport.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("123")
                .airportIcao("1234")
                .location("random location")
                .build();

        Airport airport1 = Airport.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("124")
                .airportIcao("1233")
                .location("random location")
                .build();

        airportRepository.save(newAirport);
        airportRepository.save(airport);
        airportRepository.save(airport1);
        AirportSearchCriteria criteria = new AirportSearchCriteria();
        //when
        //then
        assertEquals(3, getCountOfElementsInList(criteria, 2));
    }

    @Test
    void test_getAirports_shouldGetAirportOnSecondPage() {
        //given
        Airport airport = Airport.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("123")
                .airportIcao("1234")
                .location("random location")
                .build();

        Airport airport1 = Airport.builder()
                .name("random name")
                .maxCountOfSyncFlights(7)
                .airportIata("124")
                .airportIcao("1233")
                .location("random location")
                .build();

        airportRepository.save(newAirport);
        airportRepository.save(airport);
        airportRepository.save(airport1);
        AirportSearchCriteria criteria = new AirportSearchCriteria();
        //when
        Pageable pageable = PageRequest.of(1,2);
        Page<AirportDTO> page = airportService.getAirports(criteria,pageable);
        AirportDTO airportDTO = page.getContent().get(0);
//        then
        assertEquals("124",airportDTO.getAirportIata());
        assertEquals("1233",airportDTO.getAirportIcao());
    }

    private Integer getCountOfElementsInList(AirportSearchCriteria criteria,
                                             Integer countOfElementsOnAPage) {

        Page<AirportDTO> page;
        int pageNumber = 0;
        int countOfRecords = 0;
        do {
            Pageable pageable = PageRequest.of(pageNumber, countOfElementsOnAPage);
            page = airportService.getAirports(criteria, pageable);
            log.info("On page {} elements {}", pageNumber, page.getContent().size());
            countOfRecords += page.getContent().size();
            pageNumber++;
        }
        while (!page.isLast());

        return countOfRecords;
    }
}