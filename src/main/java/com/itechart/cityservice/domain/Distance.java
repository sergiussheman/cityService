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

    @ManyToOne
    @JoinColumn(name = "first_city")
    private City firstCity;

    @ManyToOne
    @JoinColumn(name = "second_city")
    private City secondCity;

    private Long distance;
}
