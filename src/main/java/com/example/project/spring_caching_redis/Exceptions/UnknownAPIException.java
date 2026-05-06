package com.example.project.spring_caching_redis.Exceptions;

public class UnknownAPIException extends RuntimeException {

    public UnknownAPIException(String message) {
        super(message);
    }
}
