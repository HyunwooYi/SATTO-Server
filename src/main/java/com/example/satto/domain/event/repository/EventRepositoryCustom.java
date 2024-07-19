package com.example.satto.domain.event.repository;

import com.example.satto.domain.event.dto.TimetableContestResponseDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepositoryCustom {
    List<TimetableContestResponseDto> findAllTimetableContest();
}
