package com.example.project.spring_caching_redis.Service;

import com.example.project.spring_caching_redis.DTO.EmployeeDTO;
import com.example.project.spring_caching_redis.Entity.EmployeeEntity;
import com.example.project.spring_caching_redis.Exceptions.ResourceNotFoundException;
import com.example.project.spring_caching_redis.Repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

    @Cacheable(cacheNames = "employees", key = "{#empId}")
    public EmployeeDTO getEmployeeById(Long empId) {

        log.info("getting the employee with id:{}",empId);
       EmployeeEntity employee =  empRep.findById(empId)
               .orElseThrow(() -> new ResourceNotFoundException("employee not found"));

       return modelMapper.map(employee,EmployeeDTO.class);
    }

    public List<EmployeeDTO> getAllEmployees() {
        log.info("getting all the employees");

        List<EmployeeEntity> employeeEntityList = empRep.findAll();

        return employeeEntityList
                .stream()
                .map(empEntity -> modelMapper.map(empEntity,EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    public EmployeeDTO createNewEmployee(EmployeeDTO inputEmployee) {
        log.info("getting the employee with email:{}",inputEmployee.getEmail());

        EmployeeEntity toMapEmployee = modelMapper.map(inputEmployee, EmployeeEntity.class);
        EmployeeEntity savedEmployee = empRep.save(toMapEmployee);

        return modelMapper.map(savedEmployee,EmployeeDTO.class);
    }

    public EmployeeDTO updateEmployeeById(EmployeeDTO employeeDTO, Long empId) {

        whetherEmployeeExists(empId);
        log.info("updating the employee with id:{}",empId);

        EmployeeEntity employee = modelMapper.map(employeeDTO,EmployeeEntity.class);
        employee.setId(empId);
        EmployeeEntity savedEmployeeEntity = empRep.save(employee);
        return modelMapper.map(savedEmployeeEntity,EmployeeDTO.class);

    }

    public void whetherEmployeeExists(Long empId){
       boolean exists =  empRep.existsById(empId);

       if(!exists)
           throw new ResourceNotFoundException("this employee does not exist with id "+empId);

    }

    public boolean deleteEmployeeById(Long empId) {
       whetherEmployeeExists(empId);

            empRep.deleteById(empId);
            return true;

    }

    public EmployeeDTO updatePartialEmployee(Long empId, Map<String, Object> updates) {
        whetherEmployeeExists(empId);
        log.info("patching the employee with id:{}",empId);
        EmployeeEntity employeeEntity = empRep.findById(empId).orElseThrow();

        updates.forEach((field,value)->{
            Field fieldToBeUpdated = ReflectionUtils.findField(EmployeeEntity.class,field);
            fieldToBeUpdated.setAccessible(true);
            //modifying the field of employeeEntity using the fieldToBeUpdated, and value from updates
            ReflectionUtils.setField(fieldToBeUpdated,employeeEntity,value);
        });

        return modelMapper.map(empRep.save(employeeEntity),EmployeeDTO.class);



    }
}
