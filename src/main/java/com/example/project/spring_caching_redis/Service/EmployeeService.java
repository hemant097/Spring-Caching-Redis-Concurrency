package com.example.project.spring_caching_redis.Service;

import com.example.project.spring_caching_redis.DTO.EmployeeDTO;
import com.example.project.spring_caching_redis.Entity.Employee;
import com.example.project.spring_caching_redis.Exceptions.ResourceNotFoundException;
import com.example.project.spring_caching_redis.Repository.EmployeeRepository;
import jakarta.transaction.Transactional;
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
    private final SalaryAccountService salaryAccountService;
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
    @Transactional
    public EmployeeDTO createNewEmployee(EmployeeDTO inputEmployee) {
        log.info("creating new employee with email:{}",inputEmployee.getEmail());

        if(empRep.existsEmployeeByEmail(inputEmployee.getEmail())){
            log.error("Employee already exist with email:{}",inputEmployee.getEmail());
            throw new RuntimeException("Cannot add employee with this email again");
        }

        Employee toMapEmployee = modelMapper.map(inputEmployee, Employee.class);
        Employee savedEmployee = empRep.save(toMapEmployee);

    /*  without @Transactional, if createAccount throws an error, the Employee is saved in DB, but salary account isn't
        created, which is an inconsistent state
        Now if we do use this annotation, the employee is not saved till the entire method logic is completed without
        any error. It wraps the method call with proxy logic. All the calls made on the particular bean's method would
        actually first go to the proxy, because proxy is wrapped over the main bean.
        The Transaction manager starts the transaction at the DB level. Internally @Transaction uses AOP, Before,After,etc.
        Like when the method logic is executed gracefully. The After PointCut is called which then commits the transaction to the DB.
    */
        salaryAccountService.createAccount(savedEmployee);

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
