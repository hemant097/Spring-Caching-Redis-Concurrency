package com.example.project.spring_caching_redis.Entity;

import lombok.Data;

@Data
public class ApiResponse {

    private String name;
    private Weather[] weather;
    private Main main;

}
