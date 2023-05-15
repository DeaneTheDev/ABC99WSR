package com.weathersensors.poc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorDTO {

    private String id;
    private String title;
    private Double averageTemp;
    private Double averageHumidity;
    private Double averageWindSpeed;
    private Integer minTemp;
    private Integer minHumidity;
    private Integer minWindSpeed;
    private Integer maxTemp;
    private Integer maxHumidity;
    private Integer maxWindSpeed;
    private Integer tempSum;
    private Integer humiditySum;
    private Integer windSpeedSum;

}
