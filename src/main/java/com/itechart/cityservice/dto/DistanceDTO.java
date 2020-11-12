package com.itechart.cityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistanceDTO {
    @NotEmpty
    @NotBlank
    private String cityA;

    @NotEmpty
    @NotBlank
    private String cityB;

    @Min(1)
    private Long distance;
}
