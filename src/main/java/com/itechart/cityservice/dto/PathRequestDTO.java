package com.itechart.cityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathRequestDTO {
    @NotEmpty
    @NotBlank
    private String startCity;

    @NotEmpty
    @NotBlank
    private String destinationCity;
}
