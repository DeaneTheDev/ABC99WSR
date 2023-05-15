package com.weathersensors.poc.repository;

import com.weathersensors.poc.entity.WeatherReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WeatherReportRepository extends JpaRepository<WeatherReport, UUID> {


}
