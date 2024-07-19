package com.example.satto.domain.event.dto;

import java.time.LocalDateTime;

public record TimetableContestResponseDto(
        Long timetableContestId,
        String name,
        String photo,
        Long likeCount,
        Long dislikeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
