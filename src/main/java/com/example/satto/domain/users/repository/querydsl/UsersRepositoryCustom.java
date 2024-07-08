package com.example.satto.domain.users.repository.querydsl;

import com.example.satto.domain.users.entity.Users;

import java.util.List;

public interface UsersRepositoryCustom {
    
    List<Users> findByStudentIdStartsWith(String studentId);

    List<Users> findByNameStartsWith(String name);
}
