package com.weathersensors.poc.controller;

import com.weathersensors.poc.dto.Error;
import com.weathersensors.poc.dto.ErrorDTO;
import com.weathersensors.poc.dto.SensorDTO;
import com.weathersensors.poc.entity.Sensor;
import com.weathersensors.poc.entity.Stat;
import com.weathersensors.poc.entity.WeatherReport;
import com.weathersensors.poc.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSensor(@RequestBody Sensor sensor){
        String title = sensor.getTitle();
        if(title.length() < 1 || title.length() > 25){
            return new ResponseEntity<>
                    (errorDTO("Title length must be between 1 and 25 (inc) characters"), HttpStatus.BAD_REQUEST);
        }

        if(sensorService.findByTitle(title).isPresent()){
            return new ResponseEntity<>
                    (errorDTO("Title must be unique, a Sensor with the same title already exists"), HttpStatus.CONFLICT);
        }

        sensorService.save(sensor);

        return new ResponseEntity<>(sensorDto(sensor), HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SensorDTO>> getSensors(@RequestParam Optional<UUID []> id,
                                                      @RequestParam Optional<String []> title,
                                                      @RequestParam(defaultValue = "AVERAGE") Stat stat,
                                                      @RequestParam Optional<LocalDate> startDate,
                                                      @RequestParam Optional<LocalDate> endDate){


        List<UUID> uuids = id.map(value -> Arrays.stream(value).toList()).orElseGet(ArrayList::new);
        List<String> titles = title.map(value -> Arrays.stream(value).toList()).orElseGet(ArrayList::new);

        List<Sensor> sensors =
                sensorService.findAllSensors().stream()
                        .filter(s -> uuids.isEmpty() || uuids.contains(s.getId()))
                        .filter(s -> titles.isEmpty() || titles.contains(s.getTitle())).toList();

        if(startDate.isPresent() && endDate.isPresent()) {
            for (Sensor s : sensors) {
                s.getWeatherReports().removeIf(wr -> wr.getReportDate().toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDate().isBefore(startDate.get()) || wr.getReportDate().toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate().isAfter(endDate.get()));
            }
        }

        List<SensorDTO> sensorDTOS = sensors.stream().map(s -> sensorDto(s, stat)).toList();
        return new ResponseEntity<>(sensorDTOS, HttpStatus.OK);

    }

    private SensorDTO sensorDto(Sensor sensor){
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setId(sensor.getId().toString());
        sensorDTO.setTitle(sensor.getTitle());
        return sensorDTO;
    }

    private SensorDTO sensorDto(Sensor sensor, Stat stat){

        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setId(sensor.getId().toString());
        sensorDTO.setTitle(sensor.getTitle());

        switch(stat){
            case AVERAGE -> setAverages(sensor, sensorDTO);
            case MIN -> setMin(sensor, sensorDTO);
            case MAX -> setMax(sensor, sensorDTO);
            case SUM -> setSum(sensor, sensorDTO);
        }
        return sensorDTO;
    }

    private void setAverages(Sensor sensor, SensorDTO sensorDTO) {
        sensorDTO.setAverageTemp(getAverageTemperature(sensor));
        sensorDTO.setAverageHumidity(getAverageHumidity(sensor));
        sensorDTO.setAverageWindSpeed(getAverageWindSpeed(sensor));
    }

    private void setMin(Sensor sensor, SensorDTO sensorDTO) {
        sensorDTO.setMinTemp(getMinTemperature(sensor));
        sensorDTO.setMinHumidity(getMinHumidity(sensor));
        sensorDTO.setMinWindSpeed(getMinWindSpeed(sensor));
    }

    private void setMax(Sensor sensor, SensorDTO sensorDTO) {
        sensorDTO.setMaxTemp(getMaxTemperature(sensor));
        sensorDTO.setMaxHumidity(getMaxHumidity(sensor));
        sensorDTO.setMaxWindSpeed(getMaxWindSpeed(sensor));
    }

    private void setSum(Sensor sensor, SensorDTO sensorDTO) {
        sensorDTO.setTempSum(getTemperatureSum(sensor));
        sensorDTO.setHumiditySum(getHumiditySum(sensor));
        sensorDTO.setWindSpeedSum(getWindSpeedSum(sensor));
    }

    private Double getAverageTemperature(Sensor sensor){
        if(getAverage(sensor,"temperature").isPresent()){
            return getAverage(sensor,"temperature").getAsDouble();
        }
        return null;
    }
    private Double getAverageHumidity(Sensor sensor){
        if(getAverage(sensor,"humidity").isPresent()){
            return getAverage(sensor,"humidity").getAsDouble();
        }
        return null;
    }
    private Double getAverageWindSpeed(Sensor sensor){
        if(getAverage(sensor,"windSpeed").isPresent()){
            return getAverage(sensor,"windSpeed").getAsDouble();
        }
        return null;
    }

    private Integer getMinTemperature(Sensor sensor){
        if(getMin(sensor,"temperature").isPresent()){
            return getMin(sensor,"temperature").getAsInt();
        }
        return null;
    }
    private Integer getMinHumidity(Sensor sensor){
        if(getMin(sensor,"humidity").isPresent()){
            return getMin(sensor,"humidity").getAsInt();
        }
        return null;
    }
    private Integer getMinWindSpeed(Sensor sensor){
        if(getMin(sensor,"windSpeed").isPresent()){
            return getMin(sensor,"windSpeed").getAsInt();
        }
        return null;
    }

    private Integer getMaxTemperature(Sensor sensor){
        if(getMax(sensor,"temperature").isPresent()){
            return getMax(sensor,"temperature").getAsInt();
        }
        return null;
    }
    private Integer getMaxHumidity(Sensor sensor){
        if(getMax(sensor,"humidity").isPresent()){
            return getMax(sensor,"humidity").getAsInt();
        }
        return null;
    }
    private Integer getMaxWindSpeed(Sensor sensor){
        if(getMax(sensor,"windSpeed").isPresent()){
            return getMax(sensor,"windSpeed").getAsInt();
        }
        return null;
    }

    private Integer getTemperatureSum(Sensor sensor){
        return getSum(sensor,"temperature");
    }
    private Integer getHumiditySum(Sensor sensor){
        return getSum(sensor,"humidity");
    }

    private Integer getWindSpeedSum(Sensor sensor){
        return getSum(sensor,"windSpeed");
    }

    private OptionalDouble getAverage(Sensor sensor, String metric){
        return sensor.getWeatherReports().stream().filter(getMetricPredicate(metric)).mapToInt(getMetricFunction(metric)).average();
    }

    public OptionalInt getMin(Sensor sensor, String metric){
        return sensor.getWeatherReports().stream().filter(getMetricPredicate(metric)).mapToInt(getMetricFunction(metric)).min();
    }

    public OptionalInt getMax(Sensor sensor, String metric){
        return sensor.getWeatherReports().stream().filter(getMetricPredicate(metric)).mapToInt(getMetricFunction(metric)).max();
    }

    public int getSum(Sensor sensor, String metric){
        return sensor.getWeatherReports().stream().filter(getMetricPredicate(metric)).mapToInt(getMetricFunction(metric)).sum();
    }

    private static ToIntFunction<WeatherReport> getMetricFunction(String metric) {
        if(metric.equalsIgnoreCase("temperature")){
            return WeatherReport::getTemperature;
        }
        if(metric.equalsIgnoreCase("humidity")){
            return WeatherReport::getHumidity;
        }
        if(metric.equalsIgnoreCase("windSpeed")){
            return WeatherReport::getWindSpeed;
        }
        return WeatherReport::getTemperature;
    }

    private static Predicate<WeatherReport> getMetricPredicate(String metric){

        if(metric.equalsIgnoreCase("temperature")){
            return weatherReport -> weatherReport.getTemperature() != null;
        }
        if(metric.equalsIgnoreCase("humidity")){
            return weatherReport -> weatherReport.getHumidity() != null;
        }
        if(metric.equalsIgnoreCase("windSpeed")){
            return weatherReport -> weatherReport.getHumidity() != null;

        }
        return weatherReport -> weatherReport.getTemperature() != null;
    }


    private ErrorDTO errorDTO(String message){
        Error error = new Error(message);
        return new ErrorDTO(error);
    }


}
