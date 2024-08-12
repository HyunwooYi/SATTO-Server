package com.example.satto.domain.event.repository;

import com.example.satto.domain.event.entity.Event;
import com.example.satto.domain.event.entity.timetableContest.TimetableContest;
import com.example.satto.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableContestRepository extends JpaRepository<TimetableContest, Long> {
    boolean existsByUserAndEvent(Users user, Event event);
}
