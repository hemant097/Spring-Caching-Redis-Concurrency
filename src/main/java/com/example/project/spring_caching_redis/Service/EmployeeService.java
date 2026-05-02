package com.example.project.spring_caching_redis.Service;

import com.example.project.spring_caching_redis.DTO.EmployeeDTO;
import com.example.project.spring_caching_redis.Entity.Employee;
import com.example.project.spring_caching_redis.Exceptions.ResourceNotFoundException;
import com.example.project.spring_caching_redis.Repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository empRep;
    private final ModelMapper modelMapper;
    private final String CACHE_NAME="employees";


    @Cacheable(cacheNames = CACHE_NAME, key = "{#empId}")
    public EmployeeDTO getEmployeeById(Long empId) {

        log.info("getting the employee with id:{}",empId);
       Employee employee =  empRep.findById(empId)
               .orElseThrow(() -> new ResourceNotFoundException("employee not found"));

       return modelMapper.map(employee,EmployeeDTO.class);
    }

    public List<EmployeeDTO> getAllEmployees() {
        log.info("getting all the employees");

        List<Employee> employeeEntityList = empRep.findAll();

        return employeeEntityList
                .stream()
                .map(empEntity -> modelMapper.map(empEntity,EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    @CachePut(cacheNames = CACHE_NAME, key = "{#result.id}")
    public EmployeeDTO createNewEmployee(EmployeeDTO inputEmployee) {
        log.info("creating new employee with email:{}",inputEmployee.getEmail());

        Employee toMapEmployee = modelMapper.map(inputEmployee, Employee.class);
        Employee savedEmployee = empRep.save(toMapEmployee);

        return modelMapper.map(savedEmployee,EmployeeDTO.class);
    }

    @CachePut(cacheNames = CACHE_NAME, key = "{#empId}")
    public EmployeeDTO updateEmployeeById(EmployeeDTO employeeDTO, Long empId) {

        Employee employee = empRep.findById(empId)
                .orElseThrow(() -> {
                    log.error("employee not found with id:{}", empId);
                    return new ResourceNotFoundException("employee not found with id " + empId);
                });
        log.info("updating the employee with id:{}",empId);

        if(employeeDTO.getEmail().equals(employee.getEmail()))
            throw new RuntimeException("The email of the employee cannot be updated");

        modelMapper.map(employeeDTO,employee);
        employee.setId(empId);
        Employee savedEmployeeEntity = empRep.save(employee);
        return modelMapper.map(savedEmployeeEntity,EmployeeDTO.class);

    }

    public void whetherEmployeeExists(Long empId){
       boolean exists =  empRep.existsById(empId);

       if(!exists)
           throw new ResourceNotFoundException("this employee does not exist with id "+empId);

    }

    @CacheEvict(cacheNames = CACHE_NAME, key = "{#empId}")
    public boolean deleteEmployeeById(Long empId) {
       whetherEmployeeExists(empId);
        log.info("Deleting employee with ID:{}",empId);

        empRep.deleteById(empId);
        return true;

    }

    public EmployeeDTO updatePartialEmployee(Long empId, Map<String, Object> updates) {
        whetherEmployeeExists(empId);
        log.info("patching the employee with id:{}",empId);
        Employee employeeEntity = empRep.findById(empId).orElseThrow();

        updates.forEach((field,value)->{
            Field fieldToBeUpdated = ReflectionUtils.findField(Employee.class,field);
            fieldToBeUpdated.setAccessible(true);
            //modifying the field of employeeEntity using the fieldToBeUpdated, and value from updates
            ReflectionUtils.setField(fieldToBeUpdated,employeeEntity,value);
        });

        return modelMapper.map(empRep.save(employeeEntity),EmployeeDTO.class);



    }
}
