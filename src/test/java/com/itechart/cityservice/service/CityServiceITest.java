package com.itechart.cityservice.service;

import com.itechart.cityservice.dto.DistanceDTO;
import com.itechart.cityservice.dto.PathRequestDTO;
import com.itechart.cityservice.exception.PathNotExistsException;
import com.itechart.cityservice.exception.UnknownCityException;
import com.itechart.cityservice.repo.DistanceRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CityServiceITest {
    @Autowired CityServiceImpl cityService;
    @Autowired DistanceRepo distanceRepo;

    @BeforeEach
    void beforeEach() {
        distanceRepo.deleteAll();
    }

    @Test
    void addNewDistances_OK() {
        var inputData = getSmallInputData();
        cityService.addDistances(inputData);

        var distancesFromDB = distanceRepo.findAll();

        assertEquals(3, distancesFromDB.size());

        var cachedCities = cityService.getCachedCities();

        assertEquals(4, cachedCities.size());
        assertTrue(cachedCities.containsKey("A"));
        assertTrue(cachedCities.containsKey("B"));
        assertTrue(cachedCities.containsKey("C"));
        assertTrue(cachedCities.containsKey("D"));
    }

    @Test
    void findPathForSmallData_OK() {
        var inputData = getSmallInputData();
        cityService.addDistances(inputData);

        var pathRequest = new PathRequestDTO("A", "D");
        var pathResponse = cityService.findAllPaths(pathRequest);

        assertEquals(1, pathResponse.size());
        assertEquals(16, pathResponse.get(0).getDistance());
        assertEquals(List.of("A", "B", "C", "D"), pathResponse.get(0).getPath());
    }

    @Test
    @Timeout(value = 300, unit = TimeUnit.MILLISECONDS)
    void findPathForBigData_OK() {
        var inputData = getBigInputData();
        cityService.addDistances(inputData);

        var pathRequest = new PathRequestDTO("A", "K");
        var pathResponse = cityService.findAllPaths(pathRequest);

        assertEquals(217, pathResponse.size());

        // the shortest path
        assertEquals(12, pathResponse.get(0).getDistance());
        assertEquals(List.of("A", "B", "E", "J", "K"), pathResponse.get(0).getPath());

        // the longest path
        assertEquals(168, pathResponse.get(216).getDistance());
        assertEquals(List.of("A", "C", "H", "X", "Y", "M", "Z", "S", "T", "V", "U", "O", "N", "P", "R", "Q", "L", "I", "J", "K"),
                pathResponse.get(216).getPath());
    }

    @Test
    void findPathForData_NoPathException() {
        var invalidInputData = List.of(
                new DistanceDTO("A", "B", 5L),
                new DistanceDTO("C", "D", 1L)
        );
        cityService.addDistances(invalidInputData);

        Exception exception = assertThrows(PathNotExistsException.class, () -> {
            var pathRequest = new PathRequestDTO("A", "D");
            cityService.findAllPaths(pathRequest);
        });

        assertEquals("Unfortunately, there is no path between A and D", exception.getMessage());
    }

    @Test
    void findPathForData_UnknownCityException() {
        var inputData = getSmallInputData();
        cityService.addDistances(inputData);

        Exception exception = assertThrows(UnknownCityException.class, () -> {
            var pathRequest = new PathRequestDTO("ER", "DEST");
            cityService.findAllPaths(pathRequest);
        });

        assertEquals("ER is unknown in the system", exception.getMessage());
    }


    private List<DistanceDTO> getSmallInputData() {
        return List.of(
                new DistanceDTO("A", "B", 5L),
                new DistanceDTO("B", "C", 10L),
                new DistanceDTO("C", "D", 1L)
        );
    }

    private List<DistanceDTO> getBigInputData() {
        return List.of(
                new DistanceDTO("A", "B", 1L),
                new DistanceDTO("A", "D", 15L),
                new DistanceDTO("A", "C", 7L),
                new DistanceDTO("B", "E", 4L),
                new DistanceDTO("C", "G", 8L),
                new DistanceDTO("C", "H", 10L),
                new DistanceDTO("D", "J", 8L),
                new DistanceDTO("E", "F", 5L),
                new DistanceDTO("E", "J", 4L),
                new DistanceDTO("F", "H", 2L),
                new DistanceDTO("H", "M", 1L),
                new DistanceDTO("I", "J", 5L),
                new DistanceDTO("I", "L", 9L),
                new DistanceDTO("I", "M", 1L),
                new DistanceDTO("J", "K", 3L),
                new DistanceDTO("L", "Q", 7L),
                new DistanceDTO("L", "N", 5L),
                new DistanceDTO("N", "P", 6L),
                new DistanceDTO("O", "P", 7L),
                new DistanceDTO("N", "O", 13L),
                new DistanceDTO("Q", "R", 10L),
                new DistanceDTO("R", "P", 8L),
                new DistanceDTO("O", "U", 4L),
                new DistanceDTO("U", "V", 11L),
                new DistanceDTO("V", "W", 16L),
                new DistanceDTO("T", "V", 6L),
                new DistanceDTO("O", "T", 3L),
                new DistanceDTO("U", "T", 5L),
                new DistanceDTO("M", "O", 2L),
                new DistanceDTO("M", "S", 6L),
                new DistanceDTO("M", "S", 6L),
                new DistanceDTO("S", "T", 8L),
                new DistanceDTO("S", "Z", 14L),
                new DistanceDTO("Z", "M", 9L),
                new DistanceDTO("Z", "Y", 9L),
                new DistanceDTO("Y", "M", 17L),
                new DistanceDTO("Y", "X", 10L),
                new DistanceDTO("H", "X", 11L)
        );
    }
}
