package com.itechart.cityservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class City {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;
}
