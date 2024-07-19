package com.example.satto.domain.event.repository;

import com.example.satto.domain.event.entity.timetableContest.TimetableContest;
import com.example.satto.domain.event.entity.timetableContest.TimetableContestDislike;
import com.example.satto.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimetableContestDislikeRepository extends JpaRepository<TimetableContestDislike, Long> {
    Optional<TimetableContestDislike> findByUserAndTimetableContest(Users user, TimetableContest timetableContest);
}
