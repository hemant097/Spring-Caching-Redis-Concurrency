package com.example.project.spring_caching_redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class SpringCachingRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCachingRedisApplication.class, args);
    }

}
