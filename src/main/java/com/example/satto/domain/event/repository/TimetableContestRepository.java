package com.example.satto.domain.event.repository;

import com.example.satto.domain.event.entity.timetableContest.TimetableContest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableContestRepository extends JpaRepository<TimetableContest, Long> {
}
