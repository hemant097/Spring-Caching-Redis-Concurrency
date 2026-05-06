package com.example.project.spring_caching_redis.Config;

import com.example.project.spring_caching_redis.Exceptions.UnknownAPIException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
public class RestClientConfig {

    @Value("${weatherApi.base.url}")
    private String BASE_URL;

    @Bean
    @Qualifier("getWeatherServiceRestClient")
    RestClient getWeatherServiceRestClient() {

        return RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            throw new UnknownAPIException(new String(res.getBody().readAllBytes()));
                        })
                .build();

    }
}