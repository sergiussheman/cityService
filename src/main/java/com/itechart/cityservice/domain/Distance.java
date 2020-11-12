package com.itechart.cityservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class Distance {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "first_city")
    private String firstCity;

    @Column(name = "second_city")
    private String secondCity;

    private Long distance;
}
