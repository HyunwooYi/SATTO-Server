package com.example.satto.domain.event.repository;

import com.example.satto.domain.event.entity.timetableContest.TimetableContest;
import com.example.satto.domain.event.entity.timetableContest.TimetableContestLike;
import com.example.satto.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimetableContestLikeRepository extends JpaRepository<TimetableContestLike, Long> {
    Optional<TimetableContestLike> findByUserAndTimetableContest(Users user, TimetableContest timetableContest);

    Long countByTimetableContest(TimetableContest timetableContest);
}
