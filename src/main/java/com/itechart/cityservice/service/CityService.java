package com.itechart.cityservice.service;

import com.itechart.cityservice.dto.DistanceDTO;
import com.itechart.cityservice.dto.PathRequestDTO;
import com.itechart.cityservice.dto.PathResponse;

import java.util.List;

public interface CityService {
    void addDistances(List<DistanceDTO> distances);

    List<PathResponse> findAllPaths(PathRequestDTO pathRequest);

    void deleteAllDistances();
}
