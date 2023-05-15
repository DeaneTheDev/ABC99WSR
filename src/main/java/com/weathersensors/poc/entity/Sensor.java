package com.weathersensors.poc.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;


@Entity(name = "sensor")
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "title", unique = true, nullable = false, length = 25)
    private String title;

    @OneToMany
    @JsonIgnore
    private List<WeatherReport> weatherReports;

    public void addWeatherReport(WeatherReport weatherReport){
        weatherReports.add(weatherReport);
    }

}
