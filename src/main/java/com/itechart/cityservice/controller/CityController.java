package com.itechart.cityservice.controller;

import com.itechart.cityservice.dto.DistanceDTO;
import com.itechart.cityservice.dto.PathRequestDTO;
import com.itechart.cityservice.dto.PathResponse;
import com.itechart.cityservice.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/city")
@Validated
public class CityController {
    private final CityService cityService;


    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping("/distance")
    public void addDistances(@Valid @RequestBody List<DistanceDTO> distances) {
        cityService.addDistances(distances);
    }

    @GetMapping("/path")
    public List<PathResponse> findPath(@RequestParam @NotEmpty @NotBlank String start,
                                       @RequestParam @NotEmpty @NotBlank String destination) {
        return cityService.findAllPaths(new PathRequestDTO(start, destination));
    }

    @DeleteMapping("/distance")
    public ResponseEntity<String> deleteAllDistances() {
        cityService.deleteAllDistances();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
