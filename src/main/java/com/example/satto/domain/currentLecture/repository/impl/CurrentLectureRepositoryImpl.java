package com.example.satto.domain.currentLecture.repository.impl;

import com.example.satto.domain.currentLecture.dto.CurrentLectureResponseDTO;
import com.example.satto.domain.currentLecture.entity.QCurrentLecture;
import com.example.satto.domain.currentLecture.repository.CurrentLectureRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CurrentLectureRepositoryImpl implements CurrentLectureRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CurrentLectureResponseDTO> findLectures(
            String lectName, List<Integer> grade, int elective,
            int normal, int essential, byte humanity, byte society,
            byte nature, byte engineering, byte art, byte isCyber, List<String> timeZone, Pageable pageable) {

        QCurrentLecture lecture = QCurrentLecture.currentLecture;
        BooleanBuilder builder = new BooleanBuilder();

        if (lectName != null && !lectName.isEmpty()) {
            builder.and(lecture.lectName.contains(lectName));
        }
        if (grade != null && !grade.isEmpty()) {
            builder.and(lecture.grade.in(grade));
        }
        if (elective > 0) {
            builder.and(lecture.cmpDiv.eq("교선"));
        }
        if (normal > 0) {
            builder.and(lecture.cmpDiv.eq("일선"));
        }
        if (essential > 0) {
            builder.and(lecture.cmpDiv.eq("교필"));
        }
        if (humanity > 0) {
            builder.and(lecture.subjectType.eq("인문"));
        }
        if (society > 0) {
            builder.and(lecture.subjectType.eq("사회"));
        }
        if (nature > 0) {
            builder.and(lecture.subjectType.eq("자연"));
        }
        if (engineering > 0) {
            builder.and(lecture.subjectType.eq("공학"));
        }
        if (art > 0) {
            builder.and(lecture.subjectType.eq("예술"));
        }
        if (isCyber == 1) {
            builder.and(lecture.isCyber.eq("Y"));
        } else if (isCyber == 2) {
            builder.and(lecture.isCyber.eq("N"));
        }
        if (timeZone != null && !timeZone.isEmpty()) {
            for (String time : timeZone) {
                String likePattern = "%" + time + " %";
                String likePattern2 = "% " + time + " %";
                String likePattern3 = "% " + time;
                BooleanExpression lectTimeTemplate = lecture.lectTime.like(likePattern);
                BooleanExpression lectTimeTemplate2 = lecture.lectTime.like(likePattern2);
                BooleanExpression lectTimeTemplate3 = lecture.lectTime.like(likePattern3);
                builder.and(lectTimeTemplate.or(lectTimeTemplate2).or(lectTimeTemplate3));
            }
        }

        List<CurrentLectureResponseDTO> results = queryFactory
                .select(Projections.constructor(CurrentLectureResponseDTO.class,
                        lecture.department,
                        lecture.code,
                        lecture.codeSection,
                        lecture.lectName,
                        lecture.professor,
                        lecture.lectTime,
                        lecture.cmpDiv,
                        lecture.subjectType,
                        lecture.isCyber,
                        lecture.credit))
                .from(lecture)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(lecture.count())
                .from(lecture)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
