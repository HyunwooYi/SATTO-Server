package com.example.satto.domain.event.repository;

import com.example.satto.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {
     Optional<Event> findByCategory(String photoContest);
}
