package com.example.satto.domain.timeTable.dto;

import java.util.List;

public record UpdateTimeTableLectRequestDTO(
        List<String> codeSectionList
) {
}
