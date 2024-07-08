package com.example.satto.domain.users.repository.querydsl.Impl;

import com.example.satto.domain.users.entity.QUsers;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.domain.users.repository.querydsl.UsersRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UsersRepositoryCustomImpl implements UsersRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Users> findByStudentIdStartsWith(String studentId) {
        QUsers users = QUsers.users;

        return queryFactory.selectFrom(users)
                .where(users.studentId.startsWith(studentId))
                .fetch();    }

    @Override
    public List<Users> findByNameStartsWith(String name) {
        QUsers users = QUsers.users;

        return queryFactory.selectFrom(users)
                .where(users.name.startsWith(name))
                .fetch();

    }
}
