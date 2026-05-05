package com.example.project.spring_caching_redis.Advices;

import com.example.project.spring_caching_redis.Exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIError> resourceNotFound(ResourceNotFoundException rnfe) {

        APIError apiError = createError(rnfe.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> internalServerError(MethodArgumentNotValidException manve) {

        //getting all the binding errors from the exception and converting it to List<String> using stream
        List<String> errorList = manve.getBindingResult()
                .getAllErrors()
                .stream().map(error->error.getDefaultMessage())
                .collect(Collectors.toList());

        APIError apiError = createError("Input validation failed");
        apiError.setSubErrors(errorList);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StaleObjectStateException.class)
    public ResponseEntity<String> handleStaleObject(StaleObjectStateException exception) {
        log.error(exception.getLocalizedMessage());
        return new ResponseEntity<>("Stale data detected, cannot execute this query\n", HttpStatus.CONFLICT);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<APIError> internalServerError(Exception exception) {
//        APIError apiError = createError(exception.getMessage());
//        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    APIError createError(String message){
        String dateAndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
        return APIError.builder()
                .message(message)
                .errorRecordedTime(dateAndTime)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

}