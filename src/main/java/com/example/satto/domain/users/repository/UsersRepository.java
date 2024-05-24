package com.example.satto.domain.users.repository;

import com.example.satto.domain.users.entity.Users;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {


    Optional<Users> findByEmail(String email);

    Boolean existsByEmail(String email);
}

