package com.example.satto.domain.event.repository;

import com.example.satto.domain.event.entity.Event;
import com.example.satto.domain.event.entity.photoContest.PhotoContest;
import com.example.satto.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoContestRepository extends JpaRepository<PhotoContest, Long> {
    Long countByEvent(Event event);

    boolean existsByUserAndEvent(Users user, Event event);
}
