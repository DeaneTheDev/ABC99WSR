package com.weathersensors.poc.repository;

import com.weathersensors.poc.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, UUID> {

     Optional<Sensor> findByTitle(String title);

}
