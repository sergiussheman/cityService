package com.itechart.cityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {
    private String name;
    private Map<CityDto, Long> adjacentCities = new HashMap<>();

    @Override public String toString() {
        StringBuilder adjacentCitiesAsString = new StringBuilder();
        for (Map.Entry<CityDto, Long> currentAdjacent : adjacentCities.entrySet()) {
            adjacentCitiesAsString.append(currentAdjacent.getKey().getName()).append(":").append(currentAdjacent.getValue());
        }

        return "City{" +
                "name='" + name + '\'' +
                ", adjacentCities=" + adjacentCitiesAsString +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityDto city = (CityDto) o;
        return name.equals(city.name);
    }

    @Override public int hashCode() {
        return Objects.hash(name);
    }
}
