package com.example.hngtasks.taskone;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TaskOne {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;



    @GetMapping("/api/hello")
    public Object taskone(@RequestParam("visitor_name") String visitorName) throws IOException {
         final String API_KEY_LOCATION = System.getenv("LOCATION");
       final String API_KEY_WEATHER = System.getenv("WEATHER");
//        final String API_KEY_WEATHER = "7efb1a610595ed90937c4564fc13ab32";
        String ip = request.getRemoteAddr();
        final String LOCATION_URI = String.format("https://geo.ipify.org/api/v2/country,city?apiKey=%s&ipAddress=%s", API_KEY_LOCATION, ip);
        Map<String, Object> locationJson = objectMapper.readValue(new URL(LOCATION_URI), new TypeReference<>() {
        });
        int length = locationJson.get("location").toString().length();
        String[] locationKeys = locationJson.get("location").toString().substring(1, length - 1).split(",");
        String region = locationKeys[1].split("=")[1];
        String lat = locationKeys[3].split("=")[1];
        String lng = locationKeys[4].split("=")[1];
        final String WEATHER_URI = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s", lat, lng, API_KEY_WEATHER);
        Map<String, Object> weatherJson = objectMapper.readValue(new URL(WEATHER_URI), new TypeReference<>() {
        });
        int lengthM = weatherJson.get("main").toString().length();
        int weather = (int) (Double.parseDouble(weatherJson.get("main").toString().substring(1, lengthM -1).split(",")[0].split("=")[1]) - 273.15);


        return new HashMap<String, String>(){{
            put("client_ip", ip);
            put("location", region);
            put("greeting", String.format("Hello, %s!, the temperature is %d degrees Celsius in %s.", visitorName, weather, region));
        }};
    }
}
