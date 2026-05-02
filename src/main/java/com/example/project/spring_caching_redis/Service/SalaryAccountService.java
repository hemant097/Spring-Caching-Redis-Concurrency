package com.example.project.spring_caching_redis.Service;

import com.example.project.spring_caching_redis.DTO.EmployeeDTO;
import com.example.project.spring_caching_redis.Entity.Employee;
import com.example.project.spring_caching_redis.Entity.SalaryAccount;
import com.example.project.spring_caching_redis.Repository.EmployeeRepository;
import com.example.project.spring_caching_redis.Repository.SalaryAccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SalaryAccountService {

    private final SalaryAccountRepository salaryAccountRepository;
    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;

    public SalaryAccount createAccount(Long empId){

        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(()-> new RuntimeException("No employee exists with id "+empId));

//        if(employee.getName().equals("Anuj")) throw new RuntimeException("Anuj is not allowed");

        SalaryAccount salaryAccount = SalaryAccount.builder()
                .employee(employee)
                .balance(BigDecimal.ZERO)
                .build();

        return salaryAccountRepository.save(salaryAccount);
    }

    public SalaryAccount incrementBalance(Long accountId) {

        SalaryAccount salaryAccount = salaryAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal prevBalance = salaryAccount.getBalance();
        BigDecimal newBalance = prevBalance.add(BigDecimal.valueOf(1L));

        salaryAccount.setBalance(newBalance);

        return salaryAccountRepository.save(salaryAccount);
    }
}


