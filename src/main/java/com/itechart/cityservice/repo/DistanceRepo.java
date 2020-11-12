package com.itechart.cityservice.repo;

import com.itechart.cityservice.domain.City;
import com.itechart.cityservice.domain.Distance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistanceRepo extends JpaRepository<Distance, Long> {
    Optional<Distance> findByFirstCityAndSecondCity(City firstCity, City secondCity);
}
