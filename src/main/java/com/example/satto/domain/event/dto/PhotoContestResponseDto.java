package com.example.satto.domain.event.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PhotoContestResponseDto(
        Long photoContestId,
        String name,
        String photo,
        Long likeCount,
        Long dislikeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
