package com.example.project.spring_caching_redis.Repository;

import com.example.project.spring_caching_redis.Entity.SalaryAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryAccountRepository extends JpaRepository<SalaryAccount, Long> {

//    @Override
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SalaryAccount> findById(Long id);
}


