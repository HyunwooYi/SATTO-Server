package com.example.satto.domain.event.repository;

import com.example.satto.domain.event.entity.Contest;
import com.example.satto.domain.event.entity.Event;
import com.example.satto.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    Long countByEvent(Event event);

    boolean existsByUserAndEvent(Users user, Event event);

    List<Contest> findAllByCategory(String category);
}
