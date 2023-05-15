package com.weathersensors.poc.controller;


import com.weathersensors.poc.dto.Error;
import com.weathersensors.poc.dto.ErrorDTO;
import com.weathersensors.poc.entity.Sensor;
import com.weathersensors.poc.entity.WeatherReport;
import com.weathersensors.poc.service.SensorService;
import com.weathersensors.poc.service.WeatherReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/weather")
public class WeatherReportController {


    @Autowired
    private WeatherReportService weatherReportService;

    @Autowired
    private SensorService sensorService;

    @PostMapping(path = "/{sensor_identifier}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWeatherReport(@RequestBody WeatherReport weatherReport, @RequestParam(defaultValue = "title") String identifier,
                                              @PathVariable("sensor_identifier") String sensorIdentifier){

        Optional<Sensor> sensorOptional;
        if(identifier.equalsIgnoreCase("id")){
            try{
                sensorOptional = sensorService.findById(UUID.fromString(sensorIdentifier));
            } catch(IllegalArgumentException e){
                return getErrorDTOResponseEntity("Incorrect UUID format for id identifier", HttpStatus.BAD_REQUEST);
            }
        }
        else {
            sensorOptional = sensorService.findByTitle(sensorIdentifier);
        }

        if(sensorOptional.isEmpty()){
            return getErrorDTOResponseEntity("Sensor with " + identifier + " " + sensorIdentifier + " not found", HttpStatus.BAD_REQUEST);
        }

        WeatherReport wr;
        try{
            Sensor sensor = sensorOptional.get();
            wr = new WeatherReport.Builder(sensor)
                    .withTemperature(weatherReport.getTemperature())
                    .withHumidity(weatherReport.getHumidity())
                    .withWithSpeed(weatherReport.getWindSpeed())
                    .build();
            weatherReportService.save(wr);
            sensor.addWeatherReport(wr);
            sensorService.save(sensor);

        } catch(IllegalArgumentException e){
            return new ResponseEntity<>
                    (errorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(wr, HttpStatus.CREATED);
    }

    private ResponseEntity<ErrorDTO> getErrorDTOResponseEntity(String message, HttpStatus status) {
        return new ResponseEntity<>
                (errorDTO(message), status);
    }

    private ErrorDTO errorDTO(String message){
        Error error = new Error(message);
        return new ErrorDTO(error);
    }
}
