package com.example.satto.domain.event.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TimetableContestResponseDto(
        Long timetableContestId,
        String name,
        String photo,
        Long likeCount,
        Long dislikeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    @Builder
    public record SavedTimetableContest(
            String name,
            String photo,
            Long likeCount,
            Long dislikeCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
