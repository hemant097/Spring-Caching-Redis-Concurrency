package com.example.project.spring_caching_redis.Controllers;


import com.example.project.spring_caching_redis.DTO.EmployeeDTO;
import com.example.project.spring_caching_redis.Entity.ApiResponseDto;
import com.example.project.spring_caching_redis.Entity.SalaryAccount;
import com.example.project.spring_caching_redis.Service.EmployeeService;
import com.example.project.spring_caching_redis.Service.SalaryAccountService;
import com.example.project.spring_caching_redis.Service.WeatherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/employees" )
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService empService;
    private final SalaryAccountService salaryAccountService;
    private final WeatherService weatherService;

    //Path Variable, strictly mandatory
    @GetMapping(path = "/{empId}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long empId){

        return ResponseEntity.ok(empService.getEmployeeById(empId));

    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(@RequestParam(required = false) Integer age,
                                          @RequestParam(required = false, name="sort") String sortBy){

        return ResponseEntity.ok(empService.getAllEmployees());

    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createNewEmployee(@RequestBody @Valid EmployeeDTO inputEmployee){

        EmployeeDTO employeeDTO = empService.createNewEmployee(inputEmployee);

        return new ResponseEntity<>(employeeDTO, HttpStatus.CREATED);

    }

    @PutMapping(path="/{empId}")
    public ResponseEntity<EmployeeDTO> updateEmployeeById(@RequestBody @Valid EmployeeDTO employeeDTO,
                                                          @PathVariable Long empId){

        EmployeeDTO updateEmployeeById =  empService.updateEmployeeById(employeeDTO,empId);
        return  ResponseEntity.ok(updateEmployeeById);
    }

    @DeleteMapping(path="/{empId}")
    public ResponseEntity<Boolean> deleteEmployeeById(@PathVariable Long empId){

        boolean deleted =  empService.deleteEmployeeById(empId);

        if(deleted)
            return ResponseEntity.ok(true);
        else
            return ResponseEntity.notFound().build();

    }

    @PatchMapping(path="/{empId}")
    public ResponseEntity<EmployeeDTO> updatePartialEmployee(@PathVariable Long empId,
                                             @RequestBody Map<String,Object> updates){
        EmployeeDTO employeeDTO =  empService.updatePartialEmployee(empId,updates);
        if(employeeDTO==null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(employeeDTO);
    }

    @PutMapping("/incrementBalance/{accountId}")
    public ResponseEntity<SalaryAccount> incrementBalance(@PathVariable Long accountId) {
        SalaryAccount salaryAccount = salaryAccountService.incrementBalance(accountId);
        return ResponseEntity.ok(salaryAccount);
    }

    @GetMapping("/weather")
    public ResponseEntity<ApiResponseDto> getWeatherOfACity(@RequestParam(defaultValue = "Kanpur") String city){

        return ResponseEntity.ok(weatherService.getWeatherFromAPI(city));
    }

}
