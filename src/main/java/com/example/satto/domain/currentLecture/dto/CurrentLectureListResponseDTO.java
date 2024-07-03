package com.example.satto.domain.currentLecture.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CurrentLectureListResponseDTO(
        List<CurrentLectureResponseDTO> currentLectureResponseDTOList,
        Integer listSize,
        Integer totalPage,
        Long totalElements,
        Boolean isFirst,
        Boolean isLast
) {
}
