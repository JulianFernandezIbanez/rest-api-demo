package com.example.spring_security_jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_security_jwt.model.Role;
import java.util.Optional;

import com.example.spring_security_jwt.model.ERole;


public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(ERole name);

}
