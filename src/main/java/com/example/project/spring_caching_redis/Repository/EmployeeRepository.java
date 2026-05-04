package com.example.project.spring_caching_redis.Repository;


import com.example.project.spring_caching_redis.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    boolean existsEmployeeByEmail(String email);
}
