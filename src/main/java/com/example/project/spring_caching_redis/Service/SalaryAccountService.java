package com.example.project.spring_caching_redis.Service;

import com.example.project.spring_caching_redis.Entity.Employee;
import com.example.project.spring_caching_redis.Entity.SalaryAccount;
import com.example.project.spring_caching_redis.Repository.SalaryAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryAccountService {

    private final SalaryAccountRepository salaryAccountRepository;

    private final List<String> restrictedCountries = List.of("AF","BY","IQ","SY");

    @Transactional(propagation = Propagation.REQUIRED) //this is default
    public void createAccount(Employee employee){
        //if for some reason, there is a condition which restricts creating salary account, due to some reason
        if(restrictedCountries.contains(employee.getNationality()))
            throw new RuntimeException("This country not allowed");

        SalaryAccount salaryAccount = SalaryAccount.builder()
                .employee(employee)
                .balance(BigDecimal.ZERO)
                .build();

        salaryAccountRepository.save(salaryAccount);
    }

//    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Transactional
    public SalaryAccount incrementBalance(Long accountId) {

        SalaryAccount salaryAccount = salaryAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal prevBalance = salaryAccount.getBalance();
        salaryAccount.setBalance(prevBalance.add(BigDecimal.ONE));

        return salaryAccountRepository.save(salaryAccount);
    }
}




