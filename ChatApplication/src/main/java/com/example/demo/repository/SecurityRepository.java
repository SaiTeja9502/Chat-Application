package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Security;

@Repository
public interface SecurityRepository extends JpaRepository<Security, Long>{

}
