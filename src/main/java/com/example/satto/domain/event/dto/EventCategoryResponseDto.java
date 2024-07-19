package com.example.satto.domain.event.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventCategoryResponseDto(
        Long eventId,
        String category,
        Long participantsCount,
        LocalDateTime startWhen,
        LocalDateTime untilWhen
) {
}
