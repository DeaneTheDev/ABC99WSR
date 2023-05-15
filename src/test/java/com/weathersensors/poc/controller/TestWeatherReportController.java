package com.weathersensors.poc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weathersensors.poc.entity.Sensor;
import com.weathersensors.poc.service.SensorService;
import com.weathersensors.poc.service.WeatherReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherReportController.class)
public class TestWeatherReportController {


    @Autowired
    private MockMvc mvc;

    @MockBean
    private WeatherReportService weatherReportService;

    @MockBean
    private SensorService sensorService;


    Optional<Sensor> sensorOptional;

    @BeforeEach
    public void setUp(){

        Sensor sensor = new Sensor();
        sensor.setId(UUID.fromString("41d4bbbf-ea25-46fb-8d18-614053f4267f"));
        sensor.setTitle("Test");
        sensor.setWeatherReports(new ArrayList<>());
        sensorOptional = Optional.of(sensor);
    }

    @Test
    public void givenWeatherReport_whenPostWeatherReportWithJustTemp_returnCreated() throws Exception {



        given(sensorService.findByTitle("Test")).willReturn(sensorOptional);

        mvc.perform(MockMvcRequestBuilders.post("/weather/Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sensor.title", is("Test")))
                .andExpect(jsonPath("$.temperature", is(10)));


    }

    @Test
    public void givenWeatherReport_whenPostWeatherReportWithAllStats_returnCreated() throws Exception {

        given(sensorService.findByTitle("Test")).willReturn(sensorOptional);

        mvc.perform(MockMvcRequestBuilders.post("/weather/Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\":10," +
                                "\"humidity\":10," +
                                "\"windSpeed\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sensor.title", is("Test")))
                .andExpect(jsonPath("$.temperature", is(10)))
                .andExpect(jsonPath("$.humidity", is(10)))
                .andExpect(jsonPath("$.windSpeed", is(10)));

    }

    @Test
    public void givenNoWeatherReport_whenPostWeatherReport_returnBadRequest() throws Exception {

        given(sensorService.findByTitle("Test")).willReturn(sensorOptional);

        mvc.perform(MockMvcRequestBuilders.post("/weather/Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message",
                        is("Invalid Weather Report, at least one metric must be reported")));

    }

    @Test
    public void givenWeatherReport_whenPostWeatherReportWithTempTooHigh_returnBadRequest() throws Exception {

        given(sensorService.findByTitle("Test")).willReturn(sensorOptional);

        mvc.perform(MockMvcRequestBuilders.post("/weather/Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\":5000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message",
                        is("Temperature must be valid value between -150 and 200 Fahrenheit")));

    }

    @Test
    public void givenWeatherReport_whenPostWeatherReportWithHumidTooHigh_returnBadRequest() throws Exception {

        given(sensorService.findByTitle("Test")).willReturn(sensorOptional);

        mvc.perform(MockMvcRequestBuilders.post("/weather/Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"humidity\":5000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message",
                        is("Humidity must be between 0 and 100%")));

    }
    @Test
    public void givenWeatherReport_whenPostWeatherReportWithWindSpeedTooHigh_returnBadRequest() throws Exception {

        given(sensorService.findByTitle("Test")).willReturn(sensorOptional);

        mvc.perform(MockMvcRequestBuilders.post("/weather/Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"windSpeed\":5000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message",
                        is("Wind Speed must be between 0 and 250 MPH")));

    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void givenWeatherReport_whenPostWeatherReportWithIdentifierId_returnCreated() throws Exception {


        given(sensorService.findById(UUID.fromString("41d4bbbf-ea25-46fb-8d18-614053f4267f"))).willReturn(sensorOptional);

        mvc.perform(MockMvcRequestBuilders.post("/weather/41d4bbbf-ea25-46fb-8d18-614053f4267f?identifier=id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sensor.title", is("Test")))
                .andExpect(jsonPath("$.temperature", is(10)));


    }

    @Test
    public void givenWeatherReport_whenPostWeatherReportWithInvalidSensorDetails_returnBadRequest() throws Exception {


        given(sensorService.findByTitle("INVALID")).willReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.post("/weather/INVALID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message",
                        is("Sensor with title INVALID not found")));
    }
}
