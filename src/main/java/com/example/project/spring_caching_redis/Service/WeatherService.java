package com.example.project.spring_caching_redis.Service;

import com.example.project.spring_caching_redis.Entity.ApiResponse;
import com.example.project.spring_caching_redis.Entity.ApiResponseDto;
import com.example.project.spring_caching_redis.Exceptions.UnknownAPIException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final RestClient restClient;

    @Value("${API_KEY}")
    private String weatherAPI_KEY;

    @CachePut(cacheNames = "weather_cache", key = "{#result.cityName()}")
    public ApiResponseDto getWeatherFromAPI(String city){

        //The api returns a JSON response, we've mapped the required fields from that into our ApiResponse class
        ApiResponse weatherApiResponse =  restClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q",city)
                        .queryParam("appid", weatherAPI_KEY)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new UnknownAPIException("unable to get response from Weather API");
                })
                .body(ApiResponse.class)
                ;

        assert weatherApiResponse != null;
        return ApiResponseDto.of(weatherApiResponse);
    }
}
