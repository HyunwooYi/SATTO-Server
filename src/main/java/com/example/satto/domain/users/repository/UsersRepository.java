package com.example.satto.domain.users.repository;

import com.example.satto.domain.users.entity.Users;
import com.example.satto.domain.users.repository.querydsl.UsersRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>, UsersRepositoryCustom {


    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByStudentId(String studentId);

    Optional<Users> findByStudentId(String studentId);

    Users findByStudentId(Users studentId);

    void deleteProfileImgByStudentId(String studentId);
}

