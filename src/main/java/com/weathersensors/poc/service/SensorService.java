package com.weathersensors.poc.service;

import com.weathersensors.poc.entity.Sensor;
import com.weathersensors.poc.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SensorService {

    @Autowired
    private SensorRepository sensorRepository;

    public void save(Sensor sensor){
        sensorRepository.save(sensor);
    }

    public Optional<Sensor> findByTitle(String title){
        return sensorRepository.findByTitle(title);
    }

    public Optional<Sensor> findById(UUID id){
        return sensorRepository.findById(id);
    }

    public List<Sensor> findAllSensors(){
        return sensorRepository.findAll();
    }
}
