package com.example.satto.domain.event.repository.impl;

import com.example.satto.domain.event.dto.TimetableContestResponseDto;
import com.example.satto.domain.event.entity.timetableContest.QTimetableContest;
import com.example.satto.domain.event.entity.timetableContest.QTimetableContestDislike;
import com.example.satto.domain.event.entity.timetableContest.QTimetableContestLike;
import com.example.satto.domain.event.repository.EventRepositoryCustom;
import com.example.satto.domain.users.entity.QUsers;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TimetableContestResponseDto> findAllTimetableContest() {
        QTimetableContest timetableContest = QTimetableContest.timetableContest;
        QTimetableContestLike timetableContestLike = QTimetableContestLike.timetableContestLike;
        QTimetableContestDislike timetableContestDislike = QTimetableContestDislike.timetableContestDislike;
        QUsers user = QUsers.users;

        return queryFactory
                .select(Projections.constructor(TimetableContestResponseDto.class,
                        timetableContest.timetableContestId,
                        user.name,
                        timetableContest.photoImg,
                        Expressions.as(JPAExpressions
                                .select(timetableContestLike.count())
                                .from(timetableContestLike)
                                .where(timetableContestLike.timetableContest.eq(timetableContest)), "likeCount"),
                        Expressions.as(JPAExpressions
                                .select(timetableContestDislike.count())
                                .from(timetableContestDislike)
                                .where(timetableContestDislike.timetableContest.eq(timetableContest)), "dislikeCount"),
                        timetableContest.createdAt,
                        timetableContest.updatedAt))
                .from(timetableContest)
                .leftJoin(timetableContest.user, user)
                .fetch();
    }
}
