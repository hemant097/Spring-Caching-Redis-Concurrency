package com.example.project.spring_caching_redis.Entity;

import jakarta.persistence.*;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dept_title")
    private String department_title;
}
