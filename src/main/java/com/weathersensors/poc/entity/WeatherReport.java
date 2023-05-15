package com.weathersensors.poc.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity(name="weather_report")
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;
    @Column(nullable = false)
    private Date reportDate;
    private Integer temperature;
    private Integer humidity;
    private Integer windSpeed;

    public WeatherReport(Builder builder){
        this.sensor = builder.sensor;
        this.reportDate = builder.reportDate;
        this.temperature = builder.temperature;
        this.humidity = builder.humidity;
        this.windSpeed = builder.windSpeed;
    }

    public static class Builder {
        private final Sensor sensor;
        private final Date reportDate;
        private Integer temperature;
        private Integer humidity;
        private Integer windSpeed;

        public Builder(Sensor sensor){
            if(sensor == null){
                throw new IllegalArgumentException("Weather report must be linked to valid Sensor");
            }
            this.sensor = sensor;
            this.reportDate = new Date();
        }

        public Builder withTemperature(Integer temperature){
            if(temperature != null && isOutsideValidRange(temperature, -150, 200)){
                throw new IllegalArgumentException("Temperature must be valid value between -150 and 200 Fahrenheit");
            }
            this.temperature = temperature;
            return this;
        }

        public Builder withHumidity(Integer humidity){
            if(humidity != null && isOutsideValidRange(humidity, 0, 100)){
                throw new IllegalArgumentException("Humidity must be between 0 and 100%");
            }
            this.humidity = humidity;
            return this;
        }

        public Builder withWithSpeed(Integer windSpeed){
            if(windSpeed != null && isOutsideValidRange(windSpeed, 0, 250)){
                throw new IllegalArgumentException("Wind Speed must be between 0 and 250 MPH");
            }
            this.windSpeed = windSpeed;
            return this;
        }

        public WeatherReport build(){
            if(temperature == null && windSpeed == null && humidity == null){
                throw new IllegalArgumentException("Invalid Weather Report, at least one metric must be reported");
            }
            return new WeatherReport(this);
        }

        private boolean isOutsideValidRange(int value, int min, int max){
            return value < min || value > max;
        }
    }
}
