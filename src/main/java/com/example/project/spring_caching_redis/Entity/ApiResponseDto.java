package com.example.project.spring_caching_redis.Entity;


public record ApiResponseDto(
         String cityName,
         String weatherDescription,
         String temperatureInCelsius,
         String pressure,
         String humidity
) {


    public static ApiResponseDto of(ApiResponse response){

        return new ApiResponseDto(
                response.getName(),
                response.getWeather()[0].getMain()+", "+response.getWeather()[0].getDescription(),
                String.valueOf(response.getMain().getHumidity()),
                String.valueOf(response.getMain().getPressure()),
                String.valueOf(response.getMain().getHumidity())
        );
    }

}
