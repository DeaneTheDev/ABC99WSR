package com.weathersensors.poc.service;

import com.weathersensors.poc.entity.WeatherReport;
import com.weathersensors.poc.repository.WeatherReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherReportService {

    @Autowired
    private WeatherReportRepository weatherReportRepository;

    public void save(WeatherReport weatherReport){
        weatherReportRepository.save(weatherReport);
    }
}
