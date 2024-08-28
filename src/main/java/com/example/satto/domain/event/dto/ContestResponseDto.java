package com.example.satto.domain.event.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ContestResponseDto(
        Long contestId,
        String name,
        String studentId,
        String photo,
        Long likeCount,
        Long dislikeCount,
        boolean isLiked,
        boolean isDisliked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    @Builder
    public record SavedContest(
            String name,
            String photo,
            Long likeCount,
            Long dislikeCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
