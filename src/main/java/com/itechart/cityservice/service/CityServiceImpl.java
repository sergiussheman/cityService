package com.itechart.cityservice.service;

import com.itechart.cityservice.domain.City;
import com.itechart.cityservice.domain.Distance;
import com.itechart.cityservice.dto.CityDto;
import com.itechart.cityservice.dto.DistanceDTO;
import com.itechart.cityservice.dto.PathRequestDTO;
import com.itechart.cityservice.dto.PathResponse;
import com.itechart.cityservice.exception.PathNotExistsException;
import com.itechart.cityservice.exception.UnknownCityException;
import com.itechart.cityservice.repo.CityRepo;
import com.itechart.cityservice.repo.DistanceRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

@Service
public class CityServiceImpl implements CityService {
    private static final Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);

    private final DistanceRepo distanceRepo;
    private final CityRepo cityRepo;

    private Map<String, CityDto> cachedCities;
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final WriteLock writeLock = reentrantReadWriteLock.writeLock();
    private final ReadLock readLock = reentrantReadWriteLock.readLock();


    public CityServiceImpl(DistanceRepo distanceRepo, CityRepo cityRepo) {
        this.distanceRepo = distanceRepo;
        this.cityRepo = cityRepo;
    }

    @PostConstruct
    public void postConstruct() {
        this.updateCache();
    }

    public void updateCache() {
        try {
            // We have to update our cachedCities according to fetched distances.
            // So, we take exclusive lock for working with that map
            writeLock.lock();

            // We treat all distances between cities as bidirectional
            List<Distance> allDistances = distanceRepo.findAll();

            cachedCities = new HashMap<>();

            for (Distance distance : allDistances) {
                cachedCities.putIfAbsent(distance.getFirstCity().getName(), new CityDto(distance.getFirstCity().getName(), new HashMap<>()));
                cachedCities.putIfAbsent(distance.getSecondCity().getName(), new CityDto(distance.getSecondCity().getName(), new HashMap<>()));

                // add to map direction from A to B
                cachedCities.computeIfPresent(distance.getFirstCity().getName(), (k, v) -> {
                    CityDto secondCity = cachedCities.get(distance.getSecondCity().getName());
                    v.getAdjacentCities().put(secondCity, distance.getDistance());
                    return v;
                });

                // add to map direction from B to A
                cachedCities.computeIfPresent(distance.getSecondCity().getName(), (k, v) -> {
                    CityDto firstCity = cachedCities.get(distance.getFirstCity().getName());
                    v.getAdjacentCities().put(firstCity, distance.getDistance());
                    return v;
                });
            }
            logger.info("cachedCities was successfully updated at {}", System.currentTimeMillis());
        } catch (Exception e) {
            logger.error("Some problem occurred while updating cachedCities. So cachedCities could be stale now!", e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    @Transactional
    public void addDistances(List<DistanceDTO> distances) {
        // If we already have distance between cities in database, then we just update the distance between them.
        // Otherwise we save a new record for these cities.
        for (DistanceDTO distanceDTO : distances) {
            // check if such cities have been already added to database.
            // Otherwise we need to add to the DB new records for them
            City cityA = findOrCreateCity(distanceDTO.getCityA());
            City cityB = findOrCreateCity(distanceDTO.getCityB());

            Optional<Distance> distanceFromDB = distanceRepo.findByFirstCityAndSecondCity(cityA, cityB);

            // If we were not able to find distance between A to B, then maybe it's saved as distance between B to A?
            // It's possible because we have bidirectional distances
            if (distanceFromDB.isEmpty()) {
                distanceFromDB = distanceRepo.findByFirstCityAndSecondCity(cityB, cityA);
            }

            distanceFromDB.ifPresentOrElse(v -> {
                v.setDistance(distanceDTO.getDistance());
                distanceRepo.save(v);
            }, () -> {
                Distance freshDistance = new Distance();
                freshDistance.setFirstCity(cityA);
                freshDistance.setSecondCity(cityB);
                freshDistance.setDistance(distanceDTO.getDistance());
                distanceRepo.save(freshDistance);
            });
        }

        // we need to update our cached values in "cachedCities" map
        updateCache();
    }

    @Override
    public List<PathResponse> findAllPaths(PathRequestDTO pathRequest) {
        try {
            readLock.lock();

            CityDto startCity = cachedCities.get(pathRequest.getStartCity());
            if (startCity == null) {
                throw new UnknownCityException(String.format("%s is unknown in the system", pathRequest.getStartCity()));
            }

            CityDto destinationCity = cachedCities.get(pathRequest.getDestinationCity());
            if (destinationCity == null) {
                throw new UnknownCityException(String.format("%s is unknown in the system", pathRequest.getDestinationCity()));
            }

            List<PathResponse> result = new ArrayList<>();

            List<String> initialPath = new ArrayList<>();
            initialPath.add(pathRequest.getStartCity());
            findPath(startCity, destinationCity, initialPath, 0L, result);

            if (result.isEmpty()) {
                throw new PathNotExistsException(String.format("Unfortunately, there is no path between %s and %s",
                        pathRequest.getStartCity(), pathRequest.getDestinationCity()));
            }

            result.sort(Comparator.comparing(PathResponse::getDistance));
            return result;
        } finally {
            readLock.unlock();
        }
    }

    private void findPath(CityDto currentCity, CityDto destinationCity, List<String> currentPath, Long currentDistance,
                          List<PathResponse> foundPaths) {
        if (currentCity.equals(destinationCity)) {
            foundPaths.add(new PathResponse(new ArrayList<>(currentPath), currentDistance));
        }

        for (Map.Entry<CityDto, Long> adjacentCity : currentCity.getAdjacentCities().entrySet()) {
            // We expect that there are no two different cities with the same name
            if (!currentPath.contains(adjacentCity.getKey().getName())) {
                currentPath.add(adjacentCity.getKey().getName());
                currentDistance += adjacentCity.getValue();

                findPath(adjacentCity.getKey(), destinationCity, currentPath, currentDistance, foundPaths);

                currentPath.remove(adjacentCity.getKey().getName());
                currentDistance -= adjacentCity.getValue();
            }
        }
    }

    private City findOrCreateCity(String cityName) {
        Optional<City> cityA = cityRepo.findByName(cityName);

        if (cityA.isPresent()) {
            return cityA.get();
        } else {
            City freshCity = new City();
            freshCity.setName(cityName);
            return cityRepo.save(freshCity);
        }
    }

    @Override
    public void deleteAllDistances() {
        try {
            writeLock.lock();

            distanceRepo.deleteAll();
            cachedCities = new HashMap<>();
        } finally {
            writeLock.unlock();
        }
    }

    // use only for testing
    Map<String, CityDto> getCachedCities() {
        return this.cachedCities;
    }
}
