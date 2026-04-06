package com.example.project.spring_caching_redis.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {

    private Long id;

    @NotBlank(message = "Name of the employee cannot be blank")
    @Size(min=2, max=10, message="Name of the employee should have 2-10 characters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message="Please enter a valid email")
    private String email;

    @NotNull(message = "age cannot be null")
    @Max(value=80, message ="Age cannot be greater than 80")
    @Min(value=18, message = "Age cannot be less than 18")
    private Integer age;


    @Pattern(regexp = "^(ANALYST|DEV)$")
    @NotBlank(message = "role of the employee cannot be blank")
    private String role;


    @DecimalMin(value = "2500.50")
    @DecimalMax(value="299999.99")
    @Digits(integer = 6, fraction = 2, message = "salary should be in the form of XXXXXX.YY")
    @NotNull(message = "salary cannot be null")
    @Positive(message = "salary can only be a positive number")
    private Double salary;


}
