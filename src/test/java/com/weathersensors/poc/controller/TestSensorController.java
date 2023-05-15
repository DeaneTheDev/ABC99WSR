package com.weathersensors.poc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weathersensors.poc.entity.Sensor;
import com.weathersensors.poc.entity.WeatherReport;
import com.weathersensors.poc.service.SensorService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorController.class)
public class TestSensorController {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SensorService sensorService;

    Sensor sensor;
    WeatherReport wr1;
    WeatherReport wr2;
    List<Sensor> sensors;
    List<WeatherReport> wrs;


    @BeforeEach
    public void setUp(){

        sensor = new Sensor();
        sensor.setId(UUID.fromString("41d4bbbf-ea25-46fb-8d18-614053f4267f"));
        sensor.setTitle("Test");


        wr1 = new WeatherReport.Builder(sensor)
                .withTemperature(10)
                .withWithSpeed(20)
                .withHumidity(30)
                .build();

        wr2 = new WeatherReport.Builder(sensor)
                .withTemperature(20)
                .withWithSpeed(30)
                .withHumidity(40)
                .build();

        Date wr1Date = Date.from(LocalDate.of(2023, 1, 2)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        wr1.setReportDate(wr1Date);

        Date wr2Date = Date.from(LocalDate.of(2023, 1, 9)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        wr2.setReportDate(wr2Date);

        wrs = new ArrayList<>(List.of(wr1, wr2));
        sensor.setWeatherReports(wrs);

        var sensor2 = new Sensor();
        sensor2.setId(UUID.fromString("41d4bbbf-ea25-46fb-8d18-614053f4268f"));
        sensor2.setTitle("Test2");
        sensor2.setWeatherReports(wrs);

        sensors = List.of(sensor, sensor2);
    }

    @Test
    public void givenSensor_whenGetSensors_returnSensorDetails() throws Exception {

        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Test")));


    }

    @Test
    public void givenSensorWithWeatherReports_whenGetSensors_returnSensorDetailsWithAverageDefault() throws Exception {

        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].averageTemp", is(15.0)))
                .andExpect(jsonPath("$[0].averageHumidity", is(35.0)))
                .andExpect(jsonPath("$[0].averageWindSpeed", is(25.0)));

    }

    @Test
    public void givenSensorWithWeatherReports_whenGetSensors_returnSensorDetailsWithStatAsMin() throws Exception {

        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor?stat=MIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].minTemp", is(10)))
                .andExpect(jsonPath("$[0].minHumidity", is(30)))
                .andExpect(jsonPath("$[0].minWindSpeed", is(20)));


    }

    @Test
    public void givenSensorWithWeatherReports_whenGetSensors_returnSensorDetailsWithStatAsMax() throws Exception {

        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor?stat=MAX")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maxTemp", is(20)))
                .andExpect(jsonPath("$[0].maxHumidity", is(40)))
                .andExpect(jsonPath("$[0].maxWindSpeed", is(30)));


    }

    @Test
    public void givenSensorWithWeatherReports_whenGetSensors_returnSensorDetailsWithStatAsSum() throws Exception {

        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor?stat=SUM")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tempSum", is(30)))
                .andExpect(jsonPath("$[0].humiditySum", is(70)))
                .andExpect(jsonPath("$[0].windSpeedSum", is(50)));

    }

    @Test
    public void givenSensor_whenGetSensorWithIncorrectTitle_returnNoSensors() throws Exception {

        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor?title=NOTAVALIDTILE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void givenSensor_whenGetSensorsWithValidTitle_returnSensorDetails() throws Exception {

        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor?title=Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Test")));


    }

    @Test
    public void givenSensor_whenGetSensorsWith2ValidTitles_returnSensorDetails() throws Exception {

        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor?title=Test,Test2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Test")))
                .andExpect(jsonPath("$[1].title", is("Test2")));

    }

    @Test
    public void givenSensor_whenGetSensorsWithDateRange_returnSensorWithNoWeatherReports() throws Exception {


        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor?title=Test,Test2&startDate=2018-10-02&endDate=2018-10-03")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Test")))
                .andExpect(jsonPath("$[0].averageTemp").doesNotExist());
    }

    @Test
    public void givenSensor_whenGetSensorsWithDateRange_returnSensorWithOneWeatherReports() throws Exception {


        given(sensorService.findAllSensors()).willReturn(sensors);

        mvc.perform(MockMvcRequestBuilders.get("/sensor?title=Test,Test2&startDate=2023-01-01&endDate=2023-01-05")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Test")))
                //An average of 10 means it only used data from 1 weather report
                .andExpect(jsonPath("$[0].averageTemp", is(10.0)));
    }

    @Test
    public void givenValidSensor_whenPostSensor_returnSensorCreated() throws Exception {

        Sensor sensor = new Sensor();
        sensor.setId(UUID.fromString("41d4bbbf-ea25-46fb-8d18-614053f4267f"));
        sensor.setTitle("Test");

        mvc.perform(MockMvcRequestBuilders.post("/sensor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sensor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Test")));
    }

    @Test
    public void givenSensor_whenPostSensorWithEmptyTitle_returnError() throws Exception {

        Sensor sensor = new Sensor();
        sensor.setId(UUID.fromString("41d4bbbf-ea25-46fb-8d18-614053f4267f"));
        sensor.setTitle("");
        
        mvc.perform(MockMvcRequestBuilders.post("/sensor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sensor)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message",
                        is("Title length must be between 1 and 25 (inc) characters")));


    }

    /*
    Would add test on duplicate title etc
     */

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
